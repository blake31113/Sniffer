import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.hyperic.sigar.cmd.SysInfo;


public class VMLoad extends Thread
{
	private Conf conf;
	private BlockingQueue<String> queue;
	public VMLoad(Conf conf,BlockingQueue<String> queue)
	{
		this.conf=conf;
		this.queue=queue;
	}
	
	public void run(){
		
		ArrayList<VMInfo> list1 =new ArrayList<VMInfo>();
		ArrayList<VMInfo> list2;
		ArrayList<VMInfo> list3;
		ArrayList<String> cmd=new ArrayList<String>();
		
		while(true)
		{
			
			cmd.clear();
			//get active vm list
			cmd.add("nova list |grep ACTIVE");
			ArrayList<String> result = LinuxCmd.execCmd(cmd);
			list2=new ArrayList<VMInfo>();
			
			//analysis vm name/id/network
			for(String temp:result)
			{
				VMInfo vm = new VMInfo();
				String[] as=temp.replaceAll(" ", "").split("\\|");
				vm.name=as[2];
				vm.vm_id=as[1];
				if(as[6].length()>22)
				{
					String[] ss=as[6].split(",");
					for(String t : ss)
					{
						vm.vm_ip+=t.split("=")[1];
						vm.vm_ip+=",";
					}
					
				}
				else
				{
					
					vm.vm_ip=as[6].split("=")[1];
				}
				list2.add(vm);
			}//end for
			
			list3=new ArrayList<VMInfo>();
			boolean flag;
			
			//compare list1(already exist thread list)&list2(this time got information)
			for(VMInfo vm2:list2)
			{	
				flag=false;
				System.out.println("list1.size:"+list1.size());
				for(int i = 0;i<=list1.size();i++)
				{
					if(list1.size()!=0)
					{
						
						if(vm2.name.equals(list1.get(i).name))
						{
							flag=true;
							break;
						}
						else
						{
							flag=false;
						}
					}
					else
					{
						flag=false;
					}
					if(!flag)
					{
						//if list1 doesn't have this vm ,add to list3
						list3.add(vm2);
					}
				}
				
			}//end for
			//list2's vm has all monitored
			list1=list2;
			//generate vm loading thread in list3
			for(VMInfo testvm:list3)
			{
				String[] ips ;
				if(testvm.vm_ip.contains(","))
				{
					ips=testvm.vm_ip.split(",");
					
				}
				else
				{
					ips=new String[1];
					ips[0]=String.valueOf(testvm.vm_ip);
				}
				
				Thread[] netSpeedThread = new Thread[ips.length];
				int i=0;
				
				
				for(String temp_ip : ips)
				{
					
					cmd.clear();
					//get network name
					cmd.add("neutron port-list |grep "+temp_ip);
					ArrayList<String> port_list = LinuxCmd.execCmd(cmd);
					String nicname=port_list.get(0);
							String t1=nicname.replaceAll(" ", "");
							
							String t2=t1.split("\\|")[1];
							
							String t3=t2.substring(0, 11);
									
					netSpeedThread[i] = new NetSpeed("tap"+t3,queue,conf.hostName+"-"+testvm.name);
					netSpeedThread[i].start();
					i++;
					
				}//end for loop ips
				
				Thread vmloading =new VMLoading(conf.hostName+"-"+testvm.name,queue,testvm.vm_id);
				vmloading.start();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				};
				
			}//end for loop list3
			
			//sleep 10 minute
			try {
				Thread.sleep(600000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}//end while loop
		}
} 	

class VMInfo
{
	//vm container
	public String name;
	public String vm_id="";
	public String vm_ip="";
}
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;



public class main {

	public static void main(String[] args) {
		
		ConfigReader configreader=new ConfigReader();
		Conf conf=configreader.getConf();
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(2048);
		
		if(conf.mode.equals("openstack"))
		{
			//cpu and mem thread
			Thread cpuAndMemThread = new sigar(queue, conf.hostName);
			
			//net speed threads
			String[] nics=conf.nics.split(",");
			int nicNum;
			if(nics[nics.length-1].length()==0)
			{
				nicNum = nics.length-1;
			}
			else 
			{
				nicNum = nics.length;
			}
			Thread[] netSpeedThread = new Thread[nicNum];
			for(int i=0;i<nicNum;i++)
			{
				netSpeedThread[i] = new NetSpeed(nics[i],queue,conf.hostName);
			}
			
			//socket client
			Thread socketThread = new Socket_C(conf.monitor_ip, conf.monitor_port,conf.host_ip,conf.hostName,queue);
			Thread vMThread = new VMLoad(conf,queue);
			
			//thread start
			socketThread.start();
			cpuAndMemThread.start();
			vMThread.start();
			for(int i=0;i<nicNum;i++)
				netSpeedThread[i].start();
			
		}
		
		else
		{
			//Ryu socket thread
			Thread socketThread = new socket_Ryu(queue,conf);
			socketThread.start();
			
		}//end else
		
	}//end main

}//end class




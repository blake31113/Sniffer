import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

//vm cpu loading

public class VMLoading extends Thread 
{
	private String hostName;
	private String vmid;
	private BlockingQueue<String> queue;
	public VMLoading(String hostName,BlockingQueue<String> queue,String vmid)
	{
		this.hostName = hostName;
		this.queue = queue;
		this.vmid=vmid;
	}
	public void run()
	{
		System.out.println("=====================================================================");
		ArrayList<String> cmd =new ArrayList<String>();
		//vm memory command
		cmd.add("virsh dommemstat "+vmid);
		ArrayList<String> s=LinuxCmd.execCmd(cmd);
		//result
		double max = 	Integer.parseInt(s.get(0).split(" ")[1]);
		double rss =	Integer.parseInt(s.get(1).split(" ")[1]);
		double mem_util=	rss/max;
		NumberFormat formatter = new DecimalFormat("#.##");
		String  MEMF = formatter.format(mem_util); 
		
		System.out.println("mem max:"+max);
		System.out.println("mem rss:"+rss);
		System.out.println("mem usage:"+MEMF);
		for(String ss:s)
		{
			System.out.println(ss);
		}
		cmd.clear();
		//vm cpu command
		cmd.add("virsh cpu-stats "+vmid);
		ArrayList<String> s2=LinuxCmd.execCmd(cmd);
		
		try{
			Thread.sleep(1000);
		} 
		catch(InterruptedException e)
		{
			e.printStackTrace();
		};
		
		double cput1 =Double.parseDouble(s2.get(13).replaceAll(" ","").replaceAll("cpu_time","").replaceAll("seconds","").replaceAll("	",""));
		
		try {
			Thread.sleep(4000);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		};
		//execute command again,get cput2
		ArrayList<String> s3=LinuxCmd.execCmd(cmd);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		};
		
		//calculate
		double cput2 =Double.parseDouble(s3.get(13).replaceAll(" ","").replaceAll("cpu_time","").replaceAll("seconds","").replaceAll("	",""));
		double cpu_util=(100*(cput2-cput1))/(5*4);
		
		String  CPUF = formatter.format(cpu_util); 
		
		try {
			queue.put("G|"+hostName+"|"+CPUF+"|"+MEMF+"|");
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
}

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.BlockingQueue;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

//get machine cpu & ram util

public class sigar extends Thread
{

	private static Sigar sigar = new Sigar();
	private BlockingQueue<String> queue;
	private String hostName="";
	
	public sigar(BlockingQueue<String> queue,String hostName)
	{
		this.queue = queue;
		
		this.hostName = hostName;
	}
	
	public void getInformationsAboutCpuAndMem()
	{
		 try 
		 {
			 NumberFormat formatter = new DecimalFormat("#.##");
			 String  CPUF = formatter.format(sigar.getCpuPerc().getUser()*100); 
			 String  MEMF = formatter.format(sigar.getMem().getUsedPercent());
			 String cmdString = "D|"+ hostName +"|"+ CPUF +"|"+MEMF+"|";
			 queue.put(cmdString);
		 } 
		 catch (SigarException e) 
		 {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 catch (InterruptedException e) 
		 {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
	}
	
	
    public void run()
    {
    	try 
    	{
			while(true)
			{
				getInformationsAboutCpuAndMem();
				Thread.sleep(200);
			}
		} 
    	catch (Exception e) 
		{
			// TODO: handle exception
    		e.printStackTrace();
		}
    }
}

import java.util.concurrent.BlockingQueue;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

//get machine net speed(Rx&Tx)

public class NetSpeed extends Thread 
{
	private String nic = "";
	private BlockingQueue<String> queue;
	private Sigar sigar = new Sigar();
	private String hostNameAndif;
	
	public NetSpeed(String nic, BlockingQueue<String> queue,String hostName)
	{
		this.nic = nic;
		this.queue = queue;
		this.hostNameAndif = hostName+":"+nic;
		
	}
	public void run()
	{
		long Rxnum1=0;
		long Rxnum2=0;
		long Txnum1=0;
		long Txnum2=0;
		long Rxspeed=0;
		long Txspeed=0;
		try 
		{
			Rxnum1=sigar.getNetInterfaceStat(nic).getRxBytes();
			Txnum1=sigar.getNetInterfaceStat(nic).getTxBytes();
			while(true)
			{	
				Thread.sleep(500);
				Rxnum2=sigar.getNetInterfaceStat(nic).getRxBytes();
				Txnum2=sigar.getNetInterfaceStat(nic).getTxBytes();
				Rxspeed=(Rxnum2-Rxnum1)*2;
				Txspeed=(Txnum2-Txnum1)*2;
				Txnum1 = Txnum2;
				Rxnum1 = Rxnum2;
				
				String cmdString = "E|"+hostNameAndif+"|"+Rxspeed+"|"+Txspeed+"|";
				//System.out.println("rxspeed="+Rxspeed+" bytes/s");
				//System.out.println("txspeed="+Txspeed+" bytes/s");
				queue.put(cmdString);
			}
		} 
		catch (SigarException e) 
		{
			
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			
			e.printStackTrace();
		}
	}
}

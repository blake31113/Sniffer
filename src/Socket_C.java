import java.net.*;
import java.io.*;
import java.util.concurrent.BlockingQueue;

//socket for openstack

public class Socket_C extends Thread
{
	private InetSocketAddress	isa;
	private BlockingQueue<String> queue;
	private String host_ip;
	private String host_name;
	public Socket_C(String monitor_ip,int monitor_port,String host_ip,String host_name, BlockingQueue<String> queue)
	{
		//Creater
		isa = new InetSocketAddress(monitor_ip,monitor_port);
		this.queue = queue;
		this.host_ip = host_ip;
		this.host_name = host_name;
	}
	
	public void run()
	{
		while(true)
		{
			//start
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			//msg
			//Start connect
			Socket socket = new Socket();
			try
			{
				socket.connect(isa,15000);
				in 	= new BufferedInputStream(socket.getInputStream());
				out = new BufferedOutputStream(socket.getOutputStream());
			
				out.write(("A|"+host_ip+"|"+host_name+"||"+'\n').getBytes());
				System.out.println("A|"+host_ip+"|"+host_name+"||"+'\n');
				out.flush();
				
					while(true)
					{
						//for loop
						String temp =queue.poll();
						if(temp == null)
						{
							//System.out.print("queue is null");
							
						}
						else
						{
							temp = temp+'\n';
							out.write(temp.getBytes());
							System.out.println(temp);
							out.flush();
						}
						
					}
				
			}
			catch(ConnectException e)
			{
				System.out.println("Socket connect refused, sleep 5 sec");
				try 
				{
					Thread.sleep(5*1000);
				} 
				catch (InterruptedException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			catch(SocketTimeoutException e)
			{
				System.out.println("Socket connection timeout, sleep 5 sec");
				try 
				{
					Thread.sleep(5*1000);
				} 
				catch (InterruptedException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				queue.clear();
				System.out.println("socket error in IOException!");
			}
			finally
			{
				try
				{
					if(socket!=null)
						socket.close();
					if(in!=null)
						in.close();
					if(out!=null)
						out.close();
				}
				catch(IOException e)
				{
					System.out.println("error point : 1-12-2");
				}
			}
		}
	}

	
}
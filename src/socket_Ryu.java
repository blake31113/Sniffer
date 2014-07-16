import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

//socket for ryu

public class socket_Ryu extends Thread
{
	private InetSocketAddress	isa;
	private BlockingQueue<String> queue;
	private Conf conf;
	public socket_Ryu(BlockingQueue<String> queue,Conf conf)
	{
		//Creater
		this.conf=conf;
		this.queue=queue;
		
		isa = new InetSocketAddress(conf.monitor_ip,conf.monitor_port);
		
	}
	
	public void run()
	{
		
			//start
			BufferedInputStream in = null;
			BufferedOutputStream out = null;
			
			//Start connect
			Socket socket = new Socket();
			try
			{
				while(true)
				{
					socket.connect(isa,15000);
					in 	= new BufferedInputStream(socket.getInputStream());
					out = new BufferedOutputStream(socket.getOutputStream());
					out.write(("A|"+conf.host_ip+"|"+conf.hostName+'\n').getBytes());
					System.out.println("A|"+conf.host_ip+"|"+conf.hostName+'\n');
					out.flush();
					
					ArrayList<String> list = new ArrayList<String>();
					list.add("ps aux|grep ryu|awk -F\" \" '{print $8 \" \" $11}'");
					ArrayList<String> feedback = null;
					//queue.put("C|"+hostName+"|"+backup+"||"+'\n');
					
					while(true)
					{
						//for loop
						feedback = LinuxCmd.execCmd(list);
						int count = 0;
						for(String string : feedback)
						{
							if(!string.contains("grep"))
							{
								count++;
								if(string.toLowerCase().startsWith("z"))
								{
									//zombie
									queue.put("H|"+conf.hostName+"|"+conf.host_ip+"|2"+'\n');
									queue.put("C|"+conf.hostName+":"+conf.nic+"|"+conf.switchname+":"+conf.switchnic+"|"+conf.backupcontroller+":"+conf.backupnic+"|"+'\n');
									queue.put("F|"+conf.switch_port+"|"+conf.switchInfo+"|tcp:"+conf.backup_controller+'\n');
									Thread.sleep(10000);
								}
								else 
								{
									queue.put("H|"+conf.hostName+"|"+conf.host_ip+"|1"+'\n');
									
								}
							
							}
						}
						if(count == 0)
						{
							//controller has been killed
							queue.put("H|"+conf.hostName+"|"+conf.host_ip+"|0"+'\n');
							queue.put("C|"+conf.hostName+":"+conf.nic+"|"+conf.switchname+":"+conf.switchnic+"|"+conf.backupcontroller+":"+conf.backupnic+"|"+'\n');
							queue.put("F|"+conf.switch_port+"|"+conf.switchInfo+"|tcp:"+conf.backup_controller+'\n');
							Thread.sleep(10000);
						}
						String temp =queue.poll();
						out.write(temp.getBytes());
						System.out.println(temp);
						out.flush();
						Thread.sleep(5000);
					}
				}//end while true
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
					
					e1.printStackTrace();
				}
			}
			catch(IOException | InterruptedException e)
			{
				queue.clear();
				System.out.println("error point : 1-12-1");
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
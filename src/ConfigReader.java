import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

//config reader

public class ConfigReader 
{
	private static final String FILEPATH_STRING = "sniffer.conf"; //configuration file name
	private Conf conf;
	private void parserConigFile(String name, String value) 
	{
		
		switch (name.toLowerCase()) 
		{
		
			case "backupnic":
				conf.backupnic = value;
			break;
			case "backupcontroller":
				conf.backupcontroller = value;
			break;
			case "switchname":
				conf.switchname = value;
			break;
			case "switchnic":
				conf.switchnic = value;
			break;
			case "nic":
				conf.nic = value;
			break;
			case "mode":
				conf.mode = value;
				break;
			case "monitor_ip":
				conf.monitor_ip = value;
				break;
			case "host_ip":
				conf.host_ip = value;
				break;
			case "nics":
				conf.nics = value;
				break;
			case "monitor_port":
				conf.monitor_port = Integer.parseInt(value);
				break;
			case "hostname":
				conf.hostName = value;
				break;
			case "switchinfo":
				conf.switchInfo = value;
				break;
			case "switch_port":
				conf.switch_port = Integer.parseInt(value);
				break;
			case "backup_controller":
				conf.backup_controller = value;
				break;
			default:
				break;
		}
	}
	
	public ConfigReader()
	{
		conf = new Conf();
	}
	
	
	public Conf getConf()
	{
		File configFile = new File(FILEPATH_STRING);
		try 
		{
			
			BufferedReader reader= new BufferedReader(new FileReader(configFile));
			String line = null;
			while((line = reader.readLine()) !=null)
			{
				try 
				{
					String non_comment = line.replaceAll(" ", "").split("#")[0];
					if(non_comment.length()==0)
						continue;
					parserConigFile(non_comment.split("=")[0],non_comment.split("=")[1]);
				} 
				catch (ArrayIndexOutOfBoundsException e) 
				{
					// TODO: handle exception
					e.printStackTrace();
				}
				catch (PatternSyntaxException e) 
				{
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			reader.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conf;
	}



	
	
}
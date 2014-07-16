import java.lang.invoke.SwitchPoint;

//config container

public class Conf 
{
	public boolean isWrited = false;
	public boolean isFinish = false;
	public String  monitor_ip = "";
	public String  host_ip="";
	public String  mode;
	public String hostName;
	public String nics;
	public int monitor_port;
	public String switchInfo;//e.x:140.115.156.132
	public int switch_port;//e.x:30002
	public String backup_controller;//e.x:140.115.156.135:35102
	public String nic;
	public String switchname;
	public String switchnic;
	public String backupcontroller;
	public String backupnic;
}
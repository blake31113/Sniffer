import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

//execute command line &get result 

public class LinuxCmd 
{

//	private static LinuxCmd instance=null;
//	public static LinuxCmd getInstance()
//	{
//		if(instance==null)
//		{
//			instance=new LinuxCmd();
//		}
//	}
	public static ArrayList<String> execCmd(ArrayList<String> cmd) 
	{
		// TODO Auto-generated method stub
		//System.out.println("here is execCmd");
		ArrayList<String> feedback = new ArrayList<String>();
		File shellFile = new File("./tempFile.sh");
		try 
		{
			// create a shell file
			BufferedWriter out = new BufferedWriter(new FileWriter(shellFile));
			for(String string : cmd)
			{
				out.append(string);
				out.newLine();
				out.flush();
			}
			out.close();
			
			//exec the shell file
			Process process = Runtime.getRuntime().exec("sh ./tempFile.sh");
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while((line = bufReader.readLine())!=null)
			{
				//System.out.println("  "+line);
				feedback.add(line);
			}
			process.waitFor();
			//System.out.println();
			//System.out.println("exit : "+process.exitValue());
			process.destroy();
			
			//delete the file
			shellFile.delete();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return feedback;
	}
	
}
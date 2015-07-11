package VoxelJourneyConverter.vjc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class Logger {
	final int chunksXdirection=118;
	final int chunksYdirection=118;
	int warnings=0;
	PrintWriter writer;
	File lastUpdated;
	long [] []updateLogs= new long [chunksXdirection] [chunksYdirection];
	
	public Logger(File folder) {
		try {
		File logfile=new File(folder.getAbsolutePath()+"\\logs.txt");
		lastUpdated=new File(folder.getAbsolutePath()+"/combinedMapData/updatelog.txt");
		writer = new PrintWriter(logfile);
		}
		catch (FileNotFoundException e) {
			System.out.println("Error occured while trying to create a log file");
			System.exit(1);
		}
	}
	public void closeLogger() {
		PrintWriter updatelogwriter=null;
		try {
		updatelogwriter=new PrintWriter(lastUpdated); }
		catch (FileNotFoundException e) {
			System.out.println("Error occured while trying to create an updatelog file");
			System.exit(1);
		}
		for(int i=0;i<chunksXdirection;i++) {
			for(int j=0;j<chunksYdirection;j++) {
				updatelogwriter.println(i+","+j+":"+updateLogs[i][j]);
			}
		}
		updatelogwriter.close();
		writer.println("Finished, total warnings: "+warnings);
		writer.close();
	}
	
	public void log(String a) {
		writer.println(a);		
	}
	public void logWarning(String a) {
			writer.println("[WARNING]  "+a);
			warnings++;
	}
	public void updateTimeStamp(int x,int y,long time) {
		updateLogs [x+59] [y+59]=time;
	}
	public long getSpecificStampData(int x,int y) {
		return updateLogs[x] [y];
	}
	
	public long [] [] getTimeStampData(File updatelog) {
		long [] [] data=new long [chunksXdirection] [chunksYdirection];
		BufferedReader in=null;
		try {
		in = new BufferedReader(new FileReader(updatelog)); }
		catch (FileNotFoundException e) {
			System.out.println("Error, while trying to load an updatelog");
			System.exit(1);
		}
		String line;
		try {
		while((line = in.readLine()) != null)
		{
		    int x=Integer.parseInt(line.split(":") [0].split(",") [0]);
		    int y=Integer.parseInt(line.split(":") [0].split(",") [0]);
		    long time=Long.parseLong(line.split(":")[1]);
		    data [x] [y]=time;
		}
		in.close(); }
		catch (IOException e) {
			System.out.println("Error, while trying to read an updatelog");
		}
		return data;
	}

}

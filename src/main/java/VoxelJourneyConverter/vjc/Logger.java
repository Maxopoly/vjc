package VoxelJourneyConverter.vjc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class Logger {
	int warnings=0;
	PrintWriter writer;
	
	public Logger(File folder) {
		try {
		File logfile=new File(folder.getAbsolutePath()+"\\logs.txt");
		writer = new PrintWriter(logfile);
		}
		catch (FileNotFoundException e) {
			System.out.println("Error occured while trying to create a log file");
			System.exit(1);
		}
	}
	public void closeLogger() {
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
	

}

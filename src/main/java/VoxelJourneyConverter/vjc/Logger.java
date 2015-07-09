package VoxelJourneyConverter.vjc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Logger {
	BufferedWriter bufferedWriter;
	FileWriter writer;
	public Logger(File folder) {
		try {
		File logfile=new File(folder.getAbsolutePath()+"\\logs.txt");
		writer = new FileWriter(logfile);
		bufferedWriter =
                new BufferedWriter(writer);

		}
		catch (FileNotFoundException e) {
			System.out.println("Error occured while trying to create a log file");
			System.exit(1);
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("Error occured while trying to create a log file");
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println("Error occured while trying to create a log file");
			System.exit(1);
		}
	}
	public void closeLogger() {
		try {
		bufferedWriter.close(); }
		catch (IOException e) {
			System.out.println("Error occured while trying to create a log file");
			System.exit(1);
		}
	}
	
	public void log(String a) {
		try {
		bufferedWriter.write(a);
		bufferedWriter.newLine();}
		catch (IOException e) {
			System.out.println("Error occured while trying to write something to the log file");
			//System.exit(1);
		}
		
	}

}

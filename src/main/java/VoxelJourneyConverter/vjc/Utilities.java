package VoxelJourneyConverter.vjc;

import java.io.File;
import java.util.LinkedList;

public class Utilities {
	public static LinkedList<File> getSubFolders(File folder) {
		LinkedList <File> subfolders= new LinkedList <File>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            subfolders.add(fileEntry);
	        } 
	    }
		return subfolders;
	}

}

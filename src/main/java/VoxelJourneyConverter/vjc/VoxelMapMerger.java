package VoxelJourneyConverter.vjc;

import java.io.File;
import java.util.LinkedList;

public class VoxelMapMerger {
	int mapradius = 15100; // because you can still map data outside worldborder

	public boolean merge(File sourceFolder) {
		Logger logger=new Logger(sourceFolder);
		LinkedList<File> subfolders = Utilities.getSubFolders(sourceFolder);
		if (subfolders.size() <= 1) {
			logger.log("None or only one folder was found, this data can't be merged");
			return false;
		}
		int tilerange = mapradius / 256 + 1;
		boolean success = (new File(sourceFolder.getAbsolutePath()
				+ "/combinedMapData")).mkdirs();
		if (!success) {
			logger.log("The destination folder for the combined data could not be created");
			return false;
		}
		File destinationFolder = new File(sourceFolder.getAbsolutePath()
				+ "/combinedMapData");
		logger.log("Starting to merge data");
		for (int i = -tilerange; i <= tilerange; i++) {
			for (int j = -tilerange; j <= tilerange; j++) {
				File f = new File(destinationFolder.getAbsolutePath() + "/" + i
						+ "," + j + ".zip");
					for (int k = 0; k < subfolders.size();k++) {
						File source = new File(subfolders.get(k).getAbsolutePath() + "/" + i + ","
								+ j + ".zip");
						if (source.exists()) {
							if (f.exists()) {
								if (source.lastModified()>f.lastModified())
							source.renameTo(f);
							logger.log(source.getName()+" was moved into the destination folder, replacing a file, because it was newer"); 
							}
							else {
								source.renameTo(f);
								logger.log(source.getName()+" was moved into the destination folder"); 
							}
						}

					
				}
			}
		}

		return true;
	}

}


package VoxelJourneyConverter.vjc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class VoxelMapMerger {
	int mapradius = 15104; // because you can still map data outside worldborder
	int chunks = 118;
	Logger logger;

	public void merge(File sourceFolder) {
		logger = new Logger(sourceFolder);
		LinkedList<File> subfolders = Utilities.getSubFolders(sourceFolder);
		if (subfolders.size() <= 1) {
			logger.logWarning("None or only one folder was found, this data can't be merged");
			System.exit(1);
		}
		int tilerange = mapradius / 256 + 1;
		boolean success = (new File(sourceFolder.getAbsolutePath()
				+ "/combinedMapData")).mkdirs();
		if (!success) {
			logger.logWarning("The destination folder for the combined data could not be created");
			System.exit(1);
		}
		File destinationFolder = new File(sourceFolder.getAbsolutePath()
				+ "/combinedMapData");
		logger.log("Starting to merge data");
		for (int i = -tilerange; i <= tilerange; i++) {
			for (int j = -tilerange; j <= tilerange; j++) {
				File target = new File(destinationFolder.getAbsolutePath()
						+ "/" + i + "," + j + ".zip");
				for (int k = 0; k < subfolders.size(); k++) {
					File source = new File(subfolders.get(k).getAbsolutePath()
							+ "/" + i + "," + j + ".zip");
					if (source.exists()) {
						if (target.exists()) {
							priorizedMerge(source, target);
							logger.log(source.getName()
									+ " was successfully merged into an already existing file");

						} else {
							if (priorizedMerge(source, null)) {
								logger.log(source.getName()
										+ " was successfully moved, because no previous data existed");
							} else {
								logger.logWarning(source.getName()
										+ " was tried to move, because no previous data existed, but it failed");
							}
						}

					}

				}
			}
		}
	}

	public boolean priorizedMerge(File source, File target) {
		File updatelog=new File(source.getAbsolutePath()+"/updatelog.txt");
		long [] [] previousUpdateData;
		if (updatelog.exists()) {
			previousUpdateData=logger.getTimeStampData(updatelog);
		}
		else {
			previousUpdateData=null;
		}
		if (target==null) {
			long time=source.lastModified();
			String name=source.getName().substring(0, source.getName().length()-4);
			int x=Integer.parseInt(name.split(",") [0]);
			int y=Integer.parseInt(name.split(",") [1]);
			for(int i=16*x;i<(x+1)*16;i++) {
				for(int j=16*y;j<(y+1)*16;j++) {
					if (previousUpdateData!=null&&previousUpdateData[i+chunks/2][j+chunks/2]!=0) {   
						logger.updateTimeStamp(i, j,previousUpdateData[i+chunks/2][j+chunks/2]);
					}
					else {
			logger.updateTimeStamp(i,j,time); }
			}}
			FileUtils.copyFile(source, new File(source.getParentFile().getAbsolutePath()+"/combinedMapData/"+source.getName()));
			return false;
		}
		ZipFile zip1 = null;
		ZipFile zip2 = null;
		InputStream stream1 = null;
		InputStream stream2 = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;	
		try {
			zip1 = new ZipFile(source);
			zip2 = new ZipFile(target);
		} catch (IOException e) {
			logger.logWarning("One of the zip files " + source.getPath()
					+ " and " + target.getPath()
					+ " could not be read, they seem to be corrupted");
			return false;
		}
		Enumeration<? extends ZipEntry> entries = zip1.entries();
		try {

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				stream1 = zip1.getInputStream(entry);
			}
		} catch (IOException e) {
			logger.logWarning(source.getPath()
					+ " could not be parsed, it seems to be corrupt, it was skipped");
			return false;
		}

		Enumeration<? extends ZipEntry> entries2 = zip2.entries();
		try {

			while (entries2.hasMoreElements()) {
				ZipEntry entry2 = entries2.nextElement();
				stream2 = zip2.getInputStream(entry2);
			}
		} catch (IOException e) {
			logger.logWarning("The target .zip " + target.getName()
					+ " seems to be corrupted, it was completly replaced");
			FileUtils.copyFile(source,target);
			return false;
		}
		byte[] ogData = new byte[256 * 256 * 17];
		byte[] newData = new byte[256 * 256 * 17];
		try {
			stream1.read(newData);
		} catch (IOException e) {
			logger.logWarning("An error occured while reading the file "
					+ source.getPath()
					+ ", trying to merge it into an existing file, it was not merged");
			return false;
		}
		try {
			stream2.read(ogData);
		} catch (IOException e) {
			logger.logWarning("An error occured while reading the file "
					+ target.getPath()
					+ " this should be inspected manually, for now the file was left untouched");
			return false;
		}
		for (int i = 0; i < 256 * 256 * 17; i += 17) {
			long time=0;
			if (newData[i + 2] != 0) { // dont merge not explored territory
				if (ogData[i + 16] != 0 && newData[i + 16] != ogData[i + 16]) {
					logger.logWarning("A biome was attempted to be changed by "
							+ source.getPath() + " at the " + i / 17
							+ ".block, this might be caused by corrupted data");
					return false;
				} else {
					int x=i/256+chunks/2;
					int y=i%256+chunks/2;
					boolean merge=false;
					if ((previousUpdateData!=null&&previousUpdateData[x][y]!=0)) {
						if (previousUpdateData[x][y]>logger.getSpecificStampData(x, y)) {
							time=previousUpdateData[x][y];
							merge=true;
						}
						else {
							merge=false;
						}
						if (previousUpdateData==null) {
							if(source.lastModified()>logger.getSpecificStampData(x, y)) {
								merge=true;
								time=source.lastModified();
							}
						}
					}
					if(merge) {
					for (int k = i; k < 17; k++) {
						ogData[k] = newData[k];}
						if ((i / 17) % 16 == 0) { // TODO make this only log
													// once per chunk, it still
													// logs 16 times per chunk
							logger.updateTimeStamp(x,y,time);
						}
					}
				}
			}

		}
		try {
			byte[] buffer = new byte[1024];
			fos = new FileOutputStream(target);
			fos.write(ogData);
			zos = new ZipOutputStream(fos);
			fis=new FileInputStream(fos.)
			zos.putNextEntry(new ZipEntry(target.getName()));
			int length;
			while((length=fis.read(buffer))>0) {
				zos.write(buffer, 0, length);
			}
			zos.closeEntry();
			fis.close();
			zos.close();
		}

		catch (IOException e) {
			logger.logWarning("Error while trying to zip a merged file, the merged file for "+source.getPath()+" was not used");
			return false;
		}
return true;
	}
}

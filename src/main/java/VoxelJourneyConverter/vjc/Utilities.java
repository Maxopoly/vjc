package VoxelJourneyConverter.vjc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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
	
	public static byte [] dezipFileToByteArray(File zip) throws IOException {
		
	InputStream in=null;
			ZipFile zipFile=new ZipFile(zip);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				in = zipFile.getInputStream(entry);
	}
			byte [] data=new byte [256*256*17];
			in.read(data);
			in.close();
			zipFile.close();
			return data;
	}
	
	public static File createFileFromByteArray(byte [] data,File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos= new BufferedOutputStream(fos);
		bos.write(data);
		bos.flush();
		bos.close();
		return file;
	}
	
	public static File createZip(File file,File zip) throws FileNotFoundException,IOException {
		FileOutputStream fos = new FileOutputStream(zip);
		ZipOutputStream zos = new ZipOutputStream(fos);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(file.getName());
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		zos.close();
		fis.close();
		return zip;
	}
	
	
}

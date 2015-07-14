package VoxelJourneyConverter.vjc;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

public class Utilities {
	public static LinkedList<String> readTextfile(File file) {
		BufferedReader in = null;
		LinkedList<String> result = new LinkedList<String>();
		try {
			in = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out
					.println("Could not find file with color values, exiting now");
			e.printStackTrace();
			System.exit(1);
		}
		String line;
		try {
			while ((line = in.readLine()) != null) {
				result.add(line);
			}
			in.close();
		} catch (IOException e) {
			System.out
					.println("Error while reading the file containing the color value, exiting now");
			e.printStackTrace();
			System.exit(1);
		}
		return result;

	}

	public static BufferedImage[][] getImages(File folder) {
		BufferedImage[][] pics = new BufferedImage[120][120]; // mapdiameter/512
		for (final File fileEntry : folder.listFiles()) {
			try {
				if (!fileEntry.isDirectory() && fileEntry.getName().split("\\.")[1].equals("png")) {

					BufferedImage img = null;
					int x = Integer.parseInt(fileEntry.getName().split(
							"\\.")[0].split(",")[0]);
					int y = Integer.parseInt(fileEntry.getName().split(
							"\\.")[0].split(",")[1]);
					try {
						img = ImageIO.read(fileEntry);
						pics[x + 30][y + 30] = img;
					} catch (IOException e) {
						System.out.println("Failed to read "
								+ fileEntry.getName());
						pics[x + 60][y + 60] = null;
					}
				}
			}

			catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Failed to read " + fileEntry.getName());
			}
		}
		return pics;
	}

	public static LinkedList<File> getSubFolders(File folder) {
		LinkedList<File> subfolders = new LinkedList<File>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				subfolders.add(fileEntry);
			}
		}
		return subfolders;
	}

	public static byte[] dezipFileToByteArray(File zip) throws IOException {
		File file = null;
		byte[] buffer = new byte[1024];
		try {
			file = new File("C:\\Users\\Max\\Desktop\\data");
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				FileOutputStream fos = new FileOutputStream(file);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Path path = Paths.get(file.getAbsolutePath());
		byte[] data = Files.readAllBytes(path);
		return data;
	}

	public static File createFileFromByteArray(byte[] data, File file)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.close();
		return file;
	}

	public static File createZip(File file, File zip)
			throws FileNotFoundException, IOException {
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

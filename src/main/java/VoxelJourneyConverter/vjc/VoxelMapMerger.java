package VoxelJourneyConverter.vjc;

import java.io.File;

import java.io.IOException;
import java.util.LinkedList;

public class VoxelMapMerger {
	int mapradius = 15360; // because you can still map data outside worldborder
	int chunks = mapradius / 16 * 2;

	public void merge(File sourceFolder) {
		LinkedList<File> subfolders = Utilities.getSubFolders(sourceFolder);
		if (subfolders.size() <= 1) {
			System.out.println("None or only one folder was found, this data can't be merged");
			System.exit(1);
		}
		(new File(sourceFolder.getAbsolutePath() + "/combinedMapData"))
				.mkdirs();
		if (!new File(sourceFolder.getAbsolutePath() + "/combinedMapData")
				.exists()) {
			System.out.println("The destination folder for the combined data could not be created");
			System.exit(1);
		}
		File destinationFolder = new File(sourceFolder.getAbsolutePath()
				+ "/combinedMapData");
		Data target = new Data(destinationFolder, chunks);
		System.out.println("Starting to merge data");
		for (int i = 0; i < subfolders.size(); i++) {
			Data current = new Data(subfolders.get(i), chunks);
			// current.writeLogfile();
			for (int j = 0; j < current.getDataLength(); j++) {
				File zip = current.getZip(j);
				if (zip != null) {
					byte[] currentData = null;
					try {
						currentData = Utilities.dezipFileToByteArray(zip);
					} catch (IOException e) {
						System.out.println("The zip"
								+ zip.getAbsolutePath()
								+ "could not be parsed, it seems to be corrupted");
						e.printStackTrace();
						System.exit(1);
					}
					byte[] mergedData = new byte[256 * 256 * 17];
					int xoffset = Integer
							.parseInt(zip.getName().split("\\.")[0].split(",")[0]) * 16; /*
																						 * Chunk
																						 * offset
																						 * relative
																						 * to
																						 * 0
																						 * ,
																						 * 0
																						 * ,
																						 * based
																						 * on
																						 * which
																						 * package
																						 * is
																						 * being
																						 * read
																						 * currently
																						 * .
																						 * For
																						 * example
																						 * for
																						 * -
																						 * 5
																						 * ,
																						 * 4.
																						 * zip
																						 * ,
																						 * the
																						 * offset
																						 * would
																						 * be
																						 * -
																						 * 80
																						 * and
																						 * 64
																						 * chunks
																						 */
					int yoffset = Integer
							.parseInt(zip.getName().split("\\.")[0].split(",")[1]) * 16;
					File targetzip = target.getZip(xoffset / 16, yoffset / 16);
					if (targetzip == null) {
						for (int a = 0; a < 256; a++) {
							int x = a % 16;
							int y = a / 16;
							System.out.println(x + "," + y + "," + xoffset
									+ "," + yoffset); // TODO REMOVE
							long time = current.getTimestamp(x + xoffset, y
									+ yoffset);
							target.updateTimestamp(x + xoffset, y + yoffset,
									time);
							copyChunk(a, currentData, mergedData);
							; // TODO
						}
					} else {
						try {
							mergedData = Utilities.dezipFileToByteArray(target
									.getZip(xoffset / 16, yoffset / 16));
						} catch (IOException e) {
							System.out.println("Error, while trying to unzip"
									+ target.getZip(xoffset / 16, yoffset / 16)
											.getAbsolutePath());
							e.printStackTrace();
						}
						for (int a = 0; a < 256; a++) {
							int x = a % 16;
							int y = a / 16;
							System.out.println(x + "," + y + "," + xoffset
									+ "," + yoffset); // TODO REMOVE
							long timetarget = target.getTimestamp(x + xoffset,
									y + yoffset);
							long timecurrent = current.getTimestamp(
									x + xoffset, y + yoffset);
								if (timecurrent > timetarget) {
									target.updateTimestamp(x + xoffset, y
											+ yoffset, timecurrent);
									/*
									 * logger.log("Replaced the chunk " + a +
									 * " in " + zip.getName() +
									 * " , because it was newer, Previous timestamp:"
									 * + timetarget + " , New timestamp:" +
									 * timecurrent);
									 */
									copyChunk(a, currentData, mergedData);
								}
							
						}
					}
					File data = new File(destinationFolder.getAbsolutePath()
							+ "/data");
					try {
						data = Utilities.createFileFromByteArray(mergedData,
								data);
					} catch (IOException e) {
						System.out.println("Error while trying to write the data for "
								+ zip.getName() + " back into a file");
						e.printStackTrace();
						System.exit(1);
					}
					if (targetzip == null) {
						targetzip = new File(
								destinationFolder.getAbsolutePath() + "/"
										+ current.getZip(j).getName());
					}
					try {
						Utilities.createZip(data, targetzip);
					} catch (IOException e) {
						System.out.println("Error while trying to zip the file for "
								+ zip.getName());
						e.printStackTrace();
						System.exit(1);
					}
					
				}
			}
		}
		target.writeLogfile();
	}

	public void copyChunk(int i, byte[] sourcedata, byte[] targetdata) {
		int x = i % 16;
		int y = i / 16;
		int start = x * 16 * 17 + y * 16 * 256 * 17;
		if (sourcedata[start + 2] != 0) {

			for (int b = start; b < start + 256 * 16 * 17; b += 256 * 17) {
				for (int a = b; a < b + 16 * 17; a++) {
					targetdata[a] = sourcedata[a];
				}

			}
		}
	}

}

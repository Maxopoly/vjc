package VoxelJourneyConverter.vjc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Every folder, which is merged into another folder is turned into an instance
 * of this class, this class handles all interacting with the folder. The
 * logfile used below saves the time, when each chunk in this map was last
 * updated, if no logfile exists when initializing this class, a logfile will be
 * created from the modification dates of the data packages, this is needed to
 * determine which data is newer, during the merging process
 * 
 * @author Max
 *
 */

public class Data {
	/**
	 * Folder in which all the .zip with the data are
	 */
	File folder;
	/**
	 * Recommended value is roughly mapradius*2+100, so that also
	 * mapSizeChunks%16==0 , for civcraft 15360 is used
	 */
	int mapSizeChunks;
	File logfile;
	/**
	 * The data which is read from the logfile written into an array for
	 * accessibility
	 */
	long[][] logdata;

	public Data(File folder, int mapSizeChunks) {
		this.folder = folder;
		this.mapSizeChunks = mapSizeChunks;
		logfile = new File(folder.getAbsolutePath() + "/updatelog.txt");
		if (logfile.exists()) {
			logdata = parseLogfile();
		} else {
			logdata = createLogfile();
		}
	}

	public void writeLogfile() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(logfile);
		} catch (FileNotFoundException e) {
			System.out
					.println("Error occured while trying to create an updatelog");
			System.exit(1);
		}
		for (int i = 0; i < logdata[0].length; i++) {
			for (int j = 0; j < logdata.length; j++) {
				writer.println(i + "," + j + ":" + logdata[i][j]);
			}
		}
	}

	public File getZip(int index) {
		try {
		if(folder.listFiles()[index].getName().split("\\.")[1].equals("zip")) {
		return folder.listFiles()[index]; }
		else {
			return null;
		}}
		catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public File getZip(int x, int y) {
		File zip = new File(folder.getAbsolutePath() + "/" + x + "," + y
				+ ".zip");
		if (zip.exists()) {
			return zip;
		} else {
			return null;
		}
	}

	public int getDataLength() {
		return folder.listFiles().length;
	}

	/**
	 * Gets the last modification data of a certain chunk, x and y should always
	 * be bigger than (-1) *mapsizechunks/2 and smaller than mapsizechunks/2 -1,
	 * usually this should be exactly the range used by the chunks. This
	 * function can also be used to determine whether the instance of this class
	 * contains data on this chunk at all, if 0 is returned, thats not the case
	 * 
	 * @param x
	 *            x-coord of the most north-west block of the chunk
	 * @param y
	 *            y-coord of the most north-west block of the chunk
	 * @return date when this chunk was last modified in this data
	 */
	public long getTimestamp(int x, int y) {
		return logdata[x + (mapSizeChunks / 2)][y + (mapSizeChunks / 2)];
	}

	public void updateTimestamp(int x, int y, long time) {
		logdata[x + (mapSizeChunks / 2)][y + (mapSizeChunks / 2)] = time;
	}

	/**
	 * IF no existing logfile is found, this function is used to create one from
	 * the last modification dates of the data packages
	 * 
	 * @return last updated time of each chunk as an array;
	 */
	public long[][] createLogfile() {
		long[][] readData = new long[mapSizeChunks][mapSizeChunks];
		for (int i = 0; i < getDataLength(); i++) {
			File f = getZip(i);
			byte[] data = null;
			try {
				data = Utilities.dezipFileToByteArray(f);

				int xoffset = Integer.parseInt((f.getName().split("\\.")[0])
						.split(",")[0]);
				int yoffset = Integer.parseInt(f.getName().split("\\.")[0]
						.split(",")[1]);

				for (int a = 0; a < 256 * 256 * 17; a += 17 * 256 * 16) {
					for (int k = a; k < a + 256 * 17; k += 17 * 16) {
						if (data[k + 2] != 0) { // the item id of the block
												// read, if this is air (=0) the
												// chunk was never loaded

							int y = a / (17 * 256 * 16);
							int x = (k - a) / (16 * 17);
							readData[x + mapSizeChunks / 2 + xoffset * 16][y
									+ mapSizeChunks / 2 + yoffset * 16] = f
									.lastModified();
						}
					}
				}
			}

			catch (IOException e) {
				System.out.println("Whoops, that was no zip");
				e.printStackTrace();
			}

		}
		return readData;
	}

	/**
	 * Used to read data from an existing logfile
	 * 
	 * @return last updated time of each chunk as an array;
	 */
	public long[][] parseLogfile() {
		long[][] data = new long[mapSizeChunks][mapSizeChunks];
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(logfile));
		} catch (FileNotFoundException e) {
			System.out.println("Error, while trying to load the filelog for"
					+ folder.getName());
			System.exit(1);
		}
		String line;
		try {
			while ((line = in.readLine()) != null) {
				int x = Integer.parseInt(line.split(":")[0].split(",")[0]);
				int y = Integer.parseInt(line.split(":")[0].split(",")[1]);
				long time = Long.parseLong(line.split(":")[1]);
				data[x][y] = time;
			}
			in.close();
		} catch (IOException e) {
			System.out
					.println("Error, while trying to close the reader for reading a filelog for"
							+ folder.getName());
			System.exit(1);
		}
		return data;
	}

}

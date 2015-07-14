package VoxelJourneyConverter.vjc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class JourneyToVoxelConverter {
	int mapradius = 15360; // should be a multiply of 512
	int tilerange = mapradius / 256;
	LinkedList<String> coloursString;
	Colour[] colours = null;

	public void convert(File sourceFolder) {
		File destinationFolder = new File(sourceFolder.getAbsolutePath()
				+ "\\output");
		File convertingData = new File("resources/Ids.txt");
		coloursString = Utilities.readTextfile(convertingData);
		colours = parseColours(coloursString);
		destinationFolder.mkdirs();
		BufferedImage images[][] = Utilities.getImages(sourceFolder);
		for (int i = 0; i < images[0].length; i++) {
			for (int j = 0; j < images.length; j++) {
				BufferedImage img = images[i][j];
				if (img != null) {
					int a = (i - 60) * 2;
					int b = (j - 60) * 2;
					createVoxeldata(
							img.getSubimage(0, 0, img.getWidth() / 2,
									img.getHeight() / 2), a, b,
							destinationFolder);
					createVoxeldata(
							img.getSubimage(img.getWidth() / 2, 0,
									img.getWidth() / 2, img.getHeight() / 2),
							a+1 , b, destinationFolder);
					createVoxeldata(
							img.getSubimage(0, img.getHeight() / 2,
									img.getWidth() / 2, img.getHeight() / 2),
							a, b+1, destinationFolder);
					createVoxeldata(img.getSubimage(img.getWidth() / 2,
							img.getHeight() / 2, img.getWidth() / 2,
							img.getHeight() / 2),a+ 1, b + 1,
							destinationFolder);
				}
			}

		}

	}

	public void createVoxeldata(BufferedImage img, int x, int y, File folder) {
		// img=adjust(img);
		File data = new File("data");
		int compared;
		Colour white = new Colour(0, (byte) 0, (byte) 0);
		byte[] readData = new byte[256 * 256 * 17];
		for (int j = 0; j< img.getWidth(); j++) {
			for (int i = 0; i < img.getHeight(); i++) {
				int pixel = img.getRGB(i, j);
				Colour temp = new Colour(pixel);
				Colour bestFind = null;
				compared = 256 * 256 * 256;
				if (img.getRGB(i, j) == 0) {
					bestFind = white;
					compared = 0;
				} else {
					for (int a = 0; a < colours.length & compared != 0; a++) {

						int z = colours[a].compareTo(temp);
						if (z < compared) {

							compared = z;
							bestFind = colours[a];
						}
					}
				}
				int pos = i * 256 * 17 + j * 17;
				if (bestFind.getId() != 0) {
					readData[pos] = 64; // all converted blocks are at height 64
					readData[pos + 3] = -16; // always full light
					readData[pos + 8] = -1; // no water above it
					readData[pos + 12] = -1; // nothing else above it
					readData[pos + 16] = 28; // birch forest hill, converted
												// chunks
												// can always be recognized by
												// this
												// biome
				} else {
					readData[pos] = 0; // unless they are air
				}
				readData[pos + 2] = bestFind.getId(); // block id
				readData[pos + 1] = (byte) (bestFind.getMeta() << 4); // lore

			}
		}
		try {
			Utilities.createFileFromByteArray(readData, data);
		} catch (IOException e) {
			System.out
					.println("Error while trying to create a file of the byte array");
			e.printStackTrace();
		}
		File zip = new File(folder.getAbsolutePath() + "\\" + String.valueOf(x+60)
				+ "," + String.valueOf(y+60) + ".zip");
		try {
			Utilities.createZip(data, zip);
		} catch (FileNotFoundException e) {
			System.out.println("Error while creating a zip");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error while creating a zip");
			e.printStackTrace();
		}
	}

	public Colour[] parseColours(LinkedList<String> list) {
		Colour[] colours = new Colour[list.size()];
		for (int i = 0; i < list.size(); i++) {
			String current = list.get(i);
			int itemid = Integer.parseInt(current.split(":")[0].split(",")[0]);
			int meta = Integer.parseInt(current.split(":")[0].split(",")[1]);
			String rgb = current.split(":")[1];
			int red = Integer.parseInt(rgb.substring(0, 2), 16);
			int green = Integer.parseInt(rgb.substring(2, 4), 16);
			int blue = Integer.parseInt(rgb.substring(4, 6), 16);
			red = red << 16;
			green = green << 8;
			Colour t = new Colour(red + green + blue, (byte) itemid,
					(byte) meta);
			colours[i] = t;
		}
		return colours;
	}

	/*
	 * Turns the image right and then mirrors it at the north-south axis, this
	 * is needed so they are facing in the right direction, dont even ask me why
	 */

	public BufferedImage adjust(BufferedImage img) {
		BufferedImage result = new BufferedImage(img.getWidth(),
				img.getHeight(), img.getType());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				result.setRGB(img.getWidth() - j - 1, i, img.getRGB(i, j)); // turn
																			// right
			}
		}
		for (int i = 0; i < result.getWidth(); i++) {
			for (int j = 0; j < result.getHeight(); j++) {
				img.setRGB(result.getWidth() - i - 1, j, result.getRGB(i, j)); // mirror
			}
		}
		return img;
	}

}

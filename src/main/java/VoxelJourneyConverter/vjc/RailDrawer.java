package VoxelJourneyConverter.vjc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.Random;

public class RailDrawer {

	public void draw(String imageSize, String mapRadius, String lineWidth,
			File sourcefile) {
		int width = Integer.parseInt(imageSize);
		int height = width;
		int radius = Integer.parseInt(mapRadius);
		int actualMapRadius = 15000;
		int lineThickness = Integer.parseInt(lineWidth);
		File target = new File(sourcefile.getParentFile().getAbsolutePath()
				+ "/rails.png");
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		LinkedList<String> data = Utilities.readTextfile(sourcefile);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			sb.append(data.get(i));
		}
		String parsedData = sb.toString();
		String[] lines = parsedData.split("coordinates");
		for (int i = 1; i < lines.length; i++) {
			String current = lines[i];
			current = current.replaceAll("\"", "");
			current = current.replaceAll(" ", "");
			current = current.replaceAll(":", "");
			if (current.endsWith(",")) {
				current = current.substring(0, current.length() - 1);
			}
			String[] subdata = current.split("]");
			String color = "";
			LinkedList<Dimension> coords = new LinkedList<Dimension>();
			for (int a = 0; a < subdata.length; a++) {
				String section = subdata[a];
				if (!section.contains("properties") && section.length() > 6) {
					section = section.replace("[", "");
					section = section.replace("]", "");
					if (section.startsWith(",")) {
						section = section.substring(1, section.length());
					}
					String[] c = section.split(",");
					Dimension d = new Dimension(Integer.parseInt(c[0]),
							Integer.parseInt(c[1]));
					coords.add(d);

				} else {
					if (section.contains("color")) {
						String[] lookingForColor = section.split(",");
						for (int o = 0; o < lookingForColor.length; o++) {
							if (lookingForColor[o].contains("color")) {
								color = lookingForColor[o];
								color = color.replaceAll("}", "");
								color = color.split("#")[1];
							}
						}
					}
				}

			}
			if (coords.size() >= 2) {
				drawLines(img, radius, actualMapRadius, coords, color,
						lineThickness);
			}
		}
		Utilities.saveImage(img, target);
	}

	public void drawLines(BufferedImage img, int radius, int actualMapRadius,
			LinkedList<Dimension> coords, String color, int lineThickness) {
		Color actualColor;
		if (color.equals("")) {
			Random rand= new Random();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			actualColor=new Color(r,g,b);
		}
		else {
			
		
		actualColor=new Color((int) Long.parseLong(color, 16));
		}
		double multiplier = (double) radius / (double) actualMapRadius;
		Graphics2D g2d = img.createGraphics();
		g2d.setStroke(new BasicStroke(lineThickness));
		double mapMiddle = img.getWidth() / 2;
		g2d.setColor(actualColor);
		for (int i = 1; i < coords.size(); i++) {
			int startX = (int) ((coords.get(i - 1).getWidth()) * multiplier + mapMiddle);
			int startY = (int) ((coords.get(i - 1).getHeight()) * multiplier + mapMiddle);
			int endX = (int) ((coords.get(i).getWidth()) * multiplier + mapMiddle);
			int endY = (int) ((coords.get(i).getHeight()) * multiplier + mapMiddle);
			g2d.draw(new Line2D.Float(startX, startY, endX, endY));

		}
		g2d.dispose();
	}

}

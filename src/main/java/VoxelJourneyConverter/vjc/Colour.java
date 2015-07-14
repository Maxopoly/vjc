package VoxelJourneyConverter.vjc;

public class Colour {
	int r;
	int g;
	int b;
	int total;
	byte itemid;
	byte metadata;
	
	public Colour (int pxl,byte itemid,byte metadata) {
		r = (pxl >> 16) & 0xFF;
		g = (pxl >> 8) & 0xFF;
		b = pxl & 0xFF;
		total=pxl;
		this.itemid=itemid;
		this.metadata=metadata;
	}
	public Colour(int pxl) {
		r = (pxl >> 16) & 0xFF;
		g = (pxl >> 8) & 0xFF;
		b = pxl & 0xFF;
		total=pxl;
		itemid=-1;//REMOVE
		metadata=-1; //REMOVE
	}
	
	public int getRed() {
		return r;
	}
	public  int getBlue() {
		return b;
	}
	public int getGreen() {
		return g;
	}
	public int getRGB() {
		return total;
	}
	public byte getId() {
		return itemid;
	}
	public byte getMeta() {
		return metadata;
	}
	public int compareTo(Colour other) {
		return Math.abs(r-other.getRed())+Math.abs(b-other.getBlue())+Math.abs(g-other.getGreen());
	}
}

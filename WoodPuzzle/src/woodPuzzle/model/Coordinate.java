package woodPuzzle.model;

public class Coordinate {
	public final int x; // width
	public final int y; // height
	public final int z; // length
	
	public Coordinate(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Coordinate buildCoordinate(String text) {
		String[] result = text.split(",");
		if (result.length < 3) 
			return null;
		return new Coordinate(Integer.parseInt(result[0]), Integer.parseInt(result[1]), Integer.parseInt(result[2]));
	}		
}

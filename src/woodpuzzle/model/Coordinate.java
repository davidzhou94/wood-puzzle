package woodpuzzle.model;

public class Coordinate {
	/*
	 * No point making final ints private.
	 */
	public final int x; // width
	public final int y; // height
	public final int z; // length
	
	/**
	 * Creates a coordinate with the given x, y, and z components.
	 * @param x The x component.
	 * @param y The y component.
	 * @param z The z component.
	 */
	public Coordinate(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Creates a coordinate that has the usual vector
	 * addition applied to it. 
	 * @param x The x component.
	 * @param y The y component.
	 * @param z The z component.
	 * @return The transformed coordinate (a new coordinate instance).
	 */
	public Coordinate vectorAdd(int x, int y, int z) {
		return new Coordinate(this.x + x, this.y + y, this.z + z);
	}
	
	/**
	 * Creates a coordinate from a string representation 
	 * of a coordinate of the form "x,y,z"
	 * @param text The string representation.
	 * @return The newly built coordinate.
	 */
	public static Coordinate buildCoordinate(String text) {
		String[] result = text.split(",");
		if (result.length != 3) 
			return null;
		return new Coordinate(Integer.parseInt(result[0]), Integer.parseInt(result[1]), Integer.parseInt(result[2]));
	}
}

package woodPuzzle.model;


public class Box {

	private final int width;
	private final int height;
	private final int length;
	private final int total;
	
	private final int[] cells;
	
	/**
	 * Constructor for a Box.
	 * @param width
	 * @param height
	 * @param length
	 */
	Box(int width, int height, int length) {
		this.width = width; 
		this.height = height;
		this.length = length;
		this.total = width * height * length;
		this.cells = new int[total];
		
		init();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getLength() {
		return length;
	}
	
	private void init() {
		for (int i = 0; i < total; i++) {
			this.cells[i] = 0;
		}
	}
	
	public int hashCoordinate(Coordinate c) {
		return c.x + (width * c.y) + (c.z * (width * height) ); 
	}
	
	public boolean isValidCoordinate(int x, int y, int z) {
		if (x < 0 || x >= width)
			return false;
		if (y < 0 || y >= height)
			return false;
		if (z < 0 || z >= length)
			return false;
		return true;
	}
	
	public boolean isValidCoordinate(Coordinate c) {
		return isValidCoordinate(c.x, c.y, c.z);
	}
	
	public boolean cellIsFilled(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return false;
		int index = x + (width * y) + (z * (width * height) );
		if (this.cells[index] == 0)
			return true;
		return false;
	}
	
	public boolean cellIsFilled(Coordinate c) {
		return cellIsFilled(c.x, c.y, c.z);
	}
	
	public boolean fillCell(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return false;
		int index = x + (width * y) + (z * (width * height) );
		if (this.cells[index] != 0)
			return false;
		this.cells[index] = 1;
		return true;
	}
	
	public boolean clearCell(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return false;
		int index = x + (width * y) + (z * (width * height) );
		if (this.cells[index] == 0)
			return false;
		this.cells[index] = 0;
		return true;
	}

}

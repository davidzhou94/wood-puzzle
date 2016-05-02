package woodPuzzle.model;

import java.util.HashSet;
import java.util.Set;

public class Puzzle {
	protected final int width;
	protected final int height;
	protected final int length;
	protected final int totalCells;
	protected final int shapeSide;
	protected Set<Shape> allShapes;
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getLength() {
		return length;
	}
	
	public int getTotalCells() {
		return totalCells;
	}

	public Puzzle(int width, int height, int length, int shapeSide) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.totalCells = width * height * length;
		this.shapeSide = shapeSide;
		this.allShapes = new HashSet<Shape>();
	}
	
	public void addShape(Shape shape) {
		allShapes.add(shape);
	}
	
	public Set<Shape> getShapes() {
		return this.allShapes;
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
	
	public int hashCoordinate(Coordinate c) {
		return hashCoordinate(c.x, c.y, c.z); 
	}
	
	public int hashCoordinate(int x, int y, int z) {
		return x + (width * y) + (z * (width * height) ); 
	}
}

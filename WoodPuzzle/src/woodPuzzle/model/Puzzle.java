package woodPuzzle.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Puzzle {
	private final int width;
	private final int height;
	private final int length;
	private final int totalCells;
	private final int shapeSide;
	private Set<Shape> allShapes;
	private Set<Shape> usedShapes;
	private Set<Shape> unusedShapes;
	private Shape filledCells[];
	
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
	
	public Shape[] getFilledCells() {
		return filledCells;
	}

	public Puzzle(int width, int height, int length, int shapeSide) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.totalCells = width * height * length;
		this.shapeSide = shapeSide;
		this.allShapes = new HashSet<Shape>();
		this.usedShapes = new HashSet<Shape>();
		this.unusedShapes = new HashSet<Shape>();
		this.filledCells = new Shape[totalCells];
		this.init();
	}
	
	public Puzzle(Puzzle p) {
		this.width = p.width;
		this.height = p.height;
		this.length = p.length;
		this.totalCells = p.totalCells;
		this.shapeSide = p.shapeSide;
		this.allShapes = p.allShapes;
		this.usedShapes = new HashSet<Shape>(p.usedShapes);
		this.unusedShapes = new HashSet<Shape>(p.unusedShapes);
		this.filledCells = new Shape[p.filledCells.length];
		System.arraycopy(p.filledCells, 0, this.filledCells, 0, this.filledCells.length);
	}
		
	private void init() {
		for (int i = 0; i < totalCells; i++) {
			this.filledCells[i] = null;
		}
	}
	
	public void addShape(Shape shape) {
		allShapes.add(shape);
		unusedShapes.add(shape);
	}
	
	public Set<Shape> getUnusedShapes() {
		return unusedShapes;
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
	
	public boolean placeShape(Shape shape, List<Coordinate> position) {
		if (!unusedShapes.contains(shape)) return false;
		int lowx = Integer.MAX_VALUE, lowy = Integer.MAX_VALUE, lowz = Integer.MAX_VALUE;
		for (Coordinate c : position) {
			if (!isValidCoordinate(c)) return false;
			if (filledCells[hashCoordinate(c)] != null) return false;
			if (c.x < lowx) lowx = c.x;
			if (c.y < lowy) lowy = c.y;
			if (c.z < lowz) lowz = c.z;
		}
		int temp[] = new int[shapeSide * shapeSide * shapeSide];
		for (int i = 0; i < shapeSide * shapeSide * shapeSide; i++) temp[i] = 0;
		for (Coordinate c : position) {
			//int pos = (c.x - lowx) + (c.y - lowy) * shapeSide + (c.z - lowz) * shapeSide * shapeSide;
			temp[shape.hashCoordinate(c.vectorAdd(-lowx, -lowy, -lowz))] = 1;
		}
		
		if (!isIdenticalPermutedShape(shape, temp)) return false;
		
		for (Coordinate c : position) {
			filledCells[hashCoordinate(c)] = shape;
		}
		
		unusedShapes.remove(shape);
		usedShapes.add(shape);
		
		return true;
	}
	
	private boolean isIdenticalPermutedShape(Shape s1, int[] s2) {
		for (int yaxis = 0; yaxis <= 3; yaxis++) {
			for (int zaxis = 0; zaxis <= 3; zaxis++) {
				int[] temp;
				temp = s1.rotateShape(yaxis, zaxis);
				if (isIdenticalShape(temp, s2)) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * s1 and s2 must already be placed against the origin
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean isIdenticalShape(int[] s1, int[] s2) {
		for (int i = 0; i < s1.length; i++) {
			if (s1[i] != s2[i]) return false;			
		}
		return true;
	}
	
	public int hashCoordinate(Coordinate c) {
		return hashCoordinate(c.x, c.y, c.z); 
	}
	
	public int hashCoordinate(int x, int y, int z) {
		return x + (width * y) + (z * (width * height) ); 
	}
}

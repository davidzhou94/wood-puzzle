package woodPuzzle.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Configuration {
	
	private final Puzzle puzzle;
	private Shape cells[];
	private Set<Shape> unusedShapes; 

	public Shape[] getCells() {
		return cells;
	}
	
	public Set<Shape> getUnusedShapes() {
		return unusedShapes;
	}

	/**
	 * The base constructor. Creates an empty configuration with
	 * the given puzzle state.
	 * @param p A reference to the puzzle description.
	 */
	public Configuration(Puzzle p) {
		this.puzzle = p;
		this.cells = new Shape[p.totalCells]; 
		this.unusedShapes = new HashSet<Shape>(p.shapes);
		this.init();
	}
	
	/**
	 * The copy constructor. Creates a configuration that is a 
	 * copy of the given configuration that is entirely state
	 * independent of the given configuration, i.e. changes to this
	 * configuration will not affect the state of the given configuration.
	 * @param c A reference to the configuration to copy
	 */
	public Configuration(Configuration c) {
		this.puzzle = c.puzzle;
		this.cells = new Shape[c.cells.length];
		System.arraycopy(c.cells, 0, this.cells, 0, this.cells.length);
		this.unusedShapes = new HashSet<Shape>(c.unusedShapes);
	}
	
	/**
	 * Initialized the cells of this configuration by setting all
	 * references to null. This should not be used with the copy constructor. 
	 */
	private void init() {
		for (int i = 0; i < puzzle.getTotalCells(); i++) {
			this.cells[i] = null;
		}
	}
	
	/**
	 * Attempts to place the given shape in the position specified
	 * by the given list of coordinates. The following must be true:
	 *  1) The given coordinates are valid
	 *  2) The given coordinates do not overlap with a placed shape
	 *  3) The list of coordinates correspond to the given shape
	 * If the shape is placed successfully, it is removed from the set
	 * of unused shapes and the given coordinates in the cells of this
	 * configuration will correspond to the given shape.
	 * @param shape A reference to the shape to place.
	 * @param position The list of coordinates to place the shape.
	 * @return true if the above conditions are true, otherwise false.
	 */
	public boolean placeShape(Shape shape, List<Coordinate> position) {
		if (!unusedShapes.contains(shape)) return false;
		int lowx = Integer.MAX_VALUE, lowy = Integer.MAX_VALUE, lowz = Integer.MAX_VALUE;
		for (Coordinate c : position) {
			if (!puzzle.isValidCoordinate(c)) return false;
			if (cells[puzzle.hashCoordinate(c)] != null) return false;
			if (c.x < lowx) lowx = c.x;
			if (c.y < lowy) lowy = c.y;
			if (c.z < lowz) lowz = c.z;
		}
		int shapeTotal = puzzle.shapeSide * puzzle.shapeSide * puzzle.shapeSide;
		int temp[] = new int[shapeTotal];
		for (int i = 0; i < shapeTotal; i++) temp[i] = 0;
		for (Coordinate c : position) {
			temp[shape.hashCoordinate(c.vectorAdd(-lowx, -lowy, -lowz))] = 1;
		}
		
		if (!isIdenticalRotatedShape(shape, temp)) return false;
		
		for (Coordinate c : position) {
			cells[puzzle.hashCoordinate(c)] = shape;
		}
		
		unusedShapes.remove(shape);
		
		return true;
	}
	
	/**
	 * Checks whether there exists a rotation of the first shape 
	 * that is identical to the second shape. It is assumed that s2  
	 * is already placed "against" the origin. 
	 * @param s1 A reference to the first shape
	 * @param s2 The cells of the second shape
	 * @return true if such a rotation exists, false otherwise.
	 */
	private boolean isIdenticalRotatedShape(Shape s1, int[] s2) {
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
	 * Checks whether the two given shapes are perfectly identical, 
	 * specifically there is no check for positioning or rotation. It is
	 * assumed that s1 and s2 are already be placed "against" the origin.
	 * @param s1 The cells of a first shape
	 * @param s2 The cells of a second shape
	 * @return true if they are perfectly identical, otherwise false.
	 */
	private boolean isIdenticalShape(int[] s1, int[] s2) {
		for (int i = 0; i < s1.length; i++) {
			if (s1[i] != s2[i]) return false;			
		}
		return true;
	}
	
	/**
	 * Removes a shape from the set of unused shapes (discards it)
	 * @param s A reference to the shape to remove
	 */
	public void removeShape(Shape s) {
		if (unusedShapes.contains(s)) unusedShapes.remove(s);
	}
}

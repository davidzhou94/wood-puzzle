package woodpuzzle.model;

import java.util.HashSet;
import java.util.Set;

public class Puzzle {
	/* The fields are marked as protected to speed
	 * up access from within the model package. */
	protected final int width;
	protected final int height;
	protected final int length;
	protected final int totalCells;
	protected final int shapeSide;
	protected final int shapeCount;
	protected final int minShapeSize;
	protected final int maxShapeSize;
	protected final int minShapeFit;
	protected Set<Shape> shapes;
	
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
	
	public int getShapeCount() {
		return shapeCount;
	}
	
	public int getMinShapeSize() {
		return minShapeSize;
	}
	
	public int getMaxShapeSize() {
		return maxShapeSize;
	}
	
	public int getMinShapeFit() {
		return minShapeFit;
	}
	
	public Set<Shape> getShapes() {
		return this.shapes;
	}

	/**
	 * Creates an empty puzzle with the 
	 * specified box dimensions and shape dimensions.
	 * @param width The box width (x-axis)
	 * @param height The box height (y-axis)
	 * @param length The box length (z-axis)
	 * @param shapeSide The side length of the cube that can contain
	 *        the largest shape
	 */
	public Puzzle(int width, int height, int length, int shapeSide,
			int shapeCount, int minShapeSize, int maxShapeSize, int minShapeFit) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.totalCells = width * height * length;
		this.shapeSide = shapeSide;
		this.shapeCount = shapeCount;
		this.minShapeSize = minShapeSize;
		this.maxShapeSize = maxShapeSize;
		this.minShapeFit = minShapeFit;
		this.shapes = new HashSet<Shape>();
	}
	
	/**
	 * Adds the given shape to the puzzle.
	 * @param shape The shape to add.
	 */
	public void addShape(Shape shape) {
		shapes.add(shape);
	}
	
	/**
	 * Removes the given shape from the puzzle.
	 * @param shape The shape to remove.
	 */
	public void removeShape(Shape shape) {
		if (shapes.contains(shape)) shapes.remove(shape);
	}
	
	/**
	 * Checks whether the given coordinate is valid for the
	 * dimensions of this puzzle.
	 * @param x The x-axis component (width).
	 * @param y The y-axis component (height).
	 * @param z The z-axis component (length).
	 * @return true if the coordinate is valid, false otherwise.
	 */
	public boolean isValidCoordinate(int x, int y, int z) {
		if (x < 0 || x >= width)
			return false;
		if (y < 0 || y >= height)
			return false;
		if (z < 0 || z >= length)
			return false;
		return true;
	}
	
	/**
	 * Checks whether the given coordinate is valid for the
	 * dimensions of this puzzle.
	 * @param c A reference to the coordinate to check.
	 * @return true if the coordinate is valid, false otherwise.
	 */
	public boolean isValidCoordinate(Coordinate c) {
		return isValidCoordinate(c.x, c.y, c.z);
	}
	
	/**
	 * Returns the hashed value of the given coordinate. This
	 * is also the index of the coordinate in the cells array
	 * of a configuration. It is assumed that the given 
	 * coordinate is valid.
	 * @param c The coordinate to hash.
	 * @return The hashed value.
	 */
	public int hashCoordinate(Coordinate c) {
		return hashCoordinate(c.x, c.y, c.z); 
	}
	
	/**
	 * Returns the hashed value of the given coordinate. This
	 * is also the index of the coordinate in the cells array
	 * of a configuration. It is assumed that the given 
	 * coordinate is valid.
	 * @param x The x-axis component (width).
	 * @param y The y-axis component (height).
	 * @param z The z-axis component (length).
	 * @return The hashed value.
	 */
	public int hashCoordinate(int x, int y, int z) {
		return x + (width * y) + (z * (width * height) ); 
	}
}

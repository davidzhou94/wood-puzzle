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

	public Configuration(Puzzle p) {
		this.puzzle = p;
		this.cells = new Shape[p.totalCells]; 
		this.unusedShapes = new HashSet<Shape>(p.allShapes);
		this.init();
	}
	
	public Configuration(Configuration c) {
		this.puzzle = c.puzzle;
		this.cells = new Shape[c.cells.length];
		System.arraycopy(c.cells, 0, this.cells, 0, this.cells.length);
		this.unusedShapes = new HashSet<Shape>(c.unusedShapes);
	}
	
	private void init() {
		for (int i = 0; i < puzzle.getTotalCells(); i++) {
			this.cells[i] = null;
		}
	}
	
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
		
		if (!isIdenticalPermutedShape(shape, temp)) return false;
		
		for (Coordinate c : position) {
			cells[puzzle.hashCoordinate(c)] = shape;
		}
		
		unusedShapes.remove(shape);
		
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
}

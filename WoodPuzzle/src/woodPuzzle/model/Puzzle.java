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
	
	public Puzzle(int width, int height, int length, int shapeSide) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.totalCells = width * height * length;
		this.shapeSide = shapeSide;
		this.allShapes = new HashSet<Shape>();
		this.usedShapes = new HashSet<Shape>();
		this.unusedShapes = new HashSet<Shape>();
		this.filledCells = new Shape[shapeSide * shapeSide * shapeSide];
		this.init();
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
			if (c.x < lowx) lowx = c.x;
			if (c.y < lowy) lowy = c.y;
			if (c.z < lowz) lowz = c.z;
		}
		int temp[] = new int[shapeSide * shapeSide * shapeSide];
		for (int i = 0; i < shapeSide * shapeSide * shapeSide; i++) temp[i] = 0;
		for (Coordinate c : position) {
			int pos = (c.x - lowx) + (c.y - lowy) * shapeSide + (c.z - lowz) * shapeSide * shapeSide;
			temp[pos] = 1;
		}
		
		if (!isIdenticalPermutedShape(shape.shape, temp)) return false;
		
		for (Coordinate c : position) {
			if (filledCells[c.x + c.y * width + c.z * width * height] != null) return false;
		}
		
		for (Coordinate c : position) {
			filledCells[c.x + c.y * width + c.z * width * height] = shape;
		}
		
		unusedShapes.remove(shape);
		usedShapes.add(shape);
		
		return true;
	}
	
	private boolean isIdenticalPermutedShape(final int[] s1, int[] s2) {
		if (isIdenticalShape(s1, s2)) return true;
		for (int axis = 1; axis <= 2; axis++) {
			for (int direction = 1; direction <= 3; direction++) {
				rotateShape(s2, axis, direction);
				if (isIdenticalShape(s1, s2)) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Direction is the number of 90deg clockwise rotations (when  
	 * facing the origin) along the axis
	 * Axis is the axis of rotation, x = 0, y = 1, z = 2 
	 * after rotating the shape, it will "pull" the shape into the origin
	 * @param s
	 * @param axis
	 * @param direction
	 */
	private void rotateShape(int[] s, int axis, int direction) {
		int copy[] = new int[s.length];
		System.arraycopy(s, 0, copy, 0, s.length);
		switch (axis) {
		case 0:
			// shouldn't need to rotate along x-axis
		case 1:
			switch (direction) {
			case 0:
				// do nothing
				break;
			case 1:
				// 90 deg along y-axis
				for (int x = 0; x < shapeSide; x++) {
					for (int y = 0; y < shapeSide; y++) {
						for (int z = 0; z < shapeSide; z++) {
							int oldx = shapeSide - z - 1, oldy = y, oldz = x;
							s[x + y * shapeSide + z * shapeSide * shapeSide] = copy[oldx + oldy * shapeSide + oldz * shapeSide * shapeSide];
						}
					}
				}
				break;
			case 2:
				// 180 deg along y-axis
				for (int x = 0; x < shapeSide; x++) {
					for (int y = 0; y < shapeSide; y++) {
						for (int z = 0; z < shapeSide; z++) {
							int oldx = shapeSide - x - 1, oldy = y, oldz = shapeSide - z - 1;
							s[x + y * shapeSide + z * shapeSide * shapeSide] = copy[oldx + oldy * shapeSide + oldz * shapeSide * shapeSide];
						}
					}
				}
				break;
			case 3:
				// 270 deg along y-axis
				for (int x = 0; x < shapeSide; x++) {
					for (int y = 0; y < shapeSide; y++) {
						for (int z = 0; z < shapeSide; z++) {
							int oldx = z, oldy = y, oldz = shapeSide - x - 1;
							s[x + y * shapeSide + z * shapeSide * shapeSide] = copy[oldx + oldy * shapeSide + oldz * shapeSide * shapeSide];
						}
					}
				}
				break;
			}
			break;
		case 2:
			switch (direction) {
			case 0:
				// do nothing
				break;
			case 1:
				// 90 deg along z-axis
				for (int x = 0; x < shapeSide; x++) {
					for (int y = 0; y < shapeSide; y++) {
						for (int z = 0; z < shapeSide; z++) {
							int oldx = y, oldy = shapeSide - x - 1, oldz = z;
							s[x + y * shapeSide + z * shapeSide * shapeSide] = copy[oldx + oldy * shapeSide + oldz * shapeSide * shapeSide];
						}
					}
				}
				break;
			case 2:
				// 180 deg along z-axis
				for (int x = 0; x < shapeSide; x++) {
					for (int y = 0; y < shapeSide; y++) {
						for (int z = 0; z < shapeSide; z++) {
							int oldx = shapeSide - x - 1, oldy = shapeSide - y - 1, oldz = z;
							s[x + y * shapeSide + z * shapeSide * shapeSide] = copy[oldx + oldy * shapeSide + oldz * shapeSide * shapeSide];
						}
					}
				}
				break;
			case 3:
				// 270 deg along z-axis
				for (int x = 0; x < shapeSide; x++) {
					for (int y = 0; y < shapeSide; y++) {
						for (int z = 0; z < shapeSide; z++) {
							int oldx = shapeSide - y - 1, oldy = x, oldz = z;
							s[x + y * shapeSide + z * shapeSide * shapeSide] = copy[oldx + oldy * shapeSide + oldz * shapeSide * shapeSide];
						}
					}
				}
				break;
			}
			break;
		default:
			
		}
		int lowx = Integer.MAX_VALUE, lowy = Integer.MAX_VALUE, lowz = Integer.MAX_VALUE;
		for (int x = 0; x < shapeSide; x++) {
			for (int y = 0; y < shapeSide; y++) {
				for (int z = 0; z < shapeSide; z++) {
					if (s[x + y * shapeSide + z * shapeSide * shapeSide] == 1) {
						if (x < lowx) lowx = x;
						if (y < lowy) lowy = y;
						if (z < lowz) lowz = z;
					}
				}
			}
		}
		for (int x = 0; x < shapeSide; x++) {
			for (int y = 0; y < shapeSide; y++) {
				for (int z = 0; z < shapeSide; z++) {
					s[x + y * shapeSide + z * shapeSide * shapeSide] 
							= s[(x+lowx) + (y+lowy) * shapeSide + (z+lowz) * shapeSide * shapeSide];
				}
			}
		}
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

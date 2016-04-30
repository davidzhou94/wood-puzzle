package woodPuzzle.model;

import java.util.List;

import woodPuzzle.model.Coordinate;

public class Shape {
	public final int sideLength;
	private final int total;
	public final int[] shape;
	private final List<Coordinate> coordinates;
	
	public int getSideLength() {
		return sideLength;
	}
	
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public Shape(int sideLength, List<Coordinate> shape) {
		this.sideLength = sideLength; 
		this.total = sideLength * sideLength * sideLength;
		this.shape = new int[total];
		this.coordinates = shape;
		
		init(shape);
	}
	
	private void init(List<Coordinate> shape) {
		for (int i = 0; i < total; i++) {
			this.shape[i] = 0;
		}
		for (Coordinate c : shape) {
			this.shape[this.hashCoordinate(c)] = 1;
		}
	}
	
	/**
	 * Direction is the number of 90deg clockwise rotations (when  
	 * facing the origin) along the axis
	 * Axis is the axis of rotation, x = 0, y = 1, z = 2 
	 * after rotating the shape, it will "pull" the shape into the origin
	 * @param copy
	 * @param axis
	 * @param direction
	 * @return 
	 */
	public int[] rotateShape(int axis, int direction) {
		int ret[] = new int[shape.length];
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
				for (int x = 0; x < sideLength; x++) {
					for (int y = 0; y < sideLength; y++) {
						for (int z = 0; z < sideLength; z++) {
							int oldx = sideLength - z - 1, oldy = y, oldz = x;
							ret[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
						}
					}
				}
				break;
			case 2:
				// 180 deg along y-axis
				for (int x = 0; x < sideLength; x++) {
					for (int y = 0; y < sideLength; y++) {
						for (int z = 0; z < sideLength; z++) {
							int oldx = sideLength - x - 1, oldy = y, oldz = sideLength - z - 1;
							ret[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
						}
					}
				}
				break;
			case 3:
				// 270 deg along y-axis
				for (int x = 0; x < sideLength; x++) {
					for (int y = 0; y < sideLength; y++) {
						for (int z = 0; z < sideLength; z++) {
							int oldx = z, oldy = y, oldz = sideLength - x - 1;
							ret[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
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
				for (int x = 0; x < sideLength; x++) {
					for (int y = 0; y < sideLength; y++) {
						for (int z = 0; z < sideLength; z++) {
							int oldx = y, oldy = sideLength - x - 1, oldz = z;
							ret[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
						}
					}
				}
				break;
			case 2:
				// 180 deg along z-axis
				for (int x = 0; x < sideLength; x++) {
					for (int y = 0; y < sideLength; y++) {
						for (int z = 0; z < sideLength; z++) {
							int oldx = sideLength - x - 1, oldy = sideLength - y - 1, oldz = z;
							ret[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
						}
					}
				}
				break;
			case 3:
				// 270 deg along z-axis
				for (int x = 0; x < sideLength; x++) {
					for (int y = 0; y < sideLength; y++) {
						for (int z = 0; z < sideLength; z++) {
							int oldx = sideLength - y - 1, oldy = x, oldz = z;
							ret[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
						}
					}
				}
				break;
			}
			break;
		default:
			
		}
		int lowx = Integer.MAX_VALUE, lowy = Integer.MAX_VALUE, lowz = Integer.MAX_VALUE;
		for (int x = 0; x < sideLength; x++) {
			for (int y = 0; y < sideLength; y++) {
				for (int z = 0; z < sideLength; z++) {
					if (ret[this.hashCoordinate(x, y, z)] == 1) {
						if (x < lowx) lowx = x;
						if (y < lowy) lowy = y;
						if (z < lowz) lowz = z;
					}
				}
			}
		}
		for (int x = 0; x < sideLength - lowx; x++) {
			for (int y = 0; y < sideLength - lowy; y++) {
				for (int z = 0; z < sideLength - lowz; z++) {
					ret[this.hashCoordinate(x, y, z)] 
							= ret[this.hashCoordinate(x, y, z)];
				}
			}
		}
		return ret;
	}
	
	public int hashCoordinate(int x, int y, int z) {
		return x + (sideLength * y) + (z * (sideLength * sideLength) ); 
	}
	
	public int hashCoordinate(Coordinate c) {
		return hashCoordinate(c.x, c.y, c.z); 
	}
}

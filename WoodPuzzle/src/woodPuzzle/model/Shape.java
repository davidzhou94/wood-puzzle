package woodPuzzle.model;

import java.util.List;

import woodPuzzle.model.Coordinate;

public class Shape {
	public final int sideLength;
	private final int total;
	private final int[] shape;
	private final List<Coordinate> coordinates;
	
	public int[] getCells() {
		return shape;
	}
	
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
	 * yaxis is the number of 90deg clockwise rotations (when  
	 * facing the origin) along the yaxis
	 * likewise, zaxis is the number of 90 deg rotations along the z axis
	 * after rotating the shape, it will "pull" the shape into the origin
	 * There should be no need to rotate along the x-axis.
	 * @param yaxis
	 * @param zaxis
	 * @return 
	 */
	public int[] rotateShape(int yaxis, int zaxis) {
		int ret1[] = new int[shape.length], ret2[] = new int[shape.length];

		switch (yaxis) {
		default:
		case 0:
			System.arraycopy(shape, 0, ret1, 0, ret1.length);
			break;
		case 1:
			// 90 deg along y-axis
			for (int x = 0; x < sideLength; x++) {
				for (int y = 0; y < sideLength; y++) {
					for (int z = 0; z < sideLength; z++) {
						int oldx = sideLength - z - 1, oldy = y, oldz = x;
						ret1[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
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
						ret1[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
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
						ret1[this.hashCoordinate(x, y, z)] = shape[this.hashCoordinate(oldx, oldy, oldz)];
					}
				}
			}
			break;
		}

		switch (zaxis) {
		default:
		case 0:
			System.arraycopy(ret1, 0, ret2, 0, ret2.length);
			break;
		case 1:
			// 90 deg along z-axis
			for (int x = 0; x < sideLength; x++) {
				for (int y = 0; y < sideLength; y++) {
					for (int z = 0; z < sideLength; z++) {
						int oldx = y, oldy = sideLength - x - 1, oldz = z;
						ret2[this.hashCoordinate(x, y, z)] = ret1[this.hashCoordinate(oldx, oldy, oldz)];
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
						ret2[this.hashCoordinate(x, y, z)] = ret1[this.hashCoordinate(oldx, oldy, oldz)];
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
						ret2[this.hashCoordinate(x, y, z)] = ret1[this.hashCoordinate(oldx, oldy, oldz)];
					}
				}
			}
			break;
		}

		int lowx = Integer.MAX_VALUE, lowy = Integer.MAX_VALUE, lowz = Integer.MAX_VALUE;
		for (int x = 0; x < sideLength; x++) {
			for (int y = 0; y < sideLength; y++) {
				for (int z = 0; z < sideLength; z++) {
					if (ret2[this.hashCoordinate(x, y, z)] == 1) {
						if (x < lowx) lowx = x;
						if (y < lowy) lowy = y;
						if (z < lowz) lowz = z;
					}
				}
			}
		}
		for (int x = 0; x < sideLength; x++) {
			for (int y = 0; y < sideLength; y++) {
				for (int z = 0; z < sideLength; z++) {
					if (x < sideLength - lowx &&
							y < sideLength - lowy &&
							z < sideLength - lowz) {
						ret2[this.hashCoordinate(x, y, z)] 
								= ret2[this.hashCoordinate(x + lowx, y + lowy, z + lowz)];
					} else {
						ret2[this.hashCoordinate(x, y, z)] = 0;
					}
				}
			}
		}
		return ret2;
	}
	
	public int hashCoordinate(int x, int y, int z) {
		return x + (sideLength * y) + (z * (sideLength * sideLength) ); 
	}
	
	public int hashCoordinate(Coordinate c) {
		return hashCoordinate(c.x, c.y, c.z); 
	}
}

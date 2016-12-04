package woodpuzzle.model;

import java.util.List;

import woodpuzzle.model.Coordinate;

public class Shape {
	/* The fields are marked as protected to speed
	 * up access from within the model package. */
	protected final int sideLength;
	protected final int total;
	protected final int[] cells;
	protected final List<Coordinate> coordinates;
	
	public int[] getCells() {
		return cells;
	}
	
	public int getSideLength() {
		return sideLength;
	}
	
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	/**
	 * Creates a shape with the given side length of the cube 
	 * that contains the shape and a list of coordinates for 
	 * the cells of the shape.
	 * @param sideLength The side length.
	 * @param shape The coordinates of the shape.
	 */
	public Shape(int sideLength, List<Coordinate> shape) {
		this.sideLength = sideLength; 
		this.total = sideLength * sideLength * sideLength;
		this.cells = new int[total];
		this.coordinates = shape;
		
		init(shape);
	}
	
	/**
	 * Initialized the cells of this shape by setting all cells to 0.
	 */
	private void init(List<Coordinate> shape) {
		for (int i = 0; i < total; i++) {
			this.cells[i] = 0;
		}
		for (Coordinate c : shape) {
			this.cells[this.hashCoordinate(c)] = 1;
		}
	}
	
	/**
	 * Used for the lambda expression for rotation transforms. The single
	 * operation takes a coordinate from the target array and maps it to
	 * the corresponding cell in the source array (an index).
	 * @author david
	 *
	 */
	private interface VectorTransform {
		int op(int x, int y, int z);
	}
	
	/**
	 * Used with rotate shape to map cells from a given source array
	 * to a given target array using the given lambda expression to transform
	 * a coordinate in the target array to a source array index.
	 * @param src
	 * @param dest
	 * @param vt
	 */
	private void rotationTransform(int[] src, int[] dest, VectorTransform vt) {
		for (int x = 0; x < sideLength; x++) {
			for (int y = 0; y < sideLength; y++) {
				for (int z = 0; z < sideLength; z++) {
					dest[this.hashCoordinate(x, y, z)] = src[vt.op(x, y, z)];
				}
			}
		}
	}
	
	/**
	 * Rotates this shape the given number of rotations along each of two 
	 * possible axis. Each rotation is 90deg clockwise in the axis facing
	 * the origin. For example, rotateShape(0, 2) will rotate the shape
	 * 180deg clockwise in the z-axis when facing the origin. After computing
	 * the rotation, this will "pull" the shape into the origin such that the
	 * smallest possible x, y, and z values are 0. Finally, this will return
	 * an array representing the cells of the rotated shape. The cells of
	 * this shape are unchanged.
	 * @param yaxis Number of 90deg clockwise rotations along the y-axis.
	 * @param zaxis Number of 90deg clockwise rotations along the z-axis.
	 * @return An array representing the cells of the rotated shape.
	 */
	public int[] rotateShape(int yaxis, int zaxis) {
		int ret1[] = new int[cells.length], ret2[] = new int[cells.length];

		yaxis %= 4;
		zaxis %= 4;
		
		// rotate in the y-axis first, then rotate again in the z-axis.
		switch (yaxis) {
		default:
		case 0:
			System.arraycopy(cells, 0, ret1, 0, ret1.length);
			break;
		case 1:
			// 90 deg along y-axis
			this.rotationTransform(
					cells, ret1, (x, y, z) -> this.hashCoordinate(sideLength - z - 1, y, x));
			break;
		case 2:
			// 180 deg along y-axis
			this.rotationTransform(
					cells, ret1, (x, y, z) -> this.hashCoordinate(sideLength - x - 1, y, sideLength - z - 1));
			break;
		case 3:
			// 270 deg along y-axis
			this.rotationTransform(
					cells, ret1, (x, y, z) -> this.hashCoordinate(z, y, sideLength - x - 1));
			break;
		}

		// now rotate in the z-axis
		switch (zaxis) {
		default:
		case 0:
			System.arraycopy(ret1, 0, ret2, 0, ret2.length);
			break;
		case 1:
			// 90 deg along z-axis
			this.rotationTransform(
					ret1, ret2, (x, y, z) -> this.hashCoordinate(y, sideLength - x - 1, z));
			break;
		case 2:
			// 180 deg along z-axis
			this.rotationTransform(
					ret1, ret2, (x, y, z) -> this.hashCoordinate(sideLength - x - 1, sideLength - y - 1, z));
			break;
		case 3:
			// 270 deg along z-axis
			this.rotationTransform(
					ret1, ret2, (x, y, z) -> this.hashCoordinate(sideLength - y - 1, x, z));
			break;
		}

		// determine the smallest x, y, and z values in order 
		// to "pull" the shape into the origin.
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
		
		// pull the shape into the origin.
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
	
	/**
	 * Returns the hashed value of the given coordinate. This
	 * is also the index of the coordinate in the cells array
	 * of this shape. It is assumed that the given coordinate is valid.
	 * @param c The coordinate to hash.
	 * @return The hashed value.
	 */
	public int hashCoordinate(Coordinate c) {
		return hashCoordinate(c.x, c.y, c.z); 
	}
	
	/**
	 * Returns the hashed value of the given coordinate. This
	 * is also the index of the coordinate in the cells array
	 * of this shape. It is assumed that the given coordinate is valid.
	 * @param x The x-axis component (width).
	 * @param y The y-axis component (height).
	 * @param z The z-axis component (length).
	 * @return The hashed value.
	 */
	public int hashCoordinate(int x, int y, int z) {
		return x + (sideLength * y) + (z * (sideLength * sideLength) ); 
	}
}

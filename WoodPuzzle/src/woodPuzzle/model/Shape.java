package woodPuzzle.model;

import java.util.List;

import woodPuzzle.model.Coordinate;

public class Shape {
	public final int sideLength;
	private final int total;
	public final int[] shape;
	
	public Shape(int sideLength, List<Coordinate> shape) {
		this.sideLength = sideLength; 
		this.total = sideLength * sideLength * sideLength;
		this.shape = new int[total];
		
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
	
	public int hashCoordinate(Coordinate c) {
		return c.x + (sideLength * c.y) + (c.z * (sideLength * sideLength) ); 
	}
}

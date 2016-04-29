package woodPuzzle.model;

import java.util.List;

import woodPuzzle.model.Coordinate;

public class Shape {
	public final int width;
	public final int height;
	public final int length;
	private final int total;
	public final int[] shape;
	
	public Shape(int width, int height, int length, List<Coordinate> shape) {
		this.width = width; 
		this.height = height;
		this.length = length;
		this.total = width * height * length;
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
		return c.x + (width * c.y) + (c.z * (width * height) ); 
	}
}

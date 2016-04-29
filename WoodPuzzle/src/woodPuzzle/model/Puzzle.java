package woodPuzzle.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Puzzle {
	private Box box;
	private Set<Shape> allShapes;
	
	public Puzzle(int pWidth, int pHeight, int pLength) {
		box = new Box(pWidth, pHeight, pLength);
		allShapes = new HashSet<Shape>();
	}
	
	public void addShape(List<Coordinate> shape) {
		allShapes.add(new Shape(box.getWidth(), box.getHeight(), box.getLength(), shape));
	}
	
	public void clearShapes() {
		allShapes.clear();
	}
	
	public Set<Shape> getShapes() {
		return allShapes;
	}
	
	public Box getBox() {
		return box;
	}
}

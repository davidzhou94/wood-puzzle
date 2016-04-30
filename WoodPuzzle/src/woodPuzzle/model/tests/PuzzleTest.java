package woodPuzzle.model.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import woodPuzzle.model.Coordinate;
import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public class PuzzleTest {
	Puzzle p;
	Shape s1, s2, s3;
	static final int SHAPE_SIDE = 3;
	static final int WIDTH = 5;
	static final int HEIGHT = 2;
	static final int LENGTH = 5;
	@Before
	public void setUp() throws Exception {
		p = new Puzzle(WIDTH, HEIGHT, LENGTH, SHAPE_SIDE);
		List<Coordinate> shapeCells = new ArrayList<Coordinate>();
		shapeCells.add(new Coordinate(0, 0, 0));
		shapeCells.add(new Coordinate(1, 0, 0));
		shapeCells.add(new Coordinate(1, 0, 1));
		shapeCells.add(new Coordinate(1, 1, 0));
		shapeCells.add(new Coordinate(1, 1, 1));
		s1 = new Shape(SHAPE_SIDE, shapeCells);
		shapeCells.clear();
		shapeCells.add(new Coordinate(0, 0, 0));
		shapeCells.add(new Coordinate(1, 0, 0));
		shapeCells.add(new Coordinate(2, 0, 0));
		shapeCells.add(new Coordinate(1, 1, 0));
		shapeCells.add(new Coordinate(1, 0, 1));
		s2 = new Shape(SHAPE_SIDE, shapeCells);
		shapeCells.clear();
		shapeCells.add(new Coordinate(0, 0, 0));
		shapeCells.add(new Coordinate(1, 0, 0));
		shapeCells.add(new Coordinate(0, 0, 1));
		shapeCells.add(new Coordinate(1, 1, 0));
		shapeCells.add(new Coordinate(0, 1, 1));
		s3 = new Shape(SHAPE_SIDE, shapeCells);
		p.addShape(s1);
		p.addShape(s2);
		p.addShape(s3);
	}

	@Test
	public void test() {
		assertTrue(p.getUnusedShapes().contains(s3));
		List<Coordinate> position = new ArrayList<Coordinate>();
		position.add(new Coordinate(0,0,0));
		position.add(new Coordinate(0,1,0));
		position.add(new Coordinate(0,0,1));
		position.add(new Coordinate(1,0,1));
		position.add(new Coordinate(1,1,1));
		assertTrue(p.placeShape(s3, position));
		Puzzle p1 = p;
		p = new Puzzle(p1);
		position.clear();
		// this overlaps with the s3 that was just placed at (1,1,1)
		position.add(new Coordinate(1,1,1));
		position.add(new Coordinate(1,1,2));
		position.add(new Coordinate(1,0,2));
		position.add(new Coordinate(2,1,2));
		position.add(new Coordinate(2,0,2));
		assertFalse(p.placeShape(s1, position));
		position.clear();
		position.add(new Coordinate(0,1,1));
		position.add(new Coordinate(0,1,2));
		position.add(new Coordinate(0,0,2));
		position.add(new Coordinate(1,1,2));
		position.add(new Coordinate(1,0,2));
		assertFalse(p.placeShape(s2, position));
		assertTrue(p.placeShape(s1, position));
		p = new Puzzle(p);
		assertFalse(p.getUnusedShapes().isEmpty());
		position.clear();
		position.add(new Coordinate(1,0,0));
		position.add(new Coordinate(2,0,0));
		position.add(new Coordinate(3,0,0));
		position.add(new Coordinate(2,1,0));
		position.add(new Coordinate(2,0,1));
		assertTrue(p.placeShape(s2, position));
		assertTrue(p.getUnusedShapes().isEmpty());
		assertFalse(p1.getUnusedShapes().isEmpty());
		assertTrue(p1.getUnusedShapes().contains(s1));
	}

}

package woodpuzzle.model.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Coordinate;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

public class ConfigurationTest {
	Puzzle p;
	Shape s1, s2, s3;
	Set<Shape> shapes;
	static final int WIDTH = 5;
	static final int HEIGHT = 2;
	static final int LENGTH = 5;
	static final int SHAPE_SIDE = 3;
	static final int SHAPE_COUNT = 3;
	static final int MIN_SHAPE_SIZE = 5;
	static final int MAX_SHAPE_SIZE = 5;
	static final int MIN_SHAPE_FIT = 3;
	@Before
	public void setUp() {
		List<Coordinate> shapeCells = new ArrayList<>();
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
		shapes = new HashSet<>();
		shapes.add(s1);
		shapes.add(s2);
		shapes.add(s3);
		p = new Puzzle(WIDTH, HEIGHT, LENGTH, SHAPE_SIDE,
				SHAPE_COUNT, MIN_SHAPE_SIZE, MAX_SHAPE_SIZE, MIN_SHAPE_FIT, shapes);
	}

	@Test
	public void test() {
		Configuration c0 = new Configuration(p);
		assertTrue(c0.getUnusedShapes().contains(s3));
		List<Coordinate> position = new ArrayList<>();
		// placing s3 should succeed
		position.add(new Coordinate(0,0,0));
		position.add(new Coordinate(0,1,0));
		position.add(new Coordinate(0,0,1));
		position.add(new Coordinate(1,0,1));
		position.add(new Coordinate(1,1,1));
		assertTrue(c0.placeShape(s3, position));
		Configuration c1 = new Configuration(c0);
		position.clear();
		// this overlaps with the s3 that was just placed at (1,1,1)
		position.add(new Coordinate(1,1,1));
		position.add(new Coordinate(1,1,2));
		position.add(new Coordinate(1,0,2));
		position.add(new Coordinate(2,1,2));
		position.add(new Coordinate(2,0,2));
		assertFalse(c1.placeShape(s1, position));
		position.clear();
		// now try placing s1 in a valid position
		position.add(new Coordinate(0,1,1));
		position.add(new Coordinate(0,1,2));
		position.add(new Coordinate(0,0,2));
		position.add(new Coordinate(1,1,2));
		position.add(new Coordinate(1,0,2));
		// s1 and s2 should not match
		assertFalse(c1.placeShape(s2, position));
		assertTrue(c1.placeShape(s1, position));
		// copy the configuration, c1 has s1 and s3
		Configuration c2 = new Configuration(c1);
		assertFalse(c2.getUnusedShapes().isEmpty());
		position.clear();
		// place s2 in c2
		position.add(new Coordinate(1,0,0));
		position.add(new Coordinate(2,0,0));
		position.add(new Coordinate(3,0,0));
		position.add(new Coordinate(2,1,0));
		position.add(new Coordinate(2,0,1));
		assertTrue(c2.placeShape(s2, position));
		assertTrue(c2.getUnusedShapes().isEmpty());
		// c1 should not have changed
		assertFalse(c1.getUnusedShapes().isEmpty());
		assertTrue(c1.getUnusedShapes().contains(s2));
	}

}

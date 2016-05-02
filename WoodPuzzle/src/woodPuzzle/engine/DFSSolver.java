package woodPuzzle.engine;

import java.util.ArrayList;
import java.util.List;
import woodPuzzle.model.Configuration;
import woodPuzzle.model.Coordinate;
import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public class DFSSolver extends AbstractSolver {

	private static long count = 0;
	private static long rejects = 0;
	private static int record = Integer.MAX_VALUE;
	private static final int SMALLEST_SHAPE_CELL_COUNT = 5;

	private Node root;

	public DFSSolver(Puzzle p) {
		super(p);
		root = new Node(null);
	}

	@Override
	public Configuration findSolution() {
		this.root.config = new Configuration(this.puzzle);
		try {
			this.descend(root);
		} catch (FoundException ex) {
			return ex.config;
		}
		return null;
	}

	private void descend(Node n) throws FoundException {
		count++;
		Configuration currentConfig = n.config;
		if (currentConfig.getUnusedShapes().size() < record) record = currentConfig.getUnusedShapes().size();
		if (count % 1000 == 0) {
			System.out.print("\rConfig #" + count + " has " + currentConfig.getUnusedShapes().size() + " unused shapes, after " + rejects + " dead ends, the current best record is " + record);
		}
		Configuration newConfig = new Configuration(currentConfig);
		// you don't actually need to iterate through the shapes, in theory every shape has at 
		// least one valid placement so it suffices to pick any shape and try each of the possible positions 
		// for that shape in the current configuration.
		Shape s = currentConfig.getUnusedShapes().iterator().next();

		int sideLength = s.getSideLength();
		for(int x = 0; x < this.puzzle.getWidth() - 1; x++) {
			for(int z = 0; z < this.puzzle.getLength() - 1; z++) {
				List<Coordinate> placement;
				if (n.parent == null) System.out.println("\nAdvanced 1 position on root");
				for (int yaxis = 0; yaxis <= 3; yaxis++) {
					for (int zaxis = 0; zaxis <= 3; zaxis++) {
						int[] rotatedShape = s.rotateShape(yaxis, zaxis);
						placement = new ArrayList<Coordinate>();
						for (int i = 0; i < sideLength; i++) {
							for (int j = 0; j < sideLength; j++) {
								for (int k = 0; k < sideLength; k++) {
									if (rotatedShape[s.hashCoordinate(i, j, k)] == 1) {
										placement.add(new Coordinate(i + x, j, k + z));
									}
								}
							}
						}
						if (newConfig.placeShape(s, placement)) {
							if (!hasIsolatedCells(newConfig, SMALLEST_SHAPE_CELL_COUNT)) {
								if (newConfig.getUnusedShapes().isEmpty()) {
									throw new FoundException(newConfig);
								}
								this.descend(new Node(newConfig, n));
							}  else {
								rejects++;
							}
							newConfig = new Configuration(currentConfig);
						} else {
							rejects++;
						}
					}
				}
			}
		}
	}

	class Node {
		public Node parent;
		public Configuration config;
		public Node(Node n) {
			this.parent = n;
		}

		public Node(Configuration c, Node n) {
			this.parent = n;
			this.config = c;
		}
	}

	class FoundException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5453975780718671130L;
		public Configuration config;
		public FoundException(Configuration config) {
			this.config = config;
		}
	}
}

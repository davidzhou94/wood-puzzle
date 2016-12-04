package woodPuzzle.engine;

import java.util.Random;

import woodPuzzle.model.Configuration;
import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public class DFSSolver extends AbstractSolver {

	private static long count = 0;
	private static long rejects = 0;
	private static int record = Integer.MAX_VALUE;
	private Node root;
	private Random rng;
	private Strategy strategy;

	public DFSSolver(Puzzle p) {
		super(p);
		this.root = new Node(null);
		this.rng = new Random();
		this.strategy = new DFSStrategy(this);
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

	/**
	 * The strategy for DFS-order traversals of the possible 
	 * configurations tree.
	 * @author david
	 *
	 */
	class DFSStrategy implements Strategy {
		DFSSolver caller;
		DFSStrategy(DFSSolver dfsSolver) {
			this.caller = dfsSolver;
		}

		@Override
		public void preTraversal(Configuration c) throws EndException {
			count++;
			if (c.getUnusedShapes().size() < record) record = c.getUnusedShapes().size();
			if (count % 1000 == 0) {
				System.out.print("\rConfig #" + count + " has " + c.getUnusedShapes().size() + " unused shapes, after " + rejects + " dead ends, the current best record is " + record);
			}
		}

		@Override
		public Shape determineShape(Configuration c) {
			return (Shape) c.getUnusedShapes().toArray()[rng.nextInt(c.getUnusedShapes().size())];
		}

		@Override
		public void placementFailedGeometry(Node n) {
			rejects++;
		}

		@Override
		public void placementFailedDeadCells(Node n) {
			rejects++;
		}

		@Override
		public void placementSucceeded(Configuration newConfig, Node n) throws FoundException, EndException {
			if (newConfig.getUnusedShapes().size() < record) {
				record = newConfig.getUnusedShapes().size();
			}
			Node child = new Node(n, newConfig);
			caller.traverse(child, this);
		}
	}
	
	private void descend(Node n) throws FoundException {
		Configuration currentConfig = n.config;
		
		Shape[] set = new Shape[currentConfig.getUnusedShapes().size()];
		set = currentConfig.getUnusedShapes().toArray(set);
	    Shape[] subset = new Shape[this.puzzle.getShapeCount() - this.puzzle.getMinShapeFit()];
	    if (n.parent == null) {
	    	topLevelRecurse(set, subset, 0, 0, currentConfig);
	    } else {
	    	try {
				this.traverse(n, strategy);
			} catch (EndException e) {
				// Can safely ignore, will not generate under DFSStrategy
			}
	    }
	}
	
	/**
	 * Recursively finds all n choose k subsets of the set of unused shapes
	 * and removes those shapes from the set of unused shapes before running
	 * the usual DFS traversal of the children nodes. Here n is the number of
	 * "extra" shapes that are left unused when the minimum number of shapes 
	 * have been used to solve the puzzle.
	 * @param set The original set of shapes.
	 * @param subset The current working subset of shapes.
	 * @param subsetSize The size of the working subset.
	 * @param nextIndex The next index in the original set.
	 * @param rootConfig The root configuration with the original set of shapes.
	 * @throws FoundException Thrown when a solution is found.
	 */
	private void topLevelRecurse(Shape[] set, Shape[] subset, int subsetSize, int nextIndex, 
			Configuration rootConfig) throws FoundException {
	    if (subsetSize == subset.length) {
	    	Configuration currentConfig = new Configuration(rootConfig);
	    	for (int i = 0; i < subset.length; i++) {
	    		currentConfig.removeShape(subset[i]);
	    	}
			try {
				this.traverse(new Node(root, currentConfig), strategy);
			} catch (EndException e) {
				// Can safely ignore, will not generate under DFSStrategy
			}
	    } else {
	        for (int j = nextIndex; j < set.length; j++) {
	            subset[subsetSize] = set[j];
	            topLevelRecurse(set, subset, subsetSize + 1, j + 1, rootConfig);
	        }
	    }
	}
}

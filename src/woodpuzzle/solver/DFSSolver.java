package woodpuzzle.solver;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

public class DFSSolver extends AbstractSolver {
	protected long count = 0;
	protected long rejects = 0;
	protected int record = Integer.MAX_VALUE;
	private final ConfigurationTreeNode root;

	public DFSSolver(Puzzle puzzle) {
		super(puzzle, null);
		this.strategy = new DFSStrategy(this);
		this.root = new ConfigurationTreeNode(null);
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

	private void descend(ConfigurationTreeNode n) throws FoundException {
		Configuration currentConfig = n.config;
		
		Shape[] set = new Shape[currentConfig.getUnusedShapes().size()];
		set = currentConfig.getUnusedShapes().toArray(set);
	    Shape[] subset = new Shape[this.puzzle.getShapeCount() - this.puzzle.getMinShapesFill()];
	    if (n.parent == null) {
	    	topLevelRecurse(set, subset, 0, 0, currentConfig);
	    } else {
	    	try {
				this.traverse(n);
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
				this.traverse(new ConfigurationTreeNode(root, currentConfig));
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

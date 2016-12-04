package woodpuzzle.solver;

import java.util.ArrayList;
import java.util.List;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

public class BFSSolver extends AbstractSolver {
	
	private final BFSNode root;

	public BFSSolver(Puzzle p) {
		super(p, new BFSStrategy());
		this.root = new BFSNode(null);
	}

	@Override
	public Configuration findSolution() {
		this.root.config = new Configuration(this.puzzle);
		try {
			BFSNode n = root;
			this.descend(n);
			while (true) {
				List<BFSNode> prune = new ArrayList<BFSNode>();
				for (BFSNode c : n.children) {
					if (c.children.isEmpty()) {
						this.descend(c);
						if (c.children.isEmpty()) {
							prune.add(c);
						}
					}
					n.invalid += c.invalid;
					n.valid += c.valid;
				}
				n.invalid += prune.size();
				n.valid -= prune.size();
				for (ConfigurationTreeNode c : prune) {
					n.children.remove(c);
				}
				if (n.children.isEmpty()) {
					BFSNode c = n;
					n = (BFSNode) c.parent;
					n.invalid += c.invalid + 1;
					n.valid--;
					n.children.remove(c);
					continue;
				}
				BFSNode bestConfig = n.children.get(0);
				double bestScore = (double)(bestConfig.valid) / (double)(bestConfig.invalid + bestConfig.valid);
				double worstScore = bestScore;
				for (BFSNode c : n.children) {
					double score = (double)(c.valid) / (double)(c.invalid + c.valid);
					if (score > bestScore) {
						bestConfig = c;
						bestScore = score;
					}
					if (score < worstScore) worstScore = score;
				}
				n = bestConfig;
			}
		} catch (FoundException ex) {
			return ex.config;
		}
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
	 * the usual BFS traversal of the children nodes. Here n is the number of
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
	    	BFSNode child = new BFSNode(root, currentConfig);
	    	root.addChild(child);
	    	for (int i = 0; i < subset.length; i++) {
	    		currentConfig.removeShape(subset[i]);
	    	}
			try {
				this.traverse(child);
			} catch (EndException e) {
				// Can safely ignore, will not generate under BFSStrategy
			}
	    } else {
	        for (int j = nextIndex; j < set.length; j++) {
	            subset[subsetSize] = set[j];
	            topLevelRecurse(set, subset, subsetSize + 1, j + 1, rootConfig);
	        }
	    }
	}
}

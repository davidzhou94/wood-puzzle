package woodpuzzle.solver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

/**
 * Multi-threaded DFS traversal with a better heuristic than the DFSSolver.
 * Specifically, this solver will submit to a thread-pool a task to execute
 * a DFS search of a top-level node up to a configured limit of dead ends. 
 * If a solution is not found before the dead end limit, the thread halts
 * allowing another thread to attempt a search on a different top-level child
 * node.
 * @author david
 *
 */
public class HaltingDFSSolver extends AbstractSolver {
	private static final int SOLVER_PARALLELISM = 24;
	private final ConfigurationTreeNode root = new ConfigurationTreeNode(null);
	private final ExecutorService executor = Executors.newFixedThreadPool(SOLVER_PARALLELISM);
	private Configuration solution = null;

	/**
	 * Creates a HaltingDFSSolver.
	 * @param p The puzzle to solve.
	 */
	public HaltingDFSSolver(Puzzle p) {
		super(p, null);
		this.strategy = new HaltingDFSTopLevelStrategy(this);
	}

	/**
	 * Finds the solution.
	 * @return The first solution configuration found.
	 */
	@Override
	public Configuration findSolution() {
		this.root.config = new Configuration(this.puzzle);
		try {
			this.traverseTopLevel(root);
		} catch (Exception e) {
			System.out.println("Exception encountered while traversing top level: ");
			e.printStackTrace();
			return null;
		}
		
		while(solution == null) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				System.out.println("Interrupted while waiting for a solution...");
			}
		}
		
		executor.shutdownNow();
		
		return solution;
	}
	
	/**
	 * Carries out a "top-level" descent from the given root node that
	 * samples via DFS the sub-tree formed by each valid child of the
	 * given root node.
	 * @param root The root node to descend from.
	 * @throws Exception 
	 */
	private void traverseTopLevel(ConfigurationTreeNode root) throws Exception {
		Shape[] set = new Shape[this.puzzle.getShapeCount()];
		set = this.puzzle.getShapes().toArray(set);
	    Shape[] subset = new Shape[this.puzzle.getShapeCount() - this.puzzle.getMinShapesFill()];
	    if (root.parent != null) {
	    	throw new Exception("Not at the root configuration in top level traversal");
	    }
	    
	    topLevelRecurse(set, subset, 0, 0, root.config);
	    
		System.out.println("Top level traversal complete");
	}
	
	/**
	 * Recursively finds all minShapesFill choose shapeCount subsets of the 
	 * set of shapes in the puzzle and removes those shapes from the set of 
	 * unused shapes before running the halting DFS traversal of the children 
	 * nodes. minShapeFill is the smallest number of shapes needed to complete 
	 * the puzzle. In practice, since we know exactly one shape must be excluded 
	 * from any solution to the prime puzzle, this function will call itself 
	 * only once per non-recursive invocation.
	 * @param set The original set of shapes.
	 * @param subset The current working subset of shapes.
	 * @param subsetSize The size of the working subset.
	 * @param nextIndex The next index in the original set.
	 * @param rootConfig The root configuration with the original set of shapes.
	 * @param children The children of the rootConfig node.
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
				// Can safely ignore, will not generate under HaltingDFS
			}
	    } else {
	        for (int j = nextIndex; j < set.length; j++) {
	            subset[subsetSize] = set[j];
	            topLevelRecurse(set, subset, subsetSize + 1, j + 1, rootConfig);
	        }
	    }
	}
	
	void reportSolution(Configuration c) {
		this.solution = c;
	}
	
	void submitThreadForExecution(Runnable t) {
		this.executor.submit(t);
	}
}

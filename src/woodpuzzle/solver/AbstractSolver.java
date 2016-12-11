package woodpuzzle.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

/**
 * All solver algorithms should inherit from this class.
 * @author david
 *
 */
public abstract class AbstractSolver {
	protected final Puzzle puzzle;
	protected Set<Configuration> rootConfigs = null;

	/**
	 * Base constructor.
	 * @param p The puzzle to use with this solver instance.
	 */
	public AbstractSolver(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	
	/**
	 * Measures the variant time of the solver algorithm, attempts to find a solution
	 * and prints out the solution if it is found.
	 */
	public void solvePuzzle() {
		long begin = System.currentTimeMillis();
		Configuration sol = this.findSolution();
		long elapsed = System.currentTimeMillis() - begin;
		long second = (elapsed / 1000) % 60;
		long minute = (elapsed / (1000 * 60)) % 60;
		long hour = (elapsed / (1000 * 60 * 60)) % 24;
		elapsed %= 1000;

		String time = String.format("%02d:%02d:%02d:%d", hour, minute, second, elapsed);
		System.out.println("\nTime elapsed: " + time);
		this.printSolution(sol);
	}
	
	/**
	 * The specific implementation for finding the solution.
	 * @return The configuration of the solution.
	 */
	public abstract Configuration findSolution();

	/**
	 * Prints out the configuration.
	 * @param c The configuration to print.
	 */
	public void printSolution(Configuration c) {
		if (c == null) {
			System.out.println("\nNo solution found");
			return;
		}
		System.out.println("\nSolution found:\n");
		Shape[] cells = c.getCells();
		Map<Shape, Character> m = new HashMap<Shape, Character>();
		m.put(null, '0');
		char cur = 'A';
		for (int y = 0; y < c.getPuzzle().getHeight(); y++) {
			for (int x = 0; x < c.getPuzzle().getWidth(); x++) {
				for (int z = 0; z < c.getPuzzle().getLength(); z++) {
					Shape s = cells[c.getPuzzle().hashCoordinate(x, y, z)];
					if (!m.containsKey(s)) {
						m.put(s, cur);
						cur++;
					}
					System.out.print(m.get(s));
					System.out.print(" ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	/**
	 * Generates the root configurations (e.g. one configuration per each combination
	 * of shapes to use / removed).
	 * @param root The root configuration to copy from
	 * @throws Exception 
	 */
	protected Set<Configuration> generateRootConfigs(Configuration root) {
		if (this.rootConfigs != null) {
			return this.rootConfigs;
		}
		this.rootConfigs = new HashSet<Configuration>();
		Shape[] toRemove = new Shape[this.puzzle.getShapeCount() - this.puzzle.getMinShapesFill()];
		Shape[] allShapesArray = new Shape[this.puzzle.getShapeCount()];
		List<Shape> allShapes = new ArrayList<Shape>(this.puzzle.getShapes());
		Collections.shuffle(allShapes);
	    topLevelRecurse(allShapes.toArray(allShapesArray), toRemove, 0, 0, root);
	    System.out.println("Top level traversal complete");
	    return this.rootConfigs;
	}
	
	/**
	 * Recursively finds all n choose k subsets of the 
	 * set of shapes in the puzzle and removes those shapes from the set of 
	 * unused shapes before running the halting DFS traversal of the children 
	 * nodes. minShapeFill is the smallest number of shapes needed to complete 
	 * the puzzle. In practice, since we know exactly one shape must be excluded 
	 * from any solution to the prime puzzle, this function will call itself 
	 * only once per non-recursive invocation.
	 * @param allShapes The original set of shapes.
	 * @param toRemove The current working subset of shapes.
	 * @param toRemoveNextIndex The size of the working subset.
	 * @param allShapesNextIndex The next index in the original set.
	 * @param rootConfig The root configuration with the original set of shapes.
	 * @param children The children of the rootConfig node.
	 * @throws FoundException Thrown when a solution is found.
	 */
	private void topLevelRecurse(Shape[] allShapes, Shape[] toRemove, int toRemoveNextIndex, int allShapesNextIndex, 
			Configuration root) {
	    if (toRemoveNextIndex == toRemove.length) {
	    	// base case, determined shapes to remove on this iteration
	    	Configuration newConfig = new Configuration(root);
	    	for (int i = 0; i < toRemove.length; i++) {
	    		newConfig.removeShape(toRemove[i]);
	    	}
			this.rootConfigs.add(newConfig);
	    } else {
	    	// recursive case
	    	if (allShapesNextIndex == allShapes.length - 2) {
	    		if (toRemoveNextIndex != toRemove.length - 1) {
	    			return;
	    		}
	            Shape[] newSubset = toRemove.clone();
	            newSubset[toRemoveNextIndex] = allShapes[allShapes.length - 1];
	    		topLevelRecurse(allShapes, toRemove, toRemove.length, allShapes.length, root);
	    	}
	        for (int j = allShapesNextIndex; j < allShapes.length; j++) {
	            Shape[] newSubset = toRemove.clone();
	            newSubset[toRemoveNextIndex] = allShapes[j];
	            topLevelRecurse(allShapes, toRemove, toRemoveNextIndex + 1, j + 1, root);
	        }
	    }
	}
}
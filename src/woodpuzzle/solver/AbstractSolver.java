package woodpuzzle.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Coordinate;
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
	protected Strategy strategy;

	/**
	 * Base constructor.
	 * @param p The puzzle to use with this solver instance.
	 */
	protected AbstractSolver(Puzzle puzzle, Strategy strategy) {
		this.puzzle = puzzle;
		this.strategy = strategy;
	}
	
	/**
	 * Measures the variant time of the solver algorithm, attempts to find a solutions
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
	
	// Utility methods:
	
	/**
	 * Traverses the potential children configurations of the configuration
	 * at the given node according to the given strategy. Flow is controlled
	 * by throwing an exception to indicate whether the traversal has found
	 * a solution or whether it is terminating early due to an indication
	 * in the strategy.
	 * @param n The parent node.
	 * @param strategy The traversal strategy.
	 * @throws FoundException Thrown when a solution is found.
	 * @throws EndException Throw when the strategy terminates the traversal 
	 * before a solution is found.
	 */
	protected final void traverse(ConfigurationTreeNode n) throws FoundException, EndException {
		Configuration currentConfig = n.config;

		strategy.preTraversal(currentConfig);

		Shape s = strategy.determineShape(currentConfig);

		int sideLength = s.getSideLength();
		for(int x = 0; x < this.puzzle.getWidth() - 1; x++) {
			for(int z = 0; z < this.puzzle.getLength() - 1; z++) {
				List<Coordinate> placement;
				for (int yaxis = 0; yaxis <= 3; yaxis++) {
					for (int zaxis = 0; zaxis <= 3; zaxis++) {
						Configuration newConfig = new Configuration(currentConfig);
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
						
						if (!newConfig.placeShape(s, placement)) {
							strategy.placementFailedGeometry(n);
							continue;
						}
						if (newConfig.getUnusedShapes().isEmpty()) throw new FoundException(newConfig);
						if (hasDeadCells(newConfig)) {
							strategy.placementFailedDeadCells(n);
							continue;
						}
						
						strategy.placementSucceeded(newConfig, n);
					}
				}
			}
		}
	}
	
	/**
	 * Carries out a "top-level" descent from the given root node that
	 * samples via DFS the sub-tree formed by each valid child of the
	 * given root node.
	 * @param root The root node to descend from.
	 * @throws Exception 
	 */
	protected Set<Configuration> traverseTopLevel(Configuration root) {
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
	 * @param toRemoveSize The size of the working subset.
	 * @param nextIndex The next index in the original set.
	 * @param rootConfig The root configuration with the original set of shapes.
	 * @param children The children of the rootConfig node.
	 * @throws FoundException Thrown when a solution is found.
	 */
	private void topLevelRecurse(Shape[] allShapes, Shape[] toRemove, int toRemoveSize, int nextIndex, 
			Configuration root) {
	    if (toRemoveSize == toRemove.length) {
	    	// base case, determined shapes to remove on this iteration
	    	Configuration newConfig = new Configuration(root);
	    	for (int i = 0; i < toRemove.length; i++) {
	    		newConfig.removeShape(toRemove[i]);
	    	}
			this.rootConfigs.add(newConfig);
	    } else {
	    	// recursive case
	    	if (nextIndex == allShapes.length - 2) {
	            Shape[] newSubset = toRemove.clone();
	            newSubset[toRemoveSize] = allShapes[allShapes.length - 1];
	    		topLevelRecurse(allShapes, toRemove, toRemoveSize + 1, allShapes.length, root);
	    	}
	        for (int j = nextIndex; j < allShapes.length; j++) {
	            Shape[] newSubset = toRemove.clone();
	            newSubset[toRemoveSize] = allShapes[j];
	            topLevelRecurse(allShapes, toRemove, toRemoveSize + 1, j + 1, root);
	        }
	    }
	}
	
	/**
	 * Checks whether a configuration has isolated cells. That is,
	 * if a group of empty and connected cells is smaller than the
	 * given minimum shape size, then it is isolated. Furthermore,
	 * if all shapes are of identical size then a similar group with
	 * the number of empty cells not a multiple of the shape size is
	 * also considered isolated.
	 * @param config The configuration to check.
	 * @return true if there are isolated cells, otherwise false.
	 */
	public boolean hasDeadCells(Configuration config) {
		boolean visited[] = new boolean[this.puzzle.getTotalCells()];
		Shape cells[] = config.getCells();
		for (int i = 0; i < this.puzzle.getTotalCells(); i++) visited[i] = false;
		for (int x = 0; x < this.puzzle.getWidth(); x++) {
			for (int y = 0; y < this.puzzle.getHeight(); y++) {
				for (int z = 0; z < this.puzzle.getLength(); z++) {
					int pos = this.puzzle.hashCoordinate(x, y, z);
					if (visited[pos]) continue;
					visited[pos] = true;
					if (cells[pos] != null) continue;
					int emptyCount = 1;
					Queue<Coordinate> checkNeighbours = new LinkedList<Coordinate>();
					checkNeighbours.add(new Coordinate(x, y, z));
					while (!checkNeighbours.isEmpty()) {
						Coordinate c = checkNeighbours.poll();
						if (this.puzzle.isValidCoordinate(c.x + 1, c.y, c.z)) {
							int adj = this.puzzle.hashCoordinate(c.x+1, c.y, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(1, 0, 0));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x - 1, c.y, c.z)) {
							int adj = this.puzzle.hashCoordinate(c.x-1, c.y, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(-1, 0, 0));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x, c.y+1, c.z)) {
							int adj = this.puzzle.hashCoordinate(c.x, c.y+1, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 1, 0));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x, c.y-1, c.z)) {
							int adj = this.puzzle.hashCoordinate(c.x+1, c.y-1, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, -1, 0));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x, c.y, c.z+1)) {
							int adj = this.puzzle.hashCoordinate(c.x, c.y, c.z+1);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 0, 1));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x, c.y, c.z-1)) {
							int adj = this.puzzle.hashCoordinate(c.x, c.y, c.z-1);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 0, -1));
								}
								visited[adj] = true;
							}
						}
					}
					if (emptyCount < this.puzzle.getMinShapeSize()) return true;
					if (this.puzzle.getMinShapeSize() == this.puzzle.getMaxShapeSize() &&
							emptyCount % this.puzzle.getMaxShapeSize() != 0) return true;
				}
			}
		}
		return false;
	}
}
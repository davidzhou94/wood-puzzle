package woodpuzzle.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

	protected Puzzle puzzle;

	/**
	 * Base constructor.
	 * @param p The puzzle to use with this solver instance.
	 */
	protected AbstractSolver(Puzzle p) {
		this.puzzle = p;
	}
	
	/**
	 * Measures the variant time of the solver algorithm, attempts to find a solutions
	 * and prints out the solution if it is found.
	 */
	public void solve() {
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
	 * @param ts The traversal strategy.
	 * @throws FoundException Thrown when a solution is found.
	 * @throws EndException Throw when the strategy terminates the travesal 
	 * before a solution is found.
	 */
	protected final void traverse(Node n, Strategy ts) throws FoundException, EndException {
		Configuration currentConfig = n.config;

		ts.preTraversal(currentConfig);

		Shape s = ts.determineShape(currentConfig);

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
							ts.placementFailedGeometry(n);
							continue;
						}
						if (newConfig.getUnusedShapes().isEmpty()) throw new FoundException(newConfig);
						if (hasDeadCells(newConfig)) {
							ts.placementFailedDeadCells(n);
							continue;
						}
						
						ts.placementSucceeded(newConfig, n);
					}
				}
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

/**
 * Used with AbstractSolver.traverse(...) to indicate the actions
 * that should be taken at each part of the traversal algorithm.
 * @author david
 *
 */
interface Strategy {
	void preTraversal(Configuration c) throws EndException;
	Shape determineShape(Configuration c);
	void placementFailedGeometry(Node n);
	void placementFailedDeadCells(Node n);
	void placementSucceeded(Configuration newConfig, Node n) throws FoundException, EndException;
}

/**
 * A configuration Node.
 * @author david
 *
 */
class Node {
	public Node parent;
	public Configuration config;
	public Node(Node n) {
		this.parent = n;
	}

	public Node(Node n, Configuration c) {
		this.parent = n;
		this.config = c;
	}
}

/** 
 * The exception thrown to terminate early.
 * @author david
 *
 */
class EndException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6116331690440982576L;
}

/**
 * The exception thrown when a solution is found.
 * @author david
 *
 */
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

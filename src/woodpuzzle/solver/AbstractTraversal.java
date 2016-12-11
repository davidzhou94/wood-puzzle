package woodpuzzle.solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import woodpuzzle.model.Configuration;
import woodpuzzle.model.Coordinate;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

/**
 * All traversal algorithms should inherit from this class.
 * @author david
 *
 */
public abstract class AbstractTraversal {
	private final Puzzle puzzle;
	
	/**
	 * Base constructor.
	 * @param p The puzzle to use with this solver instance.
	 */
	protected AbstractTraversal(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	
	// Abstract methods:
	
	abstract void preTraversal(Configuration c) throws EndException;
	abstract void postTraversal(Configuration c) throws EndException;
	abstract Shape determineShape(Configuration c);
	abstract void placementFailedGeometry(ConfigurationTreeNode n);
	abstract void placementFailedDeadCells(ConfigurationTreeNode n);
	abstract void placementSucceeded(Configuration newConfig, ConfigurationTreeNode n) throws FoundException, EndException;
	
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

		this.preTraversal(currentConfig);

		Shape s = this.determineShape(currentConfig);

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
							this.placementFailedGeometry(n);
							continue;
						}
						if (newConfig.getUnusedShapes().isEmpty()) throw new FoundException(newConfig);
						if (hasDeadCells(newConfig)) {
							this.placementFailedDeadCells(n);
							continue;
						}
						
						this.placementSucceeded(newConfig, n);
					}
				}
			}
		}
		
		this.postTraversal(currentConfig);
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
	private boolean hasDeadCells(Configuration config) {
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
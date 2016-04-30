package woodPuzzle.engine;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import woodPuzzle.model.Coordinate;
import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public class BFSSolver extends AbstractSolver {
	
	Node root;

	public BFSSolver() {
		root = new Node();
	}

	@Override
	public Puzzle findSolution(Puzzle p) {
		BigInteger count = BigInteger.ZERO;
		//long count = 0;
		Queue<Node> order = new LinkedList<Node>();
		root.config = p;
		try {
			order.add(root);
			while (!order.isEmpty()) {
				count.add(BigInteger.ONE);
				if (count.mod(BigInteger.valueOf(1000)).compareTo(BigInteger.ZERO) == 0) {
					System.out.println("\rChecked " + count.toString() + " cominations");
				}
				//count++;
				//System.out.println("Checked " + count + " cominations");
				Node n = order.poll();
				this.descend(n);
				order.addAll(n.children);
			}
		} catch (FoundException ex) {
			return ex.config;
		}
		return null;
	}
	
	private void descend(Node n) throws FoundException {
		Puzzle currentConfig = n.config;
		Puzzle newConfig = new Puzzle(currentConfig);
		for (Shape s : currentConfig.getUnusedShapes()) {
			System.out.println("Checking a new shape");
			int sideLength = s.getSideLength();
			for(int x = 0; x < currentConfig.getWidth() - 2; x++) {
				for(int z = 0; z < currentConfig.getLength() - 2; z++) {
					//System.out.println("Current position (x, z) = (" + x + ", " + z + ")");
					List<Coordinate> placement;
					for (int yaxis = 0; yaxis <= 3; yaxis++) {
						for (int zaxis = 0; zaxis <= 3; zaxis++) {
							//System.out.println("Rotating current shape at current position");
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
								if (!hasIsolatedCells(newConfig, 5)) {
									if (newConfig.getUnusedShapes().isEmpty()) {
										throw new FoundException(newConfig);
									}
									n.addChild(new Node(newConfig));
								} else {
									newConfig = new Puzzle(currentConfig);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public boolean hasIsolatedCells(Puzzle p, int validGroupSize) {
		boolean visited[] = new boolean[p.getTotalCells()];
		Shape cells[] = p.getFilledCells();
		for (int i = 0; i < p.getTotalCells(); i++) visited[i] = false;
		for (int x = 0; x < p.getWidth(); x++) {
			for (int y = 0; y < p.getHeight(); y++) {
				for (int z = 0; z < p.getLength(); z++) {
					int pos = p.hashCoordinate(x, y, z);
					if (visited[pos]) continue;
					visited[pos] = true;
					if (cells[pos] != null) continue;
					int emptyCount = 1;
					Queue<Coordinate> checkNeighbours = new LinkedList<Coordinate>();
					checkNeighbours.add(new Coordinate(x, y, z));
					while (!checkNeighbours.isEmpty()) {
						Coordinate c = checkNeighbours.poll();
						if (p.isValidCoordinate(c.x + 1, c.y, c.z)) {
							int adj = p.hashCoordinate(c.x+1, c.y, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(1, 0, 0));
								}
								visited[adj] = true;
							}
						}
						if (p.isValidCoordinate(c.x - 1, c.y, c.z)) {
							int adj = p.hashCoordinate(c.x-1, c.y, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(-1, 0, 0));
								}
								visited[adj] = true;
							}
						}
						if (p.isValidCoordinate(c.x, c.y+1, c.z)) {
							int adj = p.hashCoordinate(c.x, c.y+1, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 1, 0));
								}
								visited[adj] = true;
							}
						}
						if (p.isValidCoordinate(c.x, c.y-1, c.z)) {
							int adj = p.hashCoordinate(c.x+1, c.y-1, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, -1, 0));
								}
								visited[adj] = true;
							}
						}
						if (p.isValidCoordinate(c.x, c.y, c.z+1)) {
							int adj = p.hashCoordinate(c.x, c.y, c.z+1);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 0, 1));
								}
								visited[adj] = true;
							}
						}
						if (p.isValidCoordinate(c.x, c.y, c.z-1)) {
							int adj = p.hashCoordinate(c.x, c.y, c.z-1);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 0, -1));
								}
								visited[adj] = true;
							}
						}
					}
					if (emptyCount < validGroupSize) return true;
				}
			}
		}
		return false;
	}

	class Node {
		public List<Node> children;
		public Puzzle config;
		public Node() {
			this.children = new ArrayList<Node>();
		}
		
		public Node(Puzzle p) {
			this.children = new ArrayList<Node>();
			this.config = p;
		}
		
		public void addChild(Node n) {
			this.children.add(n);
		}
	}
	
	class FoundException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5453975780718671130L;
		public Puzzle config;
		public FoundException(Puzzle config) {
			this.config = config;
		}
	}
}

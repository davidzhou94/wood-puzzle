package woodPuzzle.engine;

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
		root = new Node(null);
	}

	@Override
	public Puzzle findSolution(Puzzle p) {
		root.config = p;
		try {
			Node n = root;
			this.descend(n);
			while (true) {
				List<Node> prune = new ArrayList<Node>();
				for (Node c : n.children) {
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
				for (Node c : prune) {
					n.children.remove(c);
				}
				if (n.children.isEmpty()) {
					Node c = n;
					n = c.parent;
					n.invalid += c.invalid + 1;
					n.valid--;
					n.children.remove(c);
					System.out.println("I have reached a dead end");
					continue;
				}
				Node bestConfig = n.children.get(0);
				double bestScore = (double)(bestConfig.valid) / (double)(bestConfig.invalid + bestConfig.valid);
				double worstScore = bestScore;
				for (Node c : n.children) {
					double score = (double)(c.valid) / (double)(c.invalid + c.valid);
					if (score > bestScore) {
						bestConfig = c;
						bestScore = score;
					}
					if (score < worstScore) worstScore = score;
				}
				n = bestConfig;
				System.out.println("I chose a node with score " + bestScore + " the worst score was " + worstScore);
			}
		} catch (FoundException ex) {
			return ex.config;
		}
		//return null;
	}
	
	private void descend(Node n) throws FoundException {
		Puzzle currentConfig = n.config;
		Puzzle newConfig = new Puzzle(currentConfig);
		for (Shape s : currentConfig.getUnusedShapes()) {
			int sideLength = s.getSideLength();
			for(int x = 0; x < currentConfig.getWidth() - 1; x++) {
				for(int z = 0; z < currentConfig.getLength() - 1; z++) {
					List<Coordinate> placement;
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
								n.valid++;
								if (!hasIsolatedCells(newConfig, 5)) {
									if (newConfig.getUnusedShapes().isEmpty()) {
										throw new FoundException(newConfig);
									}
									n.addChild(new Node(newConfig, n));
								} 
								newConfig = new Puzzle(currentConfig);
							} else {
								n.invalid++;
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
					if (emptyCount % validGroupSize != 0) return true;
				}
			}
		}
		return false;
	}

	class Node {
		public long valid, invalid;
		public List<Node> children;
		public Puzzle config;
		public Node parent;
		public Node(Node parent) {
			valid = 0;
			invalid = 0;
			this.children = new ArrayList<Node>();
			this.parent = parent;
		}
		
		public Node(Puzzle p, Node parent) {
			valid = 0;
			invalid = 0;
			this.children = new ArrayList<Node>();
			this.config = p;
			this.parent = parent;
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

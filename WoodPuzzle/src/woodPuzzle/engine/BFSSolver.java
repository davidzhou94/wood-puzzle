package woodPuzzle.engine;

import java.util.ArrayList;
import java.util.List;

import woodPuzzle.model.Configuration;
import woodPuzzle.model.Coordinate;
import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public class BFSSolver extends AbstractSolver {
	
	private Node root;

	public BFSSolver(Puzzle p) {
		super(p);
		root = new Node(null);
	}

	@Override
	public Configuration findSolution() {
		this.root.config = new Configuration(this.puzzle);
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
		Configuration currentConfig = n.config;
		for (Shape s : currentConfig.getUnusedShapes()) {
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
								n.invalid++;
								continue;
							}
							if (newConfig.getUnusedShapes().isEmpty()) throw new FoundException(newConfig);
							if (hasIsolatedCells(newConfig)) {
								n.invalid++;
								continue;
							}
							
							n.addChild(new Node(n, newConfig));
						}
					}
				}
			}
		}
	}

	class Node {
		public long valid, invalid;
		public List<Node> children;
		public Configuration config;
		public Node parent;
		public Node(Node parent) {
			valid = 0;
			invalid = 0;
			this.children = new ArrayList<Node>();
			this.parent = parent;
		}
		
		public Node(Node parent, Configuration c) {
			valid = 0;
			invalid = 0;
			this.children = new ArrayList<Node>();
			this.config = c;
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
		public Configuration config;
		public FoundException(Configuration config) {
			this.config = config;
		}
	}
}

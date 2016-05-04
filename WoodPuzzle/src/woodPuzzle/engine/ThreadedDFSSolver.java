package woodPuzzle.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Comparator;

import woodPuzzle.model.Configuration;
import woodPuzzle.model.Coordinate;
import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public class ThreadedDFSSolver extends AbstractSolver {

	private Node root;
	private Random rng;

	public ThreadedDFSSolver(Puzzle p) {
		super(p);
		root = new Node(null);
		rng = new Random();
	}

	@Override
	public Configuration findSolution() {
		this.root.config = new Configuration(this.puzzle);
		try {
			this.topLevelDescend(root);
		} catch (FoundException ex) {
			return ex.config;
		}
		return null;
	}
	
	private void topLevelDescend(Node root) throws FoundException {
		Queue<Node> children = new PriorityQueue<Node>(10, new Comparator<Node>() {
			public int compare(Node n1, Node n2) {
				if (n1.recordLevel != n2.recordLevel) return n1.recordLevel - n2.recordLevel;
				if (n1.recordLevelCount != n2.recordLevelCount) return (int) (n2.recordLevelCount - n1.recordLevelCount);
				return (int) (n1.rejectCount - n2.rejectCount);
			}
		});
		
		Configuration rootConfig = root.config;
		
		for (Shape toDelete : rootConfig.getUnusedShapes()) {
			Configuration currentConfig = new Configuration(rootConfig);
			currentConfig.removeShape(toDelete);
			Shape s = (Shape) currentConfig.getUnusedShapes().toArray()[rng.nextInt(currentConfig.getUnusedShapes().size())]; 
			int sideLength = s.getSideLength();
			for(int x = 0; x < this.puzzle.getWidth() - 1; x++) {
				for(int z = 0; z < this.puzzle.getLength() - 1; z++) {
					List<Coordinate> placement;
					System.out.println("Coordinate (x,z) = (" + x + ", " + z + ")");
					for (int yaxis = 0; yaxis <= 3; yaxis++) {
						for (int zaxis = 0; zaxis <= 3; zaxis++) {
							System.out.println("  Rotation (y,z) axis = (" + yaxis + ", " + zaxis + ")");
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
							if (!newConfig.placeShape(s, placement)) continue;
							if (newConfig.getUnusedShapes().isEmpty()) throw new FoundException(newConfig);
							if (hasIsolatedCells(newConfig)) continue;
							
							Node child = new Node(root, newConfig);
							DescendThread dt = new DescendThread(child, puzzle);
							Thread t = new Thread(dt);
							t.start();
							try {
								// doing one thread at a time but this could be more.
								t.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (dt.solution == null) {
								child.recordLevel = dt.recordLevel;
								child.recordLevelCount = dt.recordLevelCount;
								child.rejectCount = dt.rejectCount;
								children.add(child);
								System.out.println("    Added child with record level " + dt.recordLevel + " with " + dt.recordLevelCount + " at record level, rejected " + dt.rejectCount);
							} else {
								throw new FoundException(dt.solution);
							}
						}
					}
				}
			}
		}
		
		System.out.println("Top level complete");
		while(!children.isEmpty()) {
			Node child = children.poll();
			System.out.println("Choosing child with record level " + child.recordLevel + " with " + child.recordLevelCount + " at record level, rejected " + child.rejectCount);
			this.descend(child);
		}
	}

	private void descend(Node n) throws FoundException {
		Queue<Node> children = new PriorityQueue<Node>(10, new Comparator<Node>() {
			public int compare(Node n1, Node n2) {
				if (n1.recordLevel != n2.recordLevel) return n1.recordLevel - n2.recordLevel;
				if (n1.recordLevelCount != n2.recordLevelCount) return (int) (n2.recordLevelCount - n1.recordLevelCount);
				return (int) (n1.rejectCount - n2.rejectCount);
			}
		});
		Configuration currentConfig = n.config;
		// you don't actually need to iterate through the shapes, in theory every shape has at 
		// least one valid placement so it suffices to pick any shape and try each of the possible positions 
		// for that shape in the current configuration.
		Shape s = (Shape) currentConfig.getUnusedShapes().toArray()[rng.nextInt(currentConfig.getUnusedShapes().size())];

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
						if (!newConfig.placeShape(s, placement)) continue;
						if (newConfig.getUnusedShapes().isEmpty()) throw new FoundException(newConfig);
						if (hasIsolatedCells(newConfig)) continue;
						
						Node child = new Node(n, newConfig);
						DescendThread dt = new DescendThread(child, puzzle);
						Thread t = new Thread(dt);
						t.start();
						try {
							// doing one thread at a time but this could be more.
							t.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (dt.solution == null) {
							child.recordLevel = dt.recordLevel;
							child.recordLevelCount = dt.recordLevelCount;
							child.rejectCount = dt.rejectCount;
							children.add(child);
						} else {
							throw new FoundException(dt.solution);
						}
					}
				}
			}
		}
		
		while(!children.isEmpty()) {
			Node child = children.poll();
			this.descend(child);
		}
	}
	
	class DescendThread implements Runnable {
		long rejectCount = 0, recordLevelCount = 0;
		int recordLevel = Integer.MAX_VALUE;
		
		static final int REJECT_LIMIT = 10000000;
		
		Node root;
		Puzzle puzzle;
		Configuration solution;
		Random rng;
		
		DescendThread(Node n, Puzzle p) {
			this.root = n;
			this.puzzle = p;
			rng = new Random();
		}
		
		private void descend(Node n) throws FoundException, EndException {
			Configuration currentConfig = n.config;
			int currentRemaining = currentConfig.getUnusedShapes().size();
			if (currentRemaining < recordLevel) {
				recordLevel = currentRemaining;
				recordLevelCount = 0; 
			}
			if (currentRemaining == recordLevel) recordLevelCount++;
			if (rejectCount > REJECT_LIMIT) throw new EndException();
			Configuration newConfig = new Configuration(currentConfig);
			// randomly pick a shape
			Shape s = (Shape) currentConfig.getUnusedShapes().toArray()[rng.nextInt(currentRemaining)];

			int sideLength = s.getSideLength();
			for(int x = 0; x < this.puzzle.getWidth() - 1; x++) {
				for(int z = 0; z < this.puzzle.getLength() - 1; z++) {
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
								if (!hasIsolatedCells(newConfig)) {
									if (newConfig.getUnusedShapes().isEmpty()) {
										throw new FoundException(newConfig);
									}
									this.descend(new Node(n, newConfig));
								}  else {
									rejectCount++;
								}
								newConfig = new Configuration(currentConfig);
							} else {
								rejectCount++;
							}
						}
					}
				}
			}
		}

		@Override
		public void run() {
			try {
				this.descend(root);
			} catch (FoundException e) {
				this.solution = e.config;
			} catch (EndException e) {
				this.solution = null;
			}
		}
		
	}

	class Node {
		Node parent;
		Configuration config;
		int recordLevel = Integer.MAX_VALUE;
		long recordLevelCount = 0;
		long rejectCount = 0;
		
		Node(Node n) {
			this.parent = n;
		}

		Node(Node n, Configuration c) {
			this.parent = n;
			this.config = c;
		}
	}
	
	class EndException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6116331690440982576L;
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

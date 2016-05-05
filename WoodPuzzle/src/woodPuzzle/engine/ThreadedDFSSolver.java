package woodPuzzle.engine;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Comparator;

import woodPuzzle.model.Configuration;
import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

/**
 * A Threaded DFS traversal with a better heuristic than the DFSSolver.
 * Specifically, this solver will "sample" each configuration that is a
 * child of the root configuration up to a certain limit on the number of
 * rejections it encounters. If a solution is not found before the reject
 * limit, it moves on to the next child configuration from the root. If
 * a solution is not found after sampling all the possible child 
 * configurations, it will pick the configuration that descended the deepest
 * with the greatest number of leaf nodes found at that level before hitting
 * the reject limit.
 * @author david
 *
 */
public class ThreadedDFSSolver extends AbstractSolver {

	static final int REJECT_LIMIT = 1000000;
	private ThreadedDFSNode root;
	private Random rng;

	/**
	 * Creates a ThreadedDFSSolver.
	 * @param p The puzzle to solve.
	 */
	public ThreadedDFSSolver(Puzzle p) {
		super(p);
		this.root = new ThreadedDFSNode(null);
		this.rng = new Random();
	}

	/**
	 * Finds the solution.
	 * @return The first solution configuration found.
	 */
	@Override
	public Configuration findSolution() {
		this.root.config = new Configuration(this.puzzle);
		try {
			this.descend(root);
		} catch (FoundException ex) {
			return ex.config;
		}
		return null;
	}
	
	/**
	 * The strategy for "top-level" traversals of a node configuration 
	 * tree. This is the traversal that takes samples for all children
	 * of a specific node and recursing based on the child node with
	 * the deepest and most leaf nodes found in the sampling phase.
	 * @author david
	 *
	 */
	class ThreadedDFSStrategy implements Strategy {
		Queue<ThreadedDFSNode> children;
		ThreadedDFSSolver caller;
		ThreadedDFSStrategy(ThreadedDFSSolver caller, Queue<ThreadedDFSNode> children) {
			this.caller = caller;
			this.children = children;
		}

		@Override
		public void preTraversal(Configuration c) throws EndException {
			// do nothing
		}

		@Override
		public Shape determineShape(Configuration c) {
			return (Shape) c.getUnusedShapes().toArray()[rng.nextInt(c.getUnusedShapes().size())];
		}

		@Override
		public void placeFailure() {
			// do nothing
		}

		@Override
		public void isolatedFailure() {
			// do nothing
		}

		@Override
		public void succeed(Configuration newConfig, Node n) throws FoundException, EndException {
			ThreadedDFSNode child = new ThreadedDFSNode(n, newConfig);
			DescendThread dt = new DescendThread(child, puzzle, caller);
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
	
	/**
	 * Carries out a "top-level" descent from the given root node that
	 * samples via DFS the sub-tree formed by each valid child of the
	 * given root node.
	 * @param root The root node to descend from.
	 * @throws FoundException Thrown when the solution is found.
	 */
	private void descend(ThreadedDFSNode root) throws FoundException {
		Queue<ThreadedDFSNode> children = new PriorityQueue<ThreadedDFSNode>(10, new Comparator<ThreadedDFSNode>() {
			public int compare(ThreadedDFSNode n1, ThreadedDFSNode n2) {
				if (n1.recordLevel != n2.recordLevel) return n1.recordLevel - n2.recordLevel;
				if (n1.recordLevelCount != n2.recordLevelCount) return (int) (n2.recordLevelCount - n1.recordLevelCount);
				return (int) (n1.rejectCount - n2.rejectCount);
			}
		});
		
		Configuration rootConfig = root.config;
		
		Shape[] set = new Shape[rootConfig.getUnusedShapes().size()];
		set = rootConfig.getUnusedShapes().toArray(set);
	    Shape[] subset = new Shape[this.puzzle.getShapeCount() - this.puzzle.getMinShapeFit()];
	    if (root.parent == null) {
	    	topLevelRecurse(set, subset, 0, 0, rootConfig, children);
	    } else {
	    	try {
				this.traverse(root, new ThreadedDFSStrategy(this, children));
			} catch (EndException e) {
				// Can safely ignore, will not generate under ThreadedDFSStrategy
			}
	    }
	    
		System.out.println("Top level complete");
		while(!children.isEmpty()) {
			ThreadedDFSNode child = children.poll();
			System.out.println("Choosing child with record level " + child.recordLevel + " with " + child.recordLevelCount + " at record level, rejected " + child.rejectCount);
			this.descend(child);
		}
	}
	
	/**
	 * Recursively finds all n choose k subsets of the set of unused shapes
	 * and removes those shapes from the set of unused shapes before running
	 * the usual sampling traversal of the children nodes. Here n is the 
	 * number of "extra" shapes that are left unused when the minimum number
	 * of shapes have been used to solve the puzzle.
	 * @param set The original set of shapes.
	 * @param subset The current working subset of shapes.
	 * @param subsetSize The size of the working subset.
	 * @param nextIndex The next index in the original set.
	 * @param rootConfig The root configuration with the original set of shapes.
	 * @param children The children of the rootConfig node.
	 * @throws FoundException Thrown when a solution is found.
	 */
	private void topLevelRecurse(Shape[] set, Shape[] subset, int subsetSize, int nextIndex, 
			Configuration rootConfig, Queue<ThreadedDFSNode> children) throws FoundException {
	    if (subsetSize == subset.length) {
	    	Configuration currentConfig = new Configuration(rootConfig);
	    	for (int i = 0; i < subset.length; i++) {
	    		currentConfig.removeShape(subset[i]);
	    	}
			try {
				this.traverse(new Node(root, currentConfig), new ThreadedDFSStrategy(this, children));
			} catch (EndException e) {
				// Can safely ignore, will not generate under ThreadedDFSStrategy
			}
	    } else {
	        for (int j = nextIndex; j < set.length; j++) {
	            subset[subsetSize] = set[j];
	            topLevelRecurse(set, subset, subsetSize + 1, j + 1, rootConfig, children);
	        }
	    }
	}

	/**
	 * A threaded implementation of DFS traversal through the possible
	 * configuration tree. This class is used only with the ThreadedDFSSolver.
	 * The thread terminates when either a solution is found or it has 
	 * reached the limit for the number of rejected configurations.
	 * @author david
	 *
	 */
	class DescendThread implements Runnable {
		long rejectCount = 0, recordLevelCount = 0;
		int recordLevel = Integer.MAX_VALUE;
		
		ThreadedDFSNode root;
		Puzzle puzzle;
		Configuration solution;
		Random rng;
		ThreadedDFSSolver caller;
		
		DescendThread(ThreadedDFSNode n, Puzzle p, ThreadedDFSSolver caller) {
			this.root = n;
			this.puzzle = p;
			this.caller = caller;
			rng = new Random();
		}
		
		class DescendThreadStrategy implements Strategy {
			long rejectCount = 0, recordLevelCount = 0;
			int recordLevel = Integer.MAX_VALUE;
			int currentRemaining;
			ThreadedDFSSolver caller;
			
			DescendThreadStrategy(ThreadedDFSSolver caller) {
				this.caller = caller;
			}
			
			@Override
			public void preTraversal(Configuration c) throws EndException {
				currentRemaining = c.getUnusedShapes().size();
				if (currentRemaining < recordLevel) {
					recordLevel = currentRemaining;
					recordLevelCount = 0; 
				}
				if (currentRemaining == recordLevel) recordLevelCount++;
				if (rejectCount > REJECT_LIMIT) throw new EndException();
			}

			@Override
			public Shape determineShape(Configuration c) {
				return (Shape) c.getUnusedShapes().toArray()[rng.nextInt(currentRemaining)];
			}

			@Override
			public void placeFailure() {
				rejectCount++;
			}

			@Override
			public void isolatedFailure() {
				rejectCount++;
			}

			@Override
			public void succeed(Configuration newConfig, Node n) throws FoundException, EndException {
				caller.traverse(new ThreadedDFSNode(n, newConfig), this);
			}
			
		}
		
		private void descend(ThreadedDFSNode n) throws FoundException, EndException {
			DescendThreadStrategy s = new DescendThreadStrategy(caller);
			caller.traverse(n, s);
			this.recordLevel = s.recordLevel;
			this.recordLevelCount = s.recordLevelCount;
			this.rejectCount = s.rejectCount;
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

	/**
	 * Like the super class Node except that this also
	 * stores metrics about the sampling of its children.
	 * @author david
	 *
	 */
	class ThreadedDFSNode extends Node {
		int recordLevel = Integer.MAX_VALUE;
		long recordLevelCount = 0;
		long rejectCount = 0;
		
		ThreadedDFSNode(Node n) {
			super(n);
		}

		ThreadedDFSNode(Node n, Configuration c) {
			super(n, c);
		}
	}
}

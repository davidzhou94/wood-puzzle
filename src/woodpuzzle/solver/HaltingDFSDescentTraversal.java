package woodpuzzle.solver;

import java.util.Random;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

class HaltingDFSDescentTraversal extends AbstractTraversal {
	private static final int DEAD_END_LIMIT = 1000000;
	
	private long deadEndCount = 0;
	private int minObservedShapesRemaining = Integer.MAX_VALUE;
	private int currentShapesRemaining;
	
	private final Random rng = new Random();
	private final HaltingDFSSolver solver;
	
	protected HaltingDFSDescentTraversal(Puzzle puzzle, HaltingDFSSolver solver) {
		super(puzzle);
		this.solver = solver;
	}
	
	@Override
	public void preTraversal(Configuration c) throws EndException {
		currentShapesRemaining = c.getUnusedShapes().size();
		if (currentShapesRemaining < minObservedShapesRemaining) {
			minObservedShapesRemaining = currentShapesRemaining;
		}
		if (deadEndCount > DEAD_END_LIMIT){
			solver.reportAbandonedTraversal(minObservedShapesRemaining);
			throw new EndException();
		}
	}

	@Override
	public Shape determineShape(Configuration c) {
		return (Shape) c.getUnusedShapes().toArray()[rng.nextInt(c.getUnusedShapes().size())];
	}

	@Override
	public void placementFailedGeometry(ConfigurationTreeNode n) {
		deadEndCount++;
	}

	@Override
	public void placementFailedDeadCells(ConfigurationTreeNode n) {
		deadEndCount++;
	}

	@Override
	public void placementSucceeded(Configuration newConfig, ConfigurationTreeNode n) throws FoundException, EndException {
		this.traverse(new ConfigurationTreeNode(n, newConfig));
	}

	@Override
	void postTraversal(Configuration c) throws EndException {
		// Do nothing
	}
}
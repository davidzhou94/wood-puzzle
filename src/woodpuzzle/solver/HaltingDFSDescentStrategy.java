package woodpuzzle.solver;

import java.util.Random;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Shape;

class HaltingDFSDescentStrategy implements Strategy {
	private static final int DEAD_END_LIMIT = 1000000;
	private long rejectedPlacementCount = 0;
	private int minObservedShapesRemaining = Integer.MAX_VALUE;
	private int currentShapesRemaining;
	private final HaltingDFSSolver caller;
	private final Random rng = new Random();
	
	HaltingDFSDescentStrategy(HaltingDFSSolver caller) {
		this.caller = caller;
	}
	
	@Override
	public void preTraversal(Configuration c) throws EndException {
		currentShapesRemaining = c.getUnusedShapes().size();
		if (currentShapesRemaining < minObservedShapesRemaining) {
			minObservedShapesRemaining = currentShapesRemaining;
		}
		if (rejectedPlacementCount > DEAD_END_LIMIT) throw new EndException();
	}

	@Override
	public Shape determineShape(Configuration c) {
		return (Shape) c.getUnusedShapes().toArray()[rng.nextInt(currentShapesRemaining)];
	}

	@Override
	public void placementFailedGeometry(ConfigurationTreeNode n) {
		rejectedPlacementCount++;
	}

	@Override
	public void placementFailedDeadCells(ConfigurationTreeNode n) {
		rejectedPlacementCount++;
	}

	@Override
	public void placementSucceeded(Configuration newConfig, ConfigurationTreeNode n) throws FoundException, EndException {
		caller.traverse(new ConfigurationTreeNode(n, newConfig));
	}
}
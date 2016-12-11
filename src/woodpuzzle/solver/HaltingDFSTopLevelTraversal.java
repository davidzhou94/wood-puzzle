package woodpuzzle.solver;

import java.util.Random;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Shape;

/**
 * The HaltingDFS strategy for "top-level" traversals of the node  
 * configuration tree. This traversal submits a HaltingDFSDescentThread
 * for each top-level configuration node (i.e. each choice of first 
 * shape placement).
 * @author david
 *
 */
class HaltingDFSTopLevelTraversal implements Strategy {
	private final HaltingDFSSolver caller;
	private final Random rng = new Random(new Random().nextLong());
	HaltingDFSTopLevelTraversal(HaltingDFSSolver caller) {
		this.caller = caller;
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
	public void placementFailedGeometry(ConfigurationTreeNode n) {
		// do nothing
	}

	@Override
	public void placementFailedDeadCells(ConfigurationTreeNode n) {
		// do nothing
	}

	@Override
	public void placementSucceeded(Configuration newConfig, ConfigurationTreeNode n) throws FoundException, EndException {
		ConfigurationTreeNode child = new ConfigurationTreeNode(n, newConfig);
		HaltingDFSDescentThread dt = new HaltingDFSDescentThread(child, caller);
//		dt.run();
		caller.submitThreadForExecution(dt);
	}
}
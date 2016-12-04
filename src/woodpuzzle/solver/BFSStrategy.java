package woodpuzzle.solver;

import java.util.Random;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Shape;

/**
 * The strategy for BFS-order traversals of the possible 
 * configurations tree.
 * @author david
 *
 */
class BFSStrategy implements Strategy {
	private final Random rng = new Random();

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
		((BFSNode) n).invalid++;
	}

	@Override
	public void placementFailedDeadCells(ConfigurationTreeNode n) {
		((BFSNode) n).invalid++;
	}

	@Override
	public void placementSucceeded(Configuration newConfig, ConfigurationTreeNode n) throws FoundException, EndException {
		((BFSNode) n).addChild(new BFSNode(n, newConfig));
	}
}
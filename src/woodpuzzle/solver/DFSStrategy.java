package woodpuzzle.solver;

import java.util.Random;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Shape;

/**
 * The strategy for DFS-order traversals of the possible 
 * configurations tree.
 * @author david
 *
 */
class DFSStrategy implements Strategy {
	private final DFSSolver caller;
	private final Random rng = new Random();
	
	DFSStrategy(DFSSolver caller) {
		this.caller = caller;
	}

	@Override
	public void preTraversal(Configuration c) throws EndException {
		caller.count++;
		if (c.getUnusedShapes().size() < caller.record) caller.record = c.getUnusedShapes().size();
		if (caller.count % 1000 == 0) {
			System.out.print("\rConfig #" + caller.count + " has " + c.getUnusedShapes().size() + " unused shapes, after " + caller.rejects + " dead ends, the current best record is " + caller.record);
		}
	}

	@Override
	public Shape determineShape(Configuration c) {
		return (Shape) c.getUnusedShapes().toArray()[rng.nextInt(c.getUnusedShapes().size())];
	}

	@Override
	public void placementFailedGeometry(ConfigurationTreeNode n) {
		caller.rejects++;
	}

	@Override
	public void placementFailedDeadCells(ConfigurationTreeNode n) {
		caller.rejects++;
	}

	@Override
	public void placementSucceeded(Configuration newConfig, ConfigurationTreeNode n) throws FoundException, EndException {
		if (newConfig.getUnusedShapes().size() < caller.record) {
			caller.record = newConfig.getUnusedShapes().size();
		}
		ConfigurationTreeNode child = new ConfigurationTreeNode(n, newConfig);
		caller.traverse(child);
	}
}
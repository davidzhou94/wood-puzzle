package woodpuzzle.solver;

import java.util.Random;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

/**
 * The strategy for DFS-order traversals of the possible 
 * configurations tree.
 * @author david
 *
 */
class DFSStrategy extends AbstractTraversal {
	private long count = 0;
	private long rejects = 0;
	private int record = Integer.MAX_VALUE;
	private final Random rng = new Random();
	
	DFSStrategy(Puzzle puzzle) {
		super(puzzle);
	}

	@Override
	public void preTraversal(Configuration currentConfig) throws EndException {
		this.count++;
		if (currentConfig.getUnusedShapes().size() < this.record) this.record = currentConfig.getUnusedShapes().size();
		if (this.count % 1000 == 0) {
			System.out.print("\rConfig #" + this.count + " has " + currentConfig.getUnusedShapes().size() + " unused shapes, after " + this.rejects + " dead ends, the current best record is " + this.record);
		}
	}

	@Override
	public Shape determineShape(Configuration currentConfig) {
		return (Shape) currentConfig.getUnusedShapes().toArray()[rng.nextInt(currentConfig.getUnusedShapes().size())];
	}

	@Override
	public void placementFailedGeometry(ConfigurationTreeNode currentNode) {
		this.rejects++;
	}

	@Override
	public void placementFailedDeadCells(ConfigurationTreeNode currentNode) {
		this.rejects++;
	}

	@Override
	public void placementSucceeded(Configuration newConfig, ConfigurationTreeNode currentNode) throws FoundException, EndException {
		if (newConfig.getUnusedShapes().size() < this.record) {
			this.record = newConfig.getUnusedShapes().size();
		}
		ConfigurationTreeNode child = new ConfigurationTreeNode(currentNode, newConfig);
		this.traverse(child);
	}
}
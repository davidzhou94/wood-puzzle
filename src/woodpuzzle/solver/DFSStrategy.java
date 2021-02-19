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
	public void preTraversal(Configuration c) throws EndException {
		this.count++;
		if (c.getUnusedShapes().size() < this.record) this.record = c.getUnusedShapes().size();
		if (this.count % 1000 == 0) {
			System.out.print("\rConfig #" + this.count + " has " + c.getUnusedShapes().size() + " unused shapes, after " + this.rejects + " dead ends, the current best record is " + this.record);
		}
	}

	@Override
	public Shape determineShape(Configuration c) {
		return (Shape) c.getUnusedShapes().toArray()[rng.nextInt(c.getUnusedShapes().size())];
	}

	@Override
	public void placementFailedGeometry(ConfigurationTreeNode n) {
		this.rejects++;
	}

	@Override
	public void placementFailedDeadCells(ConfigurationTreeNode n) {
		this.rejects++;
	}

	@Override
	public void placementSucceeded(Configuration newConfig, ConfigurationTreeNode n) throws FoundException, EndException {
		if (newConfig.getUnusedShapes().size() < this.record) {
			this.record = newConfig.getUnusedShapes().size();
		}
		ConfigurationTreeNode child = new ConfigurationTreeNode(n, newConfig);
		this.traverse(child);
	}
}
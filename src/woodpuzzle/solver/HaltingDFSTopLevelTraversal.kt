package woodpuzzle.solver;

import java.util.Random;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

/**
 * The HaltingDFS strategy for "top-level" traversals of the node  
 * configuration tree. This traversal submits a HaltingDFSDescentThread
 * for each top-level configuration node (i.e. each choice of first 
 * shape placement).
 * @author david
 *
 */
class HaltingDFSTopLevelTraversal extends AbstractTraversal {
	private final Random rng = new Random(new Random().nextLong());
	private final HaltingDFSSolver solver;
	HaltingDFSTopLevelTraversal(Puzzle puzzle, HaltingDFSSolver solver) {
		super(puzzle);
		this.solver = solver;
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
		HaltingDFSDescentTraversal traversal = new HaltingDFSDescentTraversal(n.getConfig().getPuzzle(), this.solver);
		try {
			traversal.traverse(child);
		} catch (EndException e) {
			// do nothing
		}
	}
}
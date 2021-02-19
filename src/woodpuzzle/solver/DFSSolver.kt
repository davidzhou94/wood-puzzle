package woodpuzzle.solver;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;

public class DFSSolver extends AbstractSolver {
	public DFSSolver(Puzzle puzzle) {
		super(puzzle);
	}

	@Override
	public Configuration findSolution() {
		DFSStrategy traversal = new DFSStrategy(this.getPuzzle());
		Configuration rootConfig = new Configuration(this.getPuzzle());
		ConfigurationTreeNode rootNode = new ConfigurationTreeNode(null, rootConfig);

		try {
			traversal.traverse(rootNode);
		} catch (FoundException e) {
			return e.getConfig();
		} catch (EndException e) {
			System.out.println("Unexpected exception in DFSSolver: ");
			e.printStackTrace();
		}

		return null;
	}
}

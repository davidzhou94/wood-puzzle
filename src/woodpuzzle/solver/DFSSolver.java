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
		
		this.generateRootConfigs(new Configuration(this.getPuzzle()));
		
		for (Configuration c : rootConfigs) {
			try {
				traversal.traverse(new ConfigurationTreeNode(null, c));
			} catch (FoundException e) {
				return e.config;
			} catch (EndException e) {
				System.out.println("Unexpected exception in HaltingDFSSolver: ");
				e.printStackTrace();
			}
		}
		
		return null;
	}
}

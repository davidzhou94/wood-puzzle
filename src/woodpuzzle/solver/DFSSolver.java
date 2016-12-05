package woodpuzzle.solver;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;

public class DFSSolver extends AbstractSolver {
	protected long count = 0;
	protected long rejects = 0;
	protected int record = Integer.MAX_VALUE;

	public DFSSolver(Puzzle puzzle) {
		super(puzzle, null);
		this.strategy = new DFSStrategy(this);
	}

	@Override
	public Configuration findSolution() {
		this.traverseTopLevel(new Configuration(this.puzzle));
		
		for (Configuration c : rootConfigs) {
			try {
				this.traverse(new ConfigurationTreeNode(null, c));
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

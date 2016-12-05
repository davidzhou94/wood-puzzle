package woodpuzzle.solver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;

/**
 * Multi-threaded DFS traversal with a better heuristic than the DFSSolver.
 * Specifically, this solver will submit to a thread-pool a task to execute
 * a DFS search of a top-level node up to a configured limit of dead ends. 
 * If a solution is not found before the dead end limit, the thread halts
 * allowing another thread to attempt a search on a different top-level child
 * node.
 * @author david
 *
 */
public class HaltingDFSSolver extends AbstractSolver {
	private static final int SOLVER_PARALLELISM = 8;
	private final ExecutorService executor = Executors.newFixedThreadPool(SOLVER_PARALLELISM);
	private Configuration solution = null;

	/**
	 * Creates a HaltingDFSSolver.
	 * @param p The puzzle to solve.
	 */
	public HaltingDFSSolver(Puzzle p) {
		super(p, null);
		this.strategy = new HaltingDFSTopLevelStrategy(this);
	}

	/**
	 * Finds the solution.
	 * @return The first solution configuration found.
	 */
	@Override
	public Configuration findSolution() {
		this.traverseTopLevel(new Configuration(this.puzzle));
		
		for (Configuration c : rootConfigs) {
			try {
				this.traverse(new ConfigurationTreeNode(null, c));
			} catch (FoundException | EndException e) {
				System.out.println("Unexpected exception in HaltingDFSSolver: ");
				e.printStackTrace();
			}
		}
		
		while(solution == null) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				System.out.println("Interrupted while waiting for a solution...");
			}
		}
		
		executor.shutdownNow();
		
		return solution;
	}
	
	void reportSolution(Configuration c) {
		this.solution = c;
	}
	
	void submitThreadForExecution(Runnable t) {
		this.executor.submit(t);
	}
}

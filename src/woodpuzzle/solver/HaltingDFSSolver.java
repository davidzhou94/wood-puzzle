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
	private ExecutorService executor;
	private Configuration solution = null;
	private int recordLevel = Integer.MAX_VALUE;
	private int abandonedAttempts = 0;

	/**
	 * Creates a HaltingDFSSolver.
	 * @param p The puzzle to solve.
	 */
	public HaltingDFSSolver(Puzzle p) {
		super(p);
	}

	/**
	 * Finds the solution.
	 * @return The first solution configuration found.
	 */
	@Override
	public Configuration findSolution() {
		HaltingDFSTopLevelTraversal traversal = new HaltingDFSTopLevelTraversal(this.getPuzzle(), this);
		this.generateRootConfigs(new Configuration(this.getPuzzle()));
		
		System.out.printf("Creating threadpool with %d threads... ", this.rootConfigs.size());
		this.executor = Executors.newFixedThreadPool(this.rootConfigs.size());
		System.out.println("Succeeded");
		
		for (Configuration c : this.rootConfigs) {
			executor.submit(() -> {
				try {
					traversal.traverse(new ConfigurationTreeNode(null, c));
				} catch (FoundException e) {
					this.reportSolution(e.getConfig());
				} catch (EndException e) {
					// do nothing, should not see this exception here 
					// under HaltingDFS
				}
			});
		}
		
		System.out.println("Finished traversing top level of configurations, waiting on solution.");
		
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
	
	synchronized void reportAbandonedTraversal(int recordLevel) {
		if (recordLevel < this.recordLevel) {
			this.recordLevel = recordLevel;
		}
		abandonedAttempts++;
		System.out.printf("Minimum shapes remaining: %d, %d attempts abandoned\r", this.recordLevel, this.abandonedAttempts);
	}
}

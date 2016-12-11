package woodpuzzle.solver;

/**
 * The thread used with the HaltingDFSSolver.
 * Terminates when either a solution is found or it has reached
 * the limit for the number of rejected configurations.
 * @author david
 */
class HaltingDFSDescentThread implements Runnable {
	private final ConfigurationTreeNode root;
	private final HaltingDFSSolver caller;
	private final HaltingDFSDescentTraversal traversal;
	
	HaltingDFSDescentThread(ConfigurationTreeNode n, HaltingDFSSolver caller) {
		this.root = n;
		this.caller = caller;
		this.traversal = new HaltingDFSDescentTraversal(n.config.getPuzzle());
	}

	@Override
	public void run() {
		caller.notifyThreadStart();
		try {
			this.traversal.traverse(root);
		} catch (FoundException e) {
			caller.reportSolution(e.config);
		} catch (EndException e) {
			// do nothing, we are giving up on this search
		}
		caller.notifyThreadEnd();
	}
}
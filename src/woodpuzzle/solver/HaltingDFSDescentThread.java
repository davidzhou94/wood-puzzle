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
	
	HaltingDFSDescentThread(ConfigurationTreeNode n, HaltingDFSSolver caller) {
		this.root = n;
		this.caller = caller;
	}

	@Override
	public void run() {
		System.out.println("Starting search");
		try {
			this.caller.traverse(root);
		} catch (FoundException e) {
			caller.reportSolution(e.config);
		} catch (EndException e) {
			// do nothing, we are giving up on this search
		}
		System.out.println("Abandoning search");
	}
}
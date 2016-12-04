package woodpuzzle.solver;

import woodpuzzle.model.Configuration;

/**
 * The exception thrown when a solution is found.
 * @author david
 *
 */
class FoundException extends Exception {
	private static final long serialVersionUID = 1L;
	public Configuration config;
	public FoundException(Configuration config) {
		this.config = config;
	}
}
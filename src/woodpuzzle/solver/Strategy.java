package woodpuzzle.solver;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Shape;

/**
 * Used with AbstractSolver.traverse(...) to indicate the actions
 * that should be taken at each part of the traversal algorithm.
 * @author david
 *
 */
interface Strategy {
	void preTraversal(Configuration c) throws EndException;
	Shape determineShape(Configuration c);
	void placementFailedGeometry(ConfigurationTreeNode n);
	void placementFailedDeadCells(ConfigurationTreeNode n);
	void placementSucceeded(Configuration newConfig, ConfigurationTreeNode n) throws FoundException, EndException;
}
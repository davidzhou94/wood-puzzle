package woodpuzzle.solver;

import woodpuzzle.model.Configuration;

/**
 * A configuration tree Node.
 * @author david
 *
 */
class ConfigurationTreeNode {
	public ConfigurationTreeNode parent;
	public Configuration config;
	public ConfigurationTreeNode(ConfigurationTreeNode n) {
		this.parent = n;
	}

	public ConfigurationTreeNode(ConfigurationTreeNode n, Configuration c) {
		this.parent = n;
		this.config = c;
	}
}
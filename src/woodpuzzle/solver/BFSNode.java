package woodpuzzle.solver;

import java.util.ArrayList;
import java.util.List;

import woodpuzzle.model.Configuration;

class BFSNode extends ConfigurationTreeNode {
	public long valid, invalid;
	public List<BFSNode> children;
	public BFSNode(ConfigurationTreeNode parent) {
		super(parent);
		valid = 0;
		invalid = 0;
		this.children = new ArrayList<BFSNode>();
	}
	
	public BFSNode(ConfigurationTreeNode parent, Configuration c) {
		super(parent, c);
		valid = 0;
		invalid = 0;
		this.children = new ArrayList<BFSNode>();
	}
	
	public void addChild(BFSNode n) {
		this.children.add(n);
	}
}
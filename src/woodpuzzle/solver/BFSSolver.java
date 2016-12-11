package woodpuzzle.solver;

import java.util.ArrayList;
import java.util.List;

import woodpuzzle.model.Configuration;
import woodpuzzle.model.Puzzle;

public class BFSSolver extends AbstractSolver {
	
	private final BFSNode root;

	public BFSSolver(Puzzle p) {
		super(p);
		this.root = new BFSNode(null);
	}

	@Override
	public Configuration findSolution() {
		BFSStrategy traversal = new BFSStrategy(this.puzzle);
		
		this.root.config = new Configuration(this.puzzle);
		try {
			BFSNode n = root;
			this.generateRootConfigs(this.root.config);
			for (Configuration c : this.rootConfigs) {
				n.addChild(new BFSNode(n, c));
			}
			while (true) {
				List<BFSNode> prune = new ArrayList<BFSNode>();
				for (BFSNode c : n.children) {
					if (c.children.isEmpty()) {
				    	try {
				    		traversal.traverse(n);
						} catch (EndException e) {
							System.out.println("Unexpected exception in BFSSolver: ");
							e.printStackTrace();
						}
						
						if (c.children.isEmpty()) {
							prune.add(c);
						}
					}
					n.invalid += c.invalid;
					n.valid += c.valid;
				}
				n.invalid += prune.size();
				n.valid -= prune.size();
				for (ConfigurationTreeNode c : prune) {
					n.children.remove(c);
				}
				if (n.children.isEmpty()) {
					BFSNode c = n;
					n = (BFSNode) c.parent;
					n.invalid += c.invalid + 1;
					n.valid--;
					n.children.remove(c);
					continue;
				}
				BFSNode bestConfig = n.children.get(0);
				double bestScore = (double)(bestConfig.valid) / (double)(bestConfig.invalid + bestConfig.valid);
				double worstScore = bestScore;
				for (BFSNode c : n.children) {
					double score = (double)(c.valid) / (double)(c.invalid + c.valid);
					if (score > bestScore) {
						bestConfig = c;
						bestScore = score;
					}
					if (score < worstScore) worstScore = score;
				}
				n = bestConfig;
			}
		} catch (FoundException ex) {
			return ex.config;
		}
	}
}

package woodPuzzle.engine;

import java.util.List;

import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public class BFSSolver extends AbstractSolver {
	
	Node root;

	public BFSSolver() {
		root = new Node();
	}

	@Override
	public Shape[] solve(Puzzle p) {
		root.config = p;
		return null;
	}

	class Node {
		public List<Node> children;
		public Puzzle config;
	}
}

package woodPuzzle.engine;

import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public abstract class AbstractSolver {

	public AbstractSolver() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract Shape[] solve(Puzzle p);

}

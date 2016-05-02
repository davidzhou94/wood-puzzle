package woodPuzzle.engine;

import woodPuzzle.model.Puzzle;

public class Engine {
	private static Engine instance;
	private AbstractSolver solver;
	private Puzzle puzzle;
	
	private Engine() { 
		 
	}
	
	public static Engine getInstance() {
		if (instance == null) {
			instance = new Engine();
		}
		return instance;
	}
	
	private void setupModel(String filePath) {
		puzzle = XMLReader.buildPuzzle(filePath);
	}
	
	private void setSolver(AbstractSolver solver) {
		this.solver = solver;
	}
	
	private void solve() {
		this.solver.solve(puzzle);
	}
	
	public static void main(String[] args) {
		Engine e = Engine.getInstance();
		e.setupModel(args[0]);
		//e.setSolver(new BFSSolver(e.puzzle));
		//e.setSolver(new DFSSolver(e.puzzle));
		e.setSolver(new ThreadedDFSSolver(e.puzzle));
		e.solve();
	}
}

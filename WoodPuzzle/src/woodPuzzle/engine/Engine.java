package woodPuzzle.engine;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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
	
	private void setSolver(AbstractSolver solver) {
		this.solver = solver;
	}
	
	private void solve() {
		this.solver.solve(puzzle);
	}
	
	public static void main(String[] args) {
		Engine e = Engine.getInstance();

		try {
			e.puzzle = XMLReader.buildPuzzle(args[0]);
		} catch (SAXException | ParserConfigurationException ex) {
			System.out.println("Error reading XML file: " + ex.getMessage());
			return;
		} catch (IOException ex) {
			System.out.println("Error locating or opening given file: " + ex.getMessage());
			return;
		}

		//e.setSolver(new BFSSolver(e.puzzle));
		//e.setSolver(new DFSSolver(e.puzzle));
		e.setSolver(new ThreadedDFSSolver(e.puzzle));
		e.solve();
	}
}

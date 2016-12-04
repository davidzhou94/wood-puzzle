package woodpuzzle.engine;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import woodpuzzle.model.Puzzle;
import woodpuzzle.solver.AbstractSolver;
import woodpuzzle.solver.HaltingDFSSolver;

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
		this.solver.solvePuzzle();
	}
	
	/**
	 * Run using:
	 * $ java -Xmx8g -cp F:\repos\wood-puzzle\WoodPuzzle\bin\ woodPuzzle.engine.Engine .\assets\default.xml
	 * @param args first argument is the path to the puzzle definition
	 */
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

		// Probably don't use BFSSolver, it is very slow even on the small 3x3 toy example
		//e.setSolver(new BFSSolver(e.puzzle));
		//e.setSolver(new DFSSolver(e.puzzle));
		e.setSolver(new HaltingDFSSolver(e.puzzle));
		e.solve();
	}
}

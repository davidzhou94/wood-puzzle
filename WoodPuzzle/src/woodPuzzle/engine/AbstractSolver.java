package woodPuzzle.engine;

import java.util.HashMap;
import java.util.Map;

import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public abstract class AbstractSolver {

	public AbstractSolver() {
		// TODO Auto-generated constructor stub
	}
	
	public void solve(Puzzle p) {
		Puzzle sol = this.findSolution(p);
		this.printSolution(sol);
	}
	
	public abstract Puzzle findSolution(Puzzle p);

	public void printSolution(Puzzle p) {
		if (p == null) {
			System.out.println("\nNo solution found");
			return;
		}
		System.out.println("\nSolution found:\n");
		Shape[] cells = p.getFilledCells();
		Map<Shape, Character> m = new HashMap<Shape, Character>();
		m.put(null, '0');
		char cur = 'A';
		for (int y = 0; y < p.getHeight(); y++) {
			for (int x = 0; x < p.getWidth(); x++) {
				for (int z = 0; z < p.getLength(); z++) {
					Shape s = cells[p.hashCoordinate(x, y, z)];
					if (!m.containsKey(s)) {
						m.put(s, cur);
						cur++;
					}
					System.out.print(m.get(s));
					System.out.print(" ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
}

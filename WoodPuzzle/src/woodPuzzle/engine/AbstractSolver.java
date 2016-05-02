package woodPuzzle.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import woodPuzzle.model.Configuration;
import woodPuzzle.model.Coordinate;
import woodPuzzle.model.Puzzle;
import woodPuzzle.model.Shape;

public abstract class AbstractSolver {

	protected Puzzle puzzle;

	public AbstractSolver(Puzzle p) {
		this.puzzle = p;
	}
	
	public void solve(Puzzle p) {
		Configuration sol = this.findSolution();
		this.printSolution(sol, p);
	}
	
	public abstract Configuration findSolution();

	public void printSolution(Configuration c, Puzzle p) {
		if (c == null) {
			System.out.println("\nNo solution found");
			return;
		}
		System.out.println("\nSolution found:\n");
		Shape[] cells = c.getCells();
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
	
	// Utility methods:
	public boolean hasIsolatedCells(Configuration newConfig, int validGroupSize) {
		boolean visited[] = new boolean[this.puzzle.getTotalCells()];
		Shape cells[] = newConfig.getCells();
		for (int i = 0; i < this.puzzle.getTotalCells(); i++) visited[i] = false;
		for (int x = 0; x < this.puzzle.getWidth(); x++) {
			for (int y = 0; y < this.puzzle.getHeight(); y++) {
				for (int z = 0; z < this.puzzle.getLength(); z++) {
					int pos = this.puzzle.hashCoordinate(x, y, z);
					if (visited[pos]) continue;
					visited[pos] = true;
					if (cells[pos] != null) continue;
					int emptyCount = 1;
					Queue<Coordinate> checkNeighbours = new LinkedList<Coordinate>();
					checkNeighbours.add(new Coordinate(x, y, z));
					while (!checkNeighbours.isEmpty()) {
						Coordinate c = checkNeighbours.poll();
						if (this.puzzle.isValidCoordinate(c.x + 1, c.y, c.z)) {
							int adj = this.puzzle.hashCoordinate(c.x+1, c.y, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(1, 0, 0));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x - 1, c.y, c.z)) {
							int adj = this.puzzle.hashCoordinate(c.x-1, c.y, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(-1, 0, 0));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x, c.y+1, c.z)) {
							int adj = this.puzzle.hashCoordinate(c.x, c.y+1, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 1, 0));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x, c.y-1, c.z)) {
							int adj = this.puzzle.hashCoordinate(c.x+1, c.y-1, c.z);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, -1, 0));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x, c.y, c.z+1)) {
							int adj = this.puzzle.hashCoordinate(c.x, c.y, c.z+1);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 0, 1));
								}
								visited[adj] = true;
							}
						}
						if (this.puzzle.isValidCoordinate(c.x, c.y, c.z-1)) {
							int adj = this.puzzle.hashCoordinate(c.x, c.y, c.z-1);
							if (visited[adj] == false) {
								if (cells[adj] == null) {
									emptyCount++;
									checkNeighbours.add(c.vectorAdd(0, 0, -1));
								}
								visited[adj] = true;
							}
						}
					}
					if (emptyCount % validGroupSize != 0) return true;
				}
			}
		}
		return false;
	}
}

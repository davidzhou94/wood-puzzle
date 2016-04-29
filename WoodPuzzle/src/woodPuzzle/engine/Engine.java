package woodPuzzle.engine;

import woodPuzzle.model.Puzzle;

public class Engine {
	private static Engine instance;
	
	private Puzzle puzzle;
	
	private Engine() {	}
	
	public static Engine getInstance() {
		if (instance == null) {
			instance = new Engine();
		}
		return instance;
	}
	
	public void setupModel(int width, int height, int length, String filePath) {
		puzzle = XMLReader.buildPuzzle(filePath);
	}
}

package woodpuzzle.engine

import woodpuzzle.engine.XMLReader.buildPuzzle
import woodpuzzle.model.Puzzle
import woodpuzzle.solver.haltingdfs.HaltingDFSSolver

/**
 * Run using:
 * $ cd C:\Users\david\projects\wood-puzzle\
 * $ java -jar C:\Users\david\projects\wood-puzzle\build\libs\WoodPuzzle-1.0-SNAPSHOT.jar 300000 .\assets\default.xml
 * @param args 1st arg is halting DFS dead end limit 2nd arg is the path to the puzzle definition
 */
fun main(args: Array<String>) {
    val puzzle: Puzzle = try {
        buildPuzzle(args[1])
    } catch (ex: Exception) {
        println("Error reading XML file: " + ex.message)
        return
    }

    HaltingDFSSolver(puzzle, args[0].toInt()).solvePuzzle()
}

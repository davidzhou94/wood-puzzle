package woodpuzzle.engine

import woodpuzzle.engine.XMLReader.buildPuzzle
import woodpuzzle.model.Puzzle
import woodpuzzle.solver.haltingdfs.HaltingDFSSolver
import woodpuzzle.util.Timer
import woodpuzzle.util.printSolution

/**
 * Run using:
 * $ cd C:\Users\david\projects\wood-puzzle\
 * $ java -Xmx4g -jar .\build\libs\WoodPuzzle-1.0-SNAPSHOT.jar 300000 .\assets\default.xml
 * @param args 1st arg is halting DFS dead end limit 2nd arg is the path to the puzzle definition
 */
fun main(args: Array<String>) {
    val puzzle: Puzzle = try {
        buildPuzzle(args[1])
    } catch (ex: Exception) {
        println("Error reading XML file: " + ex.message)
        return
    }

    val solver = HaltingDFSSolver(puzzle, args[0].toInt())

    val timer = Timer()
    val sol = solver.findSolution()
    timer.printElapsed()
    printSolution(sol)
}

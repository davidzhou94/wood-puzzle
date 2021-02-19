package woodpuzzle.solver

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import java.util.*

/**
 * All solver algorithms should inherit from this class.
 * @author david
 */
abstract class AbstractSolver
/**
 * Base constructor.
 * @param puzzle The puzzle to use with this solver instance.
 */
constructor (val puzzle: Puzzle) {
    @kotlin.jvm.JvmField
	protected var rootConfigs: Set<Configuration> = emptySet()

    /**
     * Measures the variant time of the solver algorithm, attempts to find a solution
     * and prints out the solution if it is found.
     */
    fun solvePuzzle() {
        val begin = System.currentTimeMillis()
        val sol = findSolution()
        val elapsed = System.currentTimeMillis() - begin
        val second = elapsed / 1000 % 60
        val minute = elapsed / (1000 * 60) % 60
        val hour = elapsed / (1000 * 60 * 60) % 24
        val millis = elapsed % 1000
        val time = String.format("%02d:%02d:%02d:%d", hour, minute, second, millis)
        println("\nTime elapsed: $time")
        printSolution(sol)
    }

    /**
     * The specific implementation for finding the solution.
     * @return The configuration of the solution.
     */
    abstract fun findSolution(): Configuration?

    /**
     * Prints out the configuration.
     * @param configuration The configuration to print.
     */
    private fun printSolution(configuration: Configuration?) {
        if (configuration == null) {
            println("\nNo solution found")
            return
        }
        println("\nSolution found:\n")
        val cells = configuration.cells
        val shapeToCharMap: MutableMap<Shape?, Char> = HashMap()
        shapeToCharMap[null] = '0'
        var currentChar = 'A'
        for (y in 0 until configuration.puzzle.height) {
            for (x in 0 until configuration.puzzle.width) {
                for (z in 0 until configuration.puzzle.length) {
                    val shape = cells[configuration.puzzle.hashCoordinate(x, y, z)]
                    if (!shapeToCharMap.containsKey(shape)) {
                        shapeToCharMap[shape] = currentChar
                        currentChar++
                    }
                    print(shapeToCharMap[shape])
                    print(" ")
                }
                println()
            }
            println()
        }
    }

    /**
     * Generates the root configurations (e.g. one configuration per each combination
     * of shapes to use / removed).
     * @param root The root configuration to copy from
     * @throws Exception
     */
    protected fun generateRootConfigs(root: Configuration) {
        val allShapes = puzzle.shapes.toMutableList()
        allShapes.shuffle()
        rootConfigs = rootConfigs + root
        println("Top level traversal complete")
    }
}
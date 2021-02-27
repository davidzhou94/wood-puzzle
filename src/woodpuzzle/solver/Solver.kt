package woodpuzzle.solver

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.util.printSolution

interface Solver {
    val puzzle: Puzzle

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
    fun findSolution(): Configuration?
}
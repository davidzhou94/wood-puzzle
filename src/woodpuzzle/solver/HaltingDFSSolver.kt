package woodpuzzle.solver

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle

/**
 * Multi-threaded DFS traversal with a better heuristic than the DFSSolver.
 * Specifically, this solver will submit to a thread-pool a task to execute
 * a DFS search of a top-level node up to a configured limit of dead ends.
 * If a solution is not found before the dead end limit, the thread halts
 * allowing another thread to attempt a search on a different top-level child
 * node.
 * @param puzzle The puzzle to solve.
 * @author david
 */
class HaltingDFSSolver(override val puzzle: Puzzle) : Solver {
    private var solution: Configuration? = null
    private var recordLevel = Int.MAX_VALUE
    private var abandonedAttempts = 0

    /**
     * Finds the solution.
     * @return The first solution configuration found.
     */
    override fun findSolution(): Configuration? {
        val traversal = HaltingDFSTopLevelTraversal(puzzle, this)
        val rootConfig = Configuration(puzzle)
        val rootNode = ConfigurationTreeNode(null, rootConfig)
        println("Starting top level traversal")
        try {
            traversal.traverse(rootNode)
        } catch (e: FoundException) {
            reportSolution(e.config)
        } catch (e: EndException) {
            // do nothing, should not see this exception here
            // under HaltingDFS
        }
        println("Finished traversing top level of configurations, waiting on solution.")
        while (solution == null && traversal.running()) {
            try {
                Thread.sleep(100L)
            } catch (e: InterruptedException) {
                println("Interrupted while waiting for a solution...")
            }
        }
        traversal.stop()
        return solution
    }

    fun reportSolution(c: Configuration) {
        solution = c
    }

    @Synchronized
    fun reportAbandonedTraversal(recordLevel: Int) {
        if (recordLevel < this.recordLevel) {
            this.recordLevel = recordLevel
        }
        abandonedAttempts++
        println("Minimum shapes remaining: ${this.recordLevel}, $abandonedAttempts attempts abandoned")
    }
}
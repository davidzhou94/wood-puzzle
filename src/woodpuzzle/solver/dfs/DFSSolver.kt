package woodpuzzle.solver.dfs

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.solver.EndException
import woodpuzzle.solver.FoundException
import woodpuzzle.solver.Solver

class DFSSolver(val puzzle: Puzzle) : Solver {
    override fun findSolution(): Configuration? {
        val traversal = DFSTraversal(puzzle)
        val rootConfig = Configuration(puzzle)
        try {
            traversal.traverse(rootConfig)
        } catch (e: FoundException) {
            return e.config
        } catch (e: EndException) {
            println("Unexpected exception in DFSSolver: ")
            e.printStackTrace()
        }
        return null
    }
}

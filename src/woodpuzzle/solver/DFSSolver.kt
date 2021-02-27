package woodpuzzle.solver

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle

class DFSSolver(override val puzzle: Puzzle) : Solver {
    override fun findSolution(): Configuration? {
        val traversal = DFSTraversal(puzzle)
        val rootConfig = Configuration(puzzle)
        val rootNode = ConfigurationTreeNode(null, rootConfig)
        try {
            traversal.traverse(rootNode)
        } catch (e: FoundException) {
            return e.config
        } catch (e: EndException) {
            println("Unexpected exception in DFSSolver: ")
            e.printStackTrace()
        }
        return null
    }
}

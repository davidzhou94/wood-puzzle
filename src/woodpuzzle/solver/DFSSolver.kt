package woodpuzzle.solver

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle

class DFSSolver(puzzle: Puzzle) : AbstractSolver(puzzle) {
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

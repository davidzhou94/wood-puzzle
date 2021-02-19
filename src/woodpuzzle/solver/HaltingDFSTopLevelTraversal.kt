package woodpuzzle.solver

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

/**
 * The HaltingDFS strategy for "top-level" traversals of the node
 * configuration tree. This traversal submits a HaltingDFSDescentThread
 * for each top-level configuration node (i.e. each choice of first
 * shape placement).
 * @author david
 */
class HaltingDFSTopLevelTraversal(puzzle: Puzzle, private val solver: HaltingDFSSolver) : AbstractTraversal(puzzle) {
    private val rng = Random(Random.nextLong())
    private val executor: ExecutorService = Executors.newCachedThreadPool()

    override fun preTraversal(currentConfig: Configuration) { /* do nothing */ }

    override fun determineShape(currentConfig: Configuration): Shape =
        currentConfig.unusedShapes.random(rng)

    override fun placementFailedGeometry() { /* do nothing */ }

    override fun placementFailedDeadCells() { /* do nothing */ }

    override fun placementSucceeded(newConfig: Configuration, currentNode: ConfigurationTreeNode) {
        val child = ConfigurationTreeNode(currentNode, newConfig)
        val traversal = HaltingDFSDescentTraversal(currentNode.config.puzzle, solver)
        executor.submit {
            try {
                println("Submitting a thread to thread pool")
                traversal.traverse(child)
            } catch (e: FoundException) {
                solver.reportSolution(e.config)
            } catch (e: EndException) {
                // do nothing, should not see this exception here
                // under HaltingDFS
            }
        }
    }

    fun stop() = executor.shutdown()
}

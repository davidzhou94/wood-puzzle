package woodpuzzle.solver

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import kotlin.random.Random

class HaltingDFSDescentTraversal(puzzle: Puzzle, private val solver: HaltingDFSSolver) : AbstractTraversal(puzzle) {
    companion object {
        private const val DEAD_END_LIMIT = 1000000
    }

    private var deadEndCount: Long = 0
    private var minObservedShapesRemaining = Int.MAX_VALUE
    private var currentShapesRemaining = 0
    private val rng = Random(Random.nextLong())

    @Throws(EndException::class)
    override fun preTraversal(currentConfig: Configuration) {
        currentShapesRemaining = currentConfig.unusedShapes.size
        if (currentShapesRemaining < minObservedShapesRemaining) {
            minObservedShapesRemaining = currentShapesRemaining
        }
        if (deadEndCount > DEAD_END_LIMIT) {
            solver.reportAbandonedTraversal(minObservedShapesRemaining)
            throw EndException
        }
    }

    override fun determineShape(currentConfig: Configuration): Shape =
        currentConfig.unusedShapes.random(rng)

    override fun placementFailedGeometry(currentNode: ConfigurationTreeNode) {
        deadEndCount++
    }

    override fun placementFailedDeadCells(currentNode: ConfigurationTreeNode) {
        deadEndCount++
    }

    @Throws(FoundException::class, EndException::class)
    override fun placementSucceeded(newConfig: Configuration, currentNode: ConfigurationTreeNode) {
        super.traverse(ConfigurationTreeNode(currentNode, newConfig))
    }
}
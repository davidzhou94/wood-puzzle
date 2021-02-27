package woodpuzzle.solver

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import kotlin.math.min
import kotlin.random.Random

class HaltingDFSDescentTraversal(puzzle: Puzzle, private val solver: HaltingDFSSolver) : AbstractTraversal(puzzle) {
    companion object {
        private const val DEAD_END_LIMIT = 1_000_000
    }

    private val rng = Random(Random.nextLong())
    private var deadEndCount: Long = 0
    private var minObservedShapesRemaining = Int.MAX_VALUE

    override fun preTraversal(currentConfig: Configuration) {
        minObservedShapesRemaining = min(currentConfig.unusedShapes.size, minObservedShapesRemaining)
        if (deadEndCount > DEAD_END_LIMIT) {
            solver.reportAbandonedTraversal(minObservedShapesRemaining)
            throw EndException
        }
    }

    override fun determineShape(currentConfig: Configuration): Shape =
        currentConfig.unusedShapes.random(rng)

    override fun placementFailedGeometry() {
        deadEndCount++
    }

    override fun placementFailedDeadCells() {
        deadEndCount++
    }

    override fun placementSucceeded(newConfig: Configuration, currentNode: ConfigurationTreeNode) {
        super.traverse(ConfigurationTreeNode(currentNode, newConfig))
    }
}

package woodpuzzle.solver.haltingdfs

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import woodpuzzle.solver.EndException
import woodpuzzle.solver.Traversal
import kotlin.math.min
import kotlin.random.Random

class HaltingDFSDescentTraversal(
    override val puzzle: Puzzle,
    private val solver: HaltingDFSSolver
) : Traversal {
    private val rng = Random(Random.nextLong())
    private var deadEndCount: Long = 0
    var minUnusedShapes = Int.MAX_VALUE

    override fun preTraversal(currentConfig: Configuration) {
        minUnusedShapes = min(currentConfig.unusedShapes.size, minUnusedShapes)
        if (deadEndCount > solver.deadEndLimit) {
            solver.reportAbandonedTraversal(minUnusedShapes)
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

    override fun placementSucceeded(newConfig: Configuration) {
        super.traverse(newConfig)
    }
}

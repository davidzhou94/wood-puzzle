package woodpuzzle.solver.haltingdfs

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.solver.EndException
import woodpuzzle.solver.Traversal

class HaltingDFSDescentTraversal(
    override val puzzle: Puzzle,
    private val solver: HaltingDFSSolver
) : Traversal {
    override var minUnusedShapes = Int.MAX_VALUE
    var deadEndCount: Long = 0

    override fun preTraversal(currentConfig: Configuration) {
        if (deadEndCount > solver.deadEndLimit) {
            solver.reportAbandonedTraversal(minUnusedShapes)
            throw EndException
        }
    }

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

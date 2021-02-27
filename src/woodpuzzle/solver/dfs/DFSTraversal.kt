package woodpuzzle.solver.dfs

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.solver.Traversal


/**
 * The strategy for DFS-order traversals of the possible
 * configurations tree.
 * @author david
 */
class DFSTraversal(override val puzzle: Puzzle) : Traversal {
    override var minUnusedShapes = Int.MAX_VALUE
    private var count: Long = 0
    private var deadEndCount: Long = 0

    override fun preTraversal(currentConfig: Configuration) {
        count++
        if (count % 1000 == 0L) {
            println("Config #$count has ${currentConfig.unusedShapes.size} unused shapes, " +
                    "after $deadEndCount dead ends, the current best record " +
                    "is $minUnusedShapes unused shapes")
        }
    }

    override fun placementFailedGeometry() {
        deadEndCount++
    }

    override fun placementFailedDeadCells() {
        deadEndCount++
    }

    override fun placementSucceeded(newConfig: Configuration) {
        if (newConfig.unusedShapes.size < minUnusedShapes) {
            minUnusedShapes = newConfig.unusedShapes.size
        }
        super.traverse(newConfig)
    }
}

package woodpuzzle.solver.dfs

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import woodpuzzle.solver.Traversal
import kotlin.math.min
import kotlin.random.Random


/**
 * The strategy for DFS-order traversals of the possible
 * configurations tree.
 * @author david
 */
class DFSTraversal(override val puzzle: Puzzle) : Traversal {
    private val rng = Random(Random.nextLong())
    private var count: Long = 0
    private var deadEndCount: Long = 0
    private var minUnusedShapes = Int.MAX_VALUE

    override fun preTraversal(currentConfig: Configuration) {
        count++
        minUnusedShapes = min(currentConfig.unusedShapes.size, minUnusedShapes)
        if (count % 1000 == 0L) {
            println("Config #$count has ${currentConfig.unusedShapes.size} unused shapes, " +
                    "after $deadEndCount dead ends, the current best record " +
                    "is $minUnusedShapes unused shapes")
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
        if (newConfig.unusedShapes.size < minUnusedShapes) {
            minUnusedShapes = newConfig.unusedShapes.size
        }
        traverse(newConfig)
    }
}

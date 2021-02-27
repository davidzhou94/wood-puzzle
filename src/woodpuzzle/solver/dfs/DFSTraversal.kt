package woodpuzzle.solver.dfs

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import woodpuzzle.solver.ConfigurationTreeNode
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
    private var minObservedShapesRemaining = Int.MAX_VALUE

    override fun preTraversal(currentConfig: Configuration) {
        count++
        minObservedShapesRemaining = min(currentConfig.unusedShapes.size, minObservedShapesRemaining)
        if (count % 1000 == 0L) {
            println("Config #$count has ${currentConfig.unusedShapes.size} unused shapes, " +
                    "after $deadEndCount dead ends, the current best record " +
                    "is $minObservedShapesRemaining unused shapes")
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
        if (newConfig.unusedShapes.size < minObservedShapesRemaining) {
            minObservedShapesRemaining = newConfig.unusedShapes.size
        }
        val child = ConfigurationTreeNode(currentNode, newConfig)
        traverse(child)
    }
}

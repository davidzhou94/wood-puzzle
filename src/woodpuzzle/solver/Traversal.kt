package woodpuzzle.solver

import woodpuzzle.model.*
import kotlin.math.min
import kotlin.random.Random

val rng = Random(Random.nextLong())

interface Traversal {
    val puzzle: Puzzle
    var minUnusedShapes: Int

    fun preTraversal(currentConfig: Configuration) { }
    fun determineShape(currentConfig: Configuration): Shape =
        currentConfig.unusedShapes.random(rng)
    fun placementFailedGeometry() { }
    fun placementFailedDeadCells() { }
    fun placementSucceeded(newConfig: Configuration) { }

    /**
     * Traverses the potential child configurations of the current configuration.
     * Flow is controlled by throwing an exception to indicate whether the traversal
     * has found a solution or whether it is terminating early.
     * @param currentConfig The current configuration
     */
    fun traverse(currentConfig: Configuration) {
        preTraversal(currentConfig)
        val shape = determineShape(currentConfig)
        val sideLength = shape.sideLength
        // Try every possible placement in the puzzle box
        for (xOffset in 0 until puzzle.width - 1) {
            for (zOffset in 0 until puzzle.length - 1) {
                // CACHED_TRANSFORMS contains all rotations combinations
                for (transform in CACHED_TRANSFORMS) {
                    val rotatedShape = shape.applyTransform(transform)
                    val placement = shapeArrayToCoordinateList(sideLength, rotatedShape)
                        .map { it.vectorAdd(xOffset, 0, zOffset) }
                    val newConfig = currentConfig.placeShape(shape, placement)
                    if (newConfig == null) {
                        placementFailedGeometry()
                        continue
                    }
                    minUnusedShapes = min(newConfig.unusedShapes.size, minUnusedShapes)
                    if (newConfig.allCellsFilled()) throw FoundException(newConfig)
                    if (newConfig.hasDeadCells()) {
                        placementFailedDeadCells()
                        continue
                    }
                    placementSucceeded(newConfig)
                }
            }
        }
    }
}

package woodpuzzle.solver

import woodpuzzle.model.*

interface Traversal {
    val puzzle: Puzzle

    fun preTraversal(currentConfig: Configuration) { }
    fun determineShape(currentConfig: Configuration): Shape = puzzle.shapes.first()
    fun placementFailedGeometry() { }
    fun placementFailedDeadCells() { }
    fun placementSucceeded(newConfig: Configuration, currentNode: ConfigurationTreeNode) { }

    /**
     * Traverses the potential children configurations of the configuration
     * at the given node according to the given strategy. Flow is controlled
     * by throwing an exception to indicate whether the traversal has found
     * a solution or whether it is terminating early due to an indication
     * in the strategy.
     * @param node The parent node.
     */
    fun traverse(node: ConfigurationTreeNode) {
        val currentConfig = node.config
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
                    if (newConfig.allCellsFilled()) throw FoundException(newConfig)
                    if (newConfig.hasDeadCells()) {
                        placementFailedDeadCells()
                        continue
                    }
                    placementSucceeded(newConfig, node)
                }
            }
        }
    }
}

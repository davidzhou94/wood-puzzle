package woodpuzzle.solver

import woodpuzzle.model.*
import java.util.*

/**
 * All traversal algorithms should inherit from this class.
 * @author david
 */
abstract class AbstractTraversal(private val puzzle: Puzzle) {
    abstract fun preTraversal(currentConfig: Configuration)
    abstract fun determineShape(currentConfig: Configuration): Shape
    abstract fun placementFailedGeometry()
    abstract fun placementFailedDeadCells()
    abstract fun placementSucceeded(newConfig: Configuration, currentNode: ConfigurationTreeNode)

    /**
     * Traverses the potential children configurations of the configuration
     * at the given node according to the given strategy. Flow is controlled
     * by throwing an exception to indicate whether the traversal has found
     * a solution or whether it is terminating early due to an indication
     * in the strategy.
     * @param node The parent node.
     * @throws FoundException Thrown when a solution is found.
     * @throws EndException Throw when the strategy terminates the traversal
     * before a solution is found.
     */
    fun traverse(node: ConfigurationTreeNode) {
        val currentConfig = node.config
        preTraversal(currentConfig)
        val shape = determineShape(currentConfig)
        val sideLength = shape.sideLength
        // Try every possible placement in the puzzle box
        for (xOffset in 0 until puzzle.width - 1) {
            for (zOffset in 0 until puzzle.length - 1) {
                for (yAxis in YAxis.values()) {
                    for (zAxis in ZAxis.values()) {
                        val newConfig = Configuration(currentConfig)
                        val rotatedShape = shape.rotateShape(yAxis, zAxis)
                        var placement: List<Coordinate> = emptyList()
                        // Un-hash the coordinates
                        for (x in 0 until sideLength) {
                            for (y in 0 until sideLength) {
                                for (z in 0 until sideLength) {
                                    if (rotatedShape[shape.hashCoordinate(x, y, z)] == 1) {
                                        placement = placement + Coordinate(x + xOffset, y, z + zOffset)
                                    }
                                }
                            }
                        }
                        if (!newConfig.placeShape(shape, placement)) {
                            placementFailedGeometry()
                            continue
                        }
                        if (newConfig.allCellsFilled()) throw FoundException(newConfig)
                        if (hasDeadCells(newConfig)) {
                            placementFailedDeadCells()
                            continue
                        }
                        placementSucceeded(newConfig, node)
                    }
                }
            }
        }
    }

    /**
     * Checks whether a configuration has isolated cells. That is,
     * if a group of empty and connected cells is smaller than the
     * given minimum shape size, then it is isolated. Furthermore,
     * if all shapes are of identical size then a similar group with
     * the number of empty cells not a multiple of the shape size is
     * also considered isolated.
     * @param config The configuration to check.
     * @return true if there are isolated cells, otherwise false.
     */
    private fun hasDeadCells(config: Configuration): Boolean {
        val visited = BooleanArray(puzzle.totalCells) // Inits to false
        val cells = config.cells
        for (x in 0 until puzzle.width) {
            for (y in 0 until puzzle.height) {
                for (z in 0 until puzzle.length) {
                    // Iterates over every cell of the box looking for an empty gap
                    val currentIndex = puzzle.hashCoordinate(x, y, z)
                    if (visited[currentIndex]) continue
                    visited[currentIndex] = true
                    if (cells[currentIndex] != null) continue
                    // If we are here then the current cell is unvisited and an empty gap, so
                    // check how big the gap is
                    var emptyCount = 1
                    val checkNeighbours: Queue<Coordinate> = LinkedList()
                    checkNeighbours.add(Coordinate(x, y, z))
                    while (!checkNeighbours.isEmpty()) {
                        val c = checkNeighbours.poll()
                        val coordinatesToVisit: List<Coordinate> = listOf(
                                Coordinate(c.x + 1, c.y, c.z),
                                Coordinate(c.x - 1, c.y, c.z),
                                Coordinate(c.x, c.y + 1, c.z),
                                Coordinate(c.x, c.y - 1, c.z),
                                Coordinate(c.x, c.y, c.z + 1),
                                Coordinate(c.x, c.y, c.z - 1),
                        )
                        for (adjacentCoordinate in coordinatesToVisit) {
                            if (!puzzle.isValidCoordinate(adjacentCoordinate)) continue
                            val adjacentIndex = puzzle.hashCoordinate(adjacentCoordinate)
                            if (visited[adjacentIndex]) continue
                            visited[adjacentIndex] = true
                            if (cells[adjacentIndex] == null) {
                                emptyCount++
                                checkNeighbours.add(adjacentCoordinate)
                            }
                        }
                    }
                    // If a gap is smaller than the smallest possible shape, it's dead
                    if (emptyCount < puzzle.minShapeSize) return true
                    // If the shapes are all the same size, and the gap is not a multiple
                    // of the shape size, then it's also dead
                    if (puzzle.minShapeSize == puzzle.maxShapeSize &&
                            emptyCount % puzzle.maxShapeSize != 0) return true
                }
            }
        }
        return false
    }
}

package woodpuzzle.model

import java.util.*
import kotlin.math.min

class Configuration {
    val puzzle: Puzzle
    val cells: Array<Shape?>
    var unusedShapes: Set<Shape>
        private set

    /**
     * The base constructor. Creates an empty configuration with
     * the given puzzle state.
     * @param puzzle A reference to the puzzle description.
     */
    constructor(puzzle: Puzzle) {
        this.puzzle = puzzle
        cells = arrayOfNulls(puzzle.totalCells)
        unusedShapes = HashSet(puzzle.shapes)
    }

    /**
     * The copy constructor. Creates a configuration that is a
     * copy of the given configuration that is entirely state
     * independent of the given configuration, i.e. changes to this
     * configuration will not affect the state of the given configuration.
     * @param configuration A reference to the configuration to copy
     */
    constructor(configuration: Configuration) {
        puzzle = configuration.puzzle
        cells = arrayOfNulls(configuration.cells.size)
        System.arraycopy(configuration.cells, 0, cells, 0, cells.size)
        unusedShapes = HashSet(configuration.unusedShapes)
    }

    fun allCellsFilled(): Boolean = !cells.contains(null)

    /**
     * Attempts to place the given shape in the position specified
     * by the given list of coordinates. The following must be true:
     * 1) The given coordinates are inside the box
     * 2) The given coordinates do not collide with a placed shape
     * 3) The list of coordinates is a rotation of the given shape
     * If the shape is placed successfully, it is removed from the set
     * of unused shapes and the given coordinates in the cells of this
     * configuration will correspond to the given shape.
     * @param shape A reference to the shape to place.
     * @param position The list of coordinates to place the shape.
     * @return true if the above conditions are true, otherwise false.
     */
    fun placeShape(shape: Shape, position: List<Coordinate>): Boolean {
        // Check whether the reference is even an unused shape.
        // Should never be false but don't really trust myself!
        if (!unusedShapes.contains(shape)) return false
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var minZ = Int.MAX_VALUE
        for (coordinate in position) {
            // Check whether the coordinate is still in the bounds of the puzzle box.
            if (!puzzle.isValidCoordinate(coordinate)) return false
            // Check whether this is a collision with an existing placement in the puzzle.
            if (cells[puzzle.hashCoordinate(coordinate)] != null) return false
            // find the offset to "pull" the piece into the origin corner for later comparison.
            minX = min(coordinate.x, minX)
            minY = min(coordinate.y, minY)
            minZ = min(coordinate.z, minZ)
        }
        val shapeTotal = puzzle.shapeSide * puzzle.shapeSide * puzzle.shapeSide
        val placeAttemptOnOrigin = IntArray(shapeTotal)
        for (i in 0 until shapeTotal) placeAttemptOnOrigin[i] = 0
        for (c in position) {
            placeAttemptOnOrigin[shape.hashCoordinate(c.vectorAdd(-minX, -minY, -minZ))] = 1
        }
        // Verifies the rotated shape actually matches the original shape's rotation
        // because I don't trust 2016 me.
        if (!isIdenticalRotatedShape(shape, placeAttemptOnOrigin)) {
            println("this should never print...")
            return false
        }
        // Place the shape and remove it from unused.
        for (c in position) {
            cells[puzzle.hashCoordinate(c)] = shape
        }
        unusedShapes = unusedShapes - shape
        return true
    }

    /**
     * Checks whether there exists a rotation of the first shape
     * that is identical to the second shape. It is assumed that s2
     * is already placed "against" the origin.
     * @param shape1 A reference to the first shape
     * @param shape2 The cells of the second shape
     * @return true if such a rotation exists, false otherwise.
     */
    private fun isIdenticalRotatedShape(shape1: Shape, shape2: IntArray): Boolean {
        for (yAxis in YAxis.values()) {
            for (zAxis in ZAxis.values()) {
                val rotatedCoordinates: IntArray = shape1.rotateShape(yAxis, zAxis)
                if (rotatedCoordinates.contentEquals(shape2)) return true
            }
        }
        return false
    }

    /**
     * Removes a shape from the set of unused shapes (discards it)
     * @param shape A reference to the shape to remove
     */
    fun removeShape(shape: Shape) {
        unusedShapes = unusedShapes - shape
    }
}
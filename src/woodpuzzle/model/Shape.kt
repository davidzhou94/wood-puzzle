package woodpuzzle.model

import kotlin.math.min

class Shape(
    private val sideLength: Int,
    private val coordinates: List<Coordinate>
): ThreeD {
    override val width = sideLength
    override val height = sideLength
    override val length = sideLength

    /**
     * Applies transform function to rotate the shape. The transform function is a
     * pre-composed function for the rotation in each of 3 axis. After computing
     * the rotation, the shape will be "pulled" into the origin such that the
     * smallest x, y, and z values for any coordinate in the shape are 0. Finally,
     * this will return an array representing the cells of the rotated shape. The
     * cells of this shape are unchanged.
     * @param transform function to transform coordinates in possibly many rotations
     * @return An array representing the cells of the rotated shape.
     */
    fun applyTransform(transform: RotationTransform): List<Coordinate> {
        // Apply the transform.
        val rotatedShape = coordinates.map { transform(sideLength, it.x, it.y, it.z) }

        // Determine the smallest x, y, and z values in order
        // to "pull" the shape into the origin.
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var minZ = Int.MAX_VALUE
        rotatedShape.forEach { (x, y, z) ->
            minX = min(minX, x)
            minY = min(minY, y)
            minZ = min(minZ, z)
        }

        return rotatedShape.map { it.vectorAdd(-minX, -minY, -minZ) }
    }
}

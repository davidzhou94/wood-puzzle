package woodpuzzle.model

class Puzzle(
        val width: Int,
        val height: Int,
        val length: Int,
        val shapeSide: Int,
        val shapeCount: Int,
        val minShapeSize: Int,
        val maxShapeSize: Int,
        val filledShapeCount: Int,
        val shapes: Set<Shape>
) {
    val totalCells: Int = width * height * length

    /**
     * Checks whether the given coordinate is valid for the
     * dimensions of this puzzle.
     * @param x The x-axis component (width).
     * @param y The y-axis component (height).
     * @param z The z-axis component (length).
     * @return true if the coordinate is valid, false otherwise.
     */
    fun isValidCoordinate(x: Int, y: Int, z: Int): Boolean =
        0 <= x && x < width && 0 <= y && y < height && 0 <= z && z < length

    fun isValidCoordinate(coordinate: Coordinate): Boolean =
        isValidCoordinate(coordinate.x, coordinate.y, coordinate.z)

    /**
     * Returns the hashed value of the given coordinate. This
     * is also the index of the coordinate in the cells array
     * of a configuration. It is assumed that the given
     * coordinate is valid.
     * @param x The x-axis component (width).
     * @param y The y-axis component (height).
     * @param z The z-axis component (length).
     * @return The hashed value.
     */
    fun hashCoordinate(x: Int, y: Int, z: Int): Int = x + width * y + z * (width * height)

    fun hashCoordinate(c: Coordinate): Int = hashCoordinate(c.x, c.y, c.z)
}
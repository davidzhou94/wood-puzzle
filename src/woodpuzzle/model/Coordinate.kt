package woodpuzzle.model

/**
 * Creates a coordinate with the given x, y, and z components.
 * @param x The x component.
 * @param y The y component.
 * @param z The z component.
 */
data class Coordinate (
    val x: Int, // width
    val y: Int, // height
    val z: Int  // length
) {
    /**
     * Creates a coordinate that has the usual vector
     * addition applied to it.
     * @param x The x component.
     * @param y The y component.
     * @param z The z component.
     * @return The transformed coordinate (a new coordinate instance).
     */
    fun vectorAdd(x: Int, y: Int, z: Int): Coordinate =
        Coordinate(this.x + x, this.y + y, this.z + z)
}
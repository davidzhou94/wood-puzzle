package woodpuzzle.model

/**
 * Creates a coordinate with the given x, y, and z components.
 * @param x The x component.
 * @param y The y component.
 * @param z The z component.
 */
class Coordinate (
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
    fun vectorAdd(x: Int, y: Int, z: Int): Coordinate {
        return Coordinate(this.x + x, this.y + y, this.z + z)
    }

    companion object {
        /**
         * Creates a coordinate from a string representation
         * of a coordinate of the form "x,y,z"
         * @param text The string representation.
         * @return The newly built coordinate.
         */
        fun buildCoordinate(text: String): Coordinate? {
            val result = text.split(",").toTypedArray()
            return if (result.size != 3)
                null
            else
                Coordinate(result[0].toInt(), result[1].toInt(), result[2].toInt())
        }
    }
}
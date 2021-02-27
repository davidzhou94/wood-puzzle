package woodpuzzle.model

import kotlin.math.min

class Shape(val sideLength: Int, coordinates: List<Coordinate>) {
    private val total: Int = sideLength * sideLength * sideLength
    private val cells: IntArray = IntArray(total)

    /**
     * Initialized the cells of this shape but setting the cells
     * with shape coordinates to 1.
     */
    init {
        for (c in coordinates) {
            cells[this.hashCoordinate(c)] = 1
        }
    }

    /**
     * Used with rotate shape to map cells from a given source array
     * to a given target array using the given lambda expression to transform
     * a coordinate in the target array to a source array index.
     * @param source
     * @param transform
     */
    private fun rotationTransform(source: IntArray, transform: RotationTransform): IntArray {
        val destination = IntArray(total)
        for (sx in 0 until sideLength) {
            for (sy in 0 until sideLength) {
                for (sz in 0 until sideLength) {
                    val (x, y, z) = transform(sideLength, sx, sy, sz)
                    val sourceIndex = this.hashCoordinate(sx, sy, sz)
                    destination[this.hashCoordinate(x, y, z)] = source[sourceIndex]
                }
            }
        }
        return destination
    }

    /**
     * Rotates this shape the given number of rotations along each of two
     * possible axis. Each rotation is 90deg clockwise in the axis facing
     * the origin. For example, rotateShape(0, 2) will rotate the shape
     * 180deg clockwise in the z-axis when facing the origin. After computing
     * the rotation, this will "pull" the shape into the origin such that the
     * smallest possible x, y, and z values are 0. Finally, this will return
     * an array representing the cells of the rotated shape. The cells of
     * this shape are unchanged.
     * @param transform function to transform coordinates in possibly many rotations
     * @return An array representing the cells of the rotated shape.
     */
    fun rotateShape(transform: RotationTransform): IntArray {
        // Apply the transform
        val rotatedShape = rotationTransform(cells, transform)

        // determine the smallest x, y, and z values in order
        // to "pull" the shape into the origin.
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var minZ = Int.MAX_VALUE
        for (x in 0 until sideLength) {
            for (y in 0 until sideLength) {
                for (z in 0 until sideLength) {
                    if (rotatedShape[this.hashCoordinate(x, y, z)] == 1) {
                        minX = min(minX, x)
                        minY = min(minY, y)
                        minZ = min(minZ, z)
                    }
                }
            }
        }

        val originAligned = IntArray(total)

        // pull the shape into the origin.
        for (x in 0 until sideLength) {
            for (y in 0 until sideLength) {
                for (z in 0 until sideLength) {
                    if (x < sideLength - minX && y < sideLength - minY && z < sideLength - minZ) {
                        originAligned[this.hashCoordinate(x, y, z)] =
                            rotatedShape[this.hashCoordinate(x + minX, y + minY, z + minZ)]
                    }
                }
            }
        }
        return originAligned
    }

    /**
     * Returns the hashed value of the given coordinate. This
     * is also the index of the coordinate in the cells array
     * of this shape. It is assumed that the given coordinate is valid.
     * @param x The x-axis component (width).
     * @param y The y-axis component (height).
     * @param z The z-axis component (length).
     * @return The hashed value.
     */
    fun hashCoordinate(x: Int, y: Int, z: Int): Int = x + sideLength * y + z * (sideLength * sideLength)

    fun hashCoordinate(c: Coordinate): Int = hashCoordinate(c.x, c.y, c.z)
}

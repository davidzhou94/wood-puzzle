package woodpuzzle.model

import kotlin.math.min

class Shape(val sideLength: Int, coordinates: List<Coordinate>) {
    private val total: Int = sideLength * sideLength * sideLength
    private val cells: IntArray = IntArray(total)

    /**
     * Initialized the cells of this shape by setting all cells to 0.
     */
    init {
        for (i in 0 until total) {
            cells[i] = 0
        }
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
        for (x in 0 until sideLength) {
            for (y in 0 until sideLength) {
                for (z in 0 until sideLength) {
                    val (sx, sy, sz) = transform(sideLength, x, y, z)
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
     * @param yAxis Number of 90deg clockwise rotations along the y-axis.
     * @param zAxis Number of 90deg clockwise rotations along the z-axis.
     * @return An array representing the cells of the rotated shape.
     */
    fun rotateShape(yAxis: YAxis, zAxis: ZAxis): IntArray {
        // Take original shape in cells and rotate it in the y-axis, storing result in firstIteration
        val firstIteration = rotationTransform(cells, yAxis.transform)
        // Take y-axis rotated shape in firstIteration and rotate it in the z-axis,
        // storing result in finalIteration
        val finalIteration = rotationTransform(firstIteration, zAxis.transform)

        // determine the smallest x, y, and z values in order
        // to "pull" the shape into the origin.
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var minZ = Int.MAX_VALUE
        for (x in 0 until sideLength) {
            for (y in 0 until sideLength) {
                for (z in 0 until sideLength) {
                    if (finalIteration[this.hashCoordinate(x, y, z)] == 1) {
                        minX = min(minX, x)
                        minY = min(minY, y)
                        minZ = min(minZ, z)
                    }
                }
            }
        }

        // pull the shape into the origin.
        for (x in 0 until sideLength) {
            for (y in 0 until sideLength) {
                for (z in 0 until sideLength) {
                    if (x < sideLength - minX && y < sideLength - minY && z < sideLength - minZ) {
                        finalIteration[this.hashCoordinate(x, y, z)] = finalIteration[this.hashCoordinate(x + minX, y + minY, z + minZ)]
                    } else {
                        finalIteration[this.hashCoordinate(x, y, z)] = 0
                    }
                }
            }
        }
        return finalIteration
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

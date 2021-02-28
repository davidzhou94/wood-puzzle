package woodpuzzle.model

typealias RotationTransform = (Int, Int, Int, Int) -> Coordinate

interface Axis {
    val steps: Int
    val transform: RotationTransform
}

enum class XAxis(override val steps: Int, override val transform: RotationTransform): Axis {
    X_000(0, { _, x, y, z -> Coordinate(x, y, z) }),
    X_090(1, { l, x, y, z -> Coordinate(x, z, l - y - 1) }),
    X_180(2, { l, x, y, z -> Coordinate(x, l - y - 1, l - z - 1) }),
    X_270(3, { l, x, y, z -> Coordinate(x, l - z - 1, y) });
}

/*
 * Note that this is a counterclockwise rotation
 * Consider formulating as applying steps of 90deg clockwise
 * rotations to achieve 180 and 270
 */
enum class YAxis(override val steps: Int, override val transform: RotationTransform): Axis {
    Y_000(0, { _, x, y, z -> Coordinate(x, y, z)}),
    Y_090(1, { l, x, y, z -> Coordinate(l - z - 1, y, x)}),
    Y_180(2, { l, x, y, z -> Coordinate(l - x - 1, y, l - z - 1)}),
    Y_270(3, { l, x, y, z -> Coordinate(z, y, l - x - 1)});
}

enum class ZAxis(override val steps: Int, override val transform: RotationTransform): Axis {
    Z_000(0, { _, x, y, z -> Coordinate(x, y, z)}),
    Z_090(1, { l, x, y, z -> Coordinate(y, l - x - 1, z)}),
    Z_180(2, { l, x, y, z -> Coordinate(l - x - 1, l - y - 1, z)}),
    Z_270(3, { l, x, y, z -> Coordinate( l - y - 1, x, z)});
}

fun composeTransforms(vararg transforms: RotationTransform) =
    transforms.reduce { acc, function ->
        { l, x, y, z ->
            val (xi, yi, zi) = function(l, x, y, z)
            acc(l, xi, yi, zi)
        }
    }

/**
 * I don't have definitive proof but it seems that rotations at
 * 180 and 270 in 1 axis are duplicates of 0 and 90 that can be
 * reached with rotations in the other 2 axis. See rotations.xlsx
 */
val CACHED_TRANSFORMS = listOf(XAxis.X_000, XAxis.X_090)
    .map { xAxis ->
        YAxis.values().map { yAxis ->
            ZAxis.values().map { zAxis ->
                Triple(xAxis, yAxis, zAxis)
            }
        }.flatten()
    }.flatten()
    .map { (xAxis, yAxis, zAxis) ->
        composeTransforms(xAxis.transform, yAxis.transform, zAxis.transform)
    }

interface ThreeD {
    val width: Int
    val height: Int
    val length: Int

    /**
     * Checks whether the given coordinate is within the bounds of this 3D box.
     */
    fun isValidCoordinate(x: Int, y: Int, z: Int): Boolean =
        0 <= x && x < width && 0 <= y && y < height && 0 <= z && z < length

    fun isValidCoordinate(coordinate: Coordinate): Boolean =
        isValidCoordinate(coordinate.x, coordinate.y, coordinate.z)

    fun hashCoordinate(x: Int, y: Int, z: Int): Int = x + width * y + z * (width * height)

    fun hashCoordinate(c: Coordinate): Int = hashCoordinate(c.x, c.y, c.z)
}

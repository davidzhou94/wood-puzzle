package woodpuzzle.model

typealias RotationTransform = (Int, Int, Int, Int) -> Triple<Int, Int, Int>

enum class XAxis(val steps: Int, val transform: RotationTransform) {
    X_000(0, { _, x, y, z -> Triple(x, y, z)}),
    X_090(1, { l, x, y, z -> Triple(x, z, l - y - 1)}),
    X_180(2, { l, x, y, z -> Triple(x, l - y - 1, l - z - 1)}),
    X_270(3, { l, x, y, z -> Triple(x, l - z - 1, y)});
}

/*
 * Note that this is a counterclockwise rotation
 * Consider formulating as applying steps of 90deg clockwise
 * rotations to achieve 180 and 270
 */
enum class YAxis(val steps: Int, val transform: RotationTransform) {
    Y_000(0, { _, x, y, z -> Triple(x, y, z)}),
    Y_090(1, { l, x, y, z -> Triple(l - z - 1, y, x)}),
    Y_180(2, { l, x, y, z -> Triple(l - x - 1, y, l - z - 1)}),
    Y_270(3, { l, x, y, z -> Triple(z, y, l - x - 1)});
}

enum class ZAxis(val steps: Int, val transform: RotationTransform) {
    Z_000(0, { _, x, y, z -> Triple(x, y, z)}),
    Z_090(1, { l, x, y, z -> Triple(y, l - x - 1, z)}),
    Z_180(2, { l, x, y, z -> Triple(l - x - 1, l - y - 1, z)}),
    Z_270(3, { l, x, y, z -> Triple( l - y - 1, x, z)});
}

fun composeTransforms(xAxis: XAxis, yAxis: YAxis, zAxis: ZAxis): RotationTransform {
    return { l, x0, y0, z0 ->
        val (x1, y1, z1) = xAxis.transform(l, x0, y0, z0)
        val (x2, y2, z2) = yAxis.transform(l, x1, y1, z1)
        zAxis.transform(l, x2, y2, z2)
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
    .map { (xAxis, yAxis, zAxis) -> composeTransforms(xAxis, yAxis, zAxis) }


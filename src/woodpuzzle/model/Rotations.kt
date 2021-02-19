package woodpuzzle.model

typealias RotationTransform = (Int, Int, Int, Int) -> Triple<Int, Int, Int>

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
    Z_270(3, { l, x, y, z -> Triple(l - y - 1, x, z)});
}
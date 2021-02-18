package woodpuzzle.model

typealias RotationTransform = (Int, Int, Int, Int) -> Triple<Int, Int, Int>

enum class YAxis(
    val degrees: Int,
    val transform: RotationTransform
) {
    ZERO(0, {_, x, y, z -> Triple(x, y, z)}),
    ONE(90, {l, x, y, z -> Triple(l - z - 1, y, x)}),
    TWO(180, {l, x, y, z -> Triple(l - x - 1, y, l - z - 1)}),
    THREE(270, {l, x, y, z -> Triple(z, y, l - x - 1)});
}

enum class ZAxis(
    val degrees: Int,
    val transform: RotationTransform
) {
    ZERO(0, {_, x, y, z -> Triple(x, y, z)}),
    ONE(90, {l, x, y, z -> Triple(y, l - x - 1, z)}),
    TWO(180, {l, x, y, z -> Triple(l - x - 1, l - y - 1, z)}),
    THREE(270, {l, x, y, z -> Triple(l - y - 1, x, z)});
}
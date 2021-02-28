package woodpuzzle.model

class Puzzle(
    override val width: Int,
    override val height: Int,
    override val length: Int,
    val minShapeSize: Int,
    val maxShapeSize: Int,
    val shapes: Set<Shape>
): ThreeD {
    val totalCells: Int = width * height * length
}
package woodpuzzle.model

class Configuration(
    val puzzle: Puzzle,
    val cells: Array<Shape?>,
    val unusedShapes: Set<Shape>
) {

    /**
     * The base constructor. Creates an empty configuration with
     * the given puzzle state.
     * @param puzzle A reference to the puzzle description.
     */
    constructor(puzzle: Puzzle) : this(
        puzzle = puzzle,
        cells = arrayOfNulls(puzzle.totalCells),
        unusedShapes = puzzle.shapes
    )

    fun allCellsFilled(): Boolean = !cells.contains(null)

    /**
     * Attempts to place the given shape in the position specified
     * by the given list of coordinates. The following must be true:
     * 1) The given coordinates are inside the box
     * 2) The given coordinates do not collide with a placed shape
     * If the shape is placed successfully, a new configuration object is
     * created without the placed shape in the set of unused shapes and
     * the given coordinates in the cells of the new configuration will
     * point to the given shape.
     * @param shape A reference to the shape to place.
     * @param placement The list of coordinates to place the shape.
     * @return a new configuration if the above conditions are true, otherwise null.
     */
    fun placeShape(shape: Shape, placement: List<Coordinate>): Configuration? {
        // Check whether the reference is even an unused shape.
        // Should never be false but don't really trust myself!
        if (!unusedShapes.contains(shape)) return null
        for (coordinate in placement) {
            // Check whether the coordinate is still in the bounds of the puzzle box.
            if (!puzzle.isValidCoordinate(coordinate)) return null
            // Check whether this is a collision with an existing placement in the puzzle.
            if (cells[puzzle.hashCoordinate(coordinate)] != null) return null
        }
        // At this point we know the shape can be placed. Create a copy of this configuration.
        val newConfig = Configuration(
            puzzle = this.puzzle,
            cells = this.cells.copyOf(),
            unusedShapes = this.unusedShapes - shape
        )
        // Place the shape and remove it from unused in the new configuration.
        for (c in placement) {
            newConfig.cells[puzzle.hashCoordinate(c)] = shape
        }
        return newConfig
    }
}
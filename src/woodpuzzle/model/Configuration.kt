package woodpuzzle.model

data class Configuration(
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
        val newConfig = this.copy(cells = this.cells.copyOf(), unusedShapes = this.unusedShapes - shape)
        // Place the shape and remove it from unused in the new configuration.
        for (c in placement) newConfig.cells[puzzle.hashCoordinate(c)] = shape
        return newConfig
    }

    /**
     * Checks whether this configuration has dead cells. That is,
     * if a group of empty and connected cells is smaller than the
     * given minimum shape size, then it is dead. Furthermore,
     * if all shapes are of identical size then a similar group with
     * the number of empty cells not a multiple of the shape size is
     * also considered dead.
     * @return true if there are dead cells, otherwise false.
     */
    fun hasDeadCells(): Boolean {
        val visited = BooleanArray(puzzle.totalCells) // Inits to false
        for (x in 0 until puzzle.width) {
            for (y in 0 until puzzle.height) {
                for (z in 0 until puzzle.length) {
                    // Iterates over every cell of the box looking for an unvisited, empty gap.
                    val currentIndex = puzzle.hashCoordinate(x, y, z)
                    if (visited[currentIndex]) continue
                    visited[currentIndex] = true
                    if (cells[currentIndex] != null) continue
                    // If we are here then the current cell is unvisited and an empty gap, so
                    // count and visit the whole gap.
                    var emptyCount = 1
                    val checkNeighbours: MutableList<Coordinate> = mutableListOf(Coordinate(x, y, z))
                    while (checkNeighbours.isNotEmpty()) {
                        val c = checkNeighbours.removeFirst()
                        val coordinatesToVisit: List<Coordinate> = listOf(
                            Coordinate(c.x + 1, c.y, c.z),
                            Coordinate(c.x - 1, c.y, c.z),
                            Coordinate(c.x, c.y + 1, c.z),
                            Coordinate(c.x, c.y - 1, c.z),
                            Coordinate(c.x, c.y, c.z + 1),
                            Coordinate(c.x, c.y, c.z - 1),
                        )
                        for (adjacentCoordinate in coordinatesToVisit) {
                            if (!puzzle.isValidCoordinate(adjacentCoordinate)) continue
                            val adjacentIndex = puzzle.hashCoordinate(adjacentCoordinate)
                            if (visited[adjacentIndex]) continue
                            visited[adjacentIndex] = true
                            if (cells[adjacentIndex] == null) {
                                emptyCount++
                                checkNeighbours.add(adjacentCoordinate)
                            }
                        }
                    }
                    // If a gap is smaller than the smallest possible shape, it's dead
                    if (emptyCount < puzzle.minShapeSize) return true
                    // If the shapes are all the same size, and the gap is not a multiple
                    // of the shape size, then it's also dead
                    if (puzzle.minShapeSize == puzzle.maxShapeSize &&
                        emptyCount % puzzle.maxShapeSize != 0) return true
                }
            }
        }
        return false
    }
}

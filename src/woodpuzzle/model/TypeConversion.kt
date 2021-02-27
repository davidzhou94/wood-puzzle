package woodpuzzle.model

fun unhashCoordinate(sideLength: Int, index: Int): Coordinate {
    val x = index % sideLength
    val y = (index / sideLength) % sideLength
    val z = index / (sideLength * sideLength)

    return Coordinate(x, y, z)
}

fun shapeArrayToCoordinateList(sideLength: Int, array: IntArray): List<Coordinate> =
    array.toList()
        .mapIndexedNotNull {
            index, value -> if (value > 0) unhashCoordinate(sideLength, index) else null
        }

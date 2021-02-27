package woodpuzzle.util

import woodpuzzle.model.Configuration
import woodpuzzle.model.Shape

/**
 * Prints out the given configuration.
 */
fun printSolution(configuration: Configuration?) {
    if (configuration == null) {
        println("\nNo solution found")
        return
    }
    println("\nSolution found:\n")
    val cells = configuration.cells
    val shapeToCharMap: MutableMap<Shape?, Char> = HashMap()
    shapeToCharMap[null] = '0'
    var currentChar = 'A'
    for (y in 0 until configuration.puzzle.height) {
        for (x in 0 until configuration.puzzle.width) {
            for (z in 0 until configuration.puzzle.length) {
                val shape = cells[configuration.puzzle.hashCoordinate(x, y, z)]
                if (!shapeToCharMap.containsKey(shape)) {
                    shapeToCharMap[shape] = currentChar
                    currentChar++
                }
                print(shapeToCharMap[shape])
                print(" ")
            }
            println()
        }
        println()
    }
}

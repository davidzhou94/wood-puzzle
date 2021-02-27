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

class Timer(private val start: Long = System.currentTimeMillis()) {
    fun printElapsed() {
        val elapsed = System.currentTimeMillis() - start
        val second = elapsed / 1000 % 60
        val minute = elapsed / (1000 * 60) % 60
        val hour = elapsed / (1000 * 60 * 60) % 24
        val millis = elapsed % 1000
        val time = String.format("%02d:%02d:%02d:%03d", hour, minute, second, millis)
        println("\nTime elapsed: $time")
    }
}

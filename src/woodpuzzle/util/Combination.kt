package woodpuzzle.util

import kotlin.collections.HashSet
import kotlin.collections.MutableSet

class Combinations<T> {
    private val output: MutableSet<Set<T>> = HashSet()

    fun generate(input: Set<T>, subsetSize: Int): Set<Set<T>> {
        recurse(input.toList(), subsetSize, 0, ArrayList())
        return output
    }

    private fun recurse(input: List<T>, subsetSize: Int, startPosition: Int, result: MutableList<T?>) {
        if (subsetSize == 0) {
            output.add(result.mapNotNull { it }.toSet())
            return
        }
        for (i in startPosition..input.size - subsetSize) {
            result[result.size - subsetSize] = input[i]
            recurse(input, subsetSize - 1, i + 1, result)
        }
    }
}
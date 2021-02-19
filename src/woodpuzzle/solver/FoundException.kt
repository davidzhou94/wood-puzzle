package woodpuzzle.solver

import woodpuzzle.model.Configuration

/**
 * The exception thrown when a solution is found.
 * @author david
 */
data class FoundException(val config: Configuration) : RuntimeException()
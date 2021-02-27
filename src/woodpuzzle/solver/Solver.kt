package woodpuzzle.solver

import woodpuzzle.model.Configuration

interface Solver {
    fun findSolution(): Configuration?
}
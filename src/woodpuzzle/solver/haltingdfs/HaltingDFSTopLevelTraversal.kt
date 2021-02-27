package woodpuzzle.solver.haltingdfs

import woodpuzzle.model.Configuration
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import woodpuzzle.solver.EndException
import woodpuzzle.solver.FoundException
import woodpuzzle.solver.Traversal
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * The HaltingDFS strategy for "top-level" traversals of the node
 * configuration tree. This traversal submits a HaltingDFSDescentThread
 * for each top-level configuration node (i.e. each choice of first
 * shape placement).
 * @author david
 */
class HaltingDFSTopLevelTraversal(
    override val puzzle: Puzzle,
    private val solver: HaltingDFSSolver
) : Traversal {
    private val rng = Random(Random.nextLong())
    private val executor: ExecutorService
    private var futures: List<Future<*>> = emptyList()

    init {
        val threadPoolSize = Runtime.getRuntime().availableProcessors() * 2
        println("Creating a thread pool with $threadPoolSize threads")
        this.executor = Executors.newFixedThreadPool(threadPoolSize)
    }

    override fun preTraversal(currentConfig: Configuration) { /* do nothing */ }

    override fun determineShape(currentConfig: Configuration): Shape =
        currentConfig.unusedShapes.random(rng)

    override fun placementFailedGeometry() { /* do nothing */ }

    override fun placementFailedDeadCells() { /* do nothing */ }

    override fun placementSucceeded(newConfig: Configuration) {
        val traversal = HaltingDFSDescentTraversal(puzzle, solver)
        futures = futures + executor.submit {
            try {
                traversal.traverse(newConfig)
            } catch (e: FoundException) {
                solver.reportSolution(e.config)
            } catch (e: EndException) {
                solver.reportAbandonedTraversal(traversal.minUnusedShapes)
            }
        }
    }

    fun running() = futures.find { !it.isDone } != null

    fun stop() {
        executor.shutdownNow()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }
}

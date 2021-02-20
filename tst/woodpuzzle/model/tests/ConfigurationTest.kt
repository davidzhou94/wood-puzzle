package woodpuzzle.model.tests

import org.junit.Assert
import org.junit.Test
import woodpuzzle.model.Configuration
import woodpuzzle.model.Coordinate
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape

class ConfigurationTest {
    companion object {
        const val WIDTH = 5
        const val HEIGHT = 2
        const val LENGTH = 5
        const val SHAPE_SIDE = 3
        const val MIN_SHAPE_SIZE = 5
        const val MAX_SHAPE_SIZE = 5
    }
    
    private val s1 = Shape(SHAPE_SIDE,
        listOf(
            Coordinate(0, 0, 0),
            Coordinate(1, 0, 0),
            Coordinate(1, 0, 1),
            Coordinate(1, 1, 0),
            Coordinate(1, 1, 1),
        ))
    private val s2 = Shape(SHAPE_SIDE,
        listOf(
            Coordinate(0, 0, 0),
            Coordinate(1, 0, 0),
            Coordinate(2, 0, 0),
            Coordinate(1, 1, 0),
            Coordinate(1, 0, 1),
        ))
    private val s3 = Shape(SHAPE_SIDE,
        listOf(
            Coordinate(0, 0, 0),
            Coordinate(1, 0, 0),
            Coordinate(0, 0, 1),
            Coordinate(1, 1, 0),
            Coordinate(0, 1, 1),
        ))
    private val shapes = setOf(s1, s2, s3)
    private val p = Puzzle(WIDTH, HEIGHT, LENGTH, SHAPE_SIDE, MIN_SHAPE_SIZE, MAX_SHAPE_SIZE, shapes)

    @Test
    fun test() {
        val c0 = Configuration(p)
        Assert.assertTrue(c0.unusedShapes.contains(s3))
        val placement1 = listOf(
            Coordinate(0, 0, 0),
            Coordinate(0, 1, 0),
            Coordinate(0, 0, 1),
            Coordinate(1, 0, 1),
            Coordinate(1, 1, 1), 
        )
        Assert.assertTrue(c0.placeShape(s3, placement1))
        // Can't place the same shape again.
        Assert.assertFalse(c0.placeShape(s3, placement1))
        val c1 = Configuration(c0)
        // This overlaps with the s3 that was just placed at (1,1,1).
        val placement2 = listOf(
            Coordinate(1, 1, 1),
            Coordinate(1, 1, 2),
            Coordinate(1, 0, 2),
            Coordinate(2, 1, 2),
            Coordinate(2, 0, 2),
        )
        Assert.assertFalse(c1.placeShape(s1, placement2))
        // Now try placing s1 in a valid position
        val placement3 = listOf(
            Coordinate(0, 1, 1),
            Coordinate(0, 1, 2),
            Coordinate(0, 0, 2),
            Coordinate(1, 1, 2),
            Coordinate(1, 0, 2),
        )
        // But first try s2, which shouldn't match a rotation of s1.
        Assert.assertFalse(c1.placeShape(s2, placement3))
        Assert.assertTrue(c1.placeShape(s1, placement3))
        // Copy the configuration, c1 has s1 and s3
        val c2 = Configuration(c1)
        Assert.assertFalse(c2.unusedShapes.isEmpty())
        // place s2 in c2
        val placement4 = listOf(
            Coordinate(1, 0, 0),
            Coordinate(2, 0, 0),
            Coordinate(3, 0, 0),
            Coordinate(2, 1, 0),
            Coordinate(2, 0, 1),
        )
        Assert.assertTrue(c2.placeShape(s2, placement4))
        Assert.assertTrue(c2.unusedShapes.isEmpty())
        // c1 should not have changed
        Assert.assertFalse(c1.unusedShapes.isEmpty())
        Assert.assertTrue(c1.unusedShapes.contains(s2))
    }
}
package woodpuzzle.model.tests

import org.junit.Assert
import org.junit.Test
import woodpuzzle.model.*

class GeometryTest {
    @Test
    fun test() {
        val sideLength = 3

        var culmulativeX: RotationTransform = { _, x, y, z -> Coordinate(x, y, z) }
        var culmulativeY: RotationTransform = { _, x, y, z -> Coordinate(x, y, z) }
        var culmulativeZ: RotationTransform = { _, x, y, z -> Coordinate(x, y, z) }

        for (step in 0 until 4) {
            val xAxis = XAxis.values().find { it.steps == step }!!
            val yAxis = YAxis.values().find { it.steps == step }!!
            val zAxis = ZAxis.values().find { it.steps == step }!!

            for (x in 0 until sideLength) {
                for (y in 0 until sideLength) {
                    for (z in 0 until sideLength) {
                        Assert.assertEquals(culmulativeX(sideLength, x, y, z), xAxis.transform(sideLength, x, y, z))
                        Assert.assertEquals(culmulativeY(sideLength, x, y, z), yAxis.transform(sideLength, x, y, z))
                        Assert.assertEquals(culmulativeZ(sideLength, x, y, z), zAxis.transform(sideLength, x, y, z))
                    }
                }
            }

            culmulativeX = composeTransforms(culmulativeX, XAxis.X_090.transform)
            culmulativeY = composeTransforms(culmulativeY, YAxis.Y_090.transform)
            culmulativeZ = composeTransforms(culmulativeZ, ZAxis.Z_090.transform)
        }
    }
}

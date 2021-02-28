package woodpuzzle.engine

import org.w3c.dom.Node
import woodpuzzle.model.Coordinate
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object XMLReader {
    /**
     * Creates a coordinate from a string representation
     * of a coordinate of the form "x,y,z"
     * @param text The string representation.
     * @return The newly built coordinate.
     */
    private fun parseCoordinate(text: String): Coordinate? {
        val result = text.split(",").toTypedArray()
        return if (result.size != 3)
            null
        else
            Coordinate(result[0].toInt(), result[1].toInt(), result[2].toInt())
    }

    /**
     * Utility method for parsing a single Shape from the given
     * XML file. Called only from buildPuzzle.
     * @param sideLength The side length of the cube that contains the largest shape.
     * @param shapeNode The XML node containing the information for the given shape.
     * @return A new instance of Shape.
     */
    private fun parseShape(sideLength: Int, shapeNode: Node): Shape {
        val shapeCells = (0 until shapeNode.childNodes.length)
                .map { index -> shapeNode.childNodes.item(index) }
                .filter { it.nodeName == "Coordinate" && it.nodeType == Node.ELEMENT_NODE }
                .mapNotNull { parseCoordinate(it.textContent) }
        return Shape(sideLength, shapeCells)
    }

    /**
     * Utility method for reading in an XML file and building an
     * instance of a puzzle from the XML specification.
     * @param filePath The path to the file.
     * @return A new instance of Puzzle.
     */
    fun buildPuzzle(filePath: String): Puzzle {
        val xmlFile = File(filePath)
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(xmlFile)
        val rootElement = document.documentElement
        rootElement.normalize()
        val sideLength: Int = rootElement.getAttribute("shapeSide").toInt()
        val shapeList = document.getElementsByTagName("Shape")
        val shapes = (0 until shapeList.length)
                .map { index -> parseShape(sideLength, shapeList.item(index)) }
                .toSet()
        return Puzzle(
            width = rootElement.getAttribute("width").toInt(),
            height = rootElement.getAttribute("height").toInt(),
            length = rootElement.getAttribute("length").toInt(),
            minShapeSize = rootElement.getAttribute("minShapeSize").toInt(),
            maxShapeSize = rootElement.getAttribute("maxShapeSize").toInt(),
            shapes = shapes
        )
    }
}

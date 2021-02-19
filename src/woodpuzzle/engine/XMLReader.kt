package woodpuzzle.engine

import org.w3c.dom.Node
import org.xml.sax.SAXException
import woodpuzzle.model.Coordinate
import kotlin.Throws
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException
import woodpuzzle.model.Puzzle
import woodpuzzle.model.Shape
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object XMLReader {
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
                .mapNotNull { Coordinate.buildCoordinate(it.textContent) }
        return Shape(sideLength, shapeCells)
    }

    /**
     * Utility method for reading in an XML file and building an
     * instance of a puzzle from the XML specification.
     * @param filePath The path to the file.
     * @return A new instance of Puzzle.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Throws(SAXException::class, IOException::class, ParserConfigurationException::class)
    fun buildPuzzle(filePath: String): Puzzle {
        val xmlFile = File(filePath)
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = documentBuilder.parse(xmlFile)
        val rootElement = document.documentElement
        rootElement.normalize()
        val shapeSide: Int = rootElement.getAttribute("shapeSide").toInt()
        val shapeList = document.getElementsByTagName("Shape")
        val shapes = (0 until shapeList.length)
                .map { index -> parseShape(shapeSide, shapeList.item(index)) }
                .toSet()
        return Puzzle(
                width = rootElement.getAttribute("width").toInt(),
                height = rootElement.getAttribute("height").toInt(),
                length = rootElement.getAttribute("length").toInt(),
                shapeSide = shapeSide,
                minShapeSize = rootElement.getAttribute("minShapeSize").toInt(),
                maxShapeSize = rootElement.getAttribute("maxShapeSize").toInt(),
                shapes = shapes
        )
    }
}

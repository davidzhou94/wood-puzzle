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
    @JvmStatic
	@Throws(SAXException::class, IOException::class, ParserConfigurationException::class)
    fun buildPuzzle(filePath: String): Puzzle {
        val fXmlFile = File(filePath)
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(fXmlFile)
        val rootElem = doc.documentElement
        rootElem.normalize()
        val width: Int = rootElem.getAttribute("width").toInt()
        val height: Int = rootElem.getAttribute("height").toInt()
        val length: Int = rootElem.getAttribute("length").toInt()
        val shapeSide: Int = rootElem.getAttribute("shapeSide").toInt()
        val shapeCount: Int = rootElem.getAttribute("shapeCount").toInt()
        val minShapeSize: Int = rootElem.getAttribute("minShapeSize").toInt()
        val maxShapeSize: Int = rootElem.getAttribute("maxShapeSize").toInt()
        val minShapesToFill: Int = rootElem.getAttribute("minShapesToFill").toInt()
        val puzzle = Puzzle(width, height, length, shapeSide,
                shapeCount, minShapeSize, maxShapeSize, minShapesToFill)
        val nList = doc.getElementsByTagName("Shape")
        for (i in 0 until nList.length) {
            puzzle.addShape(parseShape(shapeSide, nList.item(i)))
        }
        return puzzle
    }
}

package woodpuzzle.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import woodpuzzle.model.Coordinate;
import woodpuzzle.model.Puzzle;
import woodpuzzle.model.Shape;

public class XMLReader {
	private XMLReader() { }
	
	/**
	 * Utility method for parsing a single Shape from the given
	 * XML file. Called only from buildPuzzle.
	 * @param sideLength The side length of the cube that contains the largest shape.
	 * @param n The XML node containing the information for the given shape.
	 * @return A new instance of Shape.
	 */
	private static Shape parseShape(int sideLength, Node n) {
		List<Coordinate> shapeCells = new ArrayList<Coordinate>();
		NodeList nList = n.getChildNodes();
		for (int i = 0; i < nList.getLength(); i++) {
			Node child = nList.item(i);
			if (child.getNodeName().equals("Coordinate") && child.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) child;
				Coordinate c = Coordinate.buildCoordinate(e.getTextContent());
				if (c != null)
					shapeCells.add(c);
			}
		}
		
		return new Shape(sideLength, shapeCells);
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
	public static Puzzle buildPuzzle(String filePath) throws SAXException, IOException, ParserConfigurationException {
		
		Puzzle puzzle = null;

		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		Element rootElem = doc.getDocumentElement(); 
		rootElem.normalize();
		
		int width, height, length, shapeSide, shapeCount, minShapeSize, maxShapeSize, minShapeFit;
		width = Integer.parseInt(rootElem.getAttribute("width"));
		height = Integer.parseInt(rootElem.getAttribute("height"));
		length = Integer.parseInt(rootElem.getAttribute("length"));
		
		shapeSide = Integer.parseInt(rootElem.getAttribute("shapeSide"));
		shapeCount = Integer.parseInt(rootElem.getAttribute("shapeCount"));
		minShapeSize = Integer.parseInt(rootElem.getAttribute("minShapeSize"));
		maxShapeSize = Integer.parseInt(rootElem.getAttribute("maxShapeSize"));
		minShapeFit = Integer.parseInt(rootElem.getAttribute("minShapeFit"));
			
		puzzle = new Puzzle(width, height, length, shapeSide, 
				shapeCount, minShapeSize, maxShapeSize, minShapeFit);
		NodeList nList = doc.getElementsByTagName("Shape");
		for (int i = 0; i < nList.getLength(); i++) {
			puzzle.addShape(parseShape(shapeSide, nList.item(i)));
		}

		return puzzle;
	}
}

package woodPuzzle.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import woodPuzzle.model.Coordinate;
import woodPuzzle.model.Puzzle;

public class XMLReader {
	private XMLReader() { }
	
	private static List<Coordinate> parseShape(int width, int height, int length, Node n) {
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
		return shapeCells;
	}

	public static Puzzle buildPuzzle(String filePath) {
		int width = 0, height = 0, length = 0;
		Puzzle puzzle = null;
		try {
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			Element rootElem = doc.getDocumentElement(); 
			rootElem.normalize();
			 
			if (rootElem.hasAttribute("width"))
				width = Integer.parseInt(rootElem.getAttribute("width"));
			if (rootElem.hasAttribute("height"))
				height = Integer.parseInt(rootElem.getAttribute("height"));			
			if (rootElem.hasAttribute("length"))
				length = Integer.parseInt(rootElem.getAttribute("length"));
				
			puzzle = new Puzzle(width, height, length);
			NodeList nList = doc.getElementsByTagName("Shape");
			for (int i = 0; i < nList.getLength(); i++) {
				puzzle.addShape(parseShape(width, height, length, nList.item(i)));
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return puzzle;
	}
}

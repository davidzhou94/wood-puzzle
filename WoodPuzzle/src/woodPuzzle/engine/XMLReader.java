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
import woodPuzzle.model.Shape;

public class XMLReader {
	private XMLReader() { }
	
	private static Shape parseShape(int width, int height, int length, Node n) {
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
		
		return new Shape(width, height, length, shapeCells);
	}

	public static Puzzle buildPuzzle(String filePath) {
		int pwidth = 0, pheight = 0, plength = 0, 
				swidth = 0, sheight = 0, slength =0;
		Puzzle puzzle = null;
		try {
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			Element rootElem = doc.getDocumentElement(); 
			rootElem.normalize();
			
			pwidth = Integer.parseInt(rootElem.getAttribute("pwidth"));
			pheight = Integer.parseInt(rootElem.getAttribute("pheight"));
			plength = Integer.parseInt(rootElem.getAttribute("plength"));
			
			swidth = Integer.parseInt(rootElem.getAttribute("swidth"));
			sheight = Integer.parseInt(rootElem.getAttribute("sheight"));
			slength = Integer.parseInt(rootElem.getAttribute("slength"));
				
			puzzle = new Puzzle(pwidth, pheight, plength, swidth, sheight, slength);
			NodeList nList = doc.getElementsByTagName("Shape");
			for (int i = 0; i < nList.getLength(); i++) {
				puzzle.addShape(parseShape(swidth, sheight, slength, nList.item(i)));
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return puzzle;
	}
}

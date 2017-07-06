package cc.creativecomputing.kle.simple;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class Sculpture {
	
	
	public List<Element> elements;
	public List<CCVector2> bounds;
	public int nElements;
	public CCDataElement sculptureXML;
	
	public Sculpture (Path theSculptureXMLFile) {

		sculptureXML = CCXMLIO.createXMLElement(theSculptureXMLFile);
		nElements = sculptureXML.child("elements").children().size();
		elements = new ArrayList<Element>();
		
		float z = 0;
		
		for (int i=0; i<nElements; i++) {
			
			CCDataElement xmlElement = sculptureXML.child("elements").child(i);
			float x0 = xmlElement.child("motors").child(0).floatAttribute("x")/10f;
			float x1 = xmlElement.child("motors").child(1).floatAttribute("x")/10f;
			z = xmlElement.child("motors").child(0).floatAttribute("z")/10f;
			Element e = new Element (new CCVector3(x0,0,z),
									 new CCVector3(x1,0,z),
									 new CCVector3(0,0,z), 15f);
			
			e.bounds = new ArrayList<CCVector2>();		
			int nBounds = sculptureXML.child("elements").child(0).child("bounds").children().size();
			for (int j=0; j<nBounds; j++) {
				e.bounds.add ((new CCVector2 (sculptureXML.child("elements").child(0).child("bounds").child(j).floatAttribute("x"), 
											sculptureXML.child("elements").child(0).child("bounds").child(j).floatAttribute("y"))).multiply(0.1f, -0.1f));
			}
			
			elements.add(e);
		}
	}

	public void setRopes (CCVector2[] ropes, float deltaTime, float scaleLength) {
		for (int i=0; i<nElements; i++) {
			elements.get(i).setRopes(ropes[i].x * scaleLength, ropes[i].y * scaleLength, deltaTime);
		}
	}
	
	
	// set positions
	public void setElements (CCVector2[] positions, List<CCVector2> movingPolygon, float deltaTime) {
		for (int i=0; i<nElements; i++) {
			elements.get(i).translate(positions[i].x, positions[i].y, movingPolygon, deltaTime, 1f);
		}
	}
	
	// set positions
		public void setElementsNoScale (CCVector2[] positions, float deltaTime) {
			for (int i=0; i<nElements; i++) {
				elements.get(i).translateNoScale(positions[i].x, positions[i].y, deltaTime);
			}
		}
}
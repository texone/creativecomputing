package cc.creativecomputing.kle.elements;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCSequenceElementsRenderer {
	
	protected final CCSequenceElements _myElements;
	
	@CCProperty(name = "rope color")
	private CCColor _cRopeColor = new CCColor();
	@CCProperty(name = "bound color")
	private CCColor _cBoundColor = new CCColor();
	
	public CCSequenceElementsRenderer(CCSequenceElements theElements){
		_myElements = theElements;
	}
	
	public void drawElement(CCGraphics g, CCSequenceElement theElement){
		
	}
	
	public void draw(CCGraphics g){
		for(CCSequenceElement myElement:_myElements){
			g.pushMatrix();
			g.applyMatrix(myElement.matrix());
			
			g.color(_cRopeColor);
			myElement.motorSetup().drawRopes(g);

			if(_cBoundColor.a > 0){
				g.color(_cBoundColor);
				myElement.motorSetup().drawRangeBounds(g);
				myElement.motorSetup().drawElementBounds(g);
			}

			g.pushAttribute();
			
			drawElement(g, myElement);
			
			g.popAttribute();
			
			g.popMatrix();
		}
	}
}

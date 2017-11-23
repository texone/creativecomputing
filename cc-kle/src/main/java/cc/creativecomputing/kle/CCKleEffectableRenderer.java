package cc.creativecomputing.kle;

import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.kle.motors.CCMotorSetup;
import cc.creativecomputing.math.CCColor;

public class CCKleEffectableRenderer {
	
	protected final List<CCKleEffectable> _myElements;
	
	@CCProperty(name = "rope color")
	private CCColor _cRopeColor = new CCColor();
	@CCProperty(name = "bound color")
	private CCColor _cBoundColor = new CCColor();
	
	public CCKleEffectableRenderer(List<CCKleEffectable> theElements){
		_myElements = theElements;
	}
	
	public void drawElement(CCGraphics g, CCKleEffectable theElement){
		
	}
	
	public void drawMotorSetup(CCGraphics g, CCMotorSetup theSetup){
		if(theSetup == null)return;
		
		g.color(_cRopeColor);
		theSetup.drawRopes(g);

		if(_cBoundColor.a > 0){
			g.color(_cBoundColor);
			theSetup.drawRangeBounds(g);
			theSetup.drawElementBounds(g);
		}
	}
	
	public void draw(CCGraphics g){
		for(CCKleEffectable myElement:_myElements){
			g.pushMatrix();
			g.applyMatrix(myElement.matrix());

			g.pushAttribute();
			drawMotorSetup(g, myElement.motorSetup());
			g.popAttribute();

			g.pushAttribute();
			drawElement(g, myElement);
			g.popAttribute();
			
			g.popMatrix();
		}
	}
}

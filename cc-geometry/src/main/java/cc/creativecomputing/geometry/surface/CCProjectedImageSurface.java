package cc.creativecomputing.geometry.surface;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.image.CCImage;

public class CCProjectedImageSurface extends CCSurfaceQuad{
	
	private final CCImage _myImage;
	
	public CCProjectedImageSurface(final CCImage theImage) {
		super(theImage.width(), theImage.height());
		_myImage = theImage;
	}
	
	public CCImage image() {
		return _myImage;
	}
	
	public void draw(final CCGraphics g) {
				
		g.pushMatrix();
		
		g.applyMatrix(transform());
		
		g.color(055,0,0);
		g.rectMode(CCShapeMode.CORNER);
		g.rect(0,0,_myWidth,_myHeight);
				
		g.popMatrix();
	}
	
	
}

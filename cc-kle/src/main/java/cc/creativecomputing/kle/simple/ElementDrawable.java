package cc.creativecomputing.kle.simple;

import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.kle.simple.Element;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class ElementDrawable extends Element {
	
	
	public ElementDrawable (CCVector3 theCeil0, CCVector3 theCeil1, CCVector3 theCenter, float theR) {
		super (theCeil0, theCeil1, theCenter, theR);
	}
	
	public ElementDrawable (Element theElement) {
		super (theElement.ceil0, theElement.ceil1, theElement.center, theElement.R);
		bounds = theElement.bounds;
		pos = theElement.pos;
		polygon = theElement.polygon;
		rope0 = theElement.rope0;
		rope1 = theElement.rope1;
	}
	
	public void draw (CCGraphics g, CCVector2 translate, List<CCVector2> movingPolygon) {
		
		g.color(1f);
		g.pushMatrix();
		g.translate(ceil0);
		g.box(10,10,10);
		g.popMatrix();
		
		g.pushMatrix();
		g.translate(ceil1);
		g.box(10,10,10);
		g.popMatrix();

		g.color(0.4f, 0.4f, 0.2f, 0.5f);
		g.line(ceil0, pos);
		g.line(ceil1, pos);
		
		g.pushMatrix();
		g.translate(pos);
		g.color(0.8f,0.4f,0);
		
		
		if (CCMath.abs(pos.x) > 200) g.color(0.1f,0.2f,0.1f);
		
		if (violate()) {
			if (CCMath.random()<0.4f) {
				g.color(1f);
			}
		}
		if (outOfBounds()) {
			g.color(0,1f,1f);
		}
		
		g.beginShape(CCDrawMode.TRIANGLE_FAN);
		for (int i=0; i<polygon.size(); i++) {
			g.vertex (0,0);
			g.vertex (polygon.get(i));
			g.vertex (polygon.get((i+1)%polygon.size()));
			
		}
		g.endShape();
		g.popMatrix();
		
		
		g.pushMatrix();
		g.pushAttribute();
		g.color(1f,0,0);
		g.polygonMode(CCPolygonMode.LINE);
		g.translate (center);
		g.translate (translate);
		
		g.beginShape();
		for (CCVector2 p: movingPolygon) {
			g.vertex(p);
		}
		g.endShape();
		
		g.popAttribute();
		
		g.color(0.3f,0.3f,1f);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for (CCVector2 b: bounds) {
			g.vertex(b);
		}
		g.vertex(bounds.get(0));
		g.endShape();
		g.popMatrix();
	}
}
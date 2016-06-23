package cc.creativecomputing.graphics;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCColor;

public class CCDrawAttributes {

	@CCProperty(name = "blend mode")
	private CCBlendMode _cBlendMode = CCBlendMode.BLEND;
	@CCProperty(name = "polygon mode")
	private CCPolygonMode _cPolygonMode = CCPolygonMode.FILL;
	
	@CCProperty(name = "depth test")
	private boolean _cDepthTest = true;
	@CCProperty(name = "depth mask")
	private boolean _cDepthMask = true;
	
	@CCProperty(name = "stroke weight", min = 0.1, max = 50)
	private double _cStrokeWeight = 1;
	@CCProperty(name = "point size", min = 0.1, max = 50)
	private double _cPointSize = 1;
	@CCProperty(name = "color")
	private CCColor _cColor = new CCColor();
	
	
	public void start(CCGraphics g){
		g.pushAttribute();
		if(_cDepthTest){
			g.depthTest();
		}else{
			g.noDepthTest();
		}
		
		if(_cDepthMask){
			g.depthMask();
		}else{
			g.noDepthMask();
		}
		
		g.blend(_cBlendMode);
		g.polygonMode(_cPolygonMode);
		g.strokeWeight(_cStrokeWeight);
		g.pointSize(_cPointSize);
		g.color(_cColor);
	}
	
	public void end(CCGraphics g){
		g.popAttribute();
	}
}

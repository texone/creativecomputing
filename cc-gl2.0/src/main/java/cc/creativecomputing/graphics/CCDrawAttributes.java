package cc.creativecomputing.graphics;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCCullFace;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCColor;

public class CCDrawAttributes {

	@CCProperty(name = "blend mode")
	public CCBlendMode _cBlendMode = CCBlendMode.BLEND;
	@CCProperty(name = "polygon mode")
	public CCPolygonMode _cPolygonMode = CCPolygonMode.FILL;
	
	@CCProperty(name = "depth test")
	public boolean _cDepthTest = true;
	@CCProperty(name = "depth mask")
	public boolean _cDepthMask = true;
	
	@CCProperty(name = "line width", min = 0.1, max = 50)
	public double _cLineWidth = 1;
	@CCProperty(name = "line smooth")
	public boolean _cLineSmooth = false;
	
	@CCProperty(name = "point size", min = 0.1, max = 50)
	public double _cPointSize = 1;
	@CCProperty(name = "point smooth")
	public boolean _cPointSmooth = false;
	
	@CCProperty(name = "color")
	public CCColor color = new CCColor();
	@CCProperty(name = "cullface")
	public CCCullFace _cCullFace = CCCullFace.NONE;
	
	
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
		
		g.lineWidth(_cLineWidth);
		if(_cLineSmooth)g.lineSmooth();
		
		g.pointSize(_cPointSize);
		if(_cPointSmooth)g.pointSmooth();
		g.color(color);
		
		g.cullFace(_cCullFace);
	}
	
	public void end(CCGraphics g){
		g.popAttribute();
	}
}

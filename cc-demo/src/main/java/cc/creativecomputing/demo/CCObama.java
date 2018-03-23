package cc.creativecomputing.demo;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCStopWatch;
import cc.creativecomputing.demo.geomtry.CCVoronoiDemo;
import cc.creativecomputing.geometry.hemesh.CCVoronoi;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.math.CCLine3;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.random.CCRandom;
import cc.creativecomputing.math.util.CCHistogram;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL.CCHPPaperFormat;

public class CCObama extends CCGL2Adapter {
	
	@CCProperty(name = "HP")
	private CCHPGL _cHPGL;
	
	@CCProperty(name = "texture;")
	private CCTexture2DAsset _cTexture;
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha;
	
	@CCProperty(name = "envelope")
	private CCEnvelope _cEnvelope = new CCEnvelope();
	private List<CCVector2> _myVertices = new ArrayList<>();
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cTexture = new CCTexture2DAsset(_myContext);
		_cHPGL = new CCHPGL(CCHPPaperFormat.A4);
	}
	
	

	@Override
	public void update(CCAnimator theAnimator) {
//		int myLastX = 0;
//		int myLastY = 0;
//		
//		if(_cHPGL.isConnected() && _myVertices.size() > 0){
//			CCVector2 v0 = _myVertices.remove(0);
//			CCVector2 v1 = _myVertices.remove(0);
//					
//			double myX1 = CCMath.map(v0.x, myMinX, myMaxX, 430, 7630);
//			double myY1 = CCMath.map(v0.y, myMinY, myMaxY, 200, 7400);
//			double myX2 = CCMath.map(v1.x, myMinX, myMaxX, 430, 7630);
//			double myY2 = CCMath.map(v1.y, myMinY, myMaxY, 200, 7400);
////					_cHPGL.lineTo((int)CCMath.random(430,10430), (int)CCMath.random(200,7400));
//
//			CCLog.info(_myVertices.size() + " : " + myX1 + " : " + myY1);
//					
//			int x = CCMath.round(myX1);
//			int y = CCMath.round(myY1);
//			int x2 = CCMath.round(myX2);
//			int y2 = CCMath.round(myY2);
//			if(x == myLastX && y == myLastY){
//				return;
//			}
//			myLastX = x;
//			myLastY = y;
//					_cHPGL.line(x, y, x2,y2);
//					
////					int radius = (int)CCMath.random(1000);
////					int x = (int)CCMath.random(430 + radius,10430 - radius);
////					int y = (int)CCMath.random(200 + radius,7400 - radius);
////					_cHPGL.circle(x, y, radius);
//				}
	}
	
	int width = 0;
	int height = 0;
	
	
	@CCProperty(name = "min", min = 0, max = 1)
	private double _cmin = 0;

	@Override
	public void display(CCGraphics g) {
		g.debug();
		width = g.width();
		height = g.height();
		g.clearColor(255);
		g.clear();
		
		g.ortho2D();
		g.color(1d, _cAlpha);
		
		if(_cTexture.value() == null)return; 
		g.beginShape(CCDrawMode.POINTS);
		for(int x = 0; x < _cTexture.value().width();x++){
			for(int y = 0; y < _cTexture.value().height();y++){
				double myBright = _cEnvelope.value(_cTexture.image().getPixel(x, y).brightness());
				if(myBright < _cmin)continue; 
				g.color(0); 
				g.vertex(x,y);
			}
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCObama demo = new CCObama();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

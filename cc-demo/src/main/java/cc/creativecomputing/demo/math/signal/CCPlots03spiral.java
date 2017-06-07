package cc.creativecomputing.demo.math.signal;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.signal.CCMixSignal;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL.CCHPPaperFormat;

public class CCPlots03spiral extends CCGL2Adapter{
	
	@CCProperty(name = "signal")
	private CCMixSignal _mySignal = new CCMixSignal();
	@CCProperty(name = "signal 2")
	private CCMixSignal _mySignal2 = new CCMixSignal();
	@CCProperty(name = "height", min = 0, max = 5000)
	private double _cHeight = 100;
	@CCProperty(name = "height2", min = 0, max = 1)
	private double _cHeight2 = 0.25;
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 1;
	@CCProperty(name = "drawmode")
	private CCDrawMode _cDrawMode = CCDrawMode.LINE_STRIP;
	@CCProperty(name = "offset", min = 0, max = 1)
	private double _cSignalOffset = 0;
	
	@CCProperty(name = "rotations")
	double cRotations = 20;
	@CCProperty(name = "steps")
	double cSteps = 20000d;
	
	@CCProperty(name = "HP")
	private CCHPGL _cHPGL;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cHPGL = new CCHPGL(CCHPPaperFormat.A4);
	}
	
	private List<CCVector2> _myVertices = new ArrayList<>();
	
	@Override
	public void update(CCAnimator theAnimator) {
		//,200,,7400
		
		int myLastX = 0;
		int myLastY = 0;
		CCLog.info(_cHPGL.isConnected() + ":" + _myVertices.size());
		
		if(_cHPGL.isConnected() && _myVertices.size() > 0){
			CCVector2 v0 = _myVertices.remove(0);
					
			double myX1 = CCMath.map(v0.x, myMinX, myMaxX, 430, 7630);
			double myY1 = CCMath.map(v0.y, myMinY, myMaxY, 200, 7400);
//					_cHPGL.lineTo((int)CCMath.random(430,10430), (int)CCMath.random(200,7400));

			CCLog.info(_myVertices.size() + " : " + myX1 + " : " + myY1);
					
			int x = CCMath.round(myX1);
			int y = CCMath.round(myY1);
			if(x == myLastX && y == myLastY){
				return;
			}
			myLastX = x;
			myLastY = y;
					_cHPGL.lineTo(x, y);
					
//					int radius = (int)CCMath.random(1000);
//					int x = (int)CCMath.random(430 + radius,10430 - radius);
//					int y = (int)CCMath.random(200 + radius,7400 - radius);
//					_cHPGL.circle(x, y, radius);
				}
	}
	
	private double myMinX = 100000;
	private double myMinY = 100000;
	private double myMaxX = -100000;
	private double myMaxY = -100000;
	
	@CCProperty(name = "plot")
	public void plot(){
		myMinX = 100000;
		myMinY = 100000;
		myMaxX = -100000;
		myMaxY = -100000;
		_myVertices.clear();
		for(int a = 0; a < cSteps; a++){
			double myRadius = _cHeight * (1 -  a / cSteps);
			double x = (CCMath.sin(a / cSteps * CCMath.TWO_PI * cRotations)) * myRadius;
			double y = (CCMath.cos(a / cSteps * CCMath.TWO_PI * cRotations)) * myRadius;
			double myHeight = 1 + (_mySignal.value(x,y)) * _cHeight2;
			x = x * myHeight;
			y = y * myHeight;
			myMinX = CCMath.min(x, myMinX);
			myMinY = CCMath.min(y, myMinY);
			myMaxX = CCMath.max(x, myMaxX);
			myMaxY = CCMath.max(y, myMaxY);
			_myVertices.add(new CCVector2(x,y));
		}
	}
	
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

//		g.color(1d, _cAlpha);
		g.beginShape(_cDrawMode);
		for(int a = 0; a < cSteps; a++){
			double myRadius = _cHeight * a / cSteps;
			double x = (CCMath.sin(a / cSteps * CCMath.TWO_PI * cRotations)) * myRadius;
			double y = (CCMath.cos(a / cSteps * CCMath.TWO_PI * cRotations)) * myRadius;
			double myHeight = 1 + (_mySignal.value(x,y)) * _cHeight2;
			g.vertex(x * myHeight,y * myHeight);
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		
		
		CCPlots03spiral demo = new CCPlots03spiral();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

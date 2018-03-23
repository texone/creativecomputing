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

public class CCObama2 extends CCGL2Adapter {
	
	@CCProperty(name = "HP")
	private CCHPGL _cHPGL;
	
	@CCProperty(name = "texture;")
	private CCTexture2DAsset _cTexture;
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha;
	@CCProperty(name = "number of points")
	private int _NumberOfPoints = 0;
	@CCProperty(name = "histogram")
	private CCHistogram _myHistogram;
	@CCProperty(name = "histogram 2")
	private CCHistogram _myHistogram2;
	@CCProperty(name = "envelope")
	private CCEnvelope _cEnvelope = new CCEnvelope();
	@CCProperty(name = "point", min = 1, max = 20)
	private double _cPointSize = 1;
	@CCProperty(name = "scale", min = 1, max = 20)
	private double _cScale = 1;
	
	private CCVoronoi _myVoronoi = null;
	private List<CCVector2> _myVertices = new ArrayList<>();
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cTexture = new CCTexture2DAsset(_myContext);
		_myHistogram = new CCHistogram();
		_myHistogram2 = new CCHistogram();
		_cHPGL = new CCHPGL(CCHPPaperFormat.A4);
	}
	
	@CCProperty(name = "updateVoronoi")
	private void updateVoronoi() {
		CCMath.randomSeed(0);
		List<CCVector2> myPoints = new ArrayList<>();
		
		for(int i = 0; i < _NumberOfPoints;i++){
			int x = (int)CCMath.random(_cTexture.value().width());
			int y = (int)CCMath.random(_cTexture.value().height());
			double myBrightness = _cTexture.image().getPixel(x, y).brightness();
			double myBrightness2 = _cEnvelope.value(myBrightness);
			
			if(CCMath.random() > myBrightness2){
				myPoints.add(new CCVector2(x,y));
			}
		}
		_myVoronoi = new CCVoronoi(myPoints, 0, width , 0, height );
		CCStopWatch.instance().endLast();

		CCStopWatch.instance().startWatch("mesh");
		_myVoronoi.mesh();
		CCStopWatch.instance().endLast();
	}
	
	@CCProperty(name = "draw voronoi")
	private boolean _cDrawVoronoi = false;

	@Override
	public void update(CCAnimator theAnimator) {
		int myLastX = 0;
		int myLastY = 0;
		
		if(_cHPGL.isConnected() && _myVertices.size() > 0){
			CCVector2 v0 = _myVertices.remove(0);
			CCVector2 v1 = _myVertices.remove(0);
					
			double myX1 = CCMath.map(v0.x, myMinX, myMaxX, 430, 7630);
			double myY1 = CCMath.map(v0.y, myMinY, myMaxY, 200, 7400);
			double myX2 = CCMath.map(v1.x, myMinX, myMaxX, 430, 7630);
			double myY2 = CCMath.map(v1.y, myMinY, myMaxY, 200, 7400);
//					_cHPGL.lineTo((int)CCMath.random(430,10430), (int)CCMath.random(200,7400));

			CCLog.info(_myVertices.size() + " : " + myX1 + " : " + myY1);
					
			int x = CCMath.round(myX1);
			int y = CCMath.round(myY1);
			int x2 = CCMath.round(myX2);
			int y2 = CCMath.round(myY2);
			if(x == myLastX && y == myLastY){
				return;
			}
			myLastX = x;
			myLastY = y;
					_cHPGL.line(x, y, x2,y2);
					
//					int radius = (int)CCMath.random(1000);
//					int x = (int)CCMath.random(430 + radius,10430 - radius);
//					int y = (int)CCMath.random(200 + radius,7400 - radius);
//					_cHPGL.circle(x, y, radius);
				}
	}
	
	int width = 0;
	int height = 0;
	
	@CCProperty(name = "length", min = 0, max = 1000)
	private double _cLength = 100;
	@CCProperty(name = "space", min = 0, max = 30)
	private int _cSpace = 100;
	@CCProperty(name = "octaves", min = 1, max = 8)
	private int octaves = 8;
	
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
		
		for(int x = 0; x < _cTexture.value().width();x+=_cSpace){
			for(int y = 0; y < _cTexture.value().height();y+=_cSpace){
				double myBrightness = _cTexture.image().getPixel(x, y).brightness();
				double myBrightness2 = _cEnvelope.value(myBrightness);
				int octave = CCMath.round(octaves * myBrightness2);
				if(x % CCMath.pow(2, octave)== 0){
					myMinX = CCMath.min(x, myMinX);
					myMinY = CCMath.min(y, myMinY);
					myMaxX = CCMath.max(x, myMaxX);
					myMaxY = CCMath.max(y + _cSpace, myMaxY);
					_myVertices.add(new CCVector2(x,y));
					_myVertices.add(new CCVector2(x,y + _cSpace));
//					g.vertex(x  + _cSpace, y );
//					g.vertex(x , y + _cSpace );
				}
			}
		}
	}
	
	

	@Override
	public void display(CCGraphics g) {
		width = g.width();
		height = g.height();
		g.clearColor(255);
		g.clear();
		
		g.ortho2D();
		g.color(1d, _cAlpha);
		_myHistogram.reset();
		_myHistogram2.reset();
		
		if(_cTexture.value() == null)return;
		
		g.image(_cTexture.value(),0,0);
		CCMath.randomSeed(0);
		g.color(0);
		
		g.pointSize(_cPointSize);
		
		g.pushMatrix();
		g.scale(_cScale);
//		g.beginShape(CCDrawMode.POINTS);
//		for(int i = 0; i < _NumberOfPoints;i++){
//			int x = (int)CCMath.random(_cTexture.value().width());
//			int y = (int)CCMath.random(_cTexture.value().height());
//			double myBrightness = _cTexture.image().getPixel(x, y).brightness();
//			double myBrightness2 = _cEnvelope.value(myBrightness);
//			_myHistogram.add(myBrightness);
//			_myHistogram2.add(myBrightness2);
//			if(CCMath.random() > myBrightness2){
//				g.vertex(x,y);
//			}
//		}
//		g.endShape();
		
//		for(int x = 0; x < _cTexture.value().width();x+=_cSpace){
//			for(int y = 0; y < _cTexture.value().height();y+=_cSpace){
//				double myBrightness = _cTexture.image().getPixel(x, y).brightness();
//				double myBrightness2 = 1 - _cEnvelope.value(myBrightness);
//				double width = _cSpace * myBrightness2;
//				g.rect(x - width / 2, y - width / 2,width, width);
//			}
//		}
		for(int x = 0; x < _cTexture.value().width();x+=_cSpace){
			g.beginShape(CCDrawMode.LINES);
			for(int y = 0; y < _cTexture.value().height();y+=_cSpace){
				double myBrightness = _cTexture.image().getPixel(x, y).brightness();
				double myBrightness2 = _cEnvelope.value(myBrightness);
				int octave = CCMath.round(octaves * myBrightness2);
				if(x % CCMath.pow(2, octave)== 0){
					g.vertex(x , y );
					g.vertex(x  , y + _cSpace );

//					g.vertex(x  + _cSpace, y );
//					g.vertex(x , y + _cSpace );
				}
			}
			g.endShape();
		}
		g.color(255,0,0);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i < _myHistogram.bands();i++ ){
			int x = i * 2;
			g.vertex(x, 0);
			g.vertex(x,_myHistogram.count(i) / (double)_myHistogram.max() * 200);
		}
		g.endShape();

		g.color(0,0,255);
		g.beginShape(CCDrawMode.LINES);
		for(int i = 0; i < _myHistogram2.bands();i++ ){
			int x = i * 2 + 1;
			g.vertex(x, 0);
			g.vertex(x,_myHistogram2.count(i) / (double)_myHistogram2.max() * 200);
		}
		g.endShape();
		g.popMatrix();
		
		if(_myVoronoi == null || !_cDrawVoronoi)return;

		g.pushMatrix();
		g.scale(_cScale);
		g.beginShape(CCDrawMode.LINES);
		for (CCLine3 myLine : _myVoronoi.edges()) {
			if(myLine.length() > _cLength)continue;
			g.vertex(myLine.start());
			g.vertex(myLine.end());
		}
		g.endShape();
		g.popMatrix();
	}

	public static void main(String[] args) {

		CCObama2 demo = new CCObama2();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

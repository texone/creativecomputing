package cc.creativecomputing.demo.math.sun;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.primitives.CCSphereMesh;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.time.CCDate;
import cc.creativecomputing.math.util.CCSunCalc;
import cc.creativecomputing.math.util.CCSunCalc.CCSunInfo;

public class CCSunPositionDemo3D extends CCGL2Adapter{
	
	public static class CCSunInfoLayer{
		
		private CCDate _myDate;
		
		@CCProperty(name = "day", min = 1, max = 365)
		private double day = 1;
		
		@CCProperty(name = "time", min = 0, max = 24)
		private double time = 0;
		
		@CCProperty(name = "radius", min = 1, max = 365)
		private double _cRadius = 200;

		@CCProperty(name = "sun color")
		private CCColor _mySunColor = new CCColor();
		@CCProperty(name = "sun range color")
		private CCColor _mySunRangeColor = new CCColor();
		@CCProperty(name = "sun range outline color")
		private CCColor _mySunRangeOutlineColor = new CCColor();
		@CCProperty(name = "sun path color")
		private CCColor _mySunPathColor = new CCColor();
		
		private CCSphereMesh _mySphereMesh;
		
		private CCMesh _myMesh;
		
		private double _myLatitude = 53;
		private double _myLongitude = 11;
		
		public CCSunInfoLayer(final double theLatitude, final double theLongitude){
			_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES);
			_myDate = new CCDate();
			
			_myLatitude = theLatitude;
			_myLongitude = theLongitude;
			
			createSphere(theLatitude, theLongitude);
			
			_mySphereMesh = new CCSphereMesh(20, 20);
		}
		
		
		private void createSphere(final double theLatitude, final double theLongitude){
			List<Integer> myIndices = new ArrayList<>();  

			int myDays = CCDate.shortestDay().dayOfYear() - CCDate.longestDay().dayOfYear();
			
			_myMesh = new CCMesh(CCDrawMode.TRIANGLES,(myDays + 1) * (100 + 1));

			for (int day = CCDate.longestDay().dayOfYear(); day <= CCDate.shortestDay().dayOfYear(); day++){
				CCDate myDate = new CCDate();
				myDate.dayOfYear(day);
				CCSunInfo di = CCSunCalc.sunInfo(myDate, theLatitude, theLongitude, false);
				CCDate start = di.sunrise.start;
				CCDate end = di.sunset.end;
				for (int j = 0; j <= 100; j++){
					
					CCDate myBlend = CCDate.blend(start, end, j / 100d);
					CCVector3 posPoint = CCSunCalc.sunPosition3D(myBlend, theLatitude, theLongitude);
					
					_myMesh.addNormal(posPoint);
					_myMesh.addVertex(
//						_myRadius * x + _myCenter.x(),
//						_myRadius * y + _myCenter.y(),
//						_myRadius * z + _myCenter.z()
						posPoint.x,
						posPoint.y,
						posPoint.z
					
					);
				}
			}
			
			int myPointRows = myDays + 1;
			int myPointsPerRow = 100 + 1;

			//create the index array:
			for (int i = 1; i < myPointRows; i++){
				for (int j = 0; j < (myPointsPerRow-1); j++){
					myIndices.add((i-1) * myPointsPerRow + j);
					myIndices.add(i     * myPointsPerRow + j);
					myIndices.add((i-1) * myPointsPerRow + j + 1);

					myIndices.add((i-1) * myPointsPerRow +j + 1);
					myIndices.add((i)   * myPointsPerRow +j);
					myIndices.add((i)   * myPointsPerRow +j + 1);
				}
			}
			_myMesh.indices(myIndices);
		}

		public CCMesh mesh(){
			return _myMesh;
		}

		public void update(CCAnimator theAnimator) {
			_myDate.fromDoubleDay(day);
			_myDate.fromDoubleTime(time);
		}
		
		private void drawCurvePath(CCGraphics g, CCSunInfo di) {
			CCDate start = di.sunrise.start;
			CCDate end = di.sunset.end;
			
			g.beginShape(CCDrawMode.LINE_STRIP);
			for (long time = start.timeInMilliSeconds(); time < end.timeInMilliSeconds(); time += 10 * 60 * 1000) {
				
				CCVector3 posPoint = CCSunCalc.sunPosition3D(new CCDate(time), _myLatitude, _myLongitude);
//				CCLog.info(new CCDate(time) + ":" + posPoint);
				g.vertex(posPoint.x * _cRadius, posPoint.y * _cRadius, posPoint.z * _cRadius);
			}
			g.endShape();
		}
		
		private void drawPositionPath(CCGraphics g, CCDate date) {
			CCVector3 posPoint = CCSunCalc.sunPosition3D(date, _myLatitude, _myLongitude);
			g.line(0, 0, 0, posPoint.x * _cRadius, posPoint.y * _cRadius, posPoint.z * _cRadius);
		}
		
		
		
		private void draw(CCGraphics g) {
			CCVector3 posPoint = CCSunCalc.sunPosition3D(_myDate, _myLatitude, _myLongitude);
            if (posPoint.z > -0.014) { // Nacht war vorher 0,018

				g.pushMatrix();
				g.translate(posPoint.x * _cRadius, posPoint.y * _cRadius, posPoint.z * _cRadius);
				g.color(_mySunColor);
				_mySphereMesh.draw(g);
				g.popMatrix();
			}
			
			g.color(_mySunRangeColor);
			g.noDepthMask();
			g.pushMatrix();
			g.scale(_cRadius);
			_myMesh.draw(g);
			g.popMatrix();
			g.depthMask();
			
			g.color(_mySunRangeOutlineColor);
			drawCurvePath(g, CCSunCalc.sunInfo(CCDate.longestDay(), _myLatitude, _myLongitude, false));
			drawCurvePath(g, CCSunCalc.sunInfo(CCDate.shortestDay(), _myLatitude, _myLongitude, false));
			
			g.color(_mySunPathColor);
			g.ellipse(0, 0, 0, _cRadius * 2, _cRadius * 2, true);
			
			CCSunInfo di = CCSunCalc.sunInfo(_myDate, _myLatitude, _myLongitude, false);
			drawPositionPath(g,di.sunrise.start);
			drawPositionPath(g,di.sunset.end);
			drawCurvePath(g, di);
			
			// Sonnenlinie zeichnen (gelb)
			drawPositionPath(g,_myDate);
		}
	}

	private double _myLatitude = 53;
	private double _myLongitude = 11;
	
	@CCProperty(name = "sun info layer")
	private CCSunInfoLayer _mySunMesh;
	
	private CCCameraController _myCamController;
			
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCamController = new CCCameraController(this, g, 100);
		
		_mySunMesh = new CCSunInfoLayer(_myLatitude, _myLongitude);
	}
	
	
	
	@Override
	public void update(CCAnimator theAnimator) {
//		_myDate = Calendar.getInstance().getTime();
		
		_mySunMesh.update(theAnimator);
	}
	
	
	
	
	
	@Override
	public void display(CCGraphics g) {
		g.pushMatrix();
		_myCamController.camera().draw(g);
		g.clear();
		_mySunMesh.draw(g);
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		
		
		CCSunPositionDemo3D demo = new CCSunPositionDemo3D();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

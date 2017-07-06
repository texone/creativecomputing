package cc.creativecomputing.demo;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCInterpolationDemo extends CCGL2Adapter{
	
	public static enum CCInterpolationType{
		LINEAR, CUBIC, HERMITE
	}
	
	@CCProperty(name = "scale", min = 0, max = 500)
	private double _cScale = 0;
	
	@CCProperty(name = "signal scale", min = 0, max = 10)
	private double _cSignalScale = 0;
	
	@CCProperty(name = "resolution", min = 10, max = 200)
	private double _cResolution = 10;
	
	@CCProperty(name = "interpolation resolution", min = 10, max = 200)
	private int _cInterpolationResolution = 10;
	
	@CCProperty(name = "signal")
	private CCMixSignal _cSignal = new CCMixSignal();
	
	@CCProperty(name = "type")
	private CCInterpolationType _cType = CCInterpolationType.LINEAR;
	
	@CCProperty(name = "hermite tension", min = -1, max = 1)
	private double _cTension = 0;
	@CCProperty(name = "hermite bias", min = -1, max = 1)
	private double _cBias = 0;
	
	
	@Override
	public void init(CCGraphics g) {
	}
	
	List<Double> _myValues = new ArrayList<Double>();
	List<Double> _myLinearInterpolatedValues = new ArrayList<Double>();
	List<Double> _myCubicInterpolatedValues = new ArrayList<Double>();
	List<Double> _myHermiteInterpolatedValues = new ArrayList<Double>();
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myValues.clear();
		for(int i = 0; i <= _cResolution;i++){
			double myInput = i / (double)_cResolution;
			_myValues.add(_cSignal.value(myInput * _cSignalScale) - 0.5);
		}
		

		_myLinearInterpolatedValues.clear();
		for(int i = 0; i <= _cInterpolationResolution;i++){
			double myInput = i / (double)_cInterpolationResolution;
			double myInputScale = myInput * _cResolution;
			int myLower = CCMath.floor(myInputScale);
			double myBlend = myInputScale - myLower;
			int myUpper = myLower + 1;
				
			double myVal0 = _myValues.get(CCMath.constrain(myLower, 0, (int)_cResolution));
			double myVal1 = _myValues.get(CCMath.constrain(myUpper, 0, (int)_cResolution));
			double myVal = CCMath.blend(myVal0, myVal1, myBlend);
				
			_myLinearInterpolatedValues.add(myVal);
		}
		
		_myCubicInterpolatedValues.clear();
		for(int i = 0; i <= _cInterpolationResolution;i++){
			double myInput = i / (double)_cInterpolationResolution;
			double myInputScale = myInput * _cResolution;
			int myLower = CCMath.floor(myInputScale);
			double myBlend = myInputScale - myLower;

			double myVal0 = _myValues.get(CCMath.constrain(myLower - 1, 0, (int)_cResolution));
			double myVal1 = _myValues.get(CCMath.constrain(myLower, 0, (int)_cResolution));
			double myVal2 = _myValues.get(CCMath.constrain(myLower + 1, 0, (int)_cResolution));
			double myVal3 = _myValues.get(CCMath.constrain(myLower + 2, 0, (int)_cResolution));
			double myVal = CCMath.cubicBlend(myVal0, myVal1, myVal2, myVal3,  myBlend);
				
			_myCubicInterpolatedValues.add(myVal);
		}
		
		_myHermiteInterpolatedValues.clear();
		for(int i = 0; i <= _cInterpolationResolution;i++){
			double myInput = i / (double)_cInterpolationResolution;
			double myInputScale = myInput * _cResolution;
			int myLower = CCMath.floor(myInputScale);
			double myBlend = myInputScale - myLower;

			double myVal0 = _myValues.get(CCMath.constrain(myLower - 1, 0, (int)_cResolution));
			double myVal1 = _myValues.get(CCMath.constrain(myLower, 0, (int)_cResolution));
			double myVal2 = _myValues.get(CCMath.constrain(myLower + 1, 0, (int)_cResolution));
			double myVal3 = _myValues.get(CCMath.constrain(myLower + 2, 0, (int)_cResolution));
			double myVal = CCMath.hermiteBlend(myVal0, myVal1, myVal2, myVal3,  myBlend, _cTension, _cBias);
				
			_myHermiteInterpolatedValues.add(myVal);
		}
	}
	
	private void drawValues(CCGraphics g, List<Double> theValues){
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i < theValues.size();i++){
			double myX = CCMath.map(i, 0, _cInterpolationResolution, -g.width()/2, g.width());
			g.vertex(myX, theValues.get(i) * _cScale);
		}
		g.endShape();
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();

		g.color(1f);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i < _myValues.size();i++){
			double myX = CCMath.map(i, 0, _cResolution, -g.width()/2, g.width());
			g.vertex(myX, _myValues.get(i) * _cScale);
		}
		g.endShape();
		
		g.color(0f,0,1f);
		drawValues(g, _myLinearInterpolatedValues);
		g.color(0f,1f,0);
		drawValues(g, _myCubicInterpolatedValues);
		g.color(1f,0,0);
		drawValues(g, _myHermiteInterpolatedValues);
		
	}
	
	public static void main(String[] args) {
		
		
		CCInterpolationDemo demo = new CCInterpolationDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

package cc.creativecomputing.demo.kle;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCEffectables;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCTransform;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCStickSculptureDemo extends CCGL2Adapter {
	
	private class CCStickEffectable extends CCEffectable{
		
		private double _myX;
		private double _myY;
		private double _myZ;
		
		private double _myXAnimation;
		private double _myYAnimation;
		private double _myZAnimation;
		
		private CCVector3 _myTransformedTop = new CCVector3();

		public CCStickEffectable(int theId, double theX, double theZ) {
			super(theId);
			_myX = theX;
			_myZ = theZ;
		}
		
		@Override
		public double xBlend() {
			return _myDensityEnvelope.value(super.xBlend());
		}
		
		public double x(){
			return _myXAnimation + (xBlend()  - 0.5) * _cXDistance;
		}
		
		public double y(){
			return _myYAnimation + _myY + 10;
		}
		
		public double z(){
			return _myZAnimation + _myZ * _cZDistance;
		}
		
		public CCVector3 transformedTop(){
			return _myTransformedTop;
		}
		
		@Override
		public void apply(double... theValues) {
			_myXAnimation = (theValues[0] * 2 - 1) * _cXMax;
			_myYAnimation = theValues[1] * _cYMax;
			switch(id() % 4){
			case 0:
				_myYAnimation += _myHeight0Envelope.value(xBlend()) * _cEnvYMax;
				break;
			case 1:
				_myYAnimation += _myHeight1Envelope.value(xBlend()) * _cEnvYMax;
				break;
			case 2:
				_myYAnimation += _myHeight2Envelope.value(xBlend()) * _cEnvYMax;
				break;
			case 3:
				_myYAnimation += _myHeight3Envelope.value(xBlend()) * _cEnvYMax;
				break;
			}
			_myZAnimation = (theValues[2] * 2 - 1) * _cZMax + CCMath.blend(_cCurveMin, _cCurveMax, _myEnvelope.value(xBlend()));
			
			_myTransformedTop.set(x(), y(), z());
			_myTransform.applyForward(_myTransformedTop, _myTransformedTop);
		}
	}
	
	private class CCStickBahn{
		double _myLength = 0;
		
		List<CCStickEffectable> _mySticks = new ArrayList<>();
	}
	
	private interface CCStickInfo{
		public String info();
	}
	
	@CCProperty(name = "text x0", min = 0, max = 2000)
	private double _cTextX0 = 0;
	@CCProperty(name = "text x1", min = 0, max = 2000)
	private double _cTextX1 = 0;
	@CCProperty(name = "text y0", min = 0, max = 2000)
	private double _cTextY0 = 0;
	@CCProperty(name = "text space", min = 0, max = 100)
	private double _cTextSpace0 = 0;
	
	private class CCStickInfoText{
		
		private CCStickInfo _myInfo;
		
		private String _myLabel;
		
		public CCStickInfoText(String theLabel, CCStickInfo theInfo){
			_myLabel = theLabel;
			_myInfo = theInfo;
		}
		
		public void draw(CCGraphics g, double theY){
			g.text(_myLabel, _cTextX0, theY);
			g.text(_myInfo.info(), _cTextX1, theY);
		}
	}
	
	private List<CCStickInfoText> _myInfoTexts = new ArrayList<>();
	
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	@CCProperty(name = "viewer")
	private boolean _cDrawViewerCamera = false;
	
	@CCProperty(name = "x distance", min = 0, max = 3000)
	private double _cXDistance = 400;
	
	@CCProperty(name = "z distance", min = 0, max = 1000)
	private double _cZDistance = 100;
	@CCProperty(name = "x max", min = 0, max = 50)
	private double _cXMax = 20;
	@CCProperty(name = "y max", min = 0, max = 200)
	private double _cYMax = 20;
	@CCProperty(name = "env y max", min = 0, max = 200)
	private double _cEnvYMax = 20;
	@CCProperty(name = "z max", min = 0, max = 250)
	private double _cZMax = 20;
	@CCProperty(name = "curve min", min = -1000, max = 1000)
	private double _cCurveMin = 20;
	@CCProperty(name = "curve max", min = -1000, max = 1000)
	private double _cCurveMax = 20;
	

	@CCProperty(name = "geom color")
	private CCColor _cGeomColor = new CCColor();

	@CCProperty(name = "stick color")
	private CCColor _cStickColor = new CCColor();
	
	@CCProperty(name = "band color")
	private CCColor _cBandColor = new CCColor();
	@CCProperty(name = "info color")
	private CCColor _cInfoColor = new CCColor();
	
	@CCProperty(name = "background")
	private CCColor _myBackground = new CCColor();
	
	@CCProperty(name = "blend mode")
	private CCBlendMode _cBlendMode = CCBlendMode.BLEND;
	@CCProperty(name = "depth Test")
	private boolean _cDepthTest = true;
	
	private CCEffectables<CCStickEffectable> _mySticks = new CCEffectables<>();
	

	@CCProperty(name = "effects")
	private CCEffectManager<CCStickEffectable> _myEffectManager;
	
	@CCProperty(name = "translate x", min = -1000, max = 1000)
	private double _cTranslateX = 0;
	@CCProperty(name = "translate z", min = -1000, max = 1000)
	private double _cTranslateZ = 0;
	@CCProperty(name = "rotate", min = -180, max = 180)
	private double _cRotate = 0;
	
	@CCProperty(name = "shape env")
	private CCEnvelope _myEnvelope = new CCEnvelope();
	@CCProperty(name = "height0 env")
	private CCEnvelope _myHeight0Envelope = new CCEnvelope();
	@CCProperty(name = "height1 env")
	private CCEnvelope _myHeight1Envelope = new CCEnvelope();
	@CCProperty(name = "height2 env")
	private CCEnvelope _myHeight2Envelope = new CCEnvelope();
	@CCProperty(name = "height3 env")
	private CCEnvelope _myHeight3Envelope = new CCEnvelope();
	@CCProperty(name = "density env")
	private CCEnvelope _myDensityEnvelope = new CCEnvelope();
	
	@CCProperty(name = "c blend attributes")
	private CCDrawAttributes _cStickAttributes = new CCDrawAttributes();
	
	private CCTransform _myTransform = new CCTransform();
	
	private CCTexture2D _myStickPlan;
	
//	int STICKS = 135;
	
	@CCProperty(name = "sticks", min = 75, max = 200)
	private void sticks(int STICKS){
		_mySticks.clear();
		for(int i = 0; i < STICKS;i++){
			double myX = CCMath.map(i, 0, STICKS, -0.5, 0.5);
			CCStickEffectable myStick0 = new CCStickEffectable(i * 2, myX, -1);
			myStick0._myXBlend = i / (STICKS - 1d);
			myStick0._myYBlend = 0;
			myStick0.groupIDBlend(i / (STICKS - 1d));
			myStick0.idBlend(i / (STICKS - 1d));
			myStick0.groupBlend(0);
			myStick0.group(0);
			_mySticks.add(myStick0);
			
			myX = CCMath.map(i + 0.5, 0, STICKS, -0.5, 0.5);
			CCStickEffectable myStick1 = new CCStickEffectable(i * 2 + 1, myX, 1);
			myStick1._myXBlend = (i + 0.5) / (STICKS - 1d);
			myStick1._myYBlend = 1;
			myStick1.groupIDBlend(i / (STICKS - 1d));
			myStick1.idBlend(i / (STICKS - 1d));
			myStick1.groupBlend(0);
			myStick1.group(0);
			_mySticks.add(myStick1);
		}
	}

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCameraController = new CCCameraController(this, g, 100);
		
		_myEffectManager = new CCEffectManager<CCStickEffectable>(_mySticks, "x","y","z");
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
		
		_myInfoTexts.add(new CCStickInfoText("bahnen_laenge:", () -> "" + (int)(_myLength / 10)));
		_myInfoTexts.add(new CCStickInfoText("bahnen:", () -> "" + (int)(_myCutOfflength / 500)));
		_myInfoTexts.add(new CCStickInfoText("bahnen_laenge + verschnitt:", () -> "" + (int)(_myCutOfflength / 10)));
		_myInfoTexts.add(new CCStickInfoText("maximu_laenge:", () -> "" + (int)(_myMaxLength / 10)));
		_myInfoTexts.add(new CCStickInfoText("becken left:", () -> "" + CCMath.abs(-1250 - _myBoundingRectangle.x())));
		_myInfoTexts.add(new CCStickInfoText("becken right:", () -> "" + CCMath.abs(1250 - (_myBoundingRectangle.x() + _myBoundingRectangle.width()))));
		_myInfoTexts.add(new CCStickInfoText("becken top:", () -> "" + CCMath.abs(-400 - _myBoundingRectangle.y())));
		_myInfoTexts.add(new CCStickInfoText("becken bottom:", () -> "" + CCMath.abs(400 - (_myBoundingRectangle.y() + _myBoundingRectangle.height()))));
		
		g.textFont(CCFontIO.createVectorFont("arial", 12));
		
		_myStickPlan = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/stick_plan.png")));
	}
	
	private double _myLength = 0;
	private double _myMaxLength = 0;
	private double _myCutOfflength = 0;
	
	private static double STICK_WICKLUNG = 0.2;
	
	private CCAABoundingRectangle _myBoundingRectangle = new CCAABoundingRectangle();
	
	private List<CCStickBahn> _myBahnen = new ArrayList<>();

	@Override
	public void update(CCAnimator theAnimator) {
		_myTransform.translation(_cTranslateX, 0, _cTranslateZ);
		_myTransform.rotation(CCMath.radians(_cRotate), 0, 1, 0);
		
		_myEffectManager.update(theAnimator);
		
		_myLength = STICK_WICKLUNG;
		_myMaxLength = 0;
		_myCutOfflength = STICK_WICKLUNG;
		
		_myBoundingRectangle = new CCAABoundingRectangle();
		
		_myBahnen.clear();
		
		CCStickBahn myBahn = new CCStickBahn();
		_myBahnen.add(myBahn);
		
		for(int i = 0; i < _mySticks.size() - 1; i++){
			CCStickEffectable myStick0 = _mySticks.get(i);
			CCStickEffectable myStick1 = _mySticks.get(i + 1);
			
			double myDistance = myStick0.transformedTop().distance(myStick1.transformedTop());
			_myLength += myDistance + STICK_WICKLUNG;
			_myCutOfflength += myDistance + STICK_WICKLUNG;
			_myMaxLength = CCMath.max(_myMaxLength, myDistance);
			
			myBahn._mySticks.add(myStick0);
			
			if(myBahn._myLength + myDistance + STICK_WICKLUNG > 500){
				_myCutOfflength += 500 - myBahn._myLength;
				_myBahnen.add(myBahn = new CCStickBahn());
				myBahn._mySticks.add(myStick0);
				myBahn._myLength = STICK_WICKLUNG;
			}
			myBahn._myLength += myDistance + STICK_WICKLUNG;
			
			_myBoundingRectangle.add(myStick0.transformedTop().x, myStick0.transformedTop().z);
			_myBoundingRectangle.add(myStick1.transformedTop().x, myStick1.transformedTop().z);
		}
		myBahn._mySticks.add(_mySticks.get(_mySticks.size() - 1));
	}
	
	@CCProperty(name = "draw debug")
	private boolean _cDrawDebug = false;
	@CCProperty(name = "bahn id")
	private int _cBahnID = 0;
	
	@CCProperty(name = "label size", min = 5, max = 50)
	private int _cLabelSize = 0;
	@CCProperty(name = "font size", min = 5, max = 50)
	private int _cFontSize = 0;
	
	private void drawStickLabels(CCGraphics g, List<CCStickEffectable> theSticks){
		g.textAlign(CCTextAlign.CENTER);
		g.textSize(_cLabelSize);
		for(int i = 0; i < theSticks.size(); i++){
			CCStickEffectable myStick = theSticks.get(i);
			CCStickEffectable myStick1;
			if(i == 0){
				myStick1 = theSticks.get(i + 1);
			}else{
				myStick1 = theSticks.get(i - 1);
			}
			CCVector3 myTop = myStick.transformedTop();
			g.pushMatrix();
			if(myStick.transformedTop().z > myStick1.transformedTop().z){
				g.translate(myTop.x, myTop.z + 15);
			}else{
				g.translate(myTop.x, myTop.z - 5);
			}
			g.scale(1,-1);
			g.text("S" + myStick.id(), 0, 0);
			g.popMatrix();
		}
		g.textAlign(CCTextAlign.LEFT);
		g.textSize(_cFontSize);
	}
	
	private void drawBahn(CCGraphics g, List<CCStickEffectable> theSticks){
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(CCStickEffectable myStick:theSticks){
			CCVector3 myTop = myStick.transformedTop();
			g.vertex(myTop.x, myTop.z, 0);
		}
		g.endShape();
	}
	
	@CCProperty(name = "unselected debug color")
	private CCColor _cUnselectedDebug = new CCColor();
	@CCProperty(name = "selected debug color")
	private CCColor _cSelectedDebug = new CCColor();
	@CCProperty(name = "bounds debug color")
	private CCColor _cBoundsDebug = new CCColor();
	
	@CCProperty(name = "debug text x")
	private double _cDebugTextX = 0;
	@CCProperty(name = "debug text x1")
	private double _cDebugTextX1 = 0;
	@CCProperty(name = "debug text y")
	private double _cDebugTextY = 0;
	@CCProperty(name = "debug text space")
	private double _cDebugTextSpace = 0;
	@CCProperty(name = "debug text x space")
	private double _cDebugTextXSpace = 0;

	@CCProperty(name = "debug text y row")
	private double _cDebugTextYRow = 0;
	

	@CCProperty(name = "debug graph x")
	private double _cDebugGraphX = 0;
	@CCProperty(name = "debug graph y")
	private double _cDebugGraphY = 0;
	@CCProperty(name = "draw background")
	private boolean _cDrawBackgroundTexture = false;
	
	@CCProperty(name = "plan x")
	private int _cPlanX = 0;
	@CCProperty(name = "plan y")
	private int _cPlanY = 0;
	@CCProperty(name = "plan scale", min = 0, max = 10)
	private double _cPlanScale = 0;
	
	private double printStickDist(CCGraphics g, int theID, int theOffset, double theX, double theY){
		int myID2 = theID + theOffset;
		if(myID2 < 0 || myID2 >= _mySticks.size())return 0;
		CCStickEffectable myStick0 = _mySticks.get(theID);
		CCStickEffectable myStick1 = _mySticks.get(myID2);
		
		double myDistance = round(myStick0.transformedTop().distance(myStick1.transformedTop()));
		g.text("S" + theID + " <-> S" + myID2, theX, theY);
		g.text(CCFormatUtil.nd(myDistance / 10, 2) + " m", theX + _cDebugTextX1, theY);
		return _cDebugTextSpace;
	}
	
	private CCVector2 shapedOffset(double theBlend, double theDist){
		double myX0 = (theBlend - 0.5) * _cXDistance;
		double myY0 = CCMath.blend(_cCurveMin, _cCurveMax, _myEnvelope.value(theBlend));

		double myX1 = (theBlend + 0.001 - 0.5) * _cXDistance;
		double myY1 = CCMath.blend(_cCurveMin, _cCurveMax, _myEnvelope.value(theBlend + 0.001));
		
		CCVector2 myMove = new CCVector2(myX1 - myX0, myY1 - myY0).crossLocal().normalizeLocal().multiplyLocal(theDist);
		
		return new CCVector2(myX0, myY0).addLocal(myMove);
	}
	
	private double round(double theValue){
		return CCMath.round(theValue * 2) / 2d;
	}
	@CCProperty(name = "debug scale", min = 0, max = 1)
	private double _cDebugScale = 1;
	
	private void drawDebug(CCGraphics g, int theStickID){
		
		g.color(_cUnselectedDebug);
		
		if(_cDrawBackgroundTexture){
			g.pushMatrix();
			g.scale(1,-1);
			g.image(_myStickPlan, _cPlanX, _cPlanY, _myStickPlan.width() * _cPlanScale, _myStickPlan.height() * _cPlanScale);
			g.popMatrix();
		}
		
		g.pushMatrix();
		g.scale(_cDebugScale);
		g.translate(-_myBoundingRectangle.x() + _cDebugGraphX, -_myBoundingRectangle.y() + _cDebugGraphY);
		drawBahn(g, _mySticks);
		drawStickLabels(g, _mySticks);
		
//		g.beginShape(CCDrawMode.LINE_STRIP);
//		for(int i = 0; i <= 1000;i++){
//			g.vertex(shapedOffset(CCMath.norm(i, 0, 1000),0));
//		}
//		g.endShape();
//		g.beginShape(CCDrawMode.LINES);
//		for(int i = 0; i <= 1000;i++){
//			g.vertex(shapedOffset(CCMath.norm(i, 0, 1000),0));
//			g.vertex(shapedOffset(CCMath.norm(i, 0, 1000),200));
//		}
//		g.endShape();
		
		g.color(_cBoundsDebug);
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(_myBoundingRectangle.x(),_myBoundingRectangle.y());
		g.vertex(_myBoundingRectangle.x() + _myBoundingRectangle.width(),_myBoundingRectangle.y());
		g.vertex(_myBoundingRectangle.x() + _myBoundingRectangle.width(),_myBoundingRectangle.y() + _myBoundingRectangle.height());
		g.vertex(_myBoundingRectangle.x(),_myBoundingRectangle.y() + _myBoundingRectangle.height());
		g.endShape();
		
		g.pushMatrix();
		g.scale(1,-1);
		g.text("a", _myBoundingRectangle.center().x, _myBoundingRectangle.y() - 30);
		g.text("d", _myBoundingRectangle.x() - 10, -_myBoundingRectangle.center().y);
		g.text("c", _myBoundingRectangle.center().x, _myBoundingRectangle.y() + _myBoundingRectangle.height() + 20);
		g.text("b", _myBoundingRectangle.x() + _myBoundingRectangle.width() + 10, -_myBoundingRectangle.center().y);
		g.popMatrix();
		
		if(theStickID >= _myBahnen.size()){
			g.popMatrix();
			return;
		}
		
		CCStickBahn myBahn = _myBahnen.get(theStickID);
		g.color(_cSelectedDebug);
		drawBahn(g, myBahn._mySticks);
		drawStickLabels(g, myBahn._mySticks);
		g.popMatrix();
		
		g.textAlign(CCTextAlign.LEFT);
		g.pushMatrix();
		g.translate(_cDebugTextX, _cDebugTextY);
		g.scale(1,-1);
		double myY = 0;
		double myX = 0;
		g.text("BAHN B_" + theStickID, 0, myY); myY += _cDebugTextSpace;
		g.text("Länge:", 0, myY); g.text(CCFormatUtil.nd(myBahn._myLength / 10, 2) + " m", _cDebugTextX1, myY);  myY += _cDebugTextSpace;
		g.text("Verschnitt:", 0, myY); g.text(CCFormatUtil.nd(50 - myBahn._myLength / 10, 2) + " m", _cDebugTextX1, myY); myY += _cDebugTextSpace;
		g.text("a:", 0, myY); g.text(CCFormatUtil.nd(_myBoundingRectangle.width() / 10, 2) + " m", _cDebugTextX1, myY); myY += _cDebugTextSpace;
		g.text("b:", 0, myY); g.text(CCFormatUtil.nd(_myBoundingRectangle.height() / 10, 2) + " m", _cDebugTextX1, myY); myY += _cDebugTextSpace;
		
		myX += _cDebugTextXSpace;
		int i = 0;
		int row = 0;
		for(CCStickEffectable myStick:myBahn._mySticks){
			myY = row + _cDebugTextYRow * row;
			CCVector3 myTop = myStick.transformedTop();
			double myA = round((_myBoundingRectangle.y() + _myBoundingRectangle.height() - myTop.z)) / 10;
			double myB = round((_myBoundingRectangle.x() + _myBoundingRectangle.width() - myTop.x)) / 10;
			double myC = round((myTop.z - _myBoundingRectangle.y())) / 10;
			double myD = round((myTop.x - _myBoundingRectangle.x())) / 10;
					
			myY += _cDebugTextSpace;
			g.text("Abstände S" + myStick.id(), myX, myY);myY += _cDebugTextSpace;
			myY += _cDebugTextSpace;
			g.text("Zaun Unterkante", myX, myY); g.text(CCFormatUtil.nd(myStick.transformedTop().y / 10 - 1, 2) +" m" , myX + _cDebugTextX1, myY); myY += _cDebugTextSpace;
			myY += _cDebugTextSpace;
			g.text("S <-> a", myX, myY); g.text(CCFormatUtil.nd(myA,2) +" m" , myX + _cDebugTextX1, myY); myY += _cDebugTextSpace;
			g.text("S <-> b", myX, myY); g.text(CCFormatUtil.nd(myB,2) +" m" , myX + _cDebugTextX1, myY); myY += _cDebugTextSpace;
			g.text("S <-> c", myX, myY); g.text(CCFormatUtil.nd(myC,2) +" m" , myX + _cDebugTextX1, myY); myY += _cDebugTextSpace;
			g.text("S <-> d", myX, myY); g.text(CCFormatUtil.nd(myD,2) +" m" , myX + _cDebugTextX1, myY); myY += _cDebugTextSpace;
			myY += _cDebugTextSpace;
			myY += printStickDist(g, myStick.id(),  -1, myX, myY);
			myY += printStickDist(g, myStick.id(),   1, myX, myY);
			myY += printStickDist(g, myStick.id(),  -2, myX, myY);
			myY += printStickDist(g, myStick.id(),   2, myX, myY);
			myY += printStickDist(g, myStick.id(),  -4, myX, myY);
			myY += printStickDist(g, myStick.id(),   4, myX, myY);
			myX += _cDebugTextXSpace;
			i++;
			if(i == 4){
				myX = _cDebugTextXSpace;
				row ++;
			}
		}
		g.popMatrix();
	}
	
	@CCProperty(name = "start stick")
	private int _cStartStick = 0;
	
	@CCProperty(name = "end stick")
	private int _cEndStick = 0;
	
	private boolean _myExport = false;
	@CCProperty(name = "export measures")
	public void exportMeasures(){
		_myExport = true;
//		StringBuffer myCSV = new StringBuffer();
//		for(CCStickEffectable myStick:_mySticks){
//			CCVector3 myTop = myStick.transformedTop();
//			myCSV.append(
//				"S" + myStick.id() + "," + 
//				(myTop.z - _myBoundingRectangle.y()) + "," + 
//				(_myBoundingRectangle.x() + _myBoundingRectangle.width() - myTop.x) + 
//				(_myBoundingRectangle.y() + _myBoundingRectangle.height() - myTop.z) + "," + 
//				(myTop.x - _myBoundingRectangle.x()) + "," + "\n"
//			);
//		}
//		CCNIOUtil.saveString(CCNIOUtil.dataPath("measures.csv"), myCSV.toString());
		
		
	}
	
	

	@Override
	public void display(CCGraphics g) {
		if(_myExport){
			for(int i = 0; i < _myBahnen.size();i++){
				g.clearColor(1d);
				g.clear();
				g.pushMatrix();
				g.ortho();
				drawDebug(g, i);
				g.popMatrix();
				CCScreenCapture.capture(CCNIOUtil.dataPath("measures/bahn_" + CCFormatUtil.nf(i, 3) + ".png"), g.width(), g.height());
			}
			_myExport = false;
		}
		if(_cDrawDebug){
			g.clearColor(1d);
			g.clear();
			g.pushMatrix();
			g.ortho();
			drawDebug(g, _cBahnID);
			g.popMatrix();
			return;
		}
		
		g.clearColor(_myBackground);
		g.clear();
		
		g.pushMatrix();
		_myCameraController.camera().draw(g);
		
		g.color(_cInfoColor);
		g.pushMatrix();
		g.translate(0,30,0);
		g.boxGrid(2500, 60, 800);
		g.popMatrix();
		
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(_myBoundingRectangle.x(), 0,_myBoundingRectangle.y());
		g.vertex(_myBoundingRectangle.x() + _myBoundingRectangle.width(), 0,_myBoundingRectangle.y());
		g.vertex(_myBoundingRectangle.x() + _myBoundingRectangle.width(), 0,_myBoundingRectangle.y() + _myBoundingRectangle.height());
		g.vertex(_myBoundingRectangle.x(), 0,_myBoundingRectangle.y() + _myBoundingRectangle.height());
		g.endShape();
		
		g.beginShape(CCDrawMode.QUADS);
		g.color(_myBackground);
		g.vertex(-1250, 0, -400);
		g.vertex( 1250, 0, -400);
		g.vertex( 1250, 0,  400);
		g.vertex(-1250, 0,  400);

		g.color(_cGeomColor);
		g.vertex(-1250, 60, -1000);
		g.vertex(-2500, 60, -1000);
		g.vertex(-2500, 60,  1000);
		g.vertex(-1250, 60,  1000);

		g.vertex( 1250, 60, -1000);
		g.vertex( 2500, 60, -1000);
		g.vertex( 2500, 60,  1000);
		g.vertex( 1250, 60,  1000);
		
		g.vertex(-1250, 60, -1000);
		g.vertex( 1250, 60, -1000);
		g.vertex( 1250, 60, -400);
		g.vertex(-1250, 60, -400);
		
		g.vertex(-1250, 60,  1000);
		g.vertex( 1250, 60,  1000);
		g.vertex( 1250, 60,  400);
		g.vertex(-1250, 60,  400);
		
		g.vertex( 1250, 60, -400);
		g.vertex( 1250, 0,  -400);
		g.vertex( 1250, 0,   400);
		g.vertex( 1250, 60,  400);

		g.vertex(-1250, 60, -400);
		g.vertex(-1250, 0,  -400);
		g.vertex(-1250, 0,   400);
		g.vertex(-1250, 60,  400);

		g.vertex(-1250, 60,  400);
		g.vertex(-1250, 0,   400);
		g.vertex( 1250, 0,   400);
		g.vertex( 1250, 60,  400);

		g.vertex(-1250, 60, -400);
		g.vertex(-1250, 0,  -400);
		g.vertex( 1250, 0,  -400);
		g.vertex( 1250, 60, -400);
		g.endShape();
		
		
		g.color(_cStickColor);
		for(int i = _cStartStick; i < _mySticks.size() && i < _cEndStick;i++){
			CCStickEffectable myStick = _mySticks.get(i);
			CCVector3 myTop = myStick.transformedTop();
			g.line(
				myTop.x, 0, myTop.z,
				myTop.x, 27, myTop.z
			);
		}
		_cStickAttributes.start(g);
		g.color(_cBandColor);
		g.beginShape(CCDrawMode.QUAD_STRIP);
		for(int i = _cStartStick; i < _mySticks.size() && i < _cEndStick;i++){
			CCStickEffectable myStick = _mySticks.get(i);
			CCVector3 myTop = myStick.transformedTop();
			g.vertex(myTop.x, myTop.y - 10, myTop.z);
			g.vertex(myTop.x, myTop.y, myTop.z);
		}
		g.endShape();
		_cStickAttributes.end(g);
		
		g.depthTest();
		g.color(_cInfoColor);
		g.pushMatrix();
		g.translate(0,30,0);
		g.boxGrid(2498, 60, 798);
		g.popMatrix();
		
		if(!_cDrawViewerCamera){
//			g.line(_myViewerCamera.position(), _myViewerCamera.target());
		}
		g.popMatrix();
		
		g.ortho2D();
		g.clearDepthBuffer();
		g.textAlign(CCTextAlign.LEFT);
		double myY = _cTextY0;
		for(CCStickInfoText myText:_myInfoTexts){
			myText.draw(g, myY);
			myY += _cTextSpace0;
		}
	}

	public static void main(String[] args) {

		CCStickSculptureDemo demo = new CCStickSculptureDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

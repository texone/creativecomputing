package cc.creativecomputing.demo.protocol.serial.hpgl;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.graphics.font.text.CCTextContours;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL.CCHPPaperFormat;

public class CCHPGLStarDemo extends CCGL2Adapter {
	
	@CCProperty(name = "inner radius", min = 10, max = 200)
	private double _cIRadius = 100;
	@CCProperty(name = "outer radius", min = 10, max = 200)
	private double _cORadius = 100;
	@CCProperty(name = "start rotation", min = -90, max = 90)
	private double _cStartRotation = 0;
	@CCProperty(name = "end rotation1", min = -180, max = 180)
	private double _cEndRotation = 0;

	@CCProperty(name = "iterations", min = 0, max = 100)
	private double _cIterations = 1;
	
	@CCProperty(name = "start scale", min = 0, max = 2)
	private double _cStartScale = 1;
	@CCProperty(name = "end scale", min = 0, max = 10)
	private double _cEndScale = 1;
	

	@CCProperty(name = "x font", min = 0, max = 1000)
	private double _cX = 1;
	@CCProperty(name = "y fot", min = 0, max = 720)
	private double _cY = 1;
	@CCProperty(name = "font dis", min = 0, max = 200)
	private int _cFontDis = 1;
	
	@CCProperty(name = "x star", min = 0, max = 1000)
	private double _cXStar = 1;
	@CCProperty(name = "y star", min = 0, max = 720)
	private double _cYStar = 1;
	
	@CCProperty(name = "spread")
	private CCEnvelope _cspread = new CCEnvelope();
	
	@CCProperty(name = "pen 1")
	private CCColor _cpen1 = new CCColor();
	@CCProperty(name = "pen 2")
	private CCColor _cpen2 = new CCColor();
	@CCProperty(name = "pen 3")
	private CCColor _cpen3 = new CCColor();

	@CCProperty(name = "speed")
	private int _cSpeed = 1;
	
	private List<CCVector2> _myPen1Lines = new ArrayList<CCVector2>();
	private List<CCVector2> _myPen2Lines = new ArrayList<CCVector2>();
	private List<CCVector2> _myPen3Lines = new ArrayList<CCVector2>();
	
	private List<CCVector2> _myPen1OutLines = new ArrayList<CCVector2>();
	private List<CCVector2> _myPen2OutLines = new ArrayList<CCVector2>();
	private List<CCVector2> _myPen3OutLines = new ArrayList<CCVector2>();
	
	@CCProperty(name = "HP") 
	private CCHPGL _cHPGL = new CCHPGL(CCHPPaperFormat.A4);
	
	private CCTextContours _myTextContours;
	{
		CCOutlineFont font = CCFontIO.createOutlineFont("Brush Script MT",110, 30);
				
		_myTextContours = new CCTextContours(font);
		_myTextContours.align(CCTextAlign.CENTER);
		_myTextContours.text("merryXmas"); 
		_myTextContours.breakText();
		
	}

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myPen1Lines.clear();
		_myPen2Lines.clear();
		for(int i = 0; i < _cIterations;i++){
			double myBlend = CCMath.norm(i, 0, _cIterations - 1);
			myBlend = _cspread.value(myBlend);
			double myScale = CCMath.blend(_cStartScale, _cEndScale, myBlend);
			double myRotation = CCMath.blend(_cStartRotation, _cEndRotation, myBlend);
			
			for(int j = 0; j < 5;j++){
				_myPen1Lines.add(CCVector2.circlePoint(CCMath.radians(j * 72 + myRotation), _cIRadius * myScale, _cXStar, _cYStar));
				_myPen1Lines.add(CCVector2.circlePoint(CCMath.radians(j * 72 + 36 + myRotation), _cORadius * myScale, _cXStar, _cYStar));

				_myPen2Lines.add(CCVector2.circlePoint(CCMath.radians(j * 72 + 72 + myRotation), _cIRadius * myScale, _cXStar, _cYStar));
				_myPen2Lines.add(CCVector2.circlePoint(CCMath.radians(j * 72 + 36 + myRotation), _cORadius * myScale, _cXStar, _cYStar));
			}
		}
		_myPen3Lines.clear();
		for(List<CCVector2> myContour:_myTextContours.contours()){
			CCVector2 myCenter = new CCVector2();
			for(CCVector2 myPoint:myContour){
				myCenter.addLocal(myPoint);
			}
			myCenter.divideLocal(myContour.size());
			for(int i = 0; i < myContour.size() - 1;i++){
				CCVector2 myPoint0 = myContour.get(i);
				CCVector2 myPoint1 = myContour.get((i + _cFontDis) % myContour.size());
				_myPen3Lines.add(new CCVector2(myPoint0.x + _cX, myPoint0.y + _cY));
				_myPen3Lines.add(new CCVector2(myPoint1.x + _cX, myPoint1.y + _cY));
			}
		}
		
		if(!_cHPGL.isConnected())return;
		if(_myCurrentVertices == null && _myLines.size() > 0){
			_myCurrentVertices = _myLines.remove(0);
			pen++;
			_cHPGL.selectPen(pen);
		}
		if(_myCurrentVertices == null)return;
		
		
//		_cHPGL.velocitySelect(_cSpeed);
//
//		_cHPGL.write(CCHPGLCommand.VS, 0, _cSpeed);
		if(_myCurrentVertices.size() > 0){
			CCVector2 v0 = _myCurrentVertices.remove(0);
			CCVector2 v1 = _myCurrentVertices.remove(0);
			CCLog.info(v0, v1);

			_cHPGL.velocitySelect(_cSpeed);
			_cHPGL.line(v0.x * 10, v0.y * 10, v1.x * 10, v1.y * 10);
//			_cHPGL.line(v0.x * 10, v0.y * 10);
		}
		
		if(_myCurrentVertices.size() == 0){
			_myCurrentVertices = null;
		}
		
//		if(_cHPGL.isConnected()){
////			_cHPGL.lineTo((int)CCMath.random(430,10430), (int)CCMath.random(200,7400));
//
//			_cHPGL.selectPen((int)CCMath.random(3)+1);
//			_cHPGL.line(CCMath.random(430,10430), CCMath.random(200,7400), CCMath.random(430,10430), CCMath.random(200,7400));
//			
////			int radius = (int)CCMath.random(1000);
////			int x = (int)CCMath.random(430 + radius,10430 - radius);
////			int y = (int)CCMath.random(200 + radius,7400 - radius);
////			_cHPGL.circle(x, y, radius);
//		}
	}

	private List<List<CCVector2>> _myLines = new ArrayList<>();
	private List<CCVector2> _myCurrentVertices;
	int pen = 0;
	@CCProperty(name = "draw")
	public void draw(){
		pen = 0;
		_myLines.add(new ArrayList<>(_myPen1Lines));
		_myLines.add(new ArrayList<>(_myPen2Lines));
//		_myLines.add(new ArrayList<>(_myPen3Lines));
	}
	
	@CCProperty(name = "break")
	public void breaks(){
		pen = 0;
		_myLines = null;
		_myCurrentVertices = null;
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(255);
		g.clear();
		g.ortho2D();
		g.color(255);
		
		g.color(_cpen1);
		g.beginShape(CCDrawMode.LINES);
		for(int i = 0; i < _myPen1Lines.size();i+=2){
			g.vertex(_myPen1Lines.get(i));
			g.vertex(_myPen1Lines.get(i + 1));
		}
		g.endShape();
		
		g.color(_cpen2);
		g.beginShape(CCDrawMode.LINES);
		for(int i = 0; i < _myPen2Lines.size();i+=2){
			g.vertex(_myPen2Lines.get(i));
			g.vertex(_myPen2Lines.get(i + 1));
		}
		g.endShape();
		
//		g.color(_cpen3);
//		g.beginShape(CCDrawMode.LINES);
//		for(int i = 0; i < _myPen3Lines.size();i+=2){
//			g.vertex(_myPen3Lines.get(i));
//			g.vertex(_myPen3Lines.get(i + 1));
//		}
//		g.endShape();
		g.line(500, 0, 500, 720);
		
		
	}

	public static void main(String[] args) {

		CCHPGLStarDemo demo = new CCHPGLStarDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 720);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCTriangle2;
import cc.creativecomputing.math.CCVector2;

public class CCUIColorWheel extends CCUIWidget{
		
	private double _myColorWheelWidth = 30;
	private double _myAngle = 0;
	private double _myHue = 0;
	private double _myWhite = 0;
	private double _myBlack = 0;
		
	private CCVector2 _myWheelPos = null;
	private CCVector2 _myTrianglePos = new CCVector2();
		
	private boolean _myIsInWheel = false;
	
	public CCEventManager<CCColor> changeEvents = new CCEventManager<>();
		
	private CCColor _myColor = new CCColor();
		
	public CCUIColorWheel(double theSize){
		super(new CCUIWidgetStyle(), theSize, theSize);
		
		_myMinWidth = theSize;
		_myMinHeight = theSize;
		
		_myWheelPos = new CCVector2(width() - _myColorWheelWidth / 2, -height() / 2);
			
		mousePressed.add(event -> {
			CCVector2 pos = new CCVector2(event.x, event.y);
			double myDist = pos.distance(new CCVector2(width() /2, -height() / 2));
			_myIsInWheel = myDist > width() /2 - _myColorWheelWidth;
			updateState(pos);
		});
		mouseDragged.add(pos ->{
			updateState(pos);
		});
	}
		
	public void setFromColor(CCColor theColor) {
		_myColor.set(theColor);
		double[] hsb = theColor.hsb();
		_myHue = hsb[0];
		_myBlack = 1 - hsb[2];
		_myWhite = 1 - _myBlack - hsb[1];
			
		_myAngle = (1 - _myHue) * CCMath.TWO_PI;
		_myWheelPos = CCVector2.circlePoint(_myAngle, width() /2 - _myColorWheelWidth / 2, width() /2, -height() / 2);
		
		_myTrianglePos.set(_myBlack, _myWhite);		
	}
		
	private void updateState(CCVector2 pos){
		if(_myIsInWheel){
			_myWheelPos = new CCVector2(pos.x - width() /2, -(pos.y + height() / 2));
			_myWheelPos.normalizeLocal();
			_myAngle = CCMath.atan2(-_myWheelPos.y, _myWheelPos.x);
			_myWheelPos.multiplyLocal(width() /2 - _myColorWheelWidth / 2);
			_myWheelPos.y *= -1;
			_myWheelPos.addLocal(width() /2, -height() / 2);
			_myHue = 1 - _myAngle / CCMath.TWO_PI;
		}else{
			_myTrianglePos = triangle().toBarycentricCoordinates(pos);
				
			double mySum = _myTrianglePos.x + _myTrianglePos.y;
			_myTrianglePos.x /= mySum > 1 ? mySum : 1;
			_myTrianglePos.y /= mySum > 1 ? mySum : 1;

			_myTrianglePos.x = CCMath.saturate(_myTrianglePos.x);
			_myTrianglePos.y = CCMath.saturate(_myTrianglePos.y);
				
			_myBlack = _myTrianglePos.x;
			_myWhite = _myTrianglePos.y;
		}
			
		_myColor = CCColor.createFromHSB(_myHue, 1 - (_myWhite + _myBlack), 1 - _myBlack);
		changeEvents.event(_myColor);
	}
		
	private CCTriangle2 triangle(){
		double myRadius = width() / 2 - _myColorWheelWidth;
		CCVector2 myV0 = CCVector2.circlePoint(_myAngle, myRadius, width() / 2, - height() / 2);
		CCVector2 myV1 = CCVector2.circlePoint(_myAngle - CCMath.TWO_PI / 3, myRadius, width() / 2, - height() / 2);
		CCVector2 myV2 = CCVector2.circlePoint(_myAngle + CCMath.TWO_PI / 3, myRadius, width() / 2, - height() / 2);
		return new CCTriangle2(myV0, myV1, myV2);
	}
		
	@Override
	public void drawContent(CCGraphics g) {
		
		g.beginShape(CCDrawMode.QUAD_STRIP);
		for(double i = 0; i <= 360;i++){
			double myAngle = CCMath.radians(i);
			g.color(CCColor.createFromHSB(i / 360, 1d, 1d));
			CCVector2 myPoint = CCVector2.circlePoint(myAngle, 1, 0, 0);
			g.vertex(
				myPoint.x * width() / 2 + width() / 2, 
				myPoint.y * -height() / 2 - height() / 2
			);
			g.vertex(
				myPoint.x * (width() / 2 - _myColorWheelWidth) + width() / 2, 
				myPoint.y * (- height() / 2 + _myColorWheelWidth) - height() / 2
			);
		}
		g.endShape();
			
		CCTriangle2 myTriangle = triangle();
		g.beginShape(CCDrawMode.TRIANGLES);
		g.color(CCColor.createFromHSB(_myHue, 1d, 1d));
		g.vertex(myTriangle.a());
		g.color(CCColor.WHITE);
		g.vertex(myTriangle.b());
		g.color(CCColor.BLACK);
		g.vertex(myTriangle.c());
		g.endShape();

		double myPointRadius = _myColorWheelWidth / 4;
		if(_myWheelPos != null){
			g.color(CCColor.createFromHSB(_myHue, 1d, 1d));
			g.ellipse(_myWheelPos, myPointRadius);
			g.color(0);
			g.ellipse(_myWheelPos, myPointRadius, myPointRadius,  true);
		}
		if(_myTrianglePos != null){
			CCVector2 myTrianglePos = myTriangle.toTriangleCoordinates(_myTrianglePos);
			g.color(_myColor);
			g.ellipse(myTrianglePos, myPointRadius);
			g.color(0);
			g.ellipse(myTrianglePos,myPointRadius, myPointRadius,  true);
		}
	}
}
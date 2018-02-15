package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCMath;

public class CCUIDropDownWidget extends CCUILabelWidget{
	
	private CCUIMenueWidget _myMenue;
	
	private boolean _myShowMenue = false;

	public CCUIDropDownWidget(CCFont<?> theFont) {
		super(theFont, "...");
		_myMenue = new CCUIMenueWidget(theFont);

		inset(2);
		
		mousePressed.add(event -> {
			_myShowMenue = true;
		});
		mouseReleased.add(event ->{
			_myShowMenue = false;
		});
		mouseReleasedOutside.add(event ->{
			_myShowMenue = false;
		});
		mouseMoved.add(pos -> {
			CCLog.info(pos);
		});
		mouseDragged.add(pos -> {
			CCLog.info(pos);
		});
		
		_myMenue.mouseDragged.add(pos -> {
			CCLog.info(pos);
		});
		_myMenue.mouseReleased.add(event ->{
			_myShowMenue = false;
		});
		_myMenue.mouseReleasedOutside.add(event ->{
			_myShowMenue = false;
		});
	}
	
	public CCUIMenueWidget menue(){
		return _myMenue;
	}
	
	@Override
	public double width() {
		return CCMath.max(_myWidth,_myTextField.width()) + _myInset * 2;
	}
	
	@Override
	public void width(double theWidth) {
		super.width(theWidth);
		
		_myMenue.width(theWidth);
	}
	
	@Override
	public void updateMatrices() {
		super.updateMatrices();
		_myMenue._myLocalMatrix.set(_myLocalMatrix);
		_myMenue._myLocalInverseMatrix.set(_myLocalInverseMatrix);
	}

	public void addItem(String theLabel) {
		_myMenue.addItem(theLabel, () -> {});
	}
	
	@Override
	public CCUIWidget overlayWidget() {
		return _myShowMenue ? _myMenue : null;
	}
	
	@Override
	public void draw(CCGraphics g) {
		super.draw(g);
		
		if(_myShowMenue){
			g.pushMatrix();
			g.translate(0, 0, 1);
			_myMenue.draw(g);
			g.popMatrix();
		}
	}
}

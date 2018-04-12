/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUIEditPolicy;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIDrawable;

/**
 * @author christianriekoff
 *
 */
public class CCUIWidget{

	protected CCMatrix32 _myLocalMatrix = new CCMatrix32();
	protected CCMatrix32 _myLocalInverseMatrix = new CCMatrix32();

	protected CCMatrix32 _myWorldMatrix = new CCMatrix32();
	protected CCMatrix32 _myWorldInverseMatrix = new CCMatrix32();
	
	@CCProperty(name="translation")
	private CCVector2 _myTranslation = new CCVector2();
	@CCProperty(name="scale")
	private CCVector2 _myScale = new CCVector2(1,1);
	@CCProperty(name="rotation")
	private CCVector1 _myRotation = new CCVector1();
	
	@CCProperty(name="horizontal_alignment")
	private CCUIHorizontalAlignment _myHorizontalAlignment = CCUIHorizontalAlignment.LEFT;
	@CCProperty(name="vertical_alignment")
	private CCUIVerticalAlignment _myVerticalAlignment = CCUIVerticalAlignment.TOP;
	
	@CCProperty(name="background")
	protected CCUIDrawable _myBackground;
	@CCProperty(name="border")
	protected CCUIDrawable _myBorder;
	@CCProperty(name="foreground")
	protected CCUIDrawable _myForeground;
	
	@CCProperty(name = "width")
	protected double _myWidth = 0;
	
	@CCProperty(name = "height")
	protected double _myHeight = 0;
	
	@CCProperty(name = "left inset")
	protected double _myLeftInset = 0;
	@CCProperty(name = "right inset")
	protected double _myRightInset = 0;
	@CCProperty(name = "top inset")
	protected double _myTopInset = 0;
	@CCProperty(name = "bottom inset")
	protected double _myBottomInset = 0;
	
	@CCProperty(name = "edit_policy")
	protected CCUIEditPolicy _myEditPolicy = CCUIEditPolicy.ADMIN;
	
	protected CCUIWidget _myParent;
	
	protected CCUIWidget _myOverlay;
	
	protected boolean _myIsActive = true;
	
	protected boolean _myDoStretch = false;
	
	public CCUIWidget(double theWidth, double theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public CCUIWidget() {
		this(0,0);
	}
	
	public boolean stretch(){
		return _myDoStretch;
	}
	
	public void stretch(boolean theDoStretch){
		_myDoStretch = theDoStretch;
	}
	
	public boolean isActive(){
		return _myIsActive;
	}
	
	public void isActive(boolean theIsActive){
		_myIsActive = theIsActive;
	}
	
	public CCUIWidget overlayWidget(){
		if( _myOverlay == null)return null;
		if(_myOverlay.isActive())return _myOverlay;
		return null;
	}
	
	public void parent(CCUIWidget theParent){
		_myParent = theParent;
	}
	
	public CCUIWidget parent(){
		return _myParent;
	}
	
	public CCUIWidget root(){
		if(_myParent == null)return this;
		else return _myParent.root();
	}
	
	public CCUIHorizontalAlignment horizontalAlignment() {
		return _myHorizontalAlignment;
	}

	public void horizontalAlignment(CCUIHorizontalAlignment theHorizontalAlignment) {
		_myHorizontalAlignment = theHorizontalAlignment;
	}

	public CCUIVerticalAlignment verticalAlignment() {
		return _myVerticalAlignment;
	}

	public void verticalAlignment(CCUIVerticalAlignment theVerticalAlignment) {
		_myVerticalAlignment = theVerticalAlignment;
	}
	
	public CCMatrix32 localTransform() {
		return _myLocalMatrix;
	}
	
	public CCMatrix32 localInverseTransform() {
		return _myLocalInverseMatrix;
	}
	
	public CCMatrix32 worldTransform() {
		return _myWorldMatrix;
	}
	
	public CCMatrix32 worldInverseTransform() {
		return _myWorldInverseMatrix;
	}
	
	public CCVector2 translation(){
		return _myTranslation;
	}
	
	public CCVector2 scale(){
		return _myScale;
	}
	
	public CCVector1 rotation(){
		return _myRotation;
	}
	
	public void background(CCUIDrawable theBackground) {
		_myBackground = theBackground;
	}
	
	public void border(CCUIDrawable theBorder) {
		_myBorder = theBorder;
	}
	
	public CCUIDrawable border() {
		return _myBorder;
	}
	
	public void foreground(CCUIDrawable theForeground) {
		_myForeground = theForeground;
	}
	
	public CCUIDrawable foreground() {
		return _myForeground;
	}
	
	public boolean isInsideLocal(double theX, double theY) {
		return 
			theX >= 0 && 
			theX <= width() &&
			theY <= 0 && 
			theY >= -height();
	}
	
	public boolean isInsideLocal(CCVector2 theVector) {
		return isInsideLocal(theVector.x, theVector.y);
	}
	
	public boolean isInside(CCVector2 theVector) {
		CCVector2 myLocalPos = _myWorldInverseMatrix.transform(theVector);
		return isInsideLocal(myLocalPos);
	}
	
	public boolean isInside(double theX, double theY) {
		return isInside(new CCVector2(theX, theY));
	}
	
//	private void handleEvent(CCVector2 theVector, CCVector2 theTransformedVector, CCUIInputEventType theEventType) {
//		
//		
//		
//		switch(theEventType) {
//		
//		case CLICK:
//			if(myIsInside) {
//				callListener(CCUIWidgetEventType.CLICK, theVector, theTransformedVector);
//			}
//			break;
//		case DOUBLE_CLICK:
//			if(myIsInside) {
//				callListener(CCUIWidgetEventType.DOUBLE_CLICK, theVector, theTransformedVector);
//			}
//			break;
//		
//		case MOVE:
//			if(_myIsOver && !myIsInside) {
//				callListener(CCUIWidgetEventType.OUT, theVector, theTransformedVector);
//				_myIsOver = false;
//			}
//			
//			if(!_myIsOver && myIsInside) {
//				callListener(CCUIWidgetEventType.OVER, theVector, theTransformedVector);
//				_myIsOver = true;
//			}
//			if(myIsInside) {
//				callListener(CCUIWidgetEventType.MOVE, theVector, theTransformedVector);
//			}else {
//				callListener(CCUIWidgetEventType.MOVE_OUTSIDE, theVector, theTransformedVector);
//			}
//			break;
//		case DRAGG:
//			if(_myIsOver && !myIsInside) {
//				callListener(CCUIWidgetEventType.OUT, theVector, theTransformedVector);
//				_myIsOver = false;
//			}
//			
//			if(!_myIsOver && myIsInside) {
//				callListener(CCUIWidgetEventType.OVER, theVector, theTransformedVector);
//				_myIsOver = true;
//			}
//			if(myIsInside) {
//				callListener(CCUIWidgetEventType.DRAGG, theVector, theTransformedVector);
//			}else {
//				callListener(CCUIWidgetEventType.DRAGG_OUTSIDE, theVector, theTransformedVector);
//			}
//			break;
//		}
//	}
	
	public final CCEventManager<CCGLMouseEvent> mouseReleasedOutside = new CCEventManager<>();
	public final CCEventManager<CCGLMouseEvent> mouseReleased = new CCEventManager<>();
	public final CCEventManager<CCGLMouseEvent> mousePressed = new CCEventManager<>();
	public final CCEventManager<CCGLMouseEvent> mouseClicked = new CCEventManager<>();
	public final CCEventManager<CCVector2> mouseMoved = new CCEventManager<>();
	public final CCEventManager<CCVector2> mouseDragged = new CCEventManager<>();
	
	public final CCEventManager<?> focusGained = new CCEventManager<>();
	public final CCEventManager<?> focusLost = new CCEventManager<>();
	
	public final CCEventManager<CCGLKeyEvent> keyReleased = new CCEventManager<>();
	public final CCEventManager<CCGLKeyEvent> keyPressed = new CCEventManager<>();
	public final CCEventManager<CCGLKeyEvent> keyRepeatEvents = new CCEventManager<>();
	public final CCEventManager<Character> keyChar = new CCEventManager<>();
	
	public void update(CCGLTimer theTimer) {
		
	}
	
	public void updateMatrices(){
		_myLocalMatrix.reset();
		_myLocalMatrix.translate(_myTranslation);
		_myLocalMatrix.rotate(CCMath.radians(_myRotation.x));
		_myLocalMatrix.scale(_myScale.x, _myScale.y);
		
		_myLocalInverseMatrix = _myLocalMatrix.inverse();
		
		if(_myParent != null){
			_myWorldMatrix = _myParent._myWorldMatrix.clone();
			_myWorldMatrix.preApply(_myLocalMatrix);
			
			_myWorldInverseMatrix = _myWorldMatrix.inverse();
		}else{
			_myWorldMatrix = _myLocalMatrix;
			_myWorldInverseMatrix = _myLocalInverseMatrix;
		}
		
		if(_myOverlay == null)return;
		
		_myOverlay._myLocalMatrix.set(_myLocalMatrix);
		_myOverlay._myLocalMatrix.translate(_myOverlay.translation());
		_myOverlay._myLocalInverseMatrix = _myOverlay._myLocalMatrix.inverse();
		CCMatrix32 myWorldInverse = _myWorldMatrix.clone();
		myWorldInverse.translate(_myOverlay.translation());
		_myOverlay._myWorldInverseMatrix.set(myWorldInverse.inverse());
	}
	
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.applyMatrix(_myLocalMatrix);
		drawContent(g);
		g.popMatrix();
		
		if(_myOverlay == null)return;
		if(!_myOverlay.isActive())return;
		
		g.pushMatrix();
		g.translate(0, 0, 1);
		_myOverlay.draw(g);
		g.popMatrix();
	}
	
	public void drawContent(CCGraphics g) {
		if(_myBackground != null)_myBackground.draw(g, this);
		if(_myBorder != null)_myBorder.draw(g, this);
		if(_myForeground != null)_myForeground.draw(g, this);
		
//		g.color(255,0,0);
//		g.beginShape(CCDrawMode.LINE_LOOP);
//		g.vertex(0,0);
//		g.vertex(width(),0);
//		g.vertex(width(),-height());
//		g.vertex(0,-height());
//		g.endShape();
	}
	
	protected double widthCalc(){
		return _myWidth + _myLeftInset + _myRightInset;
	}
	
	public double width() {
		double myWidth = widthCalc();
		if(!_myDoStretch)return myWidth;
		return _myParent == null ? myWidth : CCMath.max(_myParent.width(), myWidth);
	}
	
	public void width(double theWidth) {
		_myWidth = theWidth;
	}
	
	public double height() {
		return _myHeight + _myTopInset + _myBottomInset;
	}
	
	public void height(double theHeight) {
		_myHeight = theHeight;
	}
	
	public double leftInset() {
		return _myLeftInset;
	}
	
	public void leftInset(double theInset) {
		_myLeftInset = theInset;
	}
	
	public double rightInset() {
		return _myRightInset;
	}
	
	public void rightInset(double theInset) {
		_myRightInset = theInset;
	}
	
	public double topInset() {
		return _myTopInset;
	}
	
	public void topInset(double theInset) {
		_myTopInset = theInset;
	}
	
	public double bottomInset() {
		return _myBottomInset;
	}
	
	public void bottomInset(double theInset) {
		_myBottomInset = theInset;
	}
	
	public void inset(double theInset) {
		_myLeftInset = theInset;
		_myRightInset = theInset;
		_myTopInset = theInset;
		_myBottomInset = theInset;
	}
	
	public double x() {
		return _myTranslation.x;
	}
	
	public void x(double theX) {
		_myTranslation.x = theX;
	}
	
	public double y() {
		return _myTranslation.y;
	}
	
	public void y(double theY) {
		_myTranslation.y = theY;
	}
}

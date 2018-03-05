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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCCharEvent;
import cc.creativecomputing.core.events.CCEvent;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow.CCGLFWKeyListener;
import cc.creativecomputing.gl.app.CCGLWindow.CCGLFWMouseListener;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2.CCVector2Event;
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
	
	@CCProperty(name = "inset")
	protected double _myInset = 0;
	
	@CCProperty(name = "edit_policy")
	protected CCUIEditPolicy _myEditPolicy = CCUIEditPolicy.ADMIN;
	
	protected CCUIWidget _myParent;
	
	protected CCUIWidget _myOverlay;
	
	protected boolean _myIsActive = true;
	
	public CCUIWidget(double theWidth, double theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public CCUIWidget() {
		this(0,0);
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
	
	public final CCListenerManager<CCGLFWMouseListener> mouseReleasedOutside = CCListenerManager.create(CCGLFWMouseListener.class);
	public final CCListenerManager<CCGLFWMouseListener> mouseReleased = CCListenerManager.create(CCGLFWMouseListener.class);
	public final CCListenerManager<CCGLFWMouseListener> mousePressed = CCListenerManager.create(CCGLFWMouseListener.class);
	public final CCListenerManager<CCGLFWMouseListener> mouseClicked = CCListenerManager.create(CCGLFWMouseListener.class);
	public final CCListenerManager<CCVector2Event> mouseMoved = CCListenerManager.create(CCVector2Event.class);
	public final CCListenerManager<CCVector2Event> mouseDragged = CCListenerManager.create(CCVector2Event.class);
	
	public final CCListenerManager<CCEvent> focusGained = CCListenerManager.create(CCEvent.class);
	public final CCListenerManager<CCEvent> focusLost = CCListenerManager.create(CCEvent.class);
	
	public final CCListenerManager<CCGLFWKeyListener> keyReleased = CCListenerManager.create(CCGLFWKeyListener.class);
	public final CCListenerManager<CCGLFWKeyListener> keyPressed = CCListenerManager.create(CCGLFWKeyListener.class);
	public final CCListenerManager<CCGLFWKeyListener> keyRepeatEvents = CCListenerManager.create(CCGLFWKeyListener.class);
	public final CCListenerManager<CCCharEvent> keyChar = CCListenerManager.create(CCCharEvent.class);
	
	public void update(CCGLTimer theTimer) {
		
	}
	
	public void updateMatrices(){
		_myLocalMatrix.reset();
		_myLocalMatrix.translate(_myTranslation);
		_myLocalMatrix.rotate(CCMath.radians(_myRotation.x));
		_myLocalMatrix.scale(_myScale.x, _myScale.y);
		
		double _myAlignmentX = 0;
		switch(_myHorizontalAlignment) {
		case RIGHT:
			_myAlignmentX = -1;
			break;
		case CENTER:
			_myAlignmentX = -0.5f;
			break;
		case LEFT:
			_myAlignmentX = 0f;
			break;
		}
		double _myAlignmentY = 0;
		switch(_myVerticalAlignment) {
		case TOP:
			_myAlignmentY = -1;
			break;
		case CENTER:
			_myAlignmentY = -0.5f;
			break;
		case BOTTOM:
			_myAlignmentY = -0f;
			break;
		}
//		_myLocalMatrix.translate(_myAlignmentX * _myWidth, _myAlignmentY * _myHeight);
		
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
//		_myMenue._myWorldMatrix.set(_myWorldMatrix);
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
	
	public double width() {
		return _myWidth + _myInset * 2;
	}
	
	public void width(double theWidth) {
		_myWidth = theWidth;
	}
	
	public double height() {
		return _myHeight + _myInset * 2;
	}
	
	public void height(double theHeight) {
		_myHeight = theHeight;
	}
	
	public double inset() {
		return _myInset;
	}
	
	public void inset(double theInset) {
		_myInset = theInset;
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

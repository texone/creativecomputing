/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui.widget;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.CCUIEditPolicy;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIInputEventType;
import cc.creativecomputing.ui.CCUIPropertyObject;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.decorator.CCUIForegroundDecorator;
import cc.creativecomputing.ui.decorator.background.CCUIBackgroundDecorator;
import cc.creativecomputing.ui.decorator.border.CCUIBorderDecorator;
import cc.creativecomputing.ui.event.CCUIWidgetEventListener;
import cc.creativecomputing.ui.event.CCUIWidgetEventType;
import cc.creativecomputing.ui.event.CCUIWidgetInteractionEvent;
import cc.creativecomputing.ui.event.CCUIWidgetUpdateEvent;

/**
 * @author christianriekoff
 *
 */
@CCXMLPropertyObject(name = "widget")
public class CCUIWidget{

	protected CCMatrix32 _myMatrix = new CCMatrix32();
	private CCMatrix32 _myInverseMatrix = new CCMatrix32();
	
	@CCProperty(name="translation")
	private CCVector2 _myTranslation = new CCVector2();
	@CCProperty(name="scale")
	private CCVector2 _myScale = new CCVector2(1,1);
	@CCProperty(name="rotation")
	private CCVector1 _myRotation = new CCVector1();
	
	@CCProperty(name="horizontal_alignment")
	private CCUIHorizontalAlignment _myHorizontalAlignment = CCUIHorizontalAlignment.LEFT;
	@CCProperty(name="vertical_alignment")
	private CCUIVerticalAlignment _myVerticalAlignment = CCUIVerticalAlignment.BOTTOM;
	
	@CCProperty(name="background")
	protected CCUIBackgroundDecorator _myBackground;
	@CCProperty(name="border")
	protected CCUIBorderDecorator _myBorder;
	@CCProperty(name="foreground")
	protected CCUIForegroundDecorator _myForeground;
	
//	@CCXMLProperty(name="event_handler")
	private List<CCUIWidgetEventListener> _myListener = new ArrayList<CCUIWidgetEventListener>();
	
	private boolean _myIsOver = false;
	private boolean _myIsPressed = false;
	
	@CCProperty(name = "width")
	private double _myWidth = 0;
	
	@CCProperty(name = "height")
	private double _myHeight = 0;
	
	@CCProperty(name = "edit_policy")
	protected CCUIEditPolicy _myEditPolicy = CCUIEditPolicy.ADMIN;
	
	@CCProperty(name = "children")
	private List<CCUIWidget> _myChildren;
	
	private boolean _myIsInEditMode = false;
	
	public CCUIWidget(double theWidth, double theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public CCUIWidget() {
		this(0,0);
	}
	
	public void setup(CCUI theUI, CCUIWidget theParent) {
		if(_myBackground != null) {
			_myBackground.setup(theUI, this);
		}
		if(_myBorder != null)_myBorder.setup(theUI, this);
		if(_myForeground != null)_myForeground.setup(theUI, this);
		
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.setup(theUI, this);
		}
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
	
	public CCMatrix32 transformation() {
		return _myMatrix;
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
	
	public void background(CCUIBackgroundDecorator theBackground) {
		_myBackground = theBackground;
	}
	
	public void border(CCUIBorderDecorator theBorder) {
		_myBorder = theBorder;
	}
	
	public CCUIBorderDecorator border() {
		return _myBorder;
	}
	
	public void foreground(CCUIForegroundDecorator theForeground) {
		_myForeground = theForeground;
	}
	
	public CCUIForegroundDecorator foreground() {
		return _myForeground;
	}
	
	public void addChild(CCUIWidget theWidget) {
		if(_myChildren == null)_myChildren = new ArrayList<CCUIWidget>();
		_myChildren.add(theWidget);
	}
	
	public void removeChild(CCUIWidget theWidget) {
		if(_myChildren == null)return;
		_myChildren.remove(theWidget);
	}
	
	public boolean isInside(CCVector2 theVector) {
		CCVector2 myTransformedVector = _myInverseMatrix.transform(theVector);
		return 
			myTransformedVector.x >= 0 && 
			myTransformedVector.x <= width() &&
			myTransformedVector.y <= 0 && 
			myTransformedVector.y >= -height();
	}
	
	public boolean isInside(double theX, double theY) {
		return isInside(new CCVector2(theX, theY));
	}
	
	public void addListener(CCUIWidgetEventListener theListener) {
		_myListener.add(theListener);
	}
	
	public void removeListener(CCUIWidgetEventListener theListener) {
		_myListener.remove(theListener);
	}
	
	private void callListener(CCUIWidgetEventType theEventType, CCVector2 thePosition, CCVector2 theTransformedPosition) {
		for(CCUIWidgetEventListener myListener:new ArrayList<CCUIWidgetEventListener>(_myListener)) {
			myListener.onEvent(new CCUIWidgetInteractionEvent(theEventType, thePosition, theTransformedPosition), this);
		}
	}
	
	private void handleEvent(CCVector2 theVector, CCVector2 theTransformedVector, CCUIInputEventType theEventType) {
		boolean myIsInside = isInside(theTransformedVector);
		
		switch(theEventType) {
		case PRESS:
			if(myIsInside) {
				callListener(CCUIWidgetEventType.PRESS, theVector, theTransformedVector);
				_myIsPressed = true;
			} else {
				callListener(CCUIWidgetEventType.PRESS_OUTSIDE, theVector, theTransformedVector);
			}
			break;
		case CLICK:
			if(myIsInside) {
				callListener(CCUIWidgetEventType.CLICK, theVector, theTransformedVector);
			}
			break;
		case DOUBLE_CLICK:
			if(myIsInside) {
				callListener(CCUIWidgetEventType.DOUBLE_CLICK, theVector, theTransformedVector);
			}
			break;
		case RELEASE:
			if(!_myIsPressed)return;
			if(myIsInside) {
				callListener(CCUIWidgetEventType.RELEASE, theVector, theTransformedVector);
			}else {
				callListener(CCUIWidgetEventType.RELEASE_OUTSIDE, theVector, theTransformedVector);
			}
			_myIsPressed = false;
			break;
		case MOVE:
			if(_myIsOver && !myIsInside) {
				callListener(CCUIWidgetEventType.OUT, theVector, theTransformedVector);
				_myIsOver = false;
			}
			
			if(!_myIsOver && myIsInside) {
				callListener(CCUIWidgetEventType.OVER, theVector, theTransformedVector);
				_myIsOver = true;
			}
			if(myIsInside) {
				callListener(CCUIWidgetEventType.MOVE, theVector, theTransformedVector);
			}else {
				callListener(CCUIWidgetEventType.MOVE_OUTSIDE, theVector, theTransformedVector);
			}
			break;
		case DRAGG:
			if(_myIsOver && !myIsInside) {
				callListener(CCUIWidgetEventType.OUT, theVector, theTransformedVector);
				_myIsOver = false;
			}
			
			if(!_myIsOver && myIsInside) {
				callListener(CCUIWidgetEventType.OVER, theVector, theTransformedVector);
				_myIsOver = true;
			}
			if(myIsInside) {
				callListener(CCUIWidgetEventType.DRAGG, theVector, theTransformedVector);
			}else {
				callListener(CCUIWidgetEventType.DRAGG_OUTSIDE, theVector, theTransformedVector);
			}
			break;
		}
	}
	
	public void checkEvent(CCVector2 theVector, CCUIInputEventType theEventType) {
		CCVector2 myTransformedVector = _myInverseMatrix.transform(theVector);
		
		if(_myListener.size() > 0) {
			handleEvent(theVector, myTransformedVector, theEventType);
		}
		
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.checkEvent(myTransformedVector, theEventType);
		}
	}
	
	public void keyEvent(CCGLKeyEvent theKeyEvent, CCUIInputEventType theEventType) {
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.keyEvent(theKeyEvent, theEventType);
		}
	}
	
	public void keyCharEvent(char theChar){
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.keyCharEvent(theChar);
		}
	}
	
	public void update(double theDeltaTime) {
		_myMatrix.reset();
		_myMatrix.translate(_myTranslation);
		_myMatrix.rotate(CCMath.radians(_myRotation.x));
		_myMatrix.scale(_myScale.x, _myScale.y);
		
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
		_myMatrix.translate(_myAlignmentX * _myWidth, _myAlignmentY * _myHeight);
		
		_myInverseMatrix = _myMatrix.inverse();
		
		for(CCUIWidgetEventListener myListener:new ArrayList<CCUIWidgetEventListener>(_myListener)) {
			myListener.onEvent(new CCUIWidgetUpdateEvent(theDeltaTime), this);
		}
		
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.update(theDeltaTime);
		}
	}
	
	public void draw(CCGraphics g) {
		g.pushMatrix();
		g.applyMatrix(_myMatrix);
		
		if(_myBackground != null)_myBackground.draw(g, this);
		if(_myBorder != null)_myBorder.draw(g, this);
		if(_myForeground != null)_myForeground.draw(g, this);
		
		if(_myChildren != null) {;
			for(CCUIWidget myChild:_myChildren) {
				myChild.draw(g);
			}
		}
		
		g.popMatrix();
	}
	
	public double width() {
		return _myWidth;
	}
	
	public void width(double theWidth) {
		_myWidth = theWidth;
	}
	
	public double height() {
		return _myHeight;
	}
	
	public void height(double theHeight) {
		_myHeight = theHeight;
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

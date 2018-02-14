package cc.creativecomputing.ui.layout;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * Basic interface of a layout engine.
 */
public class CCUIPane extends CCUIWidget{
	
	@CCProperty(name = "margin")
	protected double _cMargin = 0;

	@CCProperty(name = "space")
	protected double _cSpace = 0;
	
	protected CCVector2 _myMinSize = new CCVector2();

	@CCProperty(name = "children")
	protected List<CCUIWidget> _myChildren;
	
	public CCUIPane() {
		super();
	}

	public CCUIPane(double theWidth, double theHeight) {
		super(theWidth, theHeight);
	}

	public List<CCUIWidget> children(){
		return _myChildren;
	}
	
	public CCVector2 minSize() {
		return _myMinSize;
	}
	
	public void margin(double theMargin) {
		_cMargin = theMargin;
	}
	
	public void space(double theSpace) {
		_cSpace = theSpace;
	}
	
	public void addChild(CCUIWidget theWidget) {
		if(_myChildren == null)_myChildren = new ArrayList<CCUIWidget>();
		_myChildren.add(theWidget);
	}
	
	public void removeChild(CCUIWidget theWidget) {
		if(_myChildren == null)return;
		_myChildren.remove(theWidget);
	}
	
	public CCUIWidget childAtPosition(CCVector2 thePosition, CCVector2 theLocalMouseCoord) {
		for(CCUIWidget myWidget:_myChildren) {
			if(myWidget instanceof CCUIPane) {
				CCVector2 myTransformedVector = myWidget.inverseTransform().transform(thePosition);
				CCLog.info(thePosition,myTransformedVector, myWidget, myWidget.x(), myWidget.y());
				CCUIWidget myResult = ((CCUIPane)myWidget).childAtPosition(myTransformedVector, theLocalMouseCoord);
				if(myResult != null){
					return myResult;
				}
			}else {
				CCVector2 myTransformedVector = myWidget.inverseTransform().transform(thePosition);
				boolean myIsInside = myWidget.isInsideLocal(myTransformedVector);
				CCLog.info(thePosition,myTransformedVector, myWidget, myWidget.x(), myWidget.y());
				if(myIsInside) {
					if(theLocalMouseCoord != null)theLocalMouseCoord.set(myTransformedVector);
					return myWidget;
				}
			}
			
		}
		return null;
	}
	
	@Override
	public double width() {
		return CCMath.max(_myWidth, minSize().x) + _myInset * 2;
	}
	
	@Override
	public double height() {
		return CCMath.max(_myHeight, minSize().y) + _myInset * 2;
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
		super.update(theTimer);
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.update(theTimer);
		}
	}
	
	@Override
	public void updateMatrices() {
		super.updateMatrices();
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.updateMatrices();
		}
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		super.drawContent(g);
		
		if(_myChildren == null) return;
		for(CCUIWidget myChild:_myChildren) {
			myChild.draw(g);
		}
	}
	
}
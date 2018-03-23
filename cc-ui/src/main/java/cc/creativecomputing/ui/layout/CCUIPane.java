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
package cc.creativecomputing.ui.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * Basic interface of a layout engine.
 */
public class CCUIPane extends CCUIWidget implements Iterable<CCUIWidget>{

	@CCProperty(name = "horizontal space")
	protected double _cHorizontalSpace = 0;
	@CCProperty(name = "vertical space")
	protected double _cVerticalSpace = 0;
	
	protected CCVector2 _myMinSize = new CCVector2();

	@CCProperty(name = "children")
	protected final List<CCUIWidget> _myChildren = new ArrayList<>();
	
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
	
	public void space(double theSpace) {
		_cHorizontalSpace = theSpace;
		_cVerticalSpace = theSpace;
	}
	
	public void addChild(CCUIWidget theWidget) {
		theWidget.parent(this);
		_myChildren.add(theWidget);
		updateLayout();
		root().updateMatrices();
	}
	
	public void removeChild(CCUIWidget theWidget) {
		if(_myChildren == null)return;
		if(_myChildren.remove(theWidget)){
			theWidget.parent(null);
			root().updateMatrices();
		}
	}

	public void removeAll() {
		if(_myChildren == null)return;
		_myChildren.clear();
		root().updateMatrices();
	}
	
	public CCUIWidget childAtPosition(CCVector2 thePosition) {
		thePosition = _myLocalInverseMatrix.transform(thePosition);
		for(CCUIWidget myWidget:_myChildren) {
			if(myWidget instanceof CCUIPane) {
//				CCVector2 myTransformedVector = myWidget.localInverseTransform().transform(thePosition);
				CCUIWidget myResult = ((CCUIPane)myWidget).childAtPosition(thePosition);
				if(myResult != null){
					return myResult;
				}
			}else {
				CCVector2 myTransformedVector = myWidget.localInverseTransform().transform(thePosition);
				boolean myIsInside = myWidget.isInsideLocal(myTransformedVector);
				if(myIsInside) {
					return myWidget;
				}
			}
			
		}
		return null;
	}
	
	@Override
	protected double widthCalc(){
		return CCMath.max(_myWidth, minSize().x);
	}
	
	@Override
	public void width(double theWidth) {
		super.width(theWidth);
		updateLayout();
	}
	
	@Override
	public double height() {
		return CCMath.max(_myHeight, minSize().y);
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
		super.update(theTimer);
		if(_myChildren == null)return;
		for(CCUIWidget myWidget:_myChildren) {
			myWidget.update(theTimer);
		}
	}
	
	public void updateLayout(){
		
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

	@Override
	public Iterator<CCUIWidget> iterator() {
		return _myChildren.iterator();
	}
	
}

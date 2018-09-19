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
package cc.creativecomputing.controlui.controls;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCPropertyPopUp;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaAlign;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaFlexDirection;

public abstract class CCValueControl<Type, Handle extends CCPropertyHandle<Type>> implements CCControl{
	
	protected Handle _myHandle;
	
	protected CCUILabelWidget _myLabel;
	
	protected CCControlComponent _myControlComponent;
	
	private boolean _myIsOver;
	
	private int _myIndex = 0;
	
	private double _myTimer = 0;
	
	private static int MAX_LABEL_LENGTH = 11;
	private static int KEEP_LABEL_LENGTH = 3;

	public CCValueControl(Handle theHandle, CCControlComponent theControlComponent){
		
		_myHandle = theHandle;
		_myControlComponent = theControlComponent;
		
        //Create the label.
		_myLabel = new CCUILabelWidget(_myHandle.name().substring(0, CCMath.min(_myHandle.name().length(), MAX_LABEL_LENGTH)));
		_myLabel.onOver.add(o ->{
			_myIsOver = true;
			_myTimer = 0;
			_myIndex = 0;
		});
		_myLabel.onOut.add(o ->{
			_myIsOver = false;
			_myIndex = 0;
		});
		
		_myLabel.updateEvents.add(t -> {
			if(!_myIsOver)return;
			if(_myHandle.name().length() < MAX_LABEL_LENGTH) {
				_myIndex = 0;
				_myLabel.text(_myHandle.name());
				return;
			}
			_myTimer += t.deltaTime() * 3;
			_myIndex = (int)_myTimer;
			_myIndex %= _myHandle.name().length() - MAX_LABEL_LENGTH + KEEP_LABEL_LENGTH * 2;
			_myIndex -= KEEP_LABEL_LENGTH;
			_myIndex = CCMath.constrain(_myIndex, 0, _myHandle.name().length() - MAX_LABEL_LENGTH);
			_myLabel.text(_myHandle.name().substring(_myIndex, _myIndex + CCMath.min(_myHandle.name().length(), MAX_LABEL_LENGTH)));
		});
		
		_myLabel.mousePressed.add(e -> {
			if(e.isAltDown()){
				_myHandle.restoreDefault();
				return;
			}
			if(e.isControlDown()){
				_myHandle.restorePreset();
				return;
			}
			if(e.button == CCGLMouseButton.BUTTON_RIGHT){
				_myLabel.updateMatrices();
				new CCPropertyPopUp(_myHandle, _myLabel, e.x, e.y);
			}
		});
	}

	public Handle property() {
		return _myHandle;
	}
	
	private CCEvent<Type> _myListener = null;
	
	public void addListener(CCEvent<Type> theListener){
		_myHandle.changeEvents.add(_myListener = theListener);
	}
	
	public void dispose() {
		if(_myListener != null)_myHandle.changeEvents.remove(_myListener);
	}
	
	
	protected GridBagConstraints constraints(int theX, int theY, int theWidth, int theAnchor, int theTop, int theLeft, int theBottom, int theRight){
		GridBagConstraints myResult = new GridBagConstraints();
		myResult.gridx = theX;
		myResult.gridy = theY;
		myResult.gridwidth = theWidth;
		myResult.insets = new Insets(theTop, theLeft, theBottom, theRight);
		myResult.anchor = theAnchor;
		return myResult;
	}
	
	protected GridBagConstraints constraints(int theX, int theY, int theAnchor, int theTop, int theLeft, int theBottom, int theRight){
		return constraints(theX, theY, 1, theAnchor,  theTop, theLeft, theBottom, theRight);
	}
	
	public abstract Type value();
	
	public void addToHorizontalPane(CCUIWidget thePane) {
		
	}
	
	@Override
	public void addToPane(CCUIWidget thePane, int theY, int theDepth) {
		CCUIWidget myUIPane = new CCUIWidget();
		myUIPane.flexDirection(CCYogaFlexDirection.ROW);
		myUIPane.alignItems(CCYogaAlign.CENTER);
		myUIPane.margin(CCYogaEdge.VERTICAL, 5);
		myUIPane.padding(CCYogaEdge.RIGHT,10);
		_myLabel.width(100);
		_myLabel.textField().align(CCTextAlign.RIGHT);
		_myLabel.minWidth(120);
		_myLabel.padding(CCYogaEdge.ALL, 4);
		_myLabel.margin(CCYogaEdge.RIGHT, 10);
		myUIPane.addChild(_myLabel);
		addToHorizontalPane(myUIPane);
		thePane.addChild(myUIPane);
	}
	
}

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
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.draw.CCUIDrawable;

public class CCUIDropDownWidget extends CCUILabelWidget{
	
	private CCUIMenu _myMenue;
	
	private CCUIDrawable _myItemSelectBackground;
	
	private CCUIDrawable _myItemBackground;
	
	private boolean _myIsInClickMode = false;
	private boolean _myIsDragged = false;
	
	private boolean _myAdjustLabel = true;
	
	private CCUIIconWidget _myIcon;
	
	private boolean _myShowIcon = true;;
	
	public CCEventManager<String> changeEvents = new CCEventManager<>();
	
	public CCUIDropDownWidget(CCFont<?> theFont, String theTitle, CCUIMenu theMenue) {
		super(theFont, theTitle);
		_myOverlay = _myMenue = theMenue;
		_myMenue.parent(this);
		
		_myIcon = new CCUIIconWidget(CCEntypoIcon.ICON_SELECT_ARROWS);

		inset(2);
		
		mousePressed.add(event -> {
			_myMenue.isActive(true);
			_myIsInClickMode = false;
			_myIsDragged = false;
		});
		mouseReleased.add(event ->{
			_myMenue.isActive(false);
		});
		mouseReleasedOutside.add(event ->{
			_myMenue.isActive(false);
		});
		
		_myMenue.mouseClicked.add(event ->{
			if(!_myIsInClickMode){
				_myMenue.isActive(true);
				_myIsInClickMode = true;
			}else{
				_myMenue.isActive(false);
				_myIsInClickMode = false;
			}
		});
		
		_myMenue.mouseDragged.add(pos ->{
			handleHover(pos);
			_myIsDragged = true;
		});
		_myMenue.mouseMoved.add(this::handleHover);

		
		_myMenue.mouseReleased.add(event -> {
			if(!_myMenue.isActive())return;
			if(!(_myIsDragged || _myIsInClickMode))return;

			for(CCUIMenuItem myItem:_myMenue.items()){
				if(myItem.checkBox() != null)myItem.checkBox().isSelected(false, true);
			}
			
			CCUIWidget myWidget = _myMenue.childAtPosition(new CCVector2(event.x, event.y));
			CCUIMenuItem mySelectedItem = myWidget instanceof CCUIMenuItem ? (CCUIMenuItem)myWidget : null;
			if(mySelectedItem != null){
				mySelectedItem.mouseReleased.event(event);
				if(_myAdjustLabel)text().text(mySelectedItem.text());
				if(mySelectedItem.checkBox() != null)mySelectedItem.checkBox().isSelected(true, true);
			}
			_myMenue.isActive(false);
		});
		_myMenue.mouseReleasedOutside.add(event ->{
			_myMenue.isActive(false);
		});
	}
	
	public void adjustLabelBySelection(boolean theAdjustLabel){
		_myAdjustLabel = theAdjustLabel;
	}
	
	public void showIcon(boolean theShowIcon) {
		_myShowIcon = theShowIcon;
	}
	
	private void handleHover(CCVector2 pos){
		for(CCUIMenuItem myItem:_myMenue.items()){
			myItem.background(_myItemBackground);
		}
		CCUIWidget myWidget = _myMenue.childAtPosition(pos);
		CCUIMenuItem mySelectedItem = myWidget instanceof CCUIMenuItem ? (CCUIMenuItem)myWidget : null;
		if(mySelectedItem != null){
			mySelectedItem.background(_myItemSelectBackground);
		}
	}
	
	public CCUIDropDownWidget(CCFont<?> theFont, CCUIMenu theMenue) {
		this(theFont, "...", theMenue);
	}
	
	public CCUIDropDownWidget(CCFont<?> theFont){
		this(theFont, new CCUIMenu(theFont));
	}
	
	public void itemSelectBackground(CCUIDrawable theSelectBackground){
		_myItemSelectBackground = theSelectBackground;
	}
	
	public void itemBackground(CCUIDrawable theItemBackground){
		_myItemBackground = theItemBackground;
	}
	
	public CCUIMenu menue(){
		return _myMenue;
	}
	
	@Override
	public double width() {
		return CCMath.max(_myWidth,_myTextField.width()) + _myLeftInset + _myRightInset;
	}
	
	@Override
	public void width(double theWidth) {
		super.width(theWidth);
		
		_myMenue.width(theWidth);
	}
	
	@Override
	public void updateMatrices() {
		super.updateMatrices();
//		_myOverlay._myLocalMatrix.set(_myLocalMatrix);
//		_myOverlay._myLocalMatrix.translate(_myOverlay.translation());
//		_myOverlay._myLocalInverseMatrix = _myOverlay._myLocalMatrix.inverse();
////		_myMenue._myWorldMatrix.set(_myWorldMatrix);
//		CCMatrix32 myWorldInverse = _myWorldMatrix.clone();
//		myWorldInverse.translate(_myOverlay.translation());
//		_myOverlay._myWorldInverseMatrix.set(myWorldInverse.inverse());
	}

	public void addItem(String theLabel) {
		_myMenue.addItem(new CCUICheckBox(), theLabel, e -> {});
	}
	
	public void addSeparator(){
		_myMenue.addSeparator();
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		super.drawContent(g);
		if(!_myShowIcon)return;
		_myIcon.text().position().set(width() - _myIcon.width(), - _myIcon.height(), 0);
		_myIcon.text().draw(g);
		
	}

	public void selectedItem(String theValue, boolean theSendEvents) {
		for(CCUIMenuItem myItem:_myMenue.items()) {
			CCUICheckBox myCheckBox = myItem.checkBox() != null ? myItem.checkBox() : null;
			if(myCheckBox == null)continue;
			
			if(myItem.text().equals(theValue)) {
				if(myCheckBox.isSelected())continue;
				if(theSendEvents)changeEvents.event(theValue);
			}else {
				myCheckBox.isSelected(false, theSendEvents);
			}
			
		}
	}

	public void removeAllItems() {
		_myMenue.removeAll();
	}

	public void editable(boolean b) {
		// TODO Auto-generated method stub
		
	}
}

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
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;

public class CCUIDropDownWidget extends CCUILabelWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = new CCUIWidgetStyle();
		myResult.font(CCUIContext.FONT_20);
		myResult.inset(4);
		myResult.verticalAlignment(CCUIVerticalAlignment.CENTER);
		myResult.horizontalAlignment(CCUIHorizontalAlignment.LEFT);
		myResult.background(new CCUIFillDrawable(new CCColor(0.3d)));
		myResult.itemSelectBackground(new CCColor(0.5d));
		return myResult;
	}
	
	private CCUIMenu _myMenue;
	
	private CCUIIconWidget _myIcon;
	
	private boolean _myShowIcon = true;;
	
	public CCEventManager<String> changeEvents = new CCEventManager<>();
	
	private boolean _myAdjustLabel = true;
	
	public CCUIDropDownWidget(CCUIWidgetStyle theStyle, String theTitle, CCUIMenu theMenue) {
		super(theStyle, theTitle);
		_myOverlay = _myMenue = theMenue;
		_myMenue.parent(this);
		
		_myMenue.clickItemEvents.add(item -> {
			if(_myAdjustLabel)textField().text(item.text());
		});
		
		_myIcon = new CCUIIconWidget(new CCUIWidgetStyle(),CCEntypoIcon.ICON_SELECT_ARROWS);
		
		mousePressed.add(event -> {
			_myMenue.translation().set(event.x, event.y);
			_myMenue.isActive(true);
			_myMenue.updateMatrices();
			_myMenue.reset();
		});
		mouseReleased.add(event ->{
			_myMenue.isActive(false);
		});
		mouseReleasedOutside.add(event ->{
			_myMenue.isActive(false);
		});	
	}
	
	public CCUIDropDownWidget(String theTitle, CCUIMenu theMenue) {
		this(createDefaultStyle(), theTitle, theMenue);
	}
	
	public CCUIDropDownWidget(CCUIWidgetStyle theStyle, CCUIMenu theMenue) {
		this(theStyle, "...", theMenue);
	}
	
	public CCUIDropDownWidget(CCUIMenu theMenue){
		this(createDefaultStyle(), theMenue);
	}
	
	public void showIcon(boolean theShowIcon) {
		_myShowIcon = theShowIcon;
	}
	
	public CCUIDropDownWidget(CCUIWidgetStyle theMenueStyle){
		this(theMenueStyle, new CCUIMenu(theMenueStyle));
	}
	
	public CCUIDropDownWidget(){
		this(createDefaultStyle());
	}
	
	public void adjustLabelBySelection(boolean theAdjustLabel){
		_myAdjustLabel = theAdjustLabel;
	}
	
	public CCUIMenu menue(){
		return _myMenue;
	}
	
	@Override
	public double width() {
		return CCMath.max(_myWidth,_myTextField.width()) + _myStyle.leftInset() + _myStyle.rightInset();
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
		_myIcon.textField().position().set(width() - _myIcon.width(), - _myIcon.height(), 0);
		_myIcon.textField().draw(g);
		
	}

	public void selectedItem(String theValue, boolean theSendEvents) {
		for(CCUIMenuItem myItem:_myMenue.items()) {
			CCUICheckBox myCheckBox = myItem.checkBox() != null ? myItem.checkBox() : null;
			if(myCheckBox == null)continue;
			
			if(myItem.text().equals(theValue)) {
				text(theValue);
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

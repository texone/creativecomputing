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

import java.nio.file.Path;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.io.CCFileChooser;

public class CCUIFileWidget extends CCUILabelWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		return CCUITextFieldWidget.createDefaultStyle();
	}
	
	private CCUIIconWidget _myChevron;
	
	public CCEventManager<Path> changeEvents = new CCEventManager<>();
	
	public CCUIFileWidget(CCUIWidgetStyle theStyle, String theTitle, CCUIMenu theMenue) {
		super(theStyle, theTitle);
		
		_myChevron = new CCUIIconWidget(CCEntypoIcon.ICON_FOLDER);
		
		mousePressed.add(event -> {
		});
		mouseReleased.add(event ->{
			Path myResult = new CCFileChooser("xml", "json").openFile("YO");
			if(myResult == null){
				return;
			}
			text(myResult.toString());
			changeEvents.event(myResult);
		});
		mouseReleasedOutside.add(event ->{
		});
	}
	
	
	public CCUIFileWidget(CCUIWidgetStyle theStyle){
		this(theStyle,"...", new CCUIMenu(theStyle));
	}
	
	public CCUIFileWidget(){
		this(createDefaultStyle());
	}
	
	@Override
	public double width() {
		return _myWidth;
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
	
	@Override
	public void drawContent(CCGraphics g) {
		super.drawContent(g);
		_myChevron.textField().position().set(width() - _myChevron.width() , - _myChevron.height() + 4, 0);
		_myChevron.textField().draw(g);
		
	}
}

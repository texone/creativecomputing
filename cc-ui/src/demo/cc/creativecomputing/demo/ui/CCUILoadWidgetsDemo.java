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
package cc.creativecomputing.demo.ui;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.input.CCUIInput;
import cc.creativecomputing.ui.input.CCUIMouseInput;
import cc.creativecomputing.ui.widget.CCUISliderWidget;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIValueBoxWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUILoadWidgetsDemo extends CCApp {
	
	private CCUI _myUI;
	private CCUIInput _myUIInput;
	private CCUIWidget _myButton;
	private CCUISliderWidget _mySlider;
	
	private CCUITextFieldWidget _myTextField;
	private CCUITextFieldWidget _myTextField2;
	
	private CCUIValueBoxWidget _myValueBox;

	@Override
	public void setup() {
		
		_myUI = new CCUI(this);
		_myUI.loadUI("ui.xml");
		_myUIInput = new CCUIMouseInput(this, _myUI);
		
		_myButton = _myUI.createWidget("button1");
		_myButton.property2f(CCUIWidget.TRANSLATION_PROPERTY).set(0,-100);
		_mySlider = _myUI.createWidget("slider1", CCUISliderWidget.class);
		
		_myTextField = _myUI.createWidget("textfield1", CCUITextFieldWidget.class);
		_myTextField2 = _myUI.createWidget("textfield2", CCUITextFieldWidget.class);
		
		_myValueBox = _myUI.createWidget("valuebox1", CCUIValueBoxWidget.class);
//		_mySlider.property2f(CCUIWidget.TRANSLATION_PROPERTY).set(0,-100);
		
		g.clearColor(100);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myUI.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		_myUI.draw(g);
		
		g.color(255);
//		g.line(-width/2,-200, width/2,-200);
//
//		g.line(-200,-height/2, -200, height/2);
//		g.line(-100,-height/2, -100, height/2);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUILoadWidgetsDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}


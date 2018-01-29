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
import cc.creativecomputing.ui.decorator.border.CCUILineBorderDecorator;
import cc.creativecomputing.ui.widget.CCUISliderWidget;

public class CCUISliderWidgetDemo extends CCApp {
	
	private CCUISliderWidget _mySliderWidget;

	@Override
	public void setup() {
		_mySliderWidget = new CCUISliderWidget(200,10);
		_mySliderWidget.bar().border(new CCUILineBorderDecorator());
		_mySliderWidget.button().border(new CCUILineBorderDecorator());
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		_mySliderWidget.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUISliderWidgetDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}


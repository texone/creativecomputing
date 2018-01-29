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

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.decorator.border.CCUILineBorderDecorator;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIWidgetPlacementDemo extends CCGLApp {
	
	private CCUIWidget _myWidget;

	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		_myWidget = new CCUIWidget(100, 200);
		_myWidget.translation().set(100,100);
		_myWidget.border(new CCUILineBorderDecorator());
		
		_myMainWindow.mouseMoveEvents.add(pos -> {
			if(_myWidget.isInside(pos.x - g.width()/2, g.height()/2 - pos.y)){
				_myWidget.border(new CCUILineBorderDecorator(CCColor.RED.clone(), 2, 30));
			}else{
				_myWidget.border(new CCUILineBorderDecorator(CCColor.WHITE.clone(), 2, 30));
			}
		});
	}

	@Override
	public void update(final CCGLTimer theDeltaTime) {
		
		_myWidget.update(theDeltaTime.deltaTime());
		
		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.line(-g.width()/2,0,g.width()/2,0);
		g.line(0,-g.height()/2,0,g.height()/2);
		g.pushAttribute();
		_myWidget.draw(g);
		g.popAttribute();
	}

	public static void main(String[] args) {
		CCUIWidgetPlacementDemo myDemo = new CCUIWidgetPlacementDemo();
		myDemo.run();
	}
}


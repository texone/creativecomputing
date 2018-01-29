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

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.input.CCUIInput;
import cc.creativecomputing.ui.input.CCUIMouseInput;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIDecoratorDemo extends CCGLApp {
	
	private CCUI _myUI;
	private CCUIInput _myUIInput;
	private CCUIWidget _myContainerWidget;

	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		_myUI = new CCUI(this);
		_myUI.loadUI(CCNIOUtil.classPath(this, "decorator.xml"));
		_myUIInput = new CCUIMouseInput(this._myMainWindow, _myUI);
		
		_myContainerWidget = _myUI.createWidget("widgetContainer");
	}

	@Override
	public void update(final CCGLTimer theTimer) {
		_myUI.update(theTimer.deltaTime());
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myUI.draw(g);
	}

	public static void main(String[] args) {
		CCUIDecoratorDemo myDemo = new CCUIDecoratorDemo();
		myDemo.width = 800;
		myDemo.height = 400;
		myDemo.run();
	}
}


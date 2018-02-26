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
package cc.creativecomputing.controlui.demo;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;

public class CCControlAppDemo extends CCGLApp{

	private CCControlApp _myControls;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		_myControls = new CCControlApp(this);
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(1.0f, 0.0f, 0.0f, 0.0f);
		g.clear();
		g.rect(0, 0, 100, 100);
	}
	
	public static void main(String[] args) {
		CCControlAppDemo myDemo = new CCControlAppDemo();
		myDemo.run();
	}
}

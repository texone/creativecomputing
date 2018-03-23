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

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCControlAppDemo extends CCGLApp{

	
	
	@CCProperty(name = "x", min = 0, max = 1000)
	private double _cX = 0;
	@CCProperty(name = "y", min = 0, max = 1000)
	private double _cY = 0;
	@CCProperty(name = "draw rect")
	private boolean _cDrawRect = true;
	@CCProperty(name = "color")
	private CCColor _cColor = new CCColor();
	@CCProperty(name = "gradient")
	private CCGradient _cGradient = new CCGradient();
	@CCProperty(name = "spline")
	private CCLinearSpline _cSpline = new CCLinearSpline(false);
	
	@Override
	public void setup() {
		for(int i = 0; i < 10; i++){
			_cSpline.addPoint(new CCVector3(CCMath.random(), CCMath.random()));
		}
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(1.0f, 0.0f, 0.0f, 0.0f);
		g.clear();
		g.color(_cColor);
		if(_cDrawRect)g.rect(_cX, _cY, 100, 100);
		
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		for(int x = -g.width()/2; x < g.width()/2;x++){
			g.color(_cGradient.color(CCMath.map(x, -g.width()/2, g.width()/2, 0, 1)));
			g.vertex(x, -30);
			g.vertex(x, 30);
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		CCGLApp myDemo = new CCControlAppDemo();
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		CCControlApp _myControls = new CCControlApp(myApplicationManager, myDemo);
		myApplicationManager.run();
	}
}

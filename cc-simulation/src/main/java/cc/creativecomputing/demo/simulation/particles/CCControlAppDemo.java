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
package cc.creativecomputing.demo.simulation.particles;

import java.nio.file.Path;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCControlAppDemo extends CCGLApp{

	@CCProperty(name = "vec", min = 0, max = 1000)
	private CCVector2 _myVec = new CCVector2();
	
	@CCProperty(name = "x", min = 0, max = 1000)
	private double _cX = 0;
	@CCProperty(name = "y", min = 0, max = 1000)
	private double _cY = 0;
	@CCProperty(name = "z", min = 0, max = 20)
	private double _cZ = 0;
	@CCProperty(name = "draw rect")
	private boolean _cDrawRect = true;
	@CCProperty(name = "color")
	private CCColor _cColor = new CCColor();
	@CCProperty(name = "gradient")
	private CCGradient _cGradient = new CCGradient();
	@CCProperty(name = "spline")
	private CCLinearSpline _cSpline = new CCLinearSpline(false);
	@CCProperty(name = "envelope")
	private CCEnvelope _cEnvelope = new CCEnvelope();
	@CCProperty(name = "path")
	private Path _cPath;
	
	@CCProperty(name = "texture")
	private CCTexture2D _myTexture;
	
	@Override
	public void setup() {
		for(int i = 0; i < 10; i++){
			_cSpline.addPoint(new CCVector3(CCMath.random(), CCMath.random()));
		}
		g.depthTest();
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("waltz.jpg")));
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
		
		g.pushMatrix();
		g.ortho();
		g.scale(g.width(), g.height());
		g.color(255);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(CCVector3 myPoint:_cSpline){
			g.vertex(myPoint);
		}
		g.endShape();
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(double i = 0; i <= 100;i++){
			g.vertex(i / 100, _cEnvelope.value(i / 100));
		}
		g.endShape();
		
		g.popMatrix();
		g.image(_myTexture, 0,0);
	}
	
	public static void main(String[] args) {
		CCGLApp myDemo = new CCControlAppDemo();
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		CCControlApp _myControls = new CCControlApp(myApplicationManager, myDemo);
		myApplicationManager.run();
	}
}

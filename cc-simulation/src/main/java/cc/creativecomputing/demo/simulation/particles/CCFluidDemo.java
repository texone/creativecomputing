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
package cc.creativecomputing.demo.simulation.particles;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.gl.app.events.CCMouseSimpleInfo;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.simulation.particles.fluidfield.CCFluid;

public class CCFluidDemo extends CCGL2Adapter {

	
	private CCFluid _myFluid;
	private CCRenderBuffer _myRenderTexture;
	
	private boolean _myMouseDragged = false;
	private CCMouseSimpleInfo _myMouseInfo;
	

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myFluid = new CCFluid(1000, 700);
		
		_myMouseInfo = new CCMouseSimpleInfo();
		mouseMotionListener().add(_myMouseInfo);
		mouseListener().add(_myMouseInfo);
		
	}
	
	
	
	float _myHue = 0;

	/**
	 * 
	 */ 
	@Override
	public void update(final CCAnimator theAnimator) {
		_myHue+=theAnimator.deltaTime();
		if(_myHue > 5) {
			_myHue -= 5;
			
		}

		_myFluid.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);
//		_myFluid.draw(g);
//		_myRenderTexture.beginDraw();
//		g.clear();
//		g.image(_myFluid.colorTexture(),-500, -350, 1000, 700);
//		_myRenderTexture.endDraw();
		g.color(255);
		g.image(_myFluid.colorBuffer().attachment(0),-g.width()/2, -g.height()/2, g.width(), g.height());
		
		g.color(0);
		g.color(255);
		g.clearDepthBuffer();
	}
	
	CCVector2 _myMouseMovement;
	CCColor _myColor = new CCColor();
	
	public void mouseMoved(final CCMouseEvent theEvent) {
		
//		myColor.setHSB(_myHue, 1, 1);
//		_myFluid.addColor(myPosition, myColor);
	}

	public static void main(String[] args) {

		CCFluidDemo demo = new CCFluidDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}


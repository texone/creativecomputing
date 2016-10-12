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
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.gl.app.events.CCMouseSimpleInfo;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.simulation.particles.fluidfield.CCFluid;

public class CCFluidDemo extends CCGL2Adapter {
		
	
	@CCProperty(name = "fluid")
	private CCFluid _myFluid;
	
	private CCMouseSimpleInfo _myMouseSimpleInfo = new CCMouseSimpleInfo();

	private CCVector2 _myMouseMovement;
	private CCColor _myColor;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	
		
		_myFluid = new CCFluid( 500, 500);
		
		mouseListener().add(_myMouseSimpleInfo);
		mouseMotionListener().add(_myMouseSimpleInfo);
		
		mouseMoved().add( theEvent -> {
			_myMouseMovement = _myMouseSimpleInfo.motion;
			_myColor = new CCColor(1f);
		});
	}
	
	@CCProperty(name = "show boundary")
	private boolean _myShowBoundary = false;
	
	float _myTime = 0;
	
	@Override
	public void update(final CCAnimator theAnimator) {
		_myTime+= theAnimator.deltaTime();
		if(_myTime > 10) {
			_myTime = 0;
		}
		
		_myFluid.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		CCVector2 myPosition = new CCVector2(
			_myMouseSimpleInfo.position.x / g.width(), 
			1 - _myMouseSimpleInfo.position.y / g.height()
		);
		
		if (_myMouseMovement != null) {
			CCVector2 myMovement = _myMouseMovement.clone();
			myMovement.normalize();
			myMovement.addLocal(1, 1);
			myMovement.multiplyLocal(0.5,-0.5);
			
			_myFluid.adImpulse(g, myPosition, myMovement);

			_myMouseMovement = null;
		}
		
		CCColor myColor = new CCColor(1f);
		myColor.setHSB(_myTime/10, 1, 0.1);
		_myFluid.addColor(g, myPosition, myColor);
		
		_myFluid.display(g);
			
		g.clear();
//		g.scale(1,-1);
//		g.translate(-width/2, -height/2);
		if(_myShowBoundary){
			g.image(_myFluid.velocityTexture(), -g.width()/2,-g.height()/2,g.width(),g.height());
		} else {
			g.image(_myFluid.colorTexture(), -g.width()/2,-g.height()/2,g.width(),g.height());
		}
	}
	
	
	public void mouseDragged(final CCMouseEvent theEvent) {
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


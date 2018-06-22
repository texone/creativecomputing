/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.demo.simulation.steering;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.CCNeighborhood;
import cc.creativecomputing.simulation.steering.behavior.CCPathFollow;
import cc.creativecomputing.simulation.steering.behavior.CCUnalignedCollisionAvoidance;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCPathFollowTest extends CCGL2Adapter{
	@CCProperty(name = "spline")
	private CCLinearSpline _mySpline = new CCLinearSpline();
	@CCProperty(name = "spline2")
	private CCLinearSpline _mySpline2 = new CCLinearSpline();
	

	private CCNeighborhood<CCTestAgent> _myNeighborhood = new CCNeighborhood<CCTestAgent>();
	
	CCLinearSpline _myPath = new CCLinearSpline();
	CCLinearSpline _myPath2 = new CCLinearSpline();
	
	@CCProperty(name = "wander")
	private CCWander _myWander ;
	@CCProperty(name = "path follow 1")
	private CCPathFollow _myPathFollow1;
	@CCProperty(name = "path follow 2")
	private CCPathFollow _myPathFollow2;
	@CCProperty(name = "collision avoidance")
	private CCUnalignedCollisionAvoidance _myAvoidance; 
	private CCSimulation _mySimulation;
		
	@CCProperty (name = "max speed", min = 0.1f, max = 5f)
	private float _cMaxSpeed = 1;

	@CCProperty (name = "max force", min = 0.1f, max = 5f)
	private float _cMaxForce = 0.6f;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();
		_myPathFollow1 = new CCPathFollow(_myPath);
		_myPathFollow2 = new CCPathFollow(_myPath2);
		_myWander = new CCWander();
		_myAvoidance = new CCUnalignedCollisionAvoidance();
		addPathGroup(_myPathFollow1);
		addPathGroup(_myPathFollow2);
		
		_mySimulation.addParticleGroup(_myNeighborhood);
	}
	
	private void addPathGroup(CCPathFollow thePath) {
		for(int i = 0; i < 25;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.position = new CCVector3(CCMath.random(-100, 100), CCMath.random(-100, 100),0);
			myAgent.velocity(new CCVector3(CCMath.random(5), CCMath.random(-5, 5)));
			myAgent.velocity().multiplyLocal(10);
			
			myAgent.addBehavior(_myWander);
			myAgent.addBehavior(thePath);
			myAgent.addBehavior(_myAvoidance);
			_myNeighborhood.add(myAgent);
		}
	}
	
	@CCProperty(name = "enforce penetration")
	private boolean _cEnforcePenetration = true;
	
	public void enforceNonPenetrationConstraint() {
		if(!_cEnforcePenetration)return;
		for(CCTestAgent myAgent:_myNeighborhood){
			for(CCTestAgent myAgent2:_myNeighborhood){
				if(myAgent == myAgent2)continue;
				
				CCVector3 myOffset = myAgent.position.subtract(myAgent2.position);
				double distance = myOffset.length();
				double sumOfRadii = 20;
				if (distance < sumOfRadii) {
					double s = (sumOfRadii - distance) / distance;
					myOffset.multiplyLocal(s);
					myAgent.position.addLocal(myOffset);
					myAgent2.position.subtract(myOffset);
					
				}
			}
		}
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myNeighborhood.update(theAnimator);
		
		for(CCTestAgent myAgent:_myNeighborhood){
			myAgent.maxSpeed = _cMaxSpeed;
			myAgent.maxForce = _cMaxForce;
			myAgent.position.z = 0;
		}
		
		
		_mySimulation.update(theAnimator);
		
		enforceNonPenetrationConstraint();
		
			
	}
	
	private void updatePathFromSpline(CCGraphics g, CCLinearSpline theSpline, CCLinearSpline thePath) {
		thePath.clear();
		for(CCVector3 myPoint:theSpline) {
			thePath.addPoint(new CCVector3(
				CCMath.blend(-g.width() / 2, g.width()/ 2, myPoint.x),
				CCMath.blend(-g.height() / 2, g.height()/ 2, myPoint.y)
			));
		}
		thePath.endEditSpline();
	}
	
	@Override
	public void display(CCGraphics g){
		updatePathFromSpline(g, _mySpline, _myPath);
		updatePathFromSpline(g, _mySpline2, _myPath2);
		g.clear();
//		_cCameraController.camera().draw(g);

		for(CCTestAgent myAgent:_myNeighborhood){

			if(myAgent.position.x < -g.width() / 2)myAgent.position.x += g.width();
			if(myAgent.position.x >  g.width() / 2)myAgent.position.x -= g.width();
			if(myAgent.position.y < -g.height() / 2)myAgent.position.y += g.height();
			if(myAgent.position.y >  g.height() / 2)myAgent.position.y -= g.height();
		}
		
		for(CCTestAgent myAgent:_myNeighborhood){
			g.color(1d,0.25d);
			g.ellipse(myAgent.position.x,  myAgent.position.y, 10,10);
			
			g.color(1d,0,0);
			g.line(
				myAgent.position.x,  
				myAgent.position.y,
				myAgent.position.x + myAgent.velocity().x * 10, 
				myAgent.position.y +  myAgent.velocity().y * 10
			);
			
			_myAvoidance.draw(g, myAgent);
		}
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(CCVector3 myPoint:_myPath) {
			g.vertex(myPoint);
		}
		g.endShape();
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCPathFollowTest demo = new CCPathFollowTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}

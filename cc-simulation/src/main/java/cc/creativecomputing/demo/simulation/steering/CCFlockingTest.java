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
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.CCNeighborhood;
import cc.creativecomputing.simulation.steering.behavior.CCFlocking;

public class CCFlockingTest extends CCGL2Adapter{
	
	@CCProperty (name = "max force", min = 0.1f, max = 5f)
	private float _cMaxForce = 0;
	
	@CCProperty (name = "max speed", min = 0.1f, max = 5f)
	private float _cMaxSpeed = 0;
	
	@CCProperty (name = "flocking distance", min = 0.1f, max = 500f)
	private float _cFlockingNearRadius = 0;

	@CCProperty (name = "flocking angle", min = 0.1f, max = 360)
	private float _cFlockingNearAngle = 0;
	
	private CCNeighborhood<CCTestAgent> _myNeighborhood = new CCNeighborhood<CCTestAgent>();
	private CCFlocking _myFlocking;
	private CCSimulation _mySimulation;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySimulation = new CCSimulation();
		_myFlocking = new CCFlocking(200,160);
		
		for(int i = 0; i < 500;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.position = new CCVector3().randomize(1000);
			myAgent.velocity(new CCVector3().randomize(50));

			myAgent.addBehavior(_myFlocking);
			_myNeighborhood.add(myAgent);
		}
		
		_mySimulation.addParticleGroup(_myNeighborhood,0);
		
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	@CCProperty (name = "reset")
	void reset(boolean bla){
		for(CCTestAgent myAgent:_myNeighborhood){
			myAgent.position = new CCVector3().randomize(1000);
			myAgent.velocity(new CCVector3().randomize(50));
		}
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		for(CCTestAgent myAgent:_myNeighborhood){
			myAgent.maxForce = _cMaxForce;
			myAgent.maxSpeed = _cMaxSpeed;
		}
		
		_myFlocking.nearAreaRadius(_cFlockingNearRadius);
		_myFlocking.nearAngle(_cFlockingNearAngle);
	}
	
	@Override
	public void display(CCGraphics g){
		g.clear();
		_cCameraController.camera().draw(g);
		
//		List<CCVector3f> myVertices = new ArrayList<CCVector3f>();
//		for(CCTestAgent myAgent:_myNeighborhood.particles()){
//			myAgent.frustumWrap(g);
//			myAgent.draw(myVertices);
//		}
//		_myMesh.vertices(myVertices,true);
//		_myMesh.draw(g);
//		for(CCTestAgent myAgent:_myNeighborhood.particles()){
//			myAgent.boundingBox().draw(g);
//		}
		g.beginShape(CCDrawMode.POINTS);
		for(CCTestAgent myAgent:_myNeighborhood){
//			myAgent.frustumWrap(g);
//			myAgent.draw(myVertices);
			g.vertex(myAgent.position);
		}
		g.endShape();
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		
		CCFlockingTest demo = new CCFlockingTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}

}

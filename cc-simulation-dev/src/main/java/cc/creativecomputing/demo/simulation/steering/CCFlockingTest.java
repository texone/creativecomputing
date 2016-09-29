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

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDirectionalLight;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCColorMaterialMode;
import cc.creativecomputing.graphics.CCMaterial;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.CCNeighborhood;
import cc.creativecomputing.simulation.steering.behavior.CCFlocking;

public class CCFlockingTest extends CCApp{
	
	@CCControl (name = "max force", min = 0.1f, max = 5f)
	private float _cMaxForce = 0;
	
	@CCControl (name = "max speed", min = 0.1f, max = 5f)
	private float _cMaxSpeed = 0;
	
	@CCControl (name = "flocking distance", min = 0.1f, max = 500f)
	private float _cFlockingNearRadius = 0;

	@CCControl (name = "flocking angle", min = 0.1f, max = 360)
	private float _cFlockingNearAngle = 0;
	
	private CCNeighborhood<CCTestAgent> _myNeighborhood = new CCNeighborhood<CCTestAgent>();
	private CCFlocking _myFlocking;
	private CCSimulation _mySimulation;
	
	private CCArcball _myArcball;
	
	
	@Override
	public void setup(){
		_mySimulation = new CCSimulation(this);
		_myFlocking = new CCFlocking(200,160);
		
		for(int i = 0; i < 500;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000, -1000, 1000);
			myAgent.velocity(new CCVector3f(CCMath.random(5), CCMath.random(-5,5)));
			myAgent.velocity().scale(10);

			myAgent.addBehavior(_myFlocking);
			_myNeighborhood.addParticle(myAgent);
		}
		
		_mySimulation.addParticleGroup(_myNeighborhood,0);
		
		g.lights();
		CCDirectionalLight myDirectionalLight = new CCDirectionalLight(1,1,1,1,0,0);
		myDirectionalLight.specular(1f, 1f, 1f);
		g.light(myDirectionalLight);
		
		CCMaterial myMaterial = new CCMaterial();
		myMaterial.diffuse(125,125,125);
		myMaterial.specular(0,0,45);
		
		g.colorMaterial(CCColorMaterialMode.OFF);
		g.material(myMaterial);
		
		_myUI.addControls("app","app", this);
		
		_myArcball = new CCArcball(this);
	}
	
	@CCControl (name = "reset", toggle = false)
	void reset(boolean bla){
		for(CCTestAgent myAgent:_myNeighborhood.particles()){
			myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000, -1000, 1000);
			myAgent.velocity(new CCVector3f(CCMath.random(5), CCMath.random(-5,5)));
			myAgent.velocity().scale(10);
		}
	}
	
	@Override
	public void update(float theDeltaTime) {
		for(CCTestAgent myAgent:_myNeighborhood.particles()){
			myAgent.maxForce = _cMaxForce;
			myAgent.maxSpeed = _cMaxSpeed;
		}
		
		_myFlocking.nearAreaRadius(_cFlockingNearRadius);
		_myFlocking.nearAngle(_cFlockingNearAngle);
	}
	
	@Override
	public void draw(){
		g.clear();
		_myArcball.draw(g);
		
		g.frustum().update();
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
		for(CCTestAgent myAgent:_myNeighborhood.particles()){
//			myAgent.frustumWrap(g);
//			myAgent.draw(myVertices);
			g.vertex(myAgent.position);
		}
		g.endShape();
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFlockingTest.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}

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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.graphics.CCDirectionalLight;
import cc.creativecomputing.graphics.CCGraphics.CCColorMaterialMode;
import cc.creativecomputing.graphics.CCMaterial;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.CCParticleGroup;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.behavior.CCSteerToRandom;

public class CCSteerToRandomTest extends CCApp{
	
	private CCParticleGroup<CCTestAgent> _myAgents = new CCParticleGroup<CCTestAgent>();
	private List<CCSteerToRandom> _mySteerToRandoms = new ArrayList<CCSteerToRandom>();
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	private CCControlUI _myUI;
	private CCArcball _myArcball;
	
	private static class Settings{
		@CCControl (name = "steer to random range", min = 10f, max = 200f)
		private static float steerToRandomRange = 1;

		@CCControl (name = "steer to random switch time", min = 0.1f, max = 5f)
		private static float steerToRandomSwitchTime = 0.6f;
		
		@CCControl (name = "max speed", min = 0.1f, max = 5f)
		private static float maxSpeed = 1;

		@CCControl (name = "max force", min = 0.1f, max = 5f)
		private static float maxForce = 0.6f;
	}
	
	@Override
	public void setup(){
		_mySimulation = new CCSimulation(this);

		for(int i = 0; i < 1000;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			
//			myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000, -1000, 1000);
			myAgent.velocity(new CCVector3f());
			myAgent.velocity().randomize();
			myAgent.velocity().scale(10);

			CCSteerToRandom mySteerToRandom = new CCSteerToRandom(200,2);
			myAgent.addBehavior(mySteerToRandom);
			
			
			_myAgents.addParticle(myAgent);
			_mySteerToRandoms.add(mySteerToRandom);
		}
		
		_mySimulation.addParticleGroup(_myAgents);
		
		_myMesh = new CCMesh();
		
		g.lights();
		CCDirectionalLight myDirectionalLight = new CCDirectionalLight(1,1,1,1,0,0);
		myDirectionalLight.specular(1f, 1f, 1f);
		g.light(myDirectionalLight);
		
		CCMaterial myMaterial = new CCMaterial();
		myMaterial.diffuse(125,125,125);
		myMaterial.specular(0,0,45);
		
		g.colorMaterial(CCColorMaterialMode.OFF);
		g.material(myMaterial);
		
		_myUI = new CCControlUI(this);
		_myUI.addControls("steer to random", new Settings());
		
		_myArcball = new CCArcball(this);
	}
	
	@Override
	public void update(final float theDeltaTime){
		for(CCSteerToRandom mySteerToRandom:_mySteerToRandoms){
			mySteerToRandom.range(Settings.steerToRandomRange);
			mySteerToRandom.switchTime(Settings.steerToRandomSwitchTime);
		}
		
		for(CCTestAgent myAgent:_myAgents.particles()){
			myAgent.maxSpeed = Settings.maxSpeed;
			myAgent.maxForce = Settings.maxForce;
		}
	}
	
	@Override
	public void draw(){
		g.clear();
		_myArcball.draw(g);
		
		g.frustum().update();
		List<CCVector3f> myVertices = new ArrayList<CCVector3f>();
		for(CCTestAgent myAgent:_myAgents.particles()){
			myAgent.frustumWrap(g);
			myAgent.draw(myVertices);
		}
		_myMesh.vertices(myVertices,true);
		_myMesh.draw(g);
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSteerToRandomTest.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}

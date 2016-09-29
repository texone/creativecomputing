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
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.domain.CCPlaneDomain;
import cc.creativecomputing.simulation.steering.behavior.CCAvoidance;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCPlaneAvoidanceTest extends CCApp{
	
	private List<CCTestAgent> _myAgents = new ArrayList<CCTestAgent>();
	private List<CCWander> _myWanders = new ArrayList<CCWander>();
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	private CCAvoidance _myAvoidance;
	private CCPlaneDomain _myPlane1;
	private CCPlaneDomain _myPlane2;
	
	private CCControlUI _myUI;
	private CCArcball _myArcball;
	
	private static class Settings{
		@CCControl (name = "wanderer strength", min = 0.1f, max = 5f)
		private static float wandererStrength = 1;

		@CCControl (name = "wanderer rate", min = 0.1f, max = 5f)
		private static float wandererRate = 0.6f;
		
		@CCControl (name = "avoidance look ahead", min = 0f, max = 200f)
		private static float avoidanceLookAhead = 100;
		
		@CCControl (name = "avoidance epsilon", min = 0f, max = 200f)
		private static float avoidanceEpsilon = 100;
		
		@CCControl (name = "max speed", min = 0.1f, max = 5f)
		private static float maxSpeed = 1;

		@CCControl (name = "max force", min = 0.1f, max = 5f)
		private static float maxForce = 0.6f;
	}
	
	@Override
	public void setup(){
		_mySimulation = new CCSimulation(this);
		_myAvoidance = new CCAvoidance(200,1);
		_myPlane1 = new CCPlaneDomain(new CCVector3f(0,-200,0),new CCVector3f(0,1,0));
		_myPlane2 = new CCPlaneDomain(new CCVector3f(0,200,0),new CCVector3f(0,1,0));
		_myAvoidance.addDomain(_myPlane1);
		_myAvoidance.addDomain(_myPlane2);
		_myAvoidance.weight(100);

		for(int i = 0; i < 500;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.velocity(new CCVector3f());
			myAgent.velocity().randomize();
			myAgent.velocity().scale(100);

			CCWander myWanderer = new CCWander();
			myAgent.addBehavior(myWanderer);
			myAgent.addBehavior(_myAvoidance);
			
			_mySimulation.addAgent(myAgent);
			_myAgents.add(myAgent);
			_myWanders.add(myWanderer);
		}
		
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
		_myUI.addControls("wanderer", new Settings());
		
		_myArcball = new CCArcball(this);
	}
	
	@Override
	public void update(final float theDeltaTime){
		for(CCWander myWanderer:_myWanders){
			myWanderer.wanderStrength(Settings.wandererStrength);
			myWanderer.wanderRate(Settings.wandererRate);
		}
		
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxSpeed = Settings.maxSpeed;
			myAgent.maxForce = Settings.maxForce;
		}
		
		_myAvoidance.lookAhead(Settings.avoidanceLookAhead);
		_myAvoidance.epsilon(Settings.avoidanceEpsilon);
	}
	
	@Override
	public void draw(){
		g.clear();
		_myArcball.draw(g);
		
		g.frustum().update();
		List<CCVector3f> myVertices = new ArrayList<CCVector3f>();
		for(CCTestAgent myAgent:_myAgents){
			myAgent.frustumWrap(g);
			myAgent.draw(myVertices);
		}
		_myMesh.vertices(myVertices,true);
		_myMesh.draw(g);
		
		_myPlane1.draw(g);
		_myPlane2.draw(g);
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPlaneAvoidanceTest.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}

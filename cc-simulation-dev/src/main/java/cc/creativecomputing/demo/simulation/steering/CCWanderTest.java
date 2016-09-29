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
import cc.creativecomputing.graphics.CCDirectionalLight;
import cc.creativecomputing.graphics.CCGraphics.CCColorMaterialMode;
import cc.creativecomputing.graphics.CCMaterial;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCWanderTest extends CCApp{
	
	private List<CCTestAgent> _myAgents = new ArrayList<CCTestAgent>();
	private List<CCWander> _myWanders = new ArrayList<CCWander>();
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	private CCArcball _myArcball;
	
	@CCControl (name = "wanderer strength", min = 0.1f, max = 5f)
	private float _cWandererStrength = 1;

	@CCControl (name = "wanderer rate", min = 0.1f, max = 15f)
	private float _cWandererRate = 0.6f;
		
	@CCControl (name = "max speed", min = 0.1f, max = 5f)
	private float _cMaxSpeed = 1;

	@CCControl (name = "max force", min = 0.1f, max = 5f)
	private float _cMaxForce = 0.6f;
	
	@Override
	public void setup(){
		_mySimulation = new CCSimulation(this);

		for(int i = 0; i < 1000;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;

			CCWander myWanderer = new CCWander();
			myAgent.addBehavior(myWanderer);
			
			_mySimulation.addAgent(myAgent);
			_myAgents.add(myAgent);
			_myWanders.add(myWanderer);
		}
		
		_myMesh = new CCMesh();
		
//		g.lights();
		CCDirectionalLight myDirectionalLight = new CCDirectionalLight(1,1,1,1,0,0);
		myDirectionalLight.specular(1f, 1f, 1f);
		g.light(myDirectionalLight);
		
		CCMaterial myMaterial = new CCMaterial();
		myMaterial.diffuse(125,125,125);
		myMaterial.specular(0,0,45);
		
		g.colorMaterial(CCColorMaterialMode.OFF);
		g.material(myMaterial);
		
		addControls("app", "wanderer", this);
		
		_myArcball = new CCArcball(this);
	}
	
	@Override
	public void update(final float theDeltaTime){
		for(CCWander myWanderer:_myWanders){
			myWanderer.wanderStrength(_cWandererStrength);
			myWanderer.wanderRate(_cWandererRate);
		}
		
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxSpeed = _cMaxSpeed;
			myAgent.maxForce = _cMaxForce;
		}
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
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCWanderTest.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}

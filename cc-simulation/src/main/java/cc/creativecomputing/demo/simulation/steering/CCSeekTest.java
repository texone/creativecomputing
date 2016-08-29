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
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.behavior.CCSeekFlee;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCSeekTest extends CCApp{

	@CCControl (name = "wander strength", min = 0.1f, max = 5f)
	private float _cWandererStrength = 1;

	@CCControl (name = "wander rate", min = 0.1f, max = 5f)
	private float _cWandererRate = 0.6f;
	
	@CCControl (name = "wander weight", min = 0, max = 1)
	private float _cWandererWeight = 0.6f;
	
	@CCControl (name = "seek min distance", min = 0, max = 1000)
	private float _cSeekMinDistance = 0;
	
	@CCControl (name = "seek max distance", min = 0, max = 1000)
	private float _cSeekMaxDistance = 0;
	
	@CCControl (name = "seek weight", min = 0, max = 1)
	private float _cSeekWeight = 0;
	
	@CCControl (name = "seek strength", min = -1, max = 1)
	private float _cSeekStrength = 0;
		
	@CCControl (name = "max speed", min = 0.1f, max = 5f)
	private float _cMaxSpeed = 1;

	@CCControl (name = "max force", min = 0.1f, max = 5f)
	private float _cMaxForce = 0.6f;
	
	private List<CCTestAgent> _myAgents = new ArrayList<CCTestAgent>();
	private List<CCWander> _myWanderers = new ArrayList<CCWander>();
	private CCSeekFlee _mySeek = new CCSeekFlee(new CCVector3f(0,0,0));
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	private CCArcball _myArcball;
	
	@Override
	public void setup(){
		_mySimulation = new CCSimulation(this);

		for(int i = 0; i < 1000;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			
			myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000, -1000, 1000);
			myAgent.velocity(new CCVector3f());
			myAgent.velocity().randomize();
			myAgent.velocity().scale(100);

			CCWander myWanderer = new CCWander();
			myAgent.addBehavior(_mySeek);
			myAgent.addBehavior(myWanderer);
			
			_mySimulation.addAgent(myAgent);
			_myAgents.add(myAgent);
			_myWanderers.add(myWanderer);
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
		
		addControls("wanderer", "wanderer", this);
		
		_myArcball = new CCArcball(this);
	}
	
	@Override
	public void update(float theDeltaTime) {
		for (CCTestAgent myAgent : _myAgents) {
			myAgent.maxSpeed = _cMaxSpeed;
			myAgent.maxForce = _cMaxForce;
		}

		for (CCWander myWanderer : _myWanderers) {
			myWanderer.wanderStrength(_cWandererStrength);
			myWanderer.wanderRate(_cWandererRate);
			myWanderer.weight(_cWandererWeight);
		}
		
		_mySeek.minActiveDistance(_cSeekMinDistance);
		_mySeek.maxActiveDistance(_cSeekMaxDistance);
		_mySeek.weight(_cSeekWeight);
		_mySeek.strength(_cSeekStrength);
	}
	
	boolean set = false;
	
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
//		for(CCTestAgent myAgent:_myAgents){
//			myAgent.boundingBox().draw(g);
//		}
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSeekTest.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}

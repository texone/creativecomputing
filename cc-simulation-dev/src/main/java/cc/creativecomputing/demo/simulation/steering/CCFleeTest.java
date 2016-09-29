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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.force.CCGravity;
import cc.creativecomputing.simulation.steering.behavior.CCFlee;
import cc.creativecomputing.simulation.steering.behavior.CCWander;

public class CCFleeTest extends CCApp{
	
	private List<CCTestAgent> _myAgents = new ArrayList<CCTestAgent>();
	private List<CCWander> _myWanderers = new ArrayList<CCWander>();
	private CCFlee _myFlee = new CCFlee(new CCVector3f(0,0,0));
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	private CCArcball _myArcball;
	
	CCGravity _myWind = new CCGravity(new CCVector3f());
	
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
			myAgent.addBehavior(_myFlee);
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
		
		_myUI.addControls("app", "app", this);
		
		_myArcball = new CCArcball(this);
	}
	
	@CCControl (name = "max speed", min = 0.1f, max = 5f)
	void maxSpeed(float theSpeed){
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxSpeed = theSpeed;
		}
	}

	@CCControl (name = "max force", min = 0.1f, max = 5f)
	void maxForce(float theForce){
		for(CCTestAgent myAgent:_myAgents){
			myAgent.maxForce = theForce;
		}
	}
	
	@CCControl (name = "wander strength", min = 0.1f, max = 5f)
	void wandererStrength(final float theStrength){
		for(CCWander myWanderer:_myWanderers){
			myWanderer.wanderStrength(theStrength);
		}
	}
	
	@CCControl (name = "wander rate", min = 0.1f, max = 5f)
	void wanderRate(final float theRate){
		for(CCWander myWanderer:_myWanderers){
			myWanderer.wanderStrength(theRate);
		}
	}
	
	@CCControl (name = "wander weight", min = 0, max = 1)
	void wanderWeight(final float theWeight){
		for(CCWander myWanderer:_myWanderers){
			myWanderer.weight(theWeight);
		}
	}
	
	@CCControl (name = "flee min distance", min = 0, max = 1000)
	void seekMinDistance(final float theDistance){
		_myFlee.minActiveDistance(theDistance);
	}
	
	@CCControl (name = "flee max distance", min = 0, max = 1000)
	void seekMaxDistance(final float theDistance){
		_myFlee.maxActiveDistance(theDistance);
	}
	
	@CCControl (name = "flee weight", min = 0, max = 1)
	void seekWeight(final float theWeight){
		_myFlee.weight(theWeight);
	}
	
	@CCControl (name = "reset", toggle = false)
	void reset(boolean bla){
		for(CCTestAgent myAgent:_myAgents){
			myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000, -1000, 1000);
			myAgent.velocity(new CCVector3f(CCMath.random(5), CCMath.random(-5,5)));
			myAgent.velocity().scale(10);
		}
	}
	
	boolean set = false;
	
	@Override
	public void draw(){
		g.clear();
		_myArcball.draw(g);
		
		_myFlee.target().x = mouseX - width/2;
		_myFlee.target().y = height/2 - mouseY;
		
		g.frustum().update();
		List<CCVector3f> myVertices = new ArrayList<CCVector3f>();
		for(CCTestAgent myAgent:_myAgents){
			myAgent.centerWrap(g);
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
		CCApplicationManager myManager = new CCApplicationManager(CCFleeTest.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}

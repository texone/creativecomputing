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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.steering.CCNeighborhood;
import cc.creativecomputing.simulation.steering.behavior.CCSeparation;

public class CCSeparationTest extends CCApp{
	
	private List<CCTestAgent> _myAgents = new ArrayList<CCTestAgent>();
	private CCSeparation _mySeparation;
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	private CCControlUI _myUI;
	private CCArcball _myArcball;
	
	private class Settings{
		
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
		
		@CCControl (name = "separation distance", min = 0.1f, max = 500f)
		void nearAreaRadius(final float theRadius){
			_mySeparation.nearAreaRadius(theRadius);
		}

		@CCControl (name = "separation angle", min = 0.1f, max = 360)
		void nearAreaAngle(final float theAngle){
			_mySeparation.nearAngle(theAngle);
		}
		
		@CCControl (name = "reset", toggle = false)
		void reset(){
			for(CCTestAgent myAgent:_myAgents){
				myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000, -1000, 1000);
				myAgent.velocity(new CCVector3f(CCMath.random(5), CCMath.random(-5,5)));
				myAgent.velocity().scale(10);
			}
		}
	}
	
	@Override
	public void setup(){
		_mySimulation = new CCSimulation(this);
		CCNeighborhood<CCTestAgent> myNeighborhood = new CCNeighborhood<CCTestAgent>();
		_mySimulation.addPreListener(myNeighborhood);
		_mySeparation = new CCSeparation(200,160);
		
		for(int i = 0; i < 500;i++){
			CCTestAgent myAgent = new CCTestAgent();
			myAgent.maxSpeed = 4.5F;
			myAgent.maxForce = 0.3F;
			myAgent.position = CCVecMath.random(-1000, 1000, -1000, 1000, -1000, 1000);
			myAgent.velocity(new CCVector3f(CCMath.random(5), CCMath.random(-5,5)));
			myAgent.velocity().scale(10);

			myAgent.addBehavior(_mySeparation);
			myNeighborhood.addParticle(myAgent);
			_mySimulation.addAgent(myAgent);
			_myAgents.add(myAgent);
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
		_myUI.addControls("cohesion", new Settings());
		
		_myArcball = new CCArcball(this);
	}
	
	boolean set = false;
	
	float noiseX = 0;
	
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
		CCApplicationManager myManager = new CCApplicationManager(CCSeparationTest.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}

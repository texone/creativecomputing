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
package cc.creativecomputing.demo.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.control.CCControlUI;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.signal.CCFarbrauschNoise;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.CCSimulation;
import cc.creativecomputing.simulation.force.CCFlowField;
import cc.creativecomputing.simulation.force.CCFlowFieldFollow;

public class CCFlowFieldFollowTest extends CCApp{
	
	private class CCNoiseFlowField extends CCFlowField{

		public CCVector3f flowAtPoint(final float theX, final float theY, final float theZ) {
			float noiseX = _myNoise.value(theX +_myNoiseX,theY,theZ) * 2 - 1f;
			float noiseY = _myNoise.value(theX+1000 +_myNoiseX,theY+1000,theZ+1000) * 2 - 1f;
			float noiseZ = _myNoise.value(theX+2000 +_myNoiseX,theY+2000,theZ+2000) * 2 - 1f;
			
			float distance = CCMath.sqrt(theY * theY + theZ * theZ);
			
			if(distance > _myRadius * 2){
				noiseX = 0;
				noiseY = -theY /distance;
				noiseZ = -theZ /distance;
			}else if(distance > _myRadius && distance <= _myRadius * 2){
				float blend = (distance - _myRadius)/_myRadius;
				noiseX *= (1 - blend);
				noiseY = noiseY * (1 - blend) + -theY /distance * blend;
				noiseZ = noiseZ * (1 - blend) + -theZ /distance * blend;
			}
			
			CCVector3f myResult = new CCVector3f(noiseX,noiseY,noiseZ);
//			myResult.normalize();
			return myResult;
		}
		
	}
	
	private List<CCTestParticle> _myParticles = new ArrayList<CCTestParticle>();
	private CCSimulation _mySimulation;
	private CCMesh _myMesh;
	
	private CCControlUI _myUI;
	private CCArcball _myArcball;
	
	private CCFarbrauschNoise _myNoise;
	private float _myRadius = 100;
	private CCNoiseFlowField _myNoiseFlowField;
	
	private static class Settings{
		@CCControl (name = "noise scale", min = 0.01f, max = 0.1f)
		private static float noiseScale = 1;
		
		@CCControl (name = "max speed", min = 0.1f, max = 5f)
		private static float maxSpeed = 1;

		@CCControl (name = "max force", min = 0.1f, max = 5f)
		private static float maxForce = 0.6f;
		
		@CCControl
		private static boolean debug = false;
	}
	
	@Override
	public void setup(){
		_mySimulation = new CCSimulation(this);
		
		_myNoise = new CCFarbrauschNoise();
		_myNoise.scale(0.03f);
		
		_myNoiseFlowField = new CCNoiseFlowField();

		CCFlowFieldFollow myFlowFieldFollow = new CCFlowFieldFollow(_myNoiseFlowField);
		_mySimulation.addForce(myFlowFieldFollow);
		
		for(int i = 0; i < 40000;i++){
			CCTestParticle myParticle = new CCTestParticle();
			myParticle.velocity(CCVecMath.random(-10, 10, -10, 10, -10, 10));
			myParticle.position = CCVecMath.random(-width/2, width/2, -height/2, height/2, -10, 10);
			myParticle.maxSpeed = 4.5F;
			myParticle.maxForce = 0.3F;
			
			_mySimulation.addParticle(myParticle);
			_myParticles.add(myParticle);
		}
		
		_myMesh = new CCMesh(CCDrawMode.QUADS,40000 * 4);
		
		_myUI = new CCControlUI(this);
		_myUI.addControls("flowfield", new Settings());
		
		_myArcball = new CCArcball(this);
	}
	
	float _myNoiseX = 0;
	
	@Override
	public void update(final float theDeltaTime){
		_myNoiseX += theDeltaTime * 100;
		
		for(CCParticle myParticle:_myParticles){
			myParticle.maxSpeed = Settings.maxSpeed;
			myParticle.maxForce = Settings.maxForce;
		}
		
		_myNoise.scale(Settings.noiseScale);
	}
	
	@Override
	public void draw(){
		g.clear();
		_myArcball.draw(g);
		
		g.frustum().update();
		_myMesh.clearVertices();
		for(CCTestParticle myParticle:_myParticles){
			myParticle.frustumWrap(g);
			myParticle.draw(_myMesh);
		}
		_myMesh.draw(g);
		
//		g.beginShape(CCGraphics.POINTS);
//		for(CCTestAgent myAgent:_myAgents){
////			myAgent.frustumWrap(g);
//			g.vertex(myAgent.position);
//		}
//		g.endShape();
		
		g.color(255,50);
		if(Settings.debug){
			g.noLights();
			g.beginShape(CCDrawMode.LINES);
			for(float x = -width/2; x < width/2;x += 40){
				for(float y = -300; y < 300;y += 40){
					for(float z = -300/2; z < 300;z += 40){
						CCVector3f position = new CCVector3f(x,y,z);
						CCVector3f myNoise = _myNoiseFlowField.flowAtPoint(position);
						
						g.vertex(x, y, z);
						g.vertex(x + myNoise.x * 50,y + myNoise.y * 50,z + myNoise.z * 50);
					}
				}
			}
			g.endShape();
		}
		
		g.clearDepthBuffer();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFlowFieldFollowTest.class);
		myManager.settings().size(1000, 600);
		myManager.start();
	}

}

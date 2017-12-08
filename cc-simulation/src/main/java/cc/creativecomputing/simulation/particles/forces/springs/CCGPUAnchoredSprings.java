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
package cc.creativecomputing.simulation.particles.forces.springs;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCGPUParticles;
import cc.creativecomputing.simulation.particles.forces.CCGPUForce;

import com.jogamp.opengl.cg.CGparameter;


public class CCGPUAnchoredSprings extends CCGPUForce {
	
	private CGparameter _mySpringConstantParameter;
	private CGparameter _myDampingParameter;
	private CGparameter _myRestLengthParameter;
	
	private CGparameter _myAnchorPositionTextureParameter;

	private CCCGShader _myInitValue01Shader;
	private CCShaderBuffer _myAnchorPositionBuffer;
	
	private CCVector3f[] _myAnchorPositions;
	private List<Integer> _myChangedSprings = new ArrayList<Integer>();
	protected PriorityQueue<CCGPUParticle> _myActiveParticles = new PriorityQueue<CCGPUParticle>();
	
	private float _mySpringConstant;
	private float _myDamping;
	private float _myRestLength;
	
	private CCGraphics _myGraphics;
	
	private int _myWidth = 0;
	
	protected double _myCurrentTime = 0;

	public CCGPUAnchoredSprings(final CCGraphics g, final float theSpringConstant, final float theDamping, final float theRestLength) {
		super("AnchoredSprings");
		_myInitValue01Shader = new CCCGShader(null, CCIOUtil.classPath(CCGPUParticles.class,"shader/initvalue.fp"));
		_myInitValue01Shader.load();
		
		_myGraphics = g;
		
		_mySpringConstant = theSpringConstant;
		_myDamping = theDamping;
		_myRestLength = theRestLength;
	}

	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		
		_myWidth = theWidth;
		
		_myAnchorPositions = new CCVector3f[theWidth * theHeight];
		
		_myAnchorPositionBuffer = new CCShaderBuffer(32, 4, theWidth, theHeight);
		_myAnchorPositionBuffer.beginDraw();
		_myGraphics.pushAttribute();
		_myGraphics.clearColor(0,0);
		_myGraphics.clear();
		_myGraphics.popAttribute();
		_myAnchorPositionBuffer.endDraw();
		
		_mySpringConstantParameter = parameter("springConstant");
		_myDampingParameter = parameter("springDamping");
		_myRestLengthParameter = parameter("restLength");
		
		_myAnchorPositionTextureParameter = parameter("anchorPositionTexture");
		
		springDamping(_myDamping);
		springConstant(_mySpringConstant);
		restLength(_myRestLength);
		
		_myVelocityShader.texture(_myAnchorPositionTextureParameter, _myAnchorPositionBuffer.attachment(0).id());
	}
	
	public void addSpring(final CCGPUParticle theParticle, final CCVector3f theAnchor) {
		if(theParticle == null)return;
		_myActiveParticles.add(theParticle);
		_myChangedSprings.add(theParticle.index());
		_myAnchorPositions[theParticle.index()] = theAnchor;
	}
	
	public void setSpringPos(final CCGPUParticle theParticle, final CCVector3f theAnchor) {
		if(theParticle == null || theParticle.index() < 0)return;
		_myChangedSprings.add(theParticle.index());
		_myAnchorPositions[theParticle.index()] = theAnchor;
	}
	
	public void springConstant(final float theSpringConstant) {
		_myVelocityShader.parameter(_mySpringConstantParameter, theSpringConstant);
	}
	
	public void springDamping(final float theSpringDamping) {
		_myVelocityShader.parameter(_myDampingParameter, theSpringDamping);
	}
	
	public void restLength(final float theRestLength) {
		_myVelocityShader.parameter(_myRestLengthParameter, theRestLength);
	}

	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myGraphics.noBlend();
		
		
		_myAnchorPositionBuffer.beginDraw();
		_myInitValue01Shader.start();
		_myGraphics.beginShape(CCDrawMode.POINTS);
		CCVector3f myAnchor;
		for (int mySpring:_myChangedSprings){
			myAnchor = _myAnchorPositions[mySpring];
			_myGraphics.textureCoords(0, myAnchor.x, myAnchor.y, myAnchor.z, 1);
			_myGraphics.vertex(mySpring % _myWidth, mySpring / _myWidth);
		}
		while (_myActiveParticles.peek() != null && _myActiveParticles.peek().timeOfDeath() < _myCurrentTime){
			CCGPUParticle myParticle = _myActiveParticles.poll();
			_myGraphics.textureCoords(0, 0, 0, 0, 0);
			_myGraphics.vertex(myParticle.index() % _myWidth,myParticle.index() / _myWidth);
		}
		_myGraphics.endShape();
		_myInitValue01Shader.end();
		_myAnchorPositionBuffer.endDraw();
		
		_myChangedSprings.clear();
		_myCurrentTime += theDeltaTime;
	}
	
	public CCShaderBuffer anchorPositionTexture() {
		return _myAnchorPositionBuffer;
	}
}

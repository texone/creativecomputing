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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.forces.CCForce;


public class CCAnchoredSprings extends CCForce {
	
	private String _mySpringConstantParameter;
	private String _myDampingParameter;
	private String _myRestLengthParameter;
	
	private String _myAnchorPositionTextureParameter;

	private CCGLProgram _myInitValue01Shader;
	private CCShaderBuffer _myAnchorPositionBuffer;
	
	private CCVector3[] _myAnchorPositions;
	private List<Integer> _myChangedSprings = new ArrayList<>();
	protected PriorityQueue<CCParticle> _myActiveParticles = new PriorityQueue<>();
	
	@CCProperty(name = "spring constant", min = 0, max = 1)
	private double _cSpringConstant = 0.2;
	@CCProperty(name = "damping", min = 0, max = 0.1)
	private double _cDamping = 0.01;
	@CCProperty(name = "rest length", min = 0, max = 100)
	private double _myRestLength = 20;
	
	private int _myWidth = 0;
	
	protected double _myCurrentTime = 0;

	public CCAnchoredSprings(final double theSpringConstant, final double theDamping, final double theRestLength) {
		super("AnchoredSprings");
		
		_cSpringConstant = theSpringConstant;
		_cDamping = theDamping;
		_myRestLength = theRestLength;
		
		_mySpringConstantParameter = parameter("springConstant");
		_myDampingParameter = parameter("springDamping");
		_myRestLengthParameter = parameter("restLength");
		
		_myAnchorPositionTextureParameter = parameter("anchorPositionTexture");
	}

	public void setSize(CCGraphics g, int theWidth, int theHeight) {
		_myInitValue01Shader = new CCGLWriteDataShader();
		_myWidth = theWidth;
		
		_myAnchorPositions = new CCVector3[theWidth * theHeight];
		
		_myAnchorPositionBuffer = new CCShaderBuffer(32, 4, theWidth, theHeight);
		_myAnchorPositionBuffer.beginDraw(g);
		g.pushAttribute();
		g.clearColor(0,0);
		g.clear();
		g.popAttribute();
		_myAnchorPositionBuffer.endDraw(g);
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
			
		_myShader.setTextureUniform(_myAnchorPositionTextureParameter, _myAnchorPositionBuffer.attachment(0));
	}
	
	public void addSpring(final CCParticle theParticle, final CCVector3 theAnchor) {
		if(theParticle == null)return;
		_myActiveParticles.add(theParticle);
		_myChangedSprings.add(theParticle.index());
		_myAnchorPositions[theParticle.index()] = theAnchor;
	}
	
	public void setSpringPos(final CCParticle theParticle, final CCVector3 theAnchor) {
		if(theParticle == null || theParticle.index() < 0)return;
		_myChangedSprings.add(theParticle.index());
		_myAnchorPositions[theParticle.index()] = theAnchor;
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_mySpringConstantParameter, _cSpringConstant);
		_myShader.uniform1f(_myDampingParameter, _cDamping);
		_myShader.uniform1f(_myRestLengthParameter, _myRestLength);
	}
	
	public void springConstant(final double theSpringConstant) {
		_cSpringConstant = theSpringConstant;
	}
	
	public void springDamping(final double theSpringDamping) {
		_cDamping = theSpringDamping;
	}
	
	public void restLength(final double theRestLength) {
		_myRestLength = theRestLength;
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		super.update(theAnimator);
		_myCurrentTime += theAnimator.deltaTime();
	}

	@Override
	public void preDisplay(CCGraphics g) {
		super.preDisplay(g);
		g.noBlend();
		
		_myAnchorPositionBuffer.beginDraw(g);
		_myInitValue01Shader.start();
		g.beginShape(CCDrawMode.POINTS);
		CCVector3 myAnchor;
		for (int mySpring:_myChangedSprings){
			myAnchor = _myAnchorPositions[mySpring];
			g.textureCoords4D(0, myAnchor.x, myAnchor.y, myAnchor.z, 1);
			g.vertex(mySpring % _myWidth, mySpring / _myWidth);
		}
		while (_myActiveParticles.peek() != null && _myActiveParticles.peek().timeOfDeath() < _myCurrentTime){
			CCParticle myParticle = _myActiveParticles.poll();
			g.textureCoords4D(0, 0, 0, 0, 0);
			g.vertex(myParticle.index() % _myWidth,myParticle.index() / _myWidth);
		}
		g.endShape();
		_myInitValue01Shader.end();
		_myAnchorPositionBuffer.endDraw(g);
		
		_myChangedSprings.clear();
	}
	
	public CCShaderBuffer anchorPositionTexture() {
		return _myAnchorPositionBuffer;
	}
}

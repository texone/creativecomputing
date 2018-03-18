/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

	private CCGLProgram _myWriteDataShader;
	private CCShaderBuffer _myAnchorPositionBuffer;
	
	private CCVector3[] _myAnchorPositions;
	private List<Integer> _myChangedSprings = new ArrayList<Integer>();
	protected PriorityQueue<CCParticle> _myActiveParticles = new PriorityQueue<CCParticle>();
	
	private double _mySpringConstant = 0.1;
	private double _myDamping = 0;
	private double _myRestLength = 5;
	
	private int _myWidth = 0;
	
	protected double _myCurrentTime = 0;

	public CCAnchoredSprings(final double theSpringConstant, final double theDamping, final double theRestLength) {
		super("AnchoredSprings");
		_myWriteDataShader = new CCGLWriteDataShader();
		
		_mySpringConstant = theSpringConstant;
		_myDamping = theDamping;
		_myRestLength = theRestLength;
	}

	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight) {
		
		_myWidth = theWidth;
		
		_myAnchorPositions = new CCVector3[theWidth * theHeight];
		
		_myAnchorPositionBuffer = new CCShaderBuffer(32, 4, theWidth, theHeight);
		_myAnchorPositionBuffer.beginDraw(g);
		g.pushAttribute();
		g.clearColor(0,0);
		g.clear();
		g.popAttribute();
		_myAnchorPositionBuffer.endDraw(g);
		
		_mySpringConstantParameter = parameter("springConstant");
		_myDampingParameter = parameter("springDamping");
		_myRestLengthParameter = parameter("restLength");
		
		_myAnchorPositionTextureParameter = parameter("anchorPositionTexture");
		
		springDamping(_myDamping);
		springConstant(_mySpringConstant);
		restLength(_myRestLength);
		
//		_myVelocityShader.texture(_myAnchorPositionTextureParameter, _myAnchorPositionBuffer.attachment(0).id());
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
	
	public void springConstant(final double theSpringConstant) {
//		_myVelocityShader.parameter(_mySpringConstantParameter, theSpringConstant);
	}
	
	public void springDamping(final double theSpringDamping) {
//		_myVelocityShader.parameter(_myDampingParameter, theSpringDamping);
	}
	
	public void restLength(final double theRestLength) {
//		_myVelocityShader.parameter(_myRestLengthParameter, theRestLength);
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		super.preDisplay(g);
		g.noBlend();
		
		
		_myAnchorPositionBuffer.beginDraw(g);
		_myWriteDataShader.start();
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
		_myWriteDataShader.end();
		_myAnchorPositionBuffer.endDraw(g);
	}

	public void update(final CCAnimator theDeltaTime) {
		super.update(theDeltaTime);
		
		_myChangedSprings.clear();
		_myCurrentTime = theDeltaTime.time();
	}
	
	public CCShaderBuffer anchorPositionTexture() {
		return _myAnchorPositionBuffer;
	}
}

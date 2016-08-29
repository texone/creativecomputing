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
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUParticle;

class CCNode{
	float _myX;
	float _myAngle;
	float _myRandom;
	List<CCNode> _myNeighbors = new ArrayList<CCNode>();
	float _myAlpha = 0f;
	int _myDepth = 0;
	private CCVector3f _myPosition;
	CCVector3f _myParticlePosition;
	
	private float _myAge = 0;
	
	private CCGPUParticle _myParticle;
	
	public CCNode(float theX, float theAngle, float theRadius) {
		_myX = theX;
		_myAngle = theAngle;
		_myRandom = CCMath.random();
		_myPosition = new CCVector3f(
			_myX, 
			CCMath.sin(_myAngle) * _myRandom * theRadius, 
			CCMath.cos(_myAngle) * _myRandom * theRadius
		);
		_myParticlePosition = _myPosition.clone();
	}
	
	public CCGPUParticle particle(){
		return _myParticle;
	}
	
	public void particle(CCGPUParticle theParticle) {
		_myParticle = theParticle;
	}
	
	public float x() {
		return _myX;
	}
	
	public float angle() {
		return _myAngle;
	}
	
	public float random() {
		return _myRandom;
	}
	
	public CCVector3f position() {
		return _myPosition;
	}
	
	public CCVector3f particlePosition() {
		return _myParticlePosition;
	}
	
	public void alpha(float theAlpha, int theDepth, int theMaxDepth) {
		if(theDepth < _myDepth)return;
		_myDepth = theDepth;
		if(theDepth >= 0) {
			float myAlpha = CCMath.blend((float)(theDepth - 1) / theMaxDepth, (float)theDepth / theMaxDepth, theAlpha);
			_myAlpha = CCMath.max(myAlpha, _myAlpha);
			for(CCNode myNode:_myNeighbors) {
				myNode.alpha(theAlpha, theDepth - 1, theMaxDepth);
			}
		}
	}
	
	public float alpha() {
		return _myAlpha;
	}
	
	public void update(final float theDeltaTime){
		_myAge += theDeltaTime;
	}
	
	public float age(){
		return _myAge;
	}
}

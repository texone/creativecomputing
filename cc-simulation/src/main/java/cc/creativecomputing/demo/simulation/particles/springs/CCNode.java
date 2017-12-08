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
package cc.creativecomputing.demo.simulation.particles.springs;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;

class CCNode{
	private double _myX;
	private double _myAngle;
	private double _myRandom;
	List<CCNode> _myNeighbors = new ArrayList<CCNode>();
	private double _myAlpha = 0f;
	private int _myDepth = 0;
	private CCVector3 _myPosition;
	private CCVector3 _myParticlePosition;
	
	private double _myAge = 0;
	
	private CCParticle _myParticle;
	
	public CCNode(double theX, double theAngle, double theRadius) {
		_myX = theX;
		_myAngle = theAngle;
		_myRandom = CCMath.random();
		_myPosition = new CCVector3(
			_myX, 
			CCMath.sin(_myAngle) * _myRandom * theRadius, 
			CCMath.cos(_myAngle) * _myRandom * theRadius
		);
		_myParticlePosition = _myPosition.clone();
	}
	
	public CCParticle particle(){
		return _myParticle;
	}
	
	public void particle(CCParticle theParticle) {
		_myParticle = theParticle;
	}
	
	public double x() {
		return _myX;
	}
	
	public double angle() {
		return _myAngle;
	}
	
	public double random() {
		return _myRandom;
	}
	
	public CCVector3 position() {
		return _myPosition;
	}
	
	public CCVector3 particlePosition() {
		return _myParticlePosition;
	}
	
	public void alpha(double theAlpha, int theDepth, int theMaxDepth) {
		if(theDepth < _myDepth)return;
		_myDepth = theDepth;
		if(theDepth >= 0) {
			double myAlpha = CCMath.blend((double)(theDepth - 1) / theMaxDepth, (double)theDepth / theMaxDepth, theAlpha);
			_myAlpha = CCMath.max(myAlpha, _myAlpha);
			for(CCNode myNode:_myNeighbors) {
				myNode.alpha(theAlpha, theDepth - 1, theMaxDepth);
			}
		}
	}
	
	public double alpha() {
		return _myAlpha;
	}
	
	public void update(final double theDeltaTime){
		_myAge += theDeltaTime;
	}
	
	public double age(){
		return _myAge;
	}
}

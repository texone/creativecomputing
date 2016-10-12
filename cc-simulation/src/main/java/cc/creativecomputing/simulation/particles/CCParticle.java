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
package cc.creativecomputing.simulation.particles;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;


public class CCParticle implements Comparable<CCParticle>{
	private double _myTimeOfDeath;
	private float _myLifeTime;
	private boolean _myIsAllocated;
	private boolean _myIsPermanent;
	
	private int _myStep;

	private int _myIndex;
	private CCColor _myColor;
	private CCColor _myTargetColor;
	private CCVector3 _myPosition;
	private CCVector3 _myVelocity;
	private CCVector3 _myTarget;
	
	private CCParticles _myParticles;
	
	private float _myAge;
	
	public CCParticle(CCParticles theParticles, int theIndex) {
		_myParticles = theParticles;
		_myIndex = theIndex;
		_myColor = new CCColor();
		_myTargetColor = new CCColor();
		_myPosition = new CCVector3();
		_myVelocity = new CCVector3();
		_myTarget = new CCVector3();
		_myIsAllocated = false;
		_myStep = 0;
		_myAge = 0;
		_myIsPermanent = false;
	}
	
	public void nextStep() {
		_myStep++;
	}
	
	public void step(int theStep) {
		_myStep = theStep;
	}
	
	public int step() {
		return _myStep;
	}
	
	public void age(float theAge) {
		_myAge = theAge;
	}
	
	public float age() {
		return _myAge;
	}
	
	public boolean isAllocated() {
		return _myIsAllocated;
	}
	
	public void isAllocated(boolean theIsAllocated) {
		_myIsAllocated = theIsAllocated;
	}
	
	public CCColor color(){
		return _myColor;
	}
	
	public CCVector3 position() {
		return _myPosition;
	}
	
	public CCVector3 velocity() {
		return _myVelocity;
	}

	public int compareTo(CCParticle theParticle) {
		if(_myTimeOfDeath < theParticle._myTimeOfDeath)return -1;
		return 1;
	}
	
	public boolean isDead() {
		return _myTimeOfDeath < _myParticles.currentTime();
	}
	
	public float lifeTime() {
		return _myLifeTime;
	}
	
	public void lifeTime(final float theLifeTime) {
		_myLifeTime = theLifeTime;
	}
	
	public boolean isPermanent() {
		return _myIsPermanent;
	}
	
	public void isPermanent(final boolean theIsPermanent) {
		_myIsPermanent = theIsPermanent;
	}
	
	public double timeOfDeath() {
		return _myTimeOfDeath;
	}
	
	public void timeOfDeath(final double theTimeOfDeath) {
		_myTimeOfDeath = theTimeOfDeath;
	}
	
	public int index() {
		return _myIndex;
	}
	
	public void index(int theIndex) {
		_myIndex = theIndex;
	}
	
	public int x() {
		return _myIndex % _myParticles.width();
	}
	
	public int y() {
		return _myIndex / _myParticles.width();
	}
	
	public void target(CCVector3 theTarget){
		_myTarget.set(theTarget);
	}
	
	public CCVector3 target(){
		return _myTarget;
	}
	
	public CCColor targetColor(){
		return _myTargetColor;
	}
}

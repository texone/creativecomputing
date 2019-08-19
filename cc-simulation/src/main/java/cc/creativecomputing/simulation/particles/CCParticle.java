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
package cc.creativecomputing.simulation.particles;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;
import cc.creativecomputing.simulation.particles.emit.CCParticleCPUGroupEmitter;


public class CCParticle implements Comparable<CCParticle>{
	public double _myTimeOfDeath;
	public double _myLifeTime;
	public boolean _myIsAllocated;

	public int _myIndex;
	public CCColor _myColor;
	public CCColor _myTargetColor;
	public CCVector3 _myPosition;
	public CCVector3 _myVelocity;
	public CCVector4 _myTarget;
	public CCVector4 _myTexCoords;
	
	public int _myGroupIndex = -1;
	public int _mySprings = 0;
	
	private CCParticles _myParticles;
	
	public double _myAge;
	
	public CCParticle(CCParticles theParticles, int theIndex) {
		_myParticles = theParticles;
		_myIndex = theIndex;
		_myColor = new CCColor();
		_myTargetColor = new CCColor();
		_myPosition = new CCVector3();
		_myVelocity = new CCVector3();
		_myTarget = new CCVector4();
		_myTexCoords = new CCVector4();
		_myIsAllocated = false;
		_myAge = 0;
	}
	
	public void reset() {
		_myColor.set(0);
		_myTargetColor.set(0);
		_myPosition.set(0, 0, 0);
		_myVelocity.set(0,0,0);
		_myTarget.set(0,0,0,0);
		_myTexCoords.set(0,0,0,0);
		_myIsAllocated = false;
		_myGroupIndex = -1;
		_myAge = 0;
	}
	
	public int groupIndex() {
		return _myGroupIndex;
	}
	
	public int groupX() {
		return _myGroupIndex % CCParticleCPUGroupEmitter.GROUP_WIDH;
	}
	
	public int groupY() {
		return _myGroupIndex / CCParticleCPUGroupEmitter.GROUP_WIDH;
	}
	
	public void groupIndex(int theGroupIndex) {
		_myGroupIndex = theGroupIndex;
	}
	
	public void age(double theAge) {
		_myAge = theAge;
	}
	
	public double age() {
		return _myAge;
	}
	
	public boolean isPermanent() {
		return _myLifeTime < 0;
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
	
	public double lifeTime() {
		return _myLifeTime;
	}
	
	public void lifeTime(final double theLifeTime) {
		_myLifeTime = theLifeTime;
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
	
	public void target(CCVector4 theTarget){
		_myTarget.set(theTarget);
	}
	
	public CCVector4 target(){
		return _myTarget;
	}
	
	public CCColor targetColor(){
		return _myTargetColor;
	}
	
	public CCVector4 texCoords() {
		return _myTexCoords;
	}
}

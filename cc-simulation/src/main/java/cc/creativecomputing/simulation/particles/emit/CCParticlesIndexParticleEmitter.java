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
package cc.creativecomputing.simulation.particles.emit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCParticlesIndexParticleEmitter extends CCParticleCPUEmitter{
	
	public class CCParticleWaitingList {
		
		private float _myTimeStep;
		private int _myOffset = 0;
		private List<CCParticle>[] _myWaitLists;

		@SuppressWarnings("unchecked")
		public CCParticleWaitingList(float theTimeStep) {
			_myTimeStep = theTimeStep;
			
			// asume a default max lifetime of 120 s
			int myNumberOfSteps = (int)(120 / theTimeStep);
			_myWaitLists = new ArrayList[myNumberOfSteps];
		}
		
		public void add(CCParticle theParticle) {
			_myPendingParticles.remove(theParticle);
			int myStep = (int)(theParticle.lifeTime() / _myTimeStep);
			myStep += _myOffset;
			myStep %= _myWaitLists.length;
			
			if(_myWaitLists[myStep] == null)_myWaitLists[myStep] = new ArrayList<CCParticle>();
			_myWaitLists[myStep].add(theParticle);
		}
		
		private float _myStepTime = 0;
		private int _myCurrentWorkedIndex = 0;
		private List<CCParticle> _myCurrentWaitList = null;
		
		private void handleCurrentWaitList(CCAnimator theAnimator){
			if(_myCurrentWaitList == null)return;

			double myFramesPerStep = _myTimeStep / theAnimator.deltaTime();
			int myChecksPerFrame = CCMath.ceil(_myCurrentWaitList.size() / myFramesPerStep);
			
			for(int i = 0; i < myChecksPerFrame && _myCurrentWorkedIndex < _myCurrentWaitList.size(); i++, _myCurrentWorkedIndex++){
				CCParticle myParticle = _myCurrentWaitList.get(_myCurrentWorkedIndex);
				_myFreeIndices.add(myParticle.index());
			}
		}
		
		public void update(CCAnimator theAnimator) {
			_myStepTime += theAnimator.deltaTime();
			
			handleCurrentWaitList(theAnimator);
			
			if(_myStepTime > _myTimeStep) {
				_myStepTime -= _myTimeStep;
				if(_myCurrentWaitList != null){
					for(;_myCurrentWorkedIndex < _myCurrentWaitList.size(); _myCurrentWorkedIndex++){
						CCParticle myParticle = _myCurrentWaitList.get(_myCurrentWorkedIndex);
						_myFreeIndices.add(myParticle.index());
					}
					_myCurrentWorkedIndex = 0;
					_myCurrentWaitList.clear();
				}
				
				if(_myWaitLists[_myOffset] != null) {
					_myCurrentWaitList = _myWaitLists[_myOffset];
				}
				_myOffset++;
				_myOffset %= _myWaitLists.length;
			}
		}
		
		public void reset() {
			for(int i = 0; i < _myWaitLists.length; i++) {
				if(_myWaitLists[i] != null)_myWaitLists[i].clear();
			}
		}
	}
	
	private int _myNumberOfParticles;

	private List<CCParticle> _myPendingParticles = new LinkedList<CCParticle>();
	private List<Integer> _myFreeIndices;
	
	private CCParticleWaitingList _myParticleWaitingList;
	protected final CCParticle[] _myActiveParticlesArray;
	
	@CCProperty(name = "track free particles")
	private boolean _cTrackFreeParticles = true;
	
	@CCProperty(name = "free particles", readBack = true)
	private int _myFreeParticles = 0;
	
	private int _myStart;
	
	protected double _myCurrentTime = 0;
	
	public CCParticlesIndexParticleEmitter(CCParticles theParticles, int theStart, int theNumberParticles) {
		super(theParticles);
		
		_myStart = theStart;
		
		_myNumberOfParticles = theNumberParticles;
		_myFreeIndices = new ArrayList<Integer>(_myNumberOfParticles);
		
		_myParticleWaitingList = new CCParticleWaitingList(0.5f);
		_myActiveParticlesArray = new CCParticle[_myNumberOfParticles];
		for(int i = 0; i < _myActiveParticlesArray.length;i++) {
			int myIndex = _myStart + i;
			_myActiveParticlesArray[i] = new CCParticle(_myParticles, myIndex);
			_myFreeIndices.add(myIndex);
		}
	}
	
	public CCParticlesIndexParticleEmitter(CCParticles theParticles) {
		this(theParticles, 0, theParticles.size());
	}
	
	/**
	 * Returns the number of currently active particles
	 * @return
	 */
	public int particlesInUse(){
		return size() - _myFreeIndices.size();
	}
	
	/**
	 * Returns the number of particles that can still be allocated from this particle system
	 * @return
	 */
	public int freeParticles() {
		return _myFreeIndices.size();
	}
	
	public int start() {
		return _myStart;
	}
	
	public int numberOfParticles() {
		return _myNumberOfParticles;
	}
	
	public int nextFreeId() {
		if (_myFreeIndices.isEmpty())
			return -1;

		return _myFreeIndices.get(_myFreeIndices.size() - 1);
	}
	
	public CCParticle nextParticle(){
		if (_myFreeIndices.isEmpty())
			return null;
		
		int myNextFreeID = nextFreeId();
		int myIndex = myNextFreeID - _myStart;
		return _myActiveParticlesArray[myIndex];
	}
	
	public int xforIndex(int theIndex) {
		return (_myStart + theIndex) % _myParticles.width();
	}
	
	public int yforIndex(int theIndex) {
		return (_myStart + theIndex) / _myParticles.width();
	}
	
	public void changeParticle(CCParticle theParticle) {
		_myParticleWaitingList.add(theParticle);
		_myStateChanges.add(theParticle);
	}
	
	public void kill(){
		for(CCParticle myParticle:new ArrayList<>(_myPendingParticles)){
			myParticle.groupIndex(-1);
			changeParticle(myParticle);
		}
	}
	
	public void kill(int theIndex){
		if(theIndex >= _myActiveParticlesArray.length)return;
		changeParticle(_myActiveParticlesArray[theIndex]);
	}
	
	private int _myLastIndex = 0;

	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * You can also define if a particle is permanent or can die. The number
	 * of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @return the allocated particle or <code>null</code>
	 */
	public CCParticle emit(
		final CCColor theColor,
		final CCVector3 thePosition, 
		final CCVector3 theVelocity, 
		final double theLifeTime
	){
		int myFreeIndex = _myLastIndex + _myStart;
		
		if(_cTrackFreeParticles){
			if(_myFreeIndices.isEmpty())return null;
			myFreeIndex = _myFreeIndices.remove(_myFreeIndices.size() - 1);
		}else{
			_myLastIndex++;
			_myLastIndex %= _myNumberOfParticles;
		}
		
		return emit(myFreeIndex, theColor, thePosition, theVelocity, theLifeTime);
	}
	
	public CCParticle emit(
		final CCVector3 thePosition, 
		final CCVector3 theVelocity, 
		final double theLifeTime
	){	
		return emit(CCColor.WHITE, thePosition, theVelocity, theLifeTime);
	}
	
	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * You can also define if a particle is permanent or can die. The number
	 * of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @param theIsPermanent <code>true</code> if the particle is permanent otherwise<code>false</code>
	 * @return the allocated particle or <code>null</code>
	 */
	public CCParticle emit(
		final int theIndex,
		final CCColor theColor,
		final CCVector3 thePosition, 
		final CCVector3 theVelocity, 
		final double theLifeTime
	){
//		_myAvailableIndices.remove(theIndex);
		int myIndex = theIndex - _myStart;
		CCParticle myActiveParticle = _myActiveParticlesArray[myIndex];
		myActiveParticle.color().set(theColor);
		myActiveParticle.position().set(thePosition);
		myActiveParticle.velocity().set(theVelocity);
		myActiveParticle.timeOfDeath(_myCurrentTime + theLifeTime);
		myActiveParticle.lifeTime(theLifeTime);

		synchronized(_myAllocatedParticles){
			_myAllocatedParticles.add(myActiveParticle);
			
			if(_cTrackFreeParticles)_myParticleWaitingList.add(myActiveParticle);
		}
		
		return myActiveParticle;
	}
	
	public void update(final CCAnimator theAnimator) {
		if(_cTrackFreeParticles){
			_myParticleWaitingList.update(theAnimator);
			_myFreeParticles = freeParticles();
		}else{
			_myFreeParticles = _myLastIndex;
		}
	}
	
	@Override
	public void reset() {
		_myFreeIndices.clear();
		
		_myParticleWaitingList = new CCParticleWaitingList(0.5f);
		for(int i = 0; i < _myActiveParticlesArray.length;i++) {
			int myIndex = _myStart + i;
			_myFreeIndices.add(myIndex);
		}
	}
	
	public int size() {
		return _myNumberOfParticles;
	}
	
	public CCParticle particle(final int theID) {
		return _myActiveParticlesArray[theID - _myStart];
	}
}

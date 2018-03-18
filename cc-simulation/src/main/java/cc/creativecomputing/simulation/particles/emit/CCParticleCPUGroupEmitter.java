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
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public class CCParticleCPUGroupEmitter extends CCParticleCPUEmitter{
	
	public interface CCParticleInfoProvider {
		public void setup(CCParticleGroup theGroup, CCParticle theParticle);
	}
	
	public class CCParticleGroup {

		@CCProperty(name = "progress", min = 0, max = 1)
		public double progressBlend = 0;
		
		@CCProperty(name = "emit min", min = 0, max = 1)
		public double emitMin = 0;
		
		@CCProperty(name = "emit max", min = 0, max = 1)
		public double emitMax = 0;
		
		public List<CCParticle> particles = new ArrayList<>();
		
		private List<Integer> _myFreeIndices;

		private int _myNumberOfParticles;

		protected final CCParticle[] _myActiveParticlesArray;
		
		private int _myStart;
		
		public final int id;
		
		private  CCParticleInfoProvider _myInfoProvider;
		
		public CCParticleGroup(CCParticles theParticles, CCParticleInfoProvider theProvider, int theId, int theStart, int theNumberOfParticles) {
			_myInfoProvider = theProvider;
			id = theId;
			_myStart = theStart;
			_myNumberOfParticles = theNumberOfParticles;
			_myFreeIndices = new ArrayList<Integer>(_myNumberOfParticles);
			_myActiveParticlesArray = new CCParticle[_myNumberOfParticles];
			for(int i = 0; i < _myActiveParticlesArray.length;i++) {
				int myIndex = theStart + i;
				_myActiveParticlesArray[i] = new CCParticle(theParticles, myIndex);
				_myFreeIndices.add(myIndex);
			}
		}
		
		public void reset() {
			_myFreeIndices.clear();
			
			for(int i = 0; i < _myActiveParticlesArray.length;i++) {
				int myIndex = _myStart + i;
				_myFreeIndices.add(myIndex);
			}
			particles.clear();
		}
		
		public void kill() {
			for(CCParticle myParticle:particles){
//				myParticle.groupIndex(-1);
				_myFreeIndices.add(myParticle.index());
				changeParticle(myParticle);
			}
			particles.clear();
		}

		public int size() {
			return _myNumberOfParticles;
		}
		
		/**
		 * Returns the number of currently active particles
		 * @return
		 */
		public int particlesInUse(){
			return size() - _myFreeIndices.size();
		}
		
		public int freeParticles() {
			return _myFreeIndices.size();
		}
		
		private int nextFreeId() {
			if (_myFreeIndices.isEmpty())
				return -1;

			return _myFreeIndices.remove(_myFreeIndices.size() - 1);
		}
		
		private CCParticle nextParticle(){
			if (_myFreeIndices.isEmpty())
				return null;
			
			int myNextFreeID = nextFreeId();
			int myIndex = myNextFreeID - _myStart;
			CCParticle myResult = _myActiveParticlesArray[myIndex];
			particles.add(myResult);
			myResult.reset();
			return myResult;
		}
		
		private double _myLastMin = 0;
		private double _myLastMax = 0;
		
		private void emitRange(double theMin, double theMax) {

			double myMin = CCMath.constrain(theMin, emitMin, emitMax);
			double myMax = CCMath.constrain(theMax, emitMin, emitMax);
			
			int myMinID = (int)(myMin * _myActiveParticlesArray.length);
			int myMaxID = (int)(myMax * _myActiveParticlesArray.length);
			
			for(int i = myMinID; i < myMaxID;i++) {
				CCParticle myParticle = _myActiveParticlesArray[i];
				if(myParticle.isAllocated())return;
				myParticle.reset();
				myParticle.isAllocated(true);
				myParticle.groupIndex(id);
				_myInfoProvider.setup(this, myParticle);
				_myAllocatedParticles.add(myParticle);
				particles.add(myParticle);
			}
		}
		
		private void killRange(double theMin, double theMax) {
			double myMin = theMin;//CCMath.constrain(theMin, emitMin, emitMax);
			double myMax = theMax;//CCMath.constrain(theMax, emitMin, emitMax);
			
			int myMinID = (int)(myMin * _myActiveParticlesArray.length);
			int myMaxID = (int)(myMax * _myActiveParticlesArray.length);
			for(int i = myMinID; i < myMaxID;i++) {
				CCParticle myParticle = _myActiveParticlesArray[i];
				if(!myParticle.isAllocated())return;
				myParticle.reset();
				myParticle.isAllocated(false);
				myParticle.groupIndex(-1);
				_myFreeIndices.add(myParticle.index());
				changeParticle(myParticle);
				particles.remove(myParticle);
			}
		}
		
		public void setData(CCGraphics g) {
//			if(emitMin < _myLastMin) {
//				emitRange(emitMin, _myLastMin);
//			}
			if(emitMax > _myLastMax) {
				emitRange(_myLastMax, emitMax);
			}
//			if(emitMin > _myLastMin) {
//				killRange(_myLastMin, emitMin);
//			}
			if(emitMax < _myLastMax) {
				killRange(emitMax, _myLastMax);
			}
			_myLastMin = emitMin;
			_myLastMax = emitMax;
		}
	}
	
	public static int GROUP_WIDH = 200;
	
	@CCProperty(name = "groups")
	private List<CCParticleGroup>  _myGroups = new ArrayList<>();
	
	private int _myNumberOfParticles;
	
	
	@CCProperty(name = "track free particles")
	private boolean _cTrackFreeParticles = true;
	
	@CCProperty(name = "free particles", readBack = true)
	private int _myFreeParticles = 0;
	
	private int _myStart;

	private int _myLastGroupStart;

	private int _myLastIndex = 0;
	
	public CCParticleCPUGroupEmitter(CCParticles theParticles, int theStart, int theNumberParticles) {
		super(theParticles);
		
		_myStart = theStart;
		_myLastGroupStart = _myStart;
		_myNumberOfParticles = theNumberParticles;
	}
	
	public CCParticleCPUGroupEmitter(CCParticles theParticles) {
		this(theParticles, 0, theParticles.size());
	}

	public List<CCParticleGroup> groups(){
		return _myGroups;
	}
	
	/**
	 * Returns the number of particles that can still be allocated from this particle system
	 * @return
	 */
	public int freeParticles() {
		int myResult = 0;
		for(CCParticleGroup myGroup:_myGroups) {
			myResult += myGroup.freeParticles();
		}
		return myResult;
	}
	
	public int start() {
		return _myStart;
	}
	
	public int numberOfParticles() {
		return _myNumberOfParticles;
	}
	
	public int xforIndex(int theIndex) {
		return (_myStart + theIndex) % _myParticles.width();
	}
	
	public int yforIndex(int theIndex) {
		return (_myStart + theIndex) / _myParticles.width();
	}
	
	public void changeParticle(CCParticle theParticle) {
		_myStateChanges.add(theParticle);
	}
	
	public void kill(){
		for(int i = 0; i < _myGroups.size();i++) {
			killGroup(i);
		}
	}
	
	public void killGroup(int theGroup){
		if(theGroup >= _myGroups.size()) {
			return;
		}
		CCParticleGroup myGroup = _myGroups.get(theGroup);
		myGroup.kill();
	}

//	/**
//	 * Allocates a new particle with the given position, velocity and data.
//	 * You can also define if a particle is permanent or can die. The number
//	 * of particles you can create is limited by the size of the data texture
//	 * that you define in the constructor of the particle system. If no particle
//	 * could be allocated this method returns null.
//	 * @param thePosition position of the particle
//	 * @param theVelocity velocity of the particle
//	 * @param theLifeTime lifetime of the particle
//	 * @param theIsPermanent <code>true</code> if the particle is permanent otherwise<code>false</code>
//	 * @return the allocated particle or <code>null</code>
//	 */
//	public CCParticle emit(
//		final CCColor theColor,
//		final CCVector3 thePosition, 
//		final CCVector3 theVelocity,
//		final int theGroup
//	){
//		if(theGroup >= _myGroups.size()) {
//			return null;
//		}
//		CCParticleGroup myGroup = _myGroups.get(theGroup);
//		CCParticle myActiveParticle = myGroup.nextParticle();
//		if(myActiveParticle == null)return null;
//		
//		myActiveParticle.color().set(theColor);
//		myActiveParticle.position().set(thePosition);
//		myActiveParticle.velocity().set(theVelocity);
//		myActiveParticle.timeOfDeath(0);
//		myActiveParticle.lifeTime(0);
//		myActiveParticle.groupIndex(theGroup);
//
//		synchronized(_myAllocatedParticles){
//			_myAllocatedParticles.add(myActiveParticle);
//			_myGroups.get(theGroup).particles.add(myActiveParticle);
//
//			CCLog.info(myGroup, myActiveParticle);
//		}
//		
//		return myActiveParticle;
//	}
//	
//	public CCParticle emit(
//		final CCVector3 thePosition, 
//		final CCVector3 theVelocity,
//		final int theGroup
//	){	
//		return emit(CCColor.WHITE, thePosition, theVelocity, theGroup);
//	}
//	
	
	public CCParticleGroup createGroup(CCParticleInfoProvider theInfoProvider, int theSize) {
		int mySize = CCMath.min(theSize, _myNumberOfParticles - _myLastGroupStart);
		if(_myLastGroupStart >= _myNumberOfParticles)return null;
		CCParticleGroup myResult = new CCParticleGroup(_myParticles, theInfoProvider, _myGroups.size(), _myLastGroupStart, mySize);
		_myLastGroupStart += mySize;
		_myGroups.add(myResult);
		return myResult;
	}
	
	
	public void update(final CCAnimator theAnimator) {
		if(_cTrackFreeParticles){
			_myFreeParticles = freeParticles();
		}else{
			_myFreeParticles = _myLastIndex;
		}
	}
	
	@Override
	public void reset() {
		for(CCParticleGroup myGroup:_myGroups) {
			myGroup.reset();
		}
	}
	
	public int size() {
		return _myNumberOfParticles;
	}
	
	@Override
	public void setData(CCGraphics g) {
		super.setData(g);
		
		for(CCParticleGroup myGroup:_myGroups) {
			myGroup.setData(g);
		}
		
		_myParticles.groupData().beginDraw(g);
		_myParticles.initValueShader().start();
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < _myGroups.size();i++){
			int x = i % GROUP_WIDH;
			int y = i / GROUP_WIDH;
			CCParticleGroup myGroup = _myGroups.get(i);
			g.textureCoords4D(0, myGroup.progressBlend, 0, 0, 1d);
			g.vertex(x + 1, y);
		}
		g.endShape();
		_myParticles.initValueShader().end();
		_myParticles.groupData().endDraw(g);
	}
}

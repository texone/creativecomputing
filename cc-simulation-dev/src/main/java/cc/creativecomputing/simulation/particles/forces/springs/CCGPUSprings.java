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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCGPUParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.CCForce;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;


/**
 * Adds support of spring forces to the particle system.
 * <p>
 * You can create a spring force between two particles using their index or position inside the data texture. 
 * Be aware that you can not create endless springs. The number of springs per particle is dependent on the 
 * number of spring textures that store the data. So in the constructor of the gpu springs you can define how 
 * many springs you want to attach per particle.
 * </p>
 * <p>
 * A spring can work in two different ways. The particles can be force to keep the defined rest length this
 * means they will be pulled together if they exceed the rest length and pushed away from each other if they get
 * closer than the given rest length. This is the case if the <code>forceRestLength</code> parameter is set
 * <code>true</code>. If you do not force the rest length particles are allowed to to get closer than the given 
 * rest length.
 * </p>
 * @author christian riekoff
 *
 */
public class CCGPUSprings extends CCForce {
	
	private CGparameter _mySpringConstantParameter;
	private CGparameter _myRestLengthParameter;
	
	private CGparameter[] _myIDTextureParameters;
	private CGparameter[] _myInfoTextureParameters;

	private CCCGShader _myInitValue01Shader;
	private CCCGShader _myKillSpringShader;
	
	private CCShaderBuffer[] _myIDTextures;
	private CCShaderBuffer[] _myKilledIDTextures;
	private CCShaderBuffer[] _myInfoTextures;
	private int _myNumberOfSprings;
	private int _myNumberOfSpringBuffers;
	
	private float _mySpringConstant;
	private float _myRestLength;
	
	private CCGraphics _myGraphics;
	
	private int _myWidth;
	private int _myHeight;
	
	private int[] _mySprings;
	private int _mySpringsSize;
	private float[][] _myRestLengths;
	private boolean[][] _myForceRestLengths;
	private List<Integer> _myChangedSprings = new ArrayList<Integer>();

	protected double _myCurrentTime = 0;

	/**
	 * Creates a new spring force with the given number of springs per particle, the given 
	 * spring constant and the given rest length.
	 * @param g reference to the graphics object
	 * @param theNumberOfSprings number of springs attached to a particle
	 * @param theSpringConstant spring constant defining the strength of a spring
	 * @param theRestLength rest length defining the space between two particles
	 */
	public CCGPUSprings(final CCGraphics g, final int theNumberOfSprings, final float theSpringConstant, final float theRestLength) {
		this("Springs", g, theNumberOfSprings, theSpringConstant, theRestLength);
	}
	
	/**
	 * Creates a new spring force with the given 
	 * spring constant and the given rest length. The number of springs that can be attached to a particle
	 * is set to 4.
	 * 
	 * @param g reference to the graphics object
	 * @param theSpringConstant spring constant defining the strength of a spring
	 * @param theRestLength rest length defining the space between two particles
	 */
	public CCGPUSprings(final CCGraphics g, final float theSpringConstant, final float theRestLength) {
		this("Springs", g, 4, theSpringConstant, theRestLength);
	}
	
	protected CCGPUSprings(final String theForceName, final CCGraphics g, final int theNumberOfSprings, final float theSpringConstant, final float theRestLength) {
		super(theForceName);
		_myInitValue01Shader = new CCCGShader(null, CCIOUtil.classPath(CCParticles.class,"shader/initvalue.fp"));
		_myInitValue01Shader.load();
		
		_myKillSpringShader = new CCCGShader(null, CCIOUtil.classPath(CCParticles.class,"shader/spring_kill.fp"));
		_myKillSpringShader.load();
		
		_myGraphics = g;
		
		_mySpringConstant = theSpringConstant;
		_myRestLength = theRestLength;
		
		_myNumberOfSpringBuffers = CCMath.ceil(theNumberOfSprings / 2f);
		_myIDTextures = new CCShaderBuffer[_myNumberOfSpringBuffers];
		_myKilledIDTextures = new CCShaderBuffer[_myNumberOfSpringBuffers];
		_myInfoTextures = new CCShaderBuffer[_myNumberOfSpringBuffers];
		
	}

	private void resetTextures() {
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			CCShaderBuffer myIDTexture = _myIDTextures[i];
			myIDTexture.beginDraw();
			_myInitValue01Shader.start();
			_myGraphics.beginShape(CCDrawMode.QUADS);
			_myGraphics.textureCoords(0, -1f, -1f, -1f, -1f);
			_myGraphics.vertex(0,0);
			_myGraphics.vertex(_myWidth,0);
			_myGraphics.vertex(_myWidth,_myHeight);
			_myGraphics.vertex(0,_myHeight);
			_myGraphics.endShape();
			_myInitValue01Shader.end();
			myIDTexture.endDraw();
			
			CCShaderBuffer myInfoTexture = _myInfoTextures[i];
			myInfoTexture.beginDraw();
			_myInitValue01Shader.start();
			_myGraphics.beginShape(CCDrawMode.QUADS);
			_myGraphics.textureCoords(0, 0f, 0f, 0f, 0f);
			_myGraphics.vertex(0,0);
			_myGraphics.vertex(_myWidth,0);
			_myGraphics.vertex(_myWidth,_myHeight);
			_myGraphics.vertex(0,_myHeight);
			_myGraphics.endShape();
			_myInitValue01Shader.end();
			myInfoTexture.endDraw();
		}
	}

	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_mySprings = new int[_myWidth * _myHeight * _myNumberOfSpringBuffers * 2];
		_mySpringsSize = _myWidth * _myHeight;
		_myRestLengths = new float[_myWidth * _myHeight][_myNumberOfSpringBuffers * 2];
		_myForceRestLengths = new boolean[_myWidth * _myHeight][_myNumberOfSpringBuffers * 2];
		for(int i = 0; i < _mySpringsSize;i++) {
			for(int j = 0; j < _myNumberOfSpringBuffers * 2;j++) {
				_mySprings[i + j * _mySpringsSize] = -1;
				_myRestLengths[i][j] = 0;
				_myForceRestLengths[i][j] = true;
			}
		}
		
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			_myIDTextures[i] = new CCShaderBuffer(16, 4, theWidth, theHeight);
			_myIDTextures[i].clear();
			
			_myKilledIDTextures[i] = new CCShaderBuffer(16, 4, theWidth, theHeight);
			_myKilledIDTextures[i].clear();

			_myInfoTextures[i] = new CCShaderBuffer(16, 4, theWidth, theHeight);
			_myInfoTextures[i].clear();
		}
		
		resetTextures();
		
		_mySpringConstantParameter = parameter("springConstant");
		_myRestLengthParameter = parameter("restLength");
		
		CGparameter myIdTexturesParameter = parameter("idTextures");
		CgGL.cgSetArraySize(myIdTexturesParameter, _myNumberOfSpringBuffers);
		_myIDTextureParameters = new CGparameter[_myNumberOfSpringBuffers];
		
		CGparameter myInfoTexturesParameter = parameter("infoTextures");
		CgGL.cgSetArraySize(myInfoTexturesParameter, _myNumberOfSpringBuffers);
		_myInfoTextureParameters = new CGparameter[_myNumberOfSpringBuffers];
		
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			_myIDTextureParameters[i] = parameter("idTextures["+i+"]");
			_myVelocityShader.texture(_myIDTextureParameters[i], _myKilledIDTextures[i].attachment(0).id());

			_myInfoTextureParameters[i] = parameter("infoTextures["+i+"]");
			_myVelocityShader.texture(_myInfoTextureParameters[i], _myInfoTextures[i].attachment(0).id());
		}
		
		springConstant(_mySpringConstant);
		restLength(_myRestLength);
		
		_myGraphics.noBlend();
	}
	
	/**
	 * Returns the number of springs that can be attached to a particle
	 * @return number of springs per particle
	 */
	public int numberOfSpringsPerParticle(){
		return _myNumberOfSprings;
	}
	
	/**
	 * Number of buffers that save spring data
	 * @return Number of buffers that save spring data
	 */
	public int numberOfSpringTextures(){
		return _myNumberOfSpringBuffers;
	}
	
	public CCShaderBuffer idBuffer(int theID){
		return _myIDTextures[theID];
	}
	
	public CCShaderBuffer tmpidBuffer(int theID){
		return _myKilledIDTextures[theID];
	}
	
	/**
	 * 
	 * @param theParticle
	 * @return
	 */
	public boolean hasFreeSpringIndex(final CCGPUParticle theParticle) {
		return getFreeSpringIndex(theParticle) >= 0;
	}
	
	private int getFreeSpringIndex(final CCGPUParticle theParticle) {
		for(int i = 0; i < _myNumberOfSpringBuffers * 2;i++) {
			if(_mySprings[theParticle.index() + i * _mySpringsSize] == -1) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * <p>
	 * Creates a spring force between the particles with the given index. Be aware that you can not create
	 * endless springs. The number of springs per particle is dependent on the number of springtextures that
	 * store the data. So in the constructor of the gpu springs you can define how many springs you want to 
	 * attach per particle.
	 * </p>
	 * <p>
	 * A spring can work in two different ways. The particles can be force to keep the defined rest length this
	 * means they will be pulled together if they exceed the rest length and pushed away from each other if they get
	 * closer than the given rest length. This is the case if the <code>forceRestLength</code> parameter is set
	 * <code>true</code>. If you do not force the rest length particles are allowed to to get closer than the given 
	 * rest length.
	 * </p>
	 * @param theA index of the particle a
	 * @param theB index of the particle b
	 * @param theRestLength rest length of the spring to create
	 * @param theForceRestLength if this is <code>true</code> particles are forced to keep the rest length
	 * @return <code>true</code> if the spring could be created otherwise <code>false</code>
	 */
	public boolean addSpring(final CCGPUParticle theA, final CCGPUParticle theB, final float theRestLength, boolean theForceRestLength) {
		if(theA == null || theB == null)return false;
		
		int myIndexA = getFreeSpringIndex(theA);
		int myIndexB = getFreeSpringIndex(theB);
		
		if(myIndexA > -1 && myIndexB > -1) {
			_mySprings[theA.index() + myIndexA * _mySpringsSize] = theB.index();
			_mySprings[theB.index() + myIndexB * _mySpringsSize] = theA.index();
			
			_myRestLengths[theA.index()][myIndexA] = theRestLength;
			_myRestLengths[theB.index()][myIndexB] = theRestLength;
			
			_myForceRestLengths[theA.index()][myIndexA] = theForceRestLength;
			_myForceRestLengths[theB.index()][myIndexB] = theForceRestLength;
			
			_myChangedSprings.add(theA.index());
			_myChangedSprings.add(theB.index());
			return true;
		}else {
			return false;
		}
	}
	
	public boolean addSpring(final CCGPUParticle theA, final CCGPUParticle theB, final float theRestLength) {
		return addSpring(theA, theB, theRestLength, true);
	}
	
	public boolean addSpring(final CCGPUParticle theParticleA, final CCGPUParticle theParticleB) {
		return addSpring(theParticleA, theParticleB, _myRestLength);
	}
	
	public boolean addOneWaySpring(final CCGPUParticle theA, final CCGPUParticle theB, final float theRestLength, boolean theForceRestLength) {
		int myIndexB = getFreeSpringIndex(theB);
		
		if(myIndexB > -1) {
			_mySprings[theB.index() + myIndexB * _mySpringsSize] = theA.index();
			_myRestLengths[theB.index()][myIndexB] = theRestLength;
			_myForceRestLengths[theB.index()][myIndexB] = theForceRestLength;
			_myChangedSprings.add(theB.index());
			return true;
		}else {
			return false;
		}
	}
	
	public boolean addOneWaySpring(final CCGPUParticle theA, final CCGPUParticle theB, final float theRestLength) {
		return addOneWaySpring(theA, theB, theRestLength, true);
	}
	
	public boolean addOneWaySpring(final CCGPUParticle theA, final CCGPUParticle theB) {
		return addOneWaySpring(theA, theB, _myRestLength, true);
	}
	
	public void springConstant(final float theSpringConstant) {
		_myVelocityShader.parameter(_mySpringConstantParameter, theSpringConstant);
	}
	
	public void restLength(final float theRestLength) {
		_myVelocityShader.parameter(_myRestLengthParameter, theRestLength);
	}

	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myGraphics.noBlend();
		
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			CCShaderBuffer myIDTexture = _myIDTextures[i];
			myIDTexture.beginDraw();
			_myInitValue01Shader.start();
			_myGraphics.beginShape(CCDrawMode.POINTS);
			
			for(int myID:_myChangedSprings) {
				int myIndex1 = _mySprings[myID + i * 2  * _mySpringsSize];

				float myX1 = myIndex1 == -1 ? -1 : myIndex1 % _myWidth;
				float myY1 = myIndex1 == -1 ? -1 : myIndex1 / _myWidth;
				
				int myIndex2 = _mySprings[myID + (i * 2 + 1)  * _mySpringsSize];
				float myX2 = myIndex2 == -1 ? -1 : myIndex2 % _myWidth;
				float myY2 = myIndex2 == -1 ? -1 : myIndex2 / _myWidth;
				
				_myGraphics.textureCoords(0, myX1, myY1, myX2, myY2);
				_myGraphics.vertex(myID % _myWidth, myID / _myWidth);
			}
			
			_myGraphics.endShape();
			_myInitValue01Shader.end();
			myIDTexture.endDraw();
			
			CCShaderBuffer myKilledIDTexture = _myKilledIDTextures[i];
			_myKillSpringShader.start();
			_myGraphics.texture(1, myIDTexture.attachment(0));
			_myGraphics.texture(0, _myParticles.dataBuffer().attachment(1));
			myKilledIDTexture.draw();
			_myGraphics.noTexture();
			_myKillSpringShader.end();
			
			CCShaderBuffer myInfoTexture = _myInfoTextures[i];
			myInfoTexture.beginDraw();
			_myInitValue01Shader.start();
			_myGraphics.beginShape(CCDrawMode.POINTS);
			
			for(int myID:_myChangedSprings) {
				float myRestLength1 = _myRestLengths[myID][i*2];
				float myRestLength2 = _myRestLengths[myID][i*2 + 1];
				
				float myForceRestLength1 = _myForceRestLengths[myID][i*2] ? 1 : 0;
				float myForceRestLength2 = _myForceRestLengths[myID][i*2] ? 1 : 0;
				
				_myGraphics.textureCoords(0, myRestLength1, myRestLength2, myForceRestLength1, myForceRestLength2);
				_myGraphics.vertex(myID % _myWidth, myID / _myWidth);
			}
			_myGraphics.endShape();
			_myInitValue01Shader.end();
			myInfoTexture.endDraw();
		}
		
		

		_myChangedSprings.clear();

		_myCurrentTime += theDeltaTime;
	}
	
	public CCShaderBuffer infoTexture(int theIndex) {
		return _myInfoTextures[theIndex]; 
	}
	
	public int[] springIndices() {
		return _mySprings;
	}
	
	@Override
	public void reset() {
		resetTextures();
		
		for(int i = 0; i < _mySpringsSize;i++) {
			for(int j = 0; j < _myNumberOfSpringBuffers * 2;j++) {
				_mySprings[i + j * _mySpringsSize] = -1;
				_myRestLengths[i][j] = 0;
				_myForceRestLengths[i][j] = true;
			}
		}
		
	}
	
	public List<Integer> lineIndices(){
		List<Integer> myIndices = new ArrayList<Integer>();
		
		for(int i = 0; i < _mySpringsSize;i++) {
			for(int j = 0; j < _myNumberOfSpringBuffers * 2;j++) {
				if(_mySprings[i + j * _mySpringsSize] != -1) {
					myIndices.add(i);
					myIndices.add(_mySprings[i + j * _mySpringsSize]);
				}
			}
		}
		return myIndices;
	}
}

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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.CCForce;

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
public class CCSpringForce extends CCForce {
	
	private String _mySpringConstantParameter;
	private String _myDampingParameter;
	private String _myNumberOfBufferParameter;
	private String _myXBufferParameter;
	private String _myTextureSizeParameter;
	private String _myInfoTextureParameter;

	private CCGLProgram _myInitValueShader;
	private CCGLProgram _myKillSpringShader;
	
	private CCShaderBuffer _myBuffer;
	private CCShaderBuffer _myTMPBuffer;
	
	private CCTexture2D _myParticleInfos;
	
	private int _myNumberOfSprings;
	private int _myNumberOfSpringBuffers;
	private int _myNumberOfXSpringBuffers;
	
	@CCProperty(name = "spring constant", min = 0, max = 1)
	private double _cSpringConstant = 0.2;
	@CCProperty(name = "damping", min = 0, max = 0.1)
	private double _cDamping = 0.01;
	
	private int _myWidth;
	private int _myHeight;
	
	private int[] _myIndexMap;
	private int _mySpringsSize;
	
	private static class CCSpringInfo{
		CCParticle particle;
		CCParticle target;
		double restLength;
		int buffer;
		boolean draw;
		
		CCSpringInfo(CCParticle theParticle, CCParticle theTarget, double theRestLength, int theBuffer, boolean theDraw){
			particle = theParticle;
			target = theTarget;
			restLength = theRestLength;
			buffer = theBuffer;
			draw = theDraw;
		}
		
	}
	private List<CCSpringInfo> _myChangedSprings = new ArrayList<>();

	protected double _myCurrentTime = 0;

	/**
	 * Creates a new spring force with the given number of springs per particle, the given 
	 * spring constant and the given rest length.
	 * @param g reference to the graphics object
	 * @param theNumberOfSprings number of springs attached to a particle
	 * @param theSpringConstant spring constant defining the strength of a spring
	 * @param theRestLength rest length defining the space between two particles
	 */
	public CCSpringForce(final int theNumberOfSprings, final double theSpringConstant) {
		this("Springs", theNumberOfSprings, theSpringConstant);
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
	public CCSpringForce(final double theSpringConstant) {
		this("Springs", 4, theSpringConstant);
	}
	
	protected CCSpringForce(final String theForceName, final int theNumberOfSprings, final double theSpringConstant) {
		super(theForceName);
		
		_cSpringConstant = theSpringConstant;
		
		_myNumberOfSpringBuffers = theNumberOfSprings;

		_mySpringConstantParameter = parameter("springConstant");
		_myInfoTextureParameter = parameter("data");
		_myDampingParameter = parameter("damping");
		
		_myNumberOfBufferParameter = parameter("numberOfBuffers");
		_myXBufferParameter = parameter("xBuffers");
		_myTextureSizeParameter = parameter("textureSize");
	}

	private void resetTextures(CCGraphics g) {
		_myBuffer.beginDraw(g);
		_myInitValueShader.start();
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords3D(0, -1f, -1f, 0);
		g.vertex(0,0);
		g.vertex(_myBuffer.width(),0);
		g.vertex(_myBuffer.width(),_myBuffer.height());
		g.vertex(0,_myBuffer.height());
		g.endShape();
		_myInitValueShader.end();
		_myBuffer.endDraw(g);
	}	
	
	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myNumberOfXSpringBuffers = CCMath.min(4000 / theWidth, _myNumberOfSpringBuffers);
		int myYSpingBuffers = CCMath.ceil(_myNumberOfSpringBuffers / (double)_myNumberOfXSpringBuffers);
		_mySpringsSize = _myWidth * _myHeight;
		_myIndexMap = new int[_mySpringsSize];
		for(int i = 0; i < _mySpringsSize;i++) {
			for(int j = 0; j < _myNumberOfSpringBuffers;j++) {
				_myIndexMap[i] = 0;
			}
		}

		_myInitValueShader = new CCGLWriteDataShader();
		
		_myKillSpringShader = new CCGLProgram(null, CCNIOUtil.classPath(this,"spring_kill.glsl"));
		
		_myBuffer = new CCShaderBuffer(16, 4, _myWidth * _myNumberOfXSpringBuffers, _myHeight * myYSpingBuffers);
		_myBuffer.clear(g);
			
		_myTMPBuffer = new CCShaderBuffer(16, 4, _myWidth * _myNumberOfXSpringBuffers, _myHeight * myYSpingBuffers);
		_myTMPBuffer.clear(g);

		resetTextures(g);
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
			
		_myShader.setTextureUniform(_myInfoTextureParameter, _myBuffer.attachment(0));
	}
	
	@Override
	public void setParticles(CCParticles theParticles) {
		_myParticleInfos = theParticles.infoData();
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_mySpringConstantParameter, _cSpringConstant);
		_myShader.uniform1f(_myDampingParameter, _cDamping);
		_myShader.uniform1i(_myNumberOfBufferParameter, _myNumberOfSpringBuffers);
		_myShader.uniform1i(_myXBufferParameter, _myNumberOfXSpringBuffers);
		_myShader.uniform2f(_myTextureSizeParameter, _myWidth, _myHeight);
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
	
	public int numberOfXBuffers(){
		return _myNumberOfXSpringBuffers;
	}
	
	public CCShaderBuffer idBuffer(){
		return _myBuffer;
	}
	
	public CCShaderBuffer tmpidBuffer(){
		return _myTMPBuffer;
	}
	
	/**
	 * 
	 * @param theParticle
	 * @return
	 */
	public boolean hasFreeSpringIndex(final CCParticle theParticle) {
		return getFreeSpringIndex(theParticle) >= 0;
	}
	
	private int getFreeSpringIndex(final CCParticle theParticle) {
		return _myIndexMap[theParticle.index()]++ % _myNumberOfSpringBuffers;
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
	public boolean addSpring(final CCParticle theA, final CCParticle theB, final double theRestLength) {
		if(theA == null || theB == null)return false;
		
		int myIndexA = getFreeSpringIndex(theA);
		int myIndexB = getFreeSpringIndex(theB);
			
		_myChangedSprings.add(new CCSpringInfo(theA, theB, theRestLength, myIndexA, true));
		_myChangedSprings.add(new CCSpringInfo(theB, theA, theRestLength, myIndexB, false));
		return true;
		
	}
	
	public boolean addSpring(final CCParticle theParticleA, final CCParticle theParticleB) {
		return addSpring(theParticleA, theParticleB, theParticleA.position().distance(theParticleB.position()));
	}
	
	public boolean addOneWaySpring(final CCParticle theA, final CCParticle theB, final double theRestLength) {
		if(theA == null || theB == null)return false;
		
		int myIndexA = getFreeSpringIndex(theA);
		
		_myChangedSprings.add(new CCSpringInfo(theA, theB, theRestLength, myIndexA, true));
		return true;
	}
	
	public void springConstant(final double theSpringConstant) {
		_cSpringConstant = theSpringConstant;
	}

	public void update(final CCAnimator theAnimator) {

		_myCurrentTime += theAnimator.deltaTime();
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		g.noBlend();
		g.texture(1, _myBuffer.attachment(0));
		g.texture(0, _myParticleInfos);
		_myKillSpringShader.start();
		_myKillSpringShader.uniform1i("infoTexture", 0);
		_myKillSpringShader.uniform1i("springIDs", 1);
		_myKillSpringShader.uniform2f("dimension", _myWidth, _myHeight);
		_myTMPBuffer.draw(g);
		_myKillSpringShader.end();
		g.noTexture();
		
		
		CCShaderBuffer mySwap = _myBuffer;
		_myBuffer = _myTMPBuffer;
		_myTMPBuffer = mySwap;
		
		_myBuffer.beginDraw(g);
		_myInitValueShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(CCSpringInfo myInfo:_myChangedSprings) {
			int myXOffset = myInfo.buffer % _myNumberOfXSpringBuffers;
			int myYOffset = myInfo.buffer / _myNumberOfXSpringBuffers;

			double myX = myInfo.target.index() == -1 ? -1 : myInfo.target.index() % _myWidth;
			double myY = myInfo.target.index() == -1 ? -1 : myInfo.target.index() / _myWidth;
				
			g.textureCoords4D(0, myX, myY, myInfo.restLength, myInfo.draw ? 1 : 0);
			g.vertex(myInfo.particle.index() % _myWidth + myXOffset * _myWidth, myInfo.particle.index() / _myWidth + myYOffset * _myHeight);
//			CCLog.info(myInfo.particle.index() % _myWidth + myXOffset * _myWidth, myInfo.particle.index() / _myWidth + myYOffset * _myHeight);
		}
			
		g.endShape();
		_myInitValueShader.end();
		_myBuffer.endDraw(g);
			
		

		_myChangedSprings.clear();
	}
	
	public CCShaderBuffer infoTexture() {
		return _myBuffer; 
	}
	
	public void reset(CCGraphics g) {
		resetTextures(g);
		
		for(int i = 0; i < _mySpringsSize;i++) {
			_myIndexMap[i] = 0;
		}
		
	}
}

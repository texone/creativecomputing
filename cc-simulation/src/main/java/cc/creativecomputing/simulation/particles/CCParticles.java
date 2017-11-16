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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCGLProgram.CCGLTextureUniform;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCGLSwapBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.constraints.CCConstraint;
import cc.creativecomputing.simulation.particles.forces.CCForce;
import cc.creativecomputing.simulation.particles.impulses.CCImpulse;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;
import cc.creativecomputing.simulation.particles.render.CCParticleRenderer;

/**
 * This particle system renders particles as points. You can add different forces
 * and constraints to change the behavior of the particles.
 * 
 * The data of the particles is stored in textures. Implementation wise the data is written into a framebuffer
 * with 4 attachments. Holding the information in the following layout:
 * <ul>
 * <li>attachment0: positions as xyz</li>
 * <li>attachment1: infos as age / lifetime / state</li>
 * <li>attachment2: velocities as xyz</li>
 * <li>attachment3: colors as rgba</li>
 * </ul>
 * You can use this data and overwrite them.
 * @author info
 * @demo cc.creativecomputing.gpu.particles.demo.CCParticlesNoiseFlowFieldTest
 * @see CCQuadParticles
 */
@SuppressWarnings("unused")
public class CCParticles{
	
	protected Map<Integer, CCVector3> _myPositionUpdates = new HashMap<Integer, CCVector3>();
	protected List<CCParticle> _myLifetimeUpdates = new ArrayList<CCParticle>();
	
	private List<CCParticleEmitter> _myEmitter = new ArrayList<CCParticleEmitter>();
	
	protected List<CCForce> _myForces;
	protected List<CCConstraint> _myConstraints;
	protected List<CCImpulse> _myImpulses;
	
	protected final int _myWidth;
	protected final int _myHeight;
	
	protected CCParticlesUpdateShader _myUpdateShader;
	
	protected CCGLWriteDataShader _mySetDataShader;
	
	protected CCGLSwapBuffer _mySwapTexture;
	
	protected double _myCurrentTime = 0;
	
	protected FloatBuffer _myPositionBuffer;
	protected FloatBuffer _myVelocityBuffer;
	
	@CCProperty(name = "forces")
	private Map<String, CCForce> _myForceMap = new LinkedHashMap<>();
	@CCProperty(name = "contraints")
	private Map<String, CCConstraint> _myContraintMap = new LinkedHashMap<>();
	@CCProperty(name = "renderer")
	private CCParticleRenderer _myParticleRender;
	
	/**
	 * <p>
	 * Creates a new particle system. To create a new particle system you have to
	 * pass the CCGraphics instance and a list with forces. You can also pass
	 * a list of constraints that act as boundary so that the particles bounce at
	 * collision.
	 * </p>
	 * <p>
	 * The number of particles you can create depends on the size of the texture
	 * that holds the particle data on the gpu. You can define this size by passing
	 * a width and height value. The number of particles you can allocate is 
	 * width * height.
	 * </p>
	 * <p>
	 * How the particles are drawn is defined by a shader. You can pass a custom
	 * shader to the particle system to define how the particles are drawn. To
	 * create your own shader you need to extend the CCDisplayShader and write your
	 * own cg shader.
	 * </p>
	 * 
	 * @param g graphics object used to initialize shaders and meshes for drawing
	 * @param theDisplayShader custom shader for displaying the particles
	 * @param theForces list with the forces applied to the particles
	 * @param theConstraints list with constraints applied to the particles
	 * @param theWidth width of particle system texture
	 * @param theHeight height of the particle system texture
	 */
	public CCParticles(
		final CCGraphics g,
		final CCParticleRenderer theRender,
		final List<CCForce> theForces, 
		final List<CCConstraint> theConstraints, 
		final List<CCImpulse> theImpulse, 
		final int theWidth, final int theHeight
	){
		_myForces = theForces;
		_myConstraints = theConstraints;
		_myImpulses = theImpulse;
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		for(CCForce myForce:theForces) {
			myForce.setSize(g, theWidth, theHeight);
			_myForceMap.put(myForce.parameter("force"), myForce);
		}
		
		for(CCConstraint myContraint:theConstraints) {
			myContraint.setSize(g, theWidth, theHeight);
			_myContraintMap.put(myContraint.parameter("contraint"), myContraint);
		}
		
		_mySetDataShader = new CCGLWriteDataShader();
		
		_mySwapTexture = new CCGLSwapBuffer(g, 32,4,4,_myWidth,_myHeight);
		
		_myParticleRender = theRender;
		_myParticleRender.setup(this);
		_myUpdateShader = new CCParticlesUpdateShader(this, g,theForces, theConstraints, theImpulse,_myWidth,_myHeight);
		
		reset(g);
		
		_myUpdateShader.setTextureUniform("positionTexture", _mySwapTexture.attachment(0));
		_myUpdateShader.setTextureUniform("infoTexture", _mySwapTexture.attachment(1));
		_myUpdateShader.setTextureUniform("velocityTexture", _mySwapTexture.attachment(2));
		_myUpdateShader.setTextureUniform("colorTexture", _mySwapTexture.attachment(3));
		_myUpdateShader.setTextureUniform("staticPositions", null);
	}
	
	public CCParticles(
		final CCGraphics g,
		CCParticleRenderer theRender, 
		List<CCForce> theForces, 
		List<CCConstraint> theConstraints, 
		int theWidth, int theHeight
	) {
		this(g, theRender, theForces, theConstraints, new ArrayList<CCImpulse>(), theWidth, theHeight);
	}

	public CCParticles(final CCGraphics g, List<CCForce> theForces, List<CCConstraint> theConstraints, int theWidth, int theHeight) {
		this(g, new CCParticlePointRenderer(), theForces, theConstraints, theWidth, theHeight);
	}

	public CCParticles(final CCGraphics g, List<CCForce> theForces, List<CCConstraint> theConstraints) {
		this(g, theForces, theConstraints,200,200);
	}

	public CCParticles(final CCGraphics g,List<CCForce> theForces) {
		this(g, theForces, new ArrayList<CCConstraint>());
	}
	
	public void addEmitter(CCParticleEmitter theEmitter) {
		_myEmitter.add(theEmitter);
	}
	
	public CCGLProgram initValueShader() {
		return _mySetDataShader;
	}
	
	public double currentTime() {
		return _myCurrentTime;
	}
	
	public void reset(CCGraphics g){

		for(CCParticleEmitter myEmitter:_myEmitter) {
			myEmitter.reset();
		}
		
		_mySwapTexture.clear(g);
		
		_mySwapTexture.beginDrawCurrent(g);
		_mySetDataShader.start();
		
		g.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < _myWidth * _myHeight; i++){
			g.textureCoords3D(0, Float.MAX_VALUE,Float.MAX_VALUE,Float.MIN_VALUE);
			g.textureCoords3D(1, 1, 1, 1);
			g.textureCoords3D(2, 0, 0, 0);
			g.textureCoords3D(3, 1, 1, 1);
			g.vertex(i % _myWidth,i / _myWidth);
		}
		g.endShape();
		
		_mySetDataShader.end();
		_mySwapTexture.endDrawCurrent(g);
		
		for(CCForce myForce:_myForces) {
			myForce.reset(g);
		}
	}
	
	/**
	 * Returns the width of the texture containing the particle data
	 * @return width of the particle texture
	 */
	public int width() {
		return _myWidth;
	}
	
	/**
	 * Returns the height of the texture containing the particle data
	 * @return height of the particle texture
	 */
	public int height() {
		return _myHeight;
	}
	
	public int size() {
		return _myWidth * _myHeight;
	}
	
	/**
	 * Returns the texture with the current positions of the particles.
	 * @return texture containing the positions of the particles
	 */
	public CCShaderBuffer dataBuffer() {
		return _mySwapTexture.currentBuffer();
	}
	
	public CCTexture2D envelopeTexture(){
		return _myUpdateShader.envelopeTexture();
	}
	
	/**
	 * Returns the position of the particle. This is useful as particle data is stored on the gpu
	 * and there for not accessible on the cpu side. Be aware that is time consuming and should only
	 * be used for a couple of particles.
	 * @param theParticle the particle to query
	 * @return the position of the given particle
	 */
	public CCVector3 position(CCParticle theParticle) {
		return position(theParticle, new CCVector3());
	}
	
	/**
	 * 
	 * @param theParticle the particle t query
	 * @param theVector vector to store the position
	 * @return the position of the particle as vector
	 */
	public CCVector3 position(CCParticle theParticle, CCVector3 theVector){
		FloatBuffer myResult = _mySwapTexture.getData(theParticle.x(), theParticle.y(), 1, 1);
		theVector.x = myResult.get();
		theVector.y = myResult.get();
		theVector.z = myResult.get();
		return theVector;
	}

	public CCShaderBuffer destinationDataTexture() {
		return _mySwapTexture.destinationBuffer();
	}
	
	/**
	 * Set the absolute position of the particle referenced by theIndex.
	 * @param theIndex index of the target particle
	 * @param thePosition target position of the particle
	 */
	public void setPosition(int theIndex, CCVector3 thePosition) {
		_myPositionUpdates.put(theIndex, thePosition);
	}
	
	private void updateManualPositionChanges(CCGraphics g) {
		
		if (_myPositionUpdates.size() == 0) {
			return;
		}
		
		// Render manually changed positions into the texture.
		_mySwapTexture.beginDrawCurrent(g,0);
		_mySetDataShader.start();
		
		g.beginShape(CCDrawMode.POINTS);
	
		Iterator<Entry<Integer, CCVector3>> it = _myPositionUpdates.entrySet().iterator();
		
	    while (it.hasNext()) {
	        Map.Entry<Integer, CCVector3> pairs = (Map.Entry<Integer, CCVector3>)it.next();
	        
	        g.textureCoords3D(0, pairs.getValue());
			g.vertex(pairs.getKey() % _myWidth, pairs.getKey() / _myWidth);
	    }
	    
		g.endShape();
		
		_mySetDataShader.end();
		_mySwapTexture.endDrawCurrent(g);
		
		_myPositionUpdates.clear();
	}
	
	/**
	 * Update the lifetime of the given particle to what is specified in 
	 * the particle instance.
	 * @param theParticle particle instance containing new lifetime data
	 */
	public void updateLifecyle(CCParticle theParticle) {
		_myLifetimeUpdates.add(theParticle);
	}
	
	private void initializeNewParticles(CCGraphics g){
		// Render velocity.
		
		// Render current position into texture.
		for(CCParticleEmitter myEmitter:_myEmitter) {
			myEmitter.setData(g);
		}
		
	}
	
	private void changeStates() {
//		_myCurrentDataTexture.beginDraw(1);
//		_myInitValue0Shader.start();
//		_myGraphics.beginShape(CCDrawMode.POINTS);
//		for(CCParticleEmitter myEmitter:_myEmitter) {
//			for (CCParticle myChangedParticle:myEmitter.stateChangedParticles()){
//				_myGraphics.textureCoords(0, myChangedParticle.age(), myChangedParticle.lifeTime(), myChangedParticle.isPermanent() ? 1 : 0, myChangedParticle.step());
//				_myGraphics.vertex(myChangedParticle.x(),myChangedParticle.y());
//			}
//			myEmitter.stateChangedParticles().clear();
//		}
//		_myGraphics.endShape();
//		
//		_myInitValue0Shader.end();
//		_myCurrentDataTexture.endDraw();
	}
	
	protected void beforeUpdate(CCGraphics g) {
		initializeNewParticles(g);
		changeStates();
	}
	
	private void cleanUpParticles() {
//		if(_myActiveParticles.size() <= 0)
//			return;
//		
//		_myCurrentPositionTexture.beginDraw(1);
//		_myInitValue1Shader.start();
//		_myGraphics.beginShape(CCDrawMode.POINTS);
//				
//		while (_myActiveParticles.peek() != null && _myActiveParticles.peek().timeOfDeath() < _myCurrentTime){
//			CCParticle myParticle = _myActiveParticles.poll();
//			if(myParticle.index == -1) continue;
//			_myAvailableIndices.add(myParticle.index);
//			_myActiveParticlesArray[myParticle.index].index = -1;
//			
//			_myGraphics.textureCoords(0, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
//			_myGraphics.textureCoords(1, 0, 0, 1, 0);
//			_myGraphics.vertex(myParticle.x() + 0.5f, myParticle.y() + 0.5f);
//		}
//		
//		_myGraphics.endShape();
//		_myInitValue1Shader.end();
//		_myCurrentPositionTexture.endDraw();
	}
	
	protected void afterUpdate(CCGraphics g){
		updateManualPositionChanges(g);
//		updateManualLifetimeReset();
		cleanUpParticles();
	}
	
	private CCTexture2D _myStaticPositionTexture = null;
	
	public void staticPositions(CCTexture2D theStaticPositions){
		_myStaticPositionTexture = theStaticPositions;
	}
	
	public void update(final CCAnimator theAnimator){
		if(theAnimator.deltaTime() <= 0)return;
		
		for(CCParticleEmitter myEmitter:_myEmitter) {
			myEmitter.update(theAnimator);
		}
		
		
		
		for(CCForce myForce:_myForces) {
			myForce.update(theAnimator);
		}
		
		for(CCConstraint myConstraint:_myConstraints) {
			myConstraint.update(theAnimator);
		}
		
		for(CCImpulse myImpulse:_myImpulses) {
			myImpulse.update(theAnimator);
		}

		_myUpdateShader.deltaTime(theAnimator.deltaTime());
		
		_myCurrentTime += theAnimator.deltaTime();
		_myParticleRender.update(theAnimator);
	}
	
	public void swapDataTextures(){
		_mySwapTexture.swap();
		_myUpdateShader.setTextureUniform("positionTexture", _mySwapTexture.attachment(0));
		_myUpdateShader.setTextureUniform("infoTexture", _mySwapTexture.attachment(1));
		_myUpdateShader.setTextureUniform("velocityTexture", _mySwapTexture.attachment(2));
		_myUpdateShader.setTextureUniform("colorTexture", _mySwapTexture.attachment(3));
	}
	
	public void animate(CCGraphics g){
		g.pushAttribute();
		g.noBlend();
		beforeUpdate(g);

		_myUpdateShader.preDisplay(g);
		_myUpdateShader.start();
		int myTextureUnit = 0;
		for(CCGLTextureUniform myTextureUniform:_myUpdateShader.textures()){
			if(myTextureUniform.texture == null)continue;
				
			g.texture(myTextureUnit, myTextureUniform.texture);
			_myUpdateShader.uniform1i(myTextureUniform.parameter, myTextureUnit);
//			CCLog.info(myTextureUnit + " : " + myTextureUniform.parameter  + " : " + myTextureUniform.texture);
			myTextureUnit++;
		}
		_mySwapTexture.draw(g);
		_myUpdateShader.end();
		g.noTexture();
		
		swapDataTextures();
		
		afterUpdate(g);
		g.popAttribute();
		
		_myParticleRender.updateData(g);
	}
	
	public void display(CCGraphics g) {
		_myParticleRender.display(g);
	}
	
	public void staticPositionBlend(float theBlend){
		_myUpdateShader.staticPositionBlend(theBlend);
	}
	
	public CCParticleRenderer renderer() {
		return _myParticleRender;
	}
}

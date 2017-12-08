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
	private String _myRestLengthParameter;
	
	private String[] _myIDTextureParameters;
	private String[] _myInfoTextureParameters;

	private CCGLProgram _myInitValue01Shader;
	private CCGLProgram _myKillSpringShader;
	
	private CCShaderBuffer[] _myIDTextures;
	private CCShaderBuffer[] _myKilledIDTextures;
	private CCShaderBuffer[] _myInfoTextures;
	private int _myNumberOfSprings;
	private int _myNumberOfSpringBuffers;
	
	private double _mySpringConstant;
	private double _myRestLength;
	
	private int _myWidth;
	private int _myHeight;
	
	private int[] _mySprings;
	private int _mySpringsSize;
	private double[][] _myRestLengths;
	private boolean[][] _myForceRestLengths;
	private List<Integer> _myChangedSprings = new ArrayList<Integer>();

	protected double _myCurrentTime = 0;
	
	@CCProperty(name = "spring constant")
	private double _cSpringConstant;
	@CCProperty(name = "rest length")
	private double _cRestLength;
	
	private CCParticles _myParticles;
	
	/**
	 * Creates a new spring force with the given 
	 * spring constant and the given rest length. The number of springs that can be attached to a particle
	 * is set to 4.
	 * 
	 * @param g reference to the graphics object
	 * @param theSpringConstant spring constant defining the strength of a spring
	 * @param theRestLength rest length defining the space between two particles
	 */
	public CCSpringForce(final double theSpringConstant, final double theRestLength) {
		this(4, theSpringConstant, theRestLength);
	}
	
	/**
	 * Creates a new spring force with the given number of springs per particle, the given 
	 * spring constant and the given rest length.
	 * @param g reference to the graphics object
	 * @param theNumberOfSprings number of springs attached to a particle
	 * @param theSpringConstant spring constant defining the strength of a spring
	 * @param theRestLength rest length defining the space between two particles
	 */
	public CCSpringForce(final int theNumberOfSprings, final double theSpringConstant, final double theRestLength) {
		super("Springs");
		
		_myKillSpringShader = new CCGLProgram(null, CCNIOUtil.classPath(this,"spring_kill.glsl"));
		
		_mySpringConstant = theSpringConstant;
		_myRestLength = theRestLength;
		
		_myNumberOfSpringBuffers = CCMath.ceil(theNumberOfSprings / 2f);
		_myIDTextures = new CCShaderBuffer[_myNumberOfSpringBuffers];
		_myKilledIDTextures = new CCShaderBuffer[_myNumberOfSpringBuffers];
		_myInfoTextures = new CCShaderBuffer[_myNumberOfSpringBuffers];
		
		_mySpringConstantParameter = parameter("springConstant");
		_myRestLengthParameter = parameter("restLength");
		
		_myIDTextureParameters = new String[_myNumberOfSpringBuffers];
		_myInfoTextureParameters = new String[_myNumberOfSpringBuffers];
		
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			_myIDTextureParameters[i] = parameter("idTextures_"+i);
			_myInfoTextureParameters[i] = parameter("infoTextures_"+i);
		}
		
		springConstant(_mySpringConstant);
		restLength(_myRestLength);
		
	}
	
	@Override
	public List<String> loadSource() {
		List<String> myResult = new ArrayList<>();
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			myResult.add("uniform sampler2DRect idTextures_" + i + ";");
		}
		myResult.add("");
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			myResult.add("uniform sampler2DRect infoTextures_" + i + ";");
		}
		myResult.add("");
		myResult.add("uniform float strength;");
		myResult.add("uniform float index;");
		myResult.add("");
		myResult.add("uniform float springConstant;");
		myResult.add("");
		myResult.add("vec3 function(vec3 thePosition, vec3 theVelocity, vec2 theTexID, float theDeltaTime){");
		myResult.add("	");
		myResult.add("	vec3 force = vec3(0);");
		myResult.add("	vec4 ids;");
		myResult.add("	vec3 position1;");
		myResult.add("	vec3 position2;");
		myResult.add("	vec4 infos;");
		myResult.add("	float restLength1;");
		myResult.add("	float restLength2;");
		myResult.add("	float forceRestLength1;");
		myResult.add("	float forceRestLength2;");
		myResult.add("	");
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			myResult.add("	ids = texture2DRect(idTextures_" + i + ", theTexID);");
			myResult.add("		");
			myResult.add("	// get positions of neighbouring particles");
			myResult.add("	position1 = texture2DRect(positionTexture, ids.xy).xyz;");
			myResult.add("	position2 = texture2DRect(positionTexture, ids.zw).xyz;");
			myResult.add("			");
			myResult.add("	infos = texture2DRect(infoTextures_" + i + ", theTexID);");
			myResult.add("	restLength1 = infos.x;");
			myResult.add("	restLength2 = infos.y;");
			myResult.add("	forceRestLength1 = infos.z;");
			myResult.add("	forceRestLength2 = infos.w;");
			myResult.add("		");
			myResult.add("	force += springForce(thePosition, position1, restLength1, forceRestLength1, springConstant) * float(ids.x >= 0);");
			myResult.add("	force += springForce(thePosition, position2, restLength2, forceRestLength2, springConstant) * float(ids.z >= 0);");
			myResult.add("	");
		}
		myResult.add("	return force * lifeTimeBlend(theTexID, index) * strength;// * targetStrength * strength;// / (theDeltaTime * 60);");
		myResult.add("}");
		// TODO Auto-generated method stub
		return myResult;
	}

	private void resetTextures(CCGraphics g) {
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			CCShaderBuffer myIDTexture = _myIDTextures[i];
			myIDTexture.beginDraw(g);
			_myInitValue01Shader.start();
			g.beginShape(CCDrawMode.QUADS);
			g.textureCoords4D(0, -1f, -1f, -1f, -1f);
			g.vertex(0,0);
			g.vertex(_myWidth,0);
			g.vertex(_myWidth,_myHeight);
			g.vertex(0,_myHeight);
			g.endShape();
			_myInitValue01Shader.end();
			myIDTexture.endDraw(g);
			
			CCShaderBuffer myInfoTexture = _myInfoTextures[i];
			myInfoTexture.beginDraw(g);
			_myInitValue01Shader.start();
			g.beginShape(CCDrawMode.QUADS);
			g.textureCoords4D(0, 0f, 0f, 0f, 0f);
			g.vertex(0,0);
			g.vertex(_myWidth,0);
			g.vertex(_myWidth,_myHeight);
			g.vertex(0,_myHeight);
			g.endShape();
			_myInitValue01Shader.end();
			myInfoTexture.endDraw(g);
		}
	}

	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myInitValue01Shader = new CCGLWriteDataShader();
		
		_mySprings = new int[_myWidth * _myHeight * _myNumberOfSpringBuffers * 2];
		_mySpringsSize = _myWidth * _myHeight;
		_myRestLengths = new double[_myWidth * _myHeight][_myNumberOfSpringBuffers * 2];
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
			_myIDTextures[i].clear(g);
			
			_myKilledIDTextures[i] = new CCShaderBuffer(16, 4, theWidth, theHeight);
			_myKilledIDTextures[i].clear(g);

			_myInfoTextures[i] = new CCShaderBuffer(16, 4, theWidth, theHeight);
			_myInfoTextures[i].clear(g);
		}
		
		resetTextures(g);
		
		
		
		g.noBlend();
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			_myShader.setTextureUniform("idTextures_"+i, _myIDTextures[i].attachment(0));
			_myShader.setTextureUniform("infoTextures_"+i,_myInfoTextures[i].attachment(0));
		}
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_mySpringConstantParameter, _cSpringConstant);
		_myShader.uniform1f(_myRestLengthParameter, _cRestLength);
	}
	
	@Override
	public void setParticles(CCParticles theParticles) {
		super.setParticles(theParticles);
		_myParticles = theParticles;
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
	public boolean hasFreeSpringIndex(final CCParticle theParticle) {
		return getFreeSpringIndex(theParticle) >= 0;
	}
	
	private int getFreeSpringIndex(final CCParticle theParticle) {
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
	public boolean addSpring(final CCParticle theA, final CCParticle theB, final double theRestLength, boolean theForceRestLength) {
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
	
	public boolean addSpring(final CCParticle theA, final CCParticle theB, final double theRestLength) {
		return addSpring(theA, theB, theRestLength, true);
	}
	
	public boolean addSpring(final CCParticle theParticleA, final CCParticle theParticleB) {
		return addSpring(theParticleA, theParticleB, _myRestLength);
	}
	
	public boolean addOneWaySpring(final CCParticle theA, final CCParticle theB, final double theRestLength, boolean theForceRestLength) {
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
	
	public boolean addOneWaySpring(final CCParticle theA, final CCParticle theB, final double theRestLength) {
		return addOneWaySpring(theA, theB, theRestLength, true);
	}
	
	public boolean addOneWaySpring(final CCParticle theA, final CCParticle theB) {
		return addOneWaySpring(theA, theB, _myRestLength, true);
	}
	
	public void springConstant(final double theSpringConstant) {
		_cSpringConstant = theSpringConstant;
	}
	
	public void restLength(final double theRestLength) {
		_cRestLength = theRestLength;
	}

	public void update(final CCAnimator theAnimator) {
		_myCurrentTime += theAnimator.fixedUpdateTime;
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		g.noBlend();
		
		for(int i = 0; i < _myNumberOfSpringBuffers;i++) {
			CCShaderBuffer myIDTexture = _myIDTextures[i];
			myIDTexture.beginDraw(g);
			_myInitValue01Shader.start();
			g.beginShape(CCDrawMode.POINTS);
			
			for(int myID:_myChangedSprings) {
				int myIndex1 = _mySprings[myID + i * 2  * _mySpringsSize];

				double myX1 = myIndex1 == -1 ? -1 : myIndex1 % _myWidth;
				double myY1 = myIndex1 == -1 ? -1 : myIndex1 / _myWidth;
				
				int myIndex2 = _mySprings[myID + (i * 2 + 1)  * _mySpringsSize];
				double myX2 = myIndex2 == -1 ? -1 : myIndex2 % _myWidth;
				double myY2 = myIndex2 == -1 ? -1 : myIndex2 / _myWidth;
				
				g.textureCoords4D(0, myX1, myY1, myX2, myY2);
				g.vertex(myID % _myWidth, myID / _myWidth);
			}
			
			g.endShape();
			_myInitValue01Shader.end();
			myIDTexture.endDraw(g);
			
			CCShaderBuffer myKilledIDTexture = _myKilledIDTextures[i];
			_myKillSpringShader.start();
			g.texture(1, myIDTexture.attachment(0));
			g.texture(0, _myParticles.dataBuffer().attachment(1));
			myKilledIDTexture.draw(g);
			g.noTexture();
			_myKillSpringShader.end();
			
			CCShaderBuffer myInfoTexture = _myInfoTextures[i];
			myInfoTexture.beginDraw(g);
			_myInitValue01Shader.start();
			g.beginShape(CCDrawMode.POINTS);
			
			for(int myID:_myChangedSprings) {
				double myRestLength1 = _myRestLengths[myID][i*2];
				double myRestLength2 = _myRestLengths[myID][i*2 + 1];
				
				double myForceRestLength1 = _myForceRestLengths[myID][i*2] ? 1 : 0;
				double myForceRestLength2 = _myForceRestLengths[myID][i*2] ? 1 : 0;
				
				g.textureCoords4D(0, myRestLength1, myRestLength2, myForceRestLength1, myForceRestLength2);
				g.vertex(myID % _myWidth, myID / _myWidth);
			}
			g.endShape();
			_myInitValue01Shader.end();
			myInfoTexture.endDraw(g);
		}
		
		_myChangedSprings.clear();
	}
	
	public CCShaderBuffer infoTexture(int theIndex) {
		return _myInfoTextures[theIndex]; 
	}
	
	public int[] springIndices() {
		return _mySprings;
	}
	
	@Override
	public void reset(CCGraphics g) {
		resetTextures(g);
		
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

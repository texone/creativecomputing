package cc.creativecomputing.simulation.particles.constraints;

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

public class CCPositionConstraint  extends CCConstraint{

	private String _myStiffnessParameter;
	private String _myNumberOfBufferParameter;
	private String _myXBufferParameter;
	private String _myTextureSizeParameter;
	private String _myInfoTextureParameter;

	private CCGLProgram _myInitValueShader;
	private CCGLProgram _myKillJointShader;
	
	private CCShaderBuffer _myBuffer;
	private CCShaderBuffer _myTMPBuffer;
	
	private CCTexture2D _myParticleInfos;
	
	private int _myNumberOfJoints;
	private int _myNumberOfJointBuffers;
	private int _myNumberOfXJointBuffers;
	
	@CCProperty(name = "stiffness", min = 0, max = 1)
	private double _cStiffness = 0.;
	
	private int _myWidth;
	private int _myHeight;
	
	private int[] _myIndexMap;
	private int _myJointsSize;
	
	private static class CCJointInfo{
		CCParticle particle;
		CCParticle target;
		double restLength;
		int buffer;
		boolean draw;
		
		CCJointInfo(CCParticle theParticle, CCParticle theTarget, double theRestLength, int theBuffer, boolean theDraw){
			particle = theParticle;
			target = theTarget;
			restLength = theRestLength;
			buffer = theBuffer;
			draw = theDraw;
		}
		
	}
	private List<CCJointInfo> _myChangedJoints = new ArrayList<>();

	protected double _myCurrentTime = 0;


	
	/**
	 * Creates a new joint force with the given 
	 * joint constant and the given rest length. The number of joints that can be attached to a particle
	 * is set to 4.
	 * 
	 * @param g reference to the graphics object
	 * @param theJointConstant joint constant defining the strength of a joint
	 * @param theRestLength rest length defining the space between two particles
	 */
	public CCPositionConstraint() {
		this(4);
	}
	/**
	 * Creates a new joint force with the given number of joints per particle, the given 
	 * joint constant and the given rest length.
	 * @param g reference to the graphics object
	 * @param theNumberOfJoints number of joints attached to a particle
	 */
	public CCPositionConstraint(final int theNumberOfJoints) {
		super("positionConstraint");
		
		_myNumberOfJointBuffers = theNumberOfJoints;

		_myStiffnessParameter = parameter("stiffness");
		_myInfoTextureParameter = parameter("data");
		
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
		
		_myNumberOfXJointBuffers = CCMath.min(4000 / theWidth, _myNumberOfJointBuffers);
		int myYSpingBuffers = CCMath.ceil(_myNumberOfJointBuffers / (double)_myNumberOfXJointBuffers);
		_myJointsSize = _myWidth * _myHeight;
		_myIndexMap = new int[_myJointsSize];
		for(int i = 0; i < _myJointsSize;i++) {
			for(int j = 0; j < _myNumberOfJointBuffers;j++) {
				_myIndexMap[i] = 0;
			}
		}

		_myInitValueShader = new CCGLWriteDataShader();
		
		_myKillJointShader = new CCGLProgram(null, CCNIOUtil.classPath(this,"joint_kill.glsl"));
		
		_myBuffer = new CCShaderBuffer(16, 4, _myWidth * _myNumberOfXJointBuffers, _myHeight * myYSpingBuffers);
		_myBuffer.clear(g);
			
		_myTMPBuffer = new CCShaderBuffer(16, 4, _myWidth * _myNumberOfXJointBuffers, _myHeight * myYSpingBuffers);
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
		_myShader.uniform1f(_myStiffnessParameter, _cStiffness);
		_myShader.uniform1i(_myNumberOfBufferParameter, _myNumberOfJointBuffers);
		_myShader.uniform1i(_myXBufferParameter, _myNumberOfXJointBuffers);
		_myShader.uniform2f(_myTextureSizeParameter, _myWidth, _myHeight);
	}
	
	/**
	 * Returns the number of joints that can be attached to a particle
	 * @return number of joints per particle
	 */
	public int numberOfJointsPerParticle(){
		return _myNumberOfJoints;
	}
	
	/**
	 * Number of buffers that save joint data
	 * @return Number of buffers that save joint data
	 */
	public int numberOfJointTextures(){
		return _myNumberOfJointBuffers;
	}
	
	public int numberOfXBuffers(){
		return _myNumberOfXJointBuffers;
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
	public boolean hasFreeJointIndex(final CCParticle theParticle) {
		return getFreeJointIndex(theParticle) >= 0;
	}
	
	private int getFreeJointIndex(final CCParticle theParticle) {
		return _myIndexMap[theParticle.index()]++ % _myNumberOfJointBuffers;
	}
	
	/**
	 * <p>
	 * Creates a joint force between the particles with the given index. Be aware that you can not create
	 * endless joints. The number of joints per particle is dependent on the number of jointtextures that
	 * store the data. So in the constructor of the gpu joints you can define how many joints you want to 
	 * attach per particle.
	 * </p>
	 * <p>
	 * A joint can work in two different ways. The particles can be force to keep the defined rest length this
	 * means they will be pulled together if they exceed the rest length and pushed away from each other if they get
	 * closer than the given rest length. This is the case if the <code>forceRestLength</code> parameter is set
	 * <code>true</code>. If you do not force the rest length particles are allowed to to get closer than the given 
	 * rest length.
	 * </p>
	 * @param theA index of the particle a
	 * @param theB index of the particle b
	 * @param theRestLength rest length of the joint to create
	 * @param theForceRestLength if this is <code>true</code> particles are forced to keep the rest length
	 * @return <code>true</code> if the joint could be created otherwise <code>false</code>
	 */
	public boolean addJoint(final CCParticle theA, final CCParticle theB, final double theRestLength) {
		if(theA == null || theB == null)return false;
		
		int myIndexA = getFreeJointIndex(theA);
		int myIndexB = getFreeJointIndex(theB);
			
		_myChangedJoints.add(new CCJointInfo(theA, theB, theRestLength, myIndexA, true));
		_myChangedJoints.add(new CCJointInfo(theB, theA, theRestLength, myIndexB, false));
		return true;
		
	}
	
	public boolean addJoint(final CCParticle theParticleA, final CCParticle theParticleB) {
		return addJoint(theParticleA, theParticleB, theParticleA.position().distance(theParticleB.position()));
	}
	
	public boolean addOneWayJoint(final CCParticle theA, final CCParticle theB, final double theRestLength) {
		if(theA == null || theB == null)return false;
		
		int myIndexA = getFreeJointIndex(theA);
		
		_myChangedJoints.add(new CCJointInfo(theA, theB, theRestLength, myIndexA, true));
		return true;
	}
	public void update(final CCAnimator theAnimator) {

		_myCurrentTime += theAnimator.deltaTime();
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		g.noBlend();
		g.texture(1, _myBuffer.attachment(0));
		g.texture(0, _myParticleInfos);
		_myKillJointShader.start();
		_myKillJointShader.uniform1i("infoTexture", 0);
		_myKillJointShader.uniform1i("jointIDs", 1);
		_myKillJointShader.uniform2f("dimension", _myWidth, _myHeight);
		_myTMPBuffer.draw(g);
		_myKillJointShader.end();
		g.noTexture();
		
		
		CCShaderBuffer mySwap = _myBuffer;
		_myBuffer = _myTMPBuffer;
		_myTMPBuffer = mySwap;
		
		_myBuffer.beginDraw(g);
		_myInitValueShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(CCJointInfo myInfo:_myChangedJoints) {
			int myXOffset = myInfo.buffer % _myNumberOfXJointBuffers;
			int myYOffset = myInfo.buffer / _myNumberOfXJointBuffers;

			double myX = myInfo.target.index() == -1 ? -1 : myInfo.target.index() % _myWidth;
			double myY = myInfo.target.index() == -1 ? -1 : myInfo.target.index() / _myWidth;
				
			g.textureCoords4D(0, myX, myY, myInfo.restLength, myInfo.draw ? 1 : 0);
			g.vertex(myInfo.particle.index() % _myWidth + myXOffset * _myWidth, myInfo.particle.index() / _myWidth + myYOffset * _myHeight);
//			CCLog.info(myInfo.particle.index() % _myWidth + myXOffset * _myWidth, myInfo.particle.index() / _myWidth + myYOffset * _myHeight);
		}
			
		g.endShape();
		_myInitValueShader.end();
		_myBuffer.endDraw(g);
			
		

		_myChangedJoints.clear();
	}
	
	public CCShaderBuffer infoTexture() {
		return _myBuffer; 
	}
	
	public void reset(CCGraphics g) {
		resetTextures(g);
		
		for(int i = 0; i < _myJointsSize;i++) {
			_myIndexMap[i] = 0;
		}
		
	}

}

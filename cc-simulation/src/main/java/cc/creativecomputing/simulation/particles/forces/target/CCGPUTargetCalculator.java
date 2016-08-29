package cc.creativecomputing.simulation.particles.forces.target;

import com.jogamp.opengl.cg.CGparameter;

import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * Assigns new targets to particles, e.g. image based etc
 * @author maxg
 */
public class CCGPUTargetCalculator {

	private CCShaderBuffer _myShaderBuffer;
	private CCParticles _myParticles;
	private CCTexture2D    _myInputTexture;
	
	private CCCGShader  _myTargetCalculateShader;
	private CGparameter _myTargetTextureParameter;
	private CGparameter _myPositionTextureParameter;
	private CGparameter _myVelocityTextureParameter;
	private CGparameter _myTextureWidthParameter1;
	private CGparameter _myTextureHeightParameter1;
	private CGparameter _myRandomSeedParameter;
	
	public CCGPUTargetCalculator (CCParticles theParticles, CCShaderBuffer theShaderBuffer, CCTexture2D theInputTexture) {
		_myShaderBuffer = theShaderBuffer;
		_myParticles = theParticles;
		_myInputTexture = theInputTexture;
		
		_myTargetCalculateShader = new CCCGShader(null, CCIOUtil.classPath(CCGPUTargetCalculator.class, "shader/shader_target_calculation.fp"));
		_myPositionTextureParameter = _myTargetCalculateShader.fragmentParameter("positionTexture");
		_myVelocityTextureParameter = _myTargetCalculateShader.fragmentParameter("velocityTexture");
		_myTargetTextureParameter   = _myTargetCalculateShader.fragmentParameter("targetTexture");
		_myRandomSeedParameter      = _myTargetCalculateShader.fragmentParameter("randomSeed");
		_myTextureWidthParameter1   = _myTargetCalculateShader.fragmentParameter("width");
		_myTextureHeightParameter1  = _myTargetCalculateShader.fragmentParameter("height");
		
		_myTargetCalculateShader.load();
	}
	
	public void update (float theDeltaTime) {
		_myShaderBuffer.clear();
		_myTargetCalculateShader.start();
		_myTargetCalculateShader.texture   (_myPositionTextureParameter, _myParticles.dataBuffer().attachment(0).id());
		_myTargetCalculateShader.texture   (_myVelocityTextureParameter, _myParticles.dataBuffer().attachment(1).id());
		_myTargetCalculateShader.texture   (_myTargetTextureParameter,   _myInputTexture.id());
		_myTargetCalculateShader.parameter (_myTextureWidthParameter1,   _myInputTexture.width());
		_myTargetCalculateShader.parameter (_myTextureHeightParameter1,  _myInputTexture.height());
		_myTargetCalculateShader.parameter (_myRandomSeedParameter, CCMath.random(100f), CCMath.random(100f),  CCMath.random(3000,10000));
		_myShaderBuffer.draw();
		_myTargetCalculateShader.end();
	}
}

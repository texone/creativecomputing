
package cc.creativecomputing.simulation.particles;
import com.jogamp.opengl.cg.CGparameter;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCGPUParticleEmitter;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * The input is a 2D vector field. For each point in the input space the absolute value is calculated and a particle emitted with a probability proportional to that value.
 * An emitted particle has the velocity of the input vector field.
 *
 * @author max goettner
 * @demo 
 */

public class CCGPUParticleSuperEmitter implements CCGPUParticleEmitter{
	
	@CCControl(name = "velocity factor", min = -1f, max = 1f, external=true)
	public float _cVelocityFactor = 1f;
	
	@CCControl(name = "lifetime factor", min = 0f, max = 1f, external=true)
	public float _cLifetimeFactor = 1f;	
	
	@CCControl(name = "brightness factor", min = 0f, max = 2f, external=true)
	public float _cBrightnessFactor = 1f;	
	
	@CCControl(name = "probability", min = 0f, external=true)
	public float _cProb = 0f;
	
	@CCControl(name = "initial speed", min = 0f)
	public float _cSpeed = 0f;
	
	@CCControl(name = "particle lifetime", min = 0f, max = 15f, external=true)
	public float _cLifetime = 1f;	
	
	@CCControl(name = "particle lifetime variance", min = 0f, max = 5f, external=true)
	public float _cLifetimeVariance = 1f;	

	@CCControl(name = "invert")
	public boolean _cInvert;

	@CCControl(name = "emit density threshold", external=true)
	public float _cDensityThreshold;
	
	private CCCGShader  _myEmitShader;
	private CCCGShader  _myDestroyShader;
	private CCCGShader  _myThruShader;
	
	private CCTexture2D _myEmitTexture1;
	private CCTexture2D _myEmitTexture2;
	
	private CCTexture2D _myDestroyTexture;
	
	// thru
	protected CGparameter _myVelocityTextureParameter;
	protected CGparameter _myPositionTextureParameter;
	protected CGparameter _myInfoTextureParameter;
	protected CGparameter _myColorTextureParameter;
	
	// emitter
	protected CGparameter _myVelocityTextureParameter1;
	protected CGparameter _myPositionTextureParameter1;
	protected CGparameter _myInfoTextureParameter1;
	protected CGparameter _myColorTextureParameter1;
	protected CGparameter _myVelocityFactorParameter1;
	protected CGparameter _myEmitPropabilityParameter1;
	protected CGparameter _myParticleSpeedParameter1;
	protected CGparameter _myParticleLifetimeParameter1;
	protected CGparameter _myEmitTextureParameter1_1;
	protected CGparameter _myEmitTextureParameter1_2;
	protected CGparameter _myEmitDensityThresholdParameter1;
	protected CGparameter _myRandomSeedParameter1;
	protected CGparameter _myTextureWidthParameter1;
	protected CGparameter _myTextureHeightParameter1;
	
	// destructor
	protected CGparameter _myLifetimeFactorParameter2;
	protected CGparameter _myBrightnessFactorParameter2;
	protected CGparameter _myRandomSeedParameter2;
	protected CGparameter _myDestroyTextureParameter2;
	protected CGparameter _myVelocityTextureParameter2;
	protected CGparameter _myPositionTextureParameter2;
	protected CGparameter _myInfoTextureParameter2;
	protected CGparameter _myVelocityFactorParameter2;
	protected CGparameter _myColorTextureParameter2;

	

	private CCParticles _myParticles;
	private CCVBOMesh      _myParticleTable;
	
	public CCGPUParticleSuperEmitter(CCParticles theParticles, int theStartID, int theEndID) {
		
		_myParticles     = theParticles;
		
		//thru shader
		_myThruShader = new CCCGShader(null, CCIOUtil.classPath(CCGPUParticleSuperEmitter.class, "shader/emit/shader_thru.fp"));
		_myPositionTextureParameter  = _myThruShader.fragmentParameter("positionTexture");
		_myInfoTextureParameter      = _myThruShader.fragmentParameter("infoTexture");
		_myVelocityTextureParameter  = _myThruShader.fragmentParameter("velocityTexture");
		_myColorTextureParameter     = _myThruShader.fragmentParameter("colorTexture");
		_myThruShader.load();
		
		
		// emit shader
		_myEmitShader = new CCCGShader(null, CCIOUtil.classPath(CCGPUParticleSuperEmitter.class, "shader/emit/shader_shape_emit.fp"));
		_myPositionTextureParameter1  = _myEmitShader.fragmentParameter("positionTexture");
		_myInfoTextureParameter1      = _myEmitShader.fragmentParameter("infoTexture");
		_myVelocityTextureParameter1  = _myEmitShader.fragmentParameter("velocityTexture");
		_myColorTextureParameter1     = _myEmitShader.fragmentParameter("colorTexture");
		_myRandomSeedParameter1       = _myEmitShader.fragmentParameter("randomSeed");
		_myEmitPropabilityParameter1  = _myEmitShader.fragmentParameter("emitProb");
		_myParticleSpeedParameter1    = _myEmitShader.fragmentParameter("emitSpeed");
		_myParticleLifetimeParameter1 = _myEmitShader.fragmentParameter("emitLifetime"); 		
		_myEmitTextureParameter1_1     = _myEmitShader.fragmentParameter("emitTexture1");
		_myEmitTextureParameter1_2     = _myEmitShader.fragmentParameter("emitTexture2");
		_myEmitDensityThresholdParameter1 = _myEmitShader.fragmentParameter("densityThreshold");
		_myTextureWidthParameter1   = _myEmitShader.fragmentParameter("width");
		_myTextureHeightParameter1  = _myEmitShader.fragmentParameter("height");
		
		_myEmitShader.load();
		
		
		// destroy
		_myDestroyShader = new CCCGShader(null, CCIOUtil.classPath(CCGPUParticleSuperEmitter.class, "shader/emit/shader_shape_destroy.fp"));
		_myPositionTextureParameter2  = _myDestroyShader.fragmentParameter("positionTexture");
		_myInfoTextureParameter2      = _myDestroyShader.fragmentParameter("infoTexture");
		_myVelocityTextureParameter2  = _myDestroyShader.fragmentParameter("velocityTexture");
		_myColorTextureParameter2	  = _myDestroyShader.fragmentParameter("colorTexture");
		_myRandomSeedParameter2       = _myDestroyShader.fragmentParameter("randomSeed");
		_myVelocityFactorParameter2   = _myDestroyShader.fragmentParameter("velocityFactor");
		_myLifetimeFactorParameter2   = _myDestroyShader.fragmentParameter("lifetimeFactor");	
		_myBrightnessFactorParameter2 = _myDestroyShader.fragmentParameter("brightnessFactor");	
		_myDestroyTextureParameter2   = _myDestroyShader.fragmentParameter("destroyTexture");
		_myDestroyShader.load();
		
		
		_myParticleTable = new CCVBOMesh(CCDrawMode.POINTS, theEndID - theStartID);
		for(int i = theStartID; i < theEndID;i++){
			float x = i % _myParticles.width();
			float y = i / _myParticles.width();
			_myParticleTable.addVertex(x + 0.5f,y + 0.5f);
		}
	}
	
	public void lifetimeFactor (float theLifetimeFactor){
		_myDestroyShader.parameter(_myLifetimeFactorParameter2, theLifetimeFactor);
	}
	public void velocityFactor (float theVelocityFactor) {
		_myDestroyShader.parameter(_myVelocityFactorParameter2, theVelocityFactor);
	}
	public void brightnessFactor (float theBrightnessFactor) {
		_myDestroyShader.parameter(_myBrightnessFactorParameter2, theBrightnessFactor);
	}
	public void emitPropability (float theEmitPropability){
		_myEmitShader.parameter(_myEmitPropabilityParameter1, theEmitPropability);
	}
	public void emitSpeed (float theSpeed) {
		_myEmitShader.parameter(_myParticleSpeedParameter1, theSpeed);
	}
	public void emitLifetime (float theLifetime) {
		_myEmitShader.parameter(_myParticleLifetimeParameter1, theLifetime);
	}
	public void densityThreshold (float theDensityThreshold) {
		_myEmitShader.parameter(_myEmitDensityThresholdParameter1, theDensityThreshold);
	}
	
	
	@Override
	public void setData(CCGraphics g) {
		
		_myParticles.destinationDataTexture().beginDraw();
		g.clear();
		_myParticles.destinationDataTexture().endDraw();

		_myThruShader.start();
		_myThruShader.texture   (_myPositionTextureParameter, _myParticles.dataBuffer().attachment(0).id());
		_myThruShader.texture   (_myInfoTextureParameter,     _myParticles.dataBuffer().attachment(1).id());
		_myThruShader.texture   (_myVelocityTextureParameter, _myParticles.dataBuffer().attachment(2).id());
		_myThruShader.texture   (_myColorTextureParameter,    _myParticles.dataBuffer().attachment(3).id());
		_myParticles.destinationDataTexture().draw();
		_myThruShader.end();
		
		if (_myEmitTexture1!=null) {
			_myEmitShader.start();
			_myEmitShader.texture   (_myPositionTextureParameter1, _myParticles.dataBuffer().attachment(0).id());
			_myEmitShader.texture   (_myInfoTextureParameter1,     _myParticles.dataBuffer().attachment(1).id());
			_myEmitShader.texture   (_myVelocityTextureParameter1, _myParticles.dataBuffer().attachment(2).id());
			_myEmitShader.texture	(_myEmitTextureParameter1_1,     _myEmitTexture1.id());
			_myEmitShader.texture	(_myEmitTextureParameter1_2,     _myEmitTexture2.id());
			_myEmitShader.parameter (_myRandomSeedParameter1, CCMath.random(100f), CCMath.random(100f),  CCMath.random(3000,10000));
			_myEmitShader.parameter (_myTextureWidthParameter1, _myEmitTexture1.width());
			_myEmitShader.parameter (_myTextureHeightParameter1, _myEmitTexture1.height());
			_myParticles.destinationDataTexture().beginDraw();
			_myParticleTable.draw(g);
			_myParticles.destinationDataTexture().endDraw();
			_myEmitShader.end();
		}
		
		if (_myDestroyTexture != null) {
			_myDestroyShader.start();
			_myDestroyShader.texture   (_myPositionTextureParameter2, _myParticles.destinationDataTexture().attachment(0).id());
			_myDestroyShader.texture   (_myInfoTextureParameter2,     _myParticles.destinationDataTexture().attachment(1).id());
			_myDestroyShader.texture   (_myVelocityTextureParameter2, _myParticles.destinationDataTexture().attachment(2).id());
			_myDestroyShader.texture   (_myColorTextureParameter2,    _myParticles.destinationDataTexture().attachment(3).id());
			_myDestroyShader.texture   (_myDestroyTextureParameter2,  _myDestroyTexture.id());
			_myDestroyShader.parameter (_myRandomSeedParameter2, CCMath.random(100f), CCMath.random(100f),  CCMath.random(3000,10000));
			_myParticles.destinationDataTexture().beginDraw();
			_myParticleTable.draw(g);
			_myParticles.destinationDataTexture().endDraw();
			_myDestroyShader.end();
		}
		_myParticles.swapDataTextures();
	}

	@Override
	public void reset() {
	}

	@Override
	public void update(float theDeltaTime) {
		lifetimeFactor   (_cLifetimeFactor);
		velocityFactor   (_cVelocityFactor);
		brightnessFactor (_cBrightnessFactor);
		emitPropability(_cProb);
		emitSpeed(_cSpeed);
		emitLifetime(CCMath.max (0, _cLifetime + CCMath.random (-_cLifetimeVariance, _cLifetimeVariance)));
		densityThreshold(_cDensityThreshold);
	}

	public void setDestroyTexture(CCTexture2D theDestroyTexture) {
		_myDestroyTexture = theDestroyTexture;
	}

	public void setEmitTextures (CCTexture2D theEmitTexture1, CCTexture2D theEmitTexture2) {
		_myEmitTexture1 = theEmitTexture1;
		_myEmitTexture2 = theEmitTexture2;
	}
}

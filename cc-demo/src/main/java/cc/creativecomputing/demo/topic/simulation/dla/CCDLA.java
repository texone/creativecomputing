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
package cc.creativecomputing.demo.topic.simulation.dla;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.imaging.CCGPUGaussianBlur;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCGLSwapBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * @author info
 * 
 */
public class CCDLA {

	@CCProperty(name = "particle replacement", min = 1, max = 20)
	private float _cParticleReplacement = 0;
	@CCProperty(name = "particle amount", min = 0, max = 1)
	private float _cParticleAmount = 0;

	@CCProperty(name = "particle speed", min = 10, max = 1000)
	private float _cParticleSpeed = 0;
	
	@CCProperty(name = "brightness increase", digits = 4)
	private double _cBrightnessIncrease = 0.1;

	private CCGraphics g;

	/* USED FOR PARTICLES */

	@CCProperty(name = "particle shader")
	private CCGLProgram _myParticleShader;
	
	@CCProperty(name = "particle output shader")
	private CCGLProgram _myParticleOutputShader;

	private CCTexture2D _myRandomTexture;

	private CCGLSwapBuffer _myParticleBuffer;

	@CCProperty(name = "init values")
	private CCGLProgram _myInitValueShader;

	private int _myParticlesSizeX;
	private int _myParticlesSizeY;

	/* USED FOR CRYSTALIZATION */

	@CCProperty(name = "crystal")
	private CCGLProgram _myCrystalShader;
	@CCProperty(name = "crystal draw")
	private CCGLProgram _myCrystalDrawShader;
	
	@CCProperty(name = "light x", min = -1, max = 1)
	private double _cLightX = 0;
	@CCProperty(name = "light y", min = -1, max = 1)
	private double _cLightY = 0;
	@CCProperty(name = "light z", min = -1, max = 1)
	private double _cLightZ = 0;
	
	@CCProperty(name = "specular pow", min = 0, max = 10)
	private double _cSpecularPow = 0;
	@CCProperty(name = "specular bright pow", min = 0, max = 10)
	private double _cSpecularBrightPow = 0;
	
	@CCProperty(name = "diffuse amp", min = 0, max = 1)
	private double _cDiffuseAmp = 0;
	@CCProperty(name = "specular amp", min = 0, max = 1)
	private double _cSpecularAmp = 0;
	@CCProperty(name = "specular bright amp", min = 0, max = 1)
	private double _cSpecularBrightAmp = 0;
	
	private CCGPUGaussianBlur _myConvolutionFilter;

	private CCShaderBuffer _myParticlesBuffer;
	private CCGLSwapBuffer _myCrystalSwapBuffer;
	private CCShaderBuffer _myCrystalBuffer;

	private int _myWidth;
	private int _myHeight;

	private CCVBOMesh _myParticlesMesh;

	public CCDLA(CCGraphics theGraphics, final int theParticlesSizeX, final int theParticlesSizeY, final int theWidth, final int theHeight) {
		g = theGraphics;
		_myParticlesSizeX = theParticlesSizeX;
		_myParticlesSizeY = theParticlesSizeY;

		_myWidth = theWidth;
		_myHeight = theHeight;
		

		CCLog.info(_myWidth + ":" + _myHeight);

		_myParticleShader = new CCGLProgram(
			null, 
			CCNIOUtil.classPath(this,"particles.glsl")
		);
		
		_myParticleOutputShader = new CCGLProgram(
			CCNIOUtil.classPath(this,"particle_output_vertex.glsl"),
			CCNIOUtil.classPath(this,"particle_output_fragment.glsl")
		);

		_myInitValueShader = new CCGLProgram(null, CCNIOUtil.classPath(this,"initvalue.glsl"));

		_myRandomTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this,"random.png")), CCTextureTarget.TEXTURE_2D);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);

		_myParticleBuffer = new CCGLSwapBuffer(g, 32, 3, _myParticlesSizeX, _myParticlesSizeY);

		_myParticlesMesh = new CCVBOMesh(CCDrawMode.POINTS, _myParticlesSizeX * _myParticlesSizeY);

		initializeParticles(g);

		_myCrystalShader = new CCGLProgram(null, CCNIOUtil.classPath(this,"crystal.glsl"));
		
		_myCrystalDrawShader = new CCGLProgram(null, CCNIOUtil.classPath(this,"crystal_draw_fragment.glsl"));

		_myParticlesBuffer = new CCShaderBuffer(32, 3, _myWidth, _myHeight);
		_myParticlesBuffer.clear(theGraphics);
		_myCrystalSwapBuffer = new CCGLSwapBuffer(g, 32, 4, 2, _myWidth, _myHeight);
		_myCrystalSwapBuffer.clear(g);
		_myCrystalBuffer = new CCShaderBuffer(32, 3, 2, _myWidth, _myHeight);
		_myCrystalBuffer.clear(theGraphics);
		 initializeCrystal(g);

		 _myConvolutionFilter = new CCGPUGaussianBlur(true,g,3);
		 _myConvolutionFilter.dimension(_myWidth, _myHeight);
	}

	private boolean _cCreateParticles = false;
	
	@CCProperty(name = "create particles")
	private void createParticles() {
		_cCreateParticles = true;
	}
	private void initializeParticles(CCGraphics g) {
		// Render velocity.
		_myParticleBuffer.beginDrawCurrent(g);
		_myInitValueShader.start();
		g.clear();
		g.beginShape(CCDrawMode.POINTS);
		
		for (int x = 0; x < _myParticlesSizeX; x++) {
			for (int y = 0; y < _myParticlesSizeY; y++) {
				g.textureCoords3D(0, CCMath.random(_myWidth * 2), CCMath.random(_myHeight * 2), CCMath.random());
				g.vertex(x, y);
			}
		}
		g.endShape();

		_myInitValueShader.end();
		_myParticleBuffer.endDrawCurrent(g);
	}

	public void beginCrystal(CCGraphics g) {
		_myCrystalSwapBuffer.beginDrawCurrent(g);
	}

	public void endCrystal(CCGraphics g) {
		_myCrystalSwapBuffer.endDrawCurrent(g);
	}

	private void initializeCrystal(CCGraphics g) {
		// Render velocity.
		_myCrystalSwapBuffer.beginDrawCurrent(g);
		// _myInitValueShader.start();

		g.beginShape(CCDrawMode.POINTS);
		
		for (int i = 0; i < 10; i++) {
			g.textureCoords3D(0, new CCVector3(1, 1, 1));
			g.vertex(CCMath.random(_myWidth), CCMath.random(_myHeight));
		}
		g.endShape();

		// _myInitValueShader.end();
		_myCrystalSwapBuffer.endDrawCurrent(g);
	}

	public void reset(CCGraphics g) {
		_myCrystalSwapBuffer.clear(g);
	}

	public void update(CCGraphics g, final CCAnimator theAnimator) {
		if(_cCreateParticles){
			_cCreateParticles = false;
			initializeParticles(g);
		}
		/* UPDATE PARTICLES */
		g.texture(0, _myRandomTexture);
		g.texture(1, _myParticleBuffer.attachment(0));
		g.texture(2, _myCrystalSwapBuffer.attachment(0));
		_myParticleShader.start();

		_myParticleShader.uniform1i("randomTexture", 0);
		_myParticleShader.uniform1i("positionTexture", 1);
		_myParticleShader.uniform1i("crystalTexture", 2);
		
		_myParticleShader.uniform2f("texOffset", new CCVector2((int) CCMath.random(500), (int) CCMath.random(500)));

		_myParticleShader.uniform1f("speed", _cParticleSpeed * theAnimator.deltaTime());
		_myParticleShader.uniform1f("amount", _cParticleAmount);
		_myParticleShader.uniform1f("replacement", _cParticleReplacement * theAnimator.deltaTime());
		_myParticleShader.uniform2f("boundary", new CCVector2(_myWidth, _myHeight));

		_myParticleBuffer.draw(g);
		_myParticleShader.end();
		g.noTexture();

		_myParticlesMesh.vertices(_myParticleBuffer.destinationBuffer());

		_myParticleBuffer.swap();

		/* UPDATE CRYSTAL */
		_myParticlesBuffer.beginDraw(g);
		g.clear();
		_myParticleOutputShader.start();
		_myParticleOutputShader.uniform1f("amount", _cParticleAmount);
		_myParticlesMesh.draw(g);
		_myParticleOutputShader.end();
		_myParticlesBuffer.endDraw(g);

		g.texture(0, _myParticlesBuffer.attachment(0));
		g.texture(1, _myCrystalSwapBuffer.attachment(0));
		g.texture(2, _myCrystalSwapBuffer.attachment(1));
		_myCrystalShader.start();

		_myCrystalShader.uniform1i("particleTexture", 0);
		_myCrystalShader.uniform1i("crystalTexture0", 1);
		_myCrystalShader.uniform1i("crystalTexture1", 2);
		_myCrystalShader.uniform1f("increase", _cBrightnessIncrease);
		
		
		g.blend(CCBlendMode.BLEND);
		_myCrystalSwapBuffer.draw(g);

		_myCrystalShader.end();
		g.noTexture();
		_myCrystalSwapBuffer.swap();
		
		g.texture(0, _myCrystalSwapBuffer.attachment(0));
		g.texture(1, _myCrystalSwapBuffer.attachment(1));
		_myCrystalDrawShader.start();
		_myCrystalDrawShader.uniform1i("colorTex", 0);
		_myCrystalDrawShader.uniform1i("brightTex", 1);

		_myCrystalDrawShader.uniform3f("lightDir", new CCVector3(_cLightX, _cLightY, _cLightZ).normalizeLocal());
		_myCrystalDrawShader.uniform1f("specularPow", _cSpecularPow);
		_myCrystalDrawShader.uniform1f("specularBrightPow", _cSpecularBrightPow);

		_myCrystalDrawShader.uniform1f("diffuseAmp", _cDiffuseAmp);
		_myCrystalDrawShader.uniform1f("specularAmp", _cSpecularAmp);
		_myCrystalDrawShader.uniform1f("specularBrightAmp", _cSpecularBrightAmp);
		
		_myCrystalBuffer.draw(g);
		_myCrystalDrawShader.end();
		g.noTexture();
		
	}
	
	@CCProperty(name = "drawParticles")
	private boolean _cDrawParticles = false;

	public void draw(CCGraphics g) {
		g.color(255);
		if(_cDrawParticles){
			CCLog.info("yes");
			g.image(_myParticlesBuffer.attachment(0), 0, 0);
		}else{

			_myConvolutionFilter.start();
			g.image(_myCrystalBuffer.attachment(0), 0, 0);
			_myConvolutionFilter.end();
		}
	}

	public CCTexture2D dlaTexture() {
		return _myCrystalSwapBuffer.attachment(0);
	}
}

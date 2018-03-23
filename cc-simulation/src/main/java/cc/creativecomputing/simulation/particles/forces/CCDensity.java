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
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.imaging.filter.CCSobelFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.render.CCParticlePointRenderer;

/**
 * This class calculates a density texture from a particles object, that can be used as force texture input to 
 * simulate forces between the particles.
 * 
 * @author max goettner
 * @demo cc.creativecomputing.gpu.particles.demo.CCParticlesThermodynamicModel TODO: ??
 */

public class CCDensity extends CCForce{
	
	
	@CCProperty(name = "point size", min = 1, max = 30)
	public int _cPointSize = 1;
		
	@CCProperty(name = "sprite shape")
	public CCDensitySpriteShape _cSpriteType = CCDensitySpriteShape.DISTANCE;
		
	@CCProperty(name = "energy density", min=0f, max=1f)
	public double _cEnergyDensity = 1f;
	
	
	public enum CCDensitySpriteShape {
		DISTANCE, SINUS, NONE
	}
	
	private CCParticles _myParticles;
	private CCParticlePointRenderer _myDefaultRenderer;
	private CCShaderBuffer _myDensityBuffer;
	private CCSobelFilter _mySobelFilter;	
	
	private CCImage _myPointSpriteImage;
	private CCTexture2D _myPointSprite;
	
	private double _myBrightness;
	private double _myFactor;
	
	private int _myWidth;
	private int _myHeight;
	
	private CCVector2 _myTextureScale;
	private CCVector2 _myTextureOffset;
	
	private String _myTextureParameter;
	private String _myTextureScaleParameter;
	private String _myTextureOffsetParameter;
	private String _myTextureSizeParameter;
	
	public CCDensity (int theWidth, int theHeight) {
		super("density");
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myDensityBuffer = new CCShaderBuffer (theWidth, theHeight);
//		_myDensityBuffer.clear();
		
		_mySobelFilter = new CCSobelFilter(_myDensityBuffer.attachment(0));
		
		updatePointSprite();
		
		_myTextureScale = new CCVector2(1,1);
		_myTextureOffset = new CCVector2(_myWidth / 2, _myHeight / 2);

		_myTextureParameter = parameter("forceTexture");
		_myTextureScaleParameter = parameter("textureScale");
		_myTextureOffsetParameter = parameter("textureOffset");
		_myTextureSizeParameter = parameter("textureSize");
	}
	
	public CCVector2 textureOffset(){
		return _myTextureOffset;
	}
	
	/**
	 *	Calc point sprite envelope and sum for later normalization. 
	 */
	private CCDensitySpriteShape _myLastSpriteType;
	
	private void updatePointSprite() {

		if(_cSpriteType == _myLastSpriteType)return;
			
		int spriteSize = 13;
		double mySum = 0;
		double myVal = 1f;
		_myPointSpriteImage = new CCImage (spriteSize,spriteSize);
		
		for (int i=0; i<spriteSize; i++) {
			for (int j=0; j<spriteSize; j++) {
				switch (_cSpriteType) {
				case SINUS:
					myVal = (CCMath.cos(CCMath.PI * CCMath.saturate(CCMath.dist(i, j, spriteSize / 2, spriteSize / 2) / (spriteSize * 0.5))) + 1) / 2;
					mySum += myVal;
					break;
				case DISTANCE:
					myVal = 1 - CCMath.saturate(CCMath.dist(i, j, spriteSize/2, spriteSize/2) / (spriteSize * 0.5));
					mySum += myVal;
					break;
				case NONE:
					mySum += myVal;
					break;
				default:
					break;
				}
				_myPointSpriteImage.setPixel(i, j, new CCColor(myVal));
			}
		}
		
		_myPointSprite = new CCTexture2D(_myPointSpriteImage);
		_myFactor = mySum / (spriteSize*spriteSize);
		_myLastSpriteType = _cSpriteType;
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myTextureParameter, _mySobelFilter.output());
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform2f(_myTextureScaleParameter, _myTextureScale);
		_myShader.uniform2f(_myTextureOffsetParameter, _myTextureOffset);
		_myShader.uniform2f(_myTextureSizeParameter, _myDensityBuffer.width(), _myDensityBuffer.height());
	}
	
	@Override
	public void setParticles(CCParticles theParticles) {
		super.setParticles(theParticles);
		_myParticles = theParticles;
		
		_myDefaultRenderer = new CCParticlePointRenderer();
		_myDefaultRenderer.setup(_myParticles);
	}
	
	public CCTexture2D density() {
		return _myDensityBuffer.attachment(0);
	}
	
	public CCTexture2D force() {
		return _mySobelFilter.output();
	}
	
	private boolean _myFirstUpdateFlag = true;
	
	@Override
	public void preDisplay(CCGraphics g) {
		if (_myFirstUpdateFlag) {
			_myFirstUpdateFlag = false;
			return;
		}
		
		updatePointSprite();
		
		_myDensityBuffer.beginDraw(g);

		g.pushAttribute();
		g.pushMatrix();
		g.scale(1,1,0);
		g.noDepthTest();
		
		g.color(255);
		g.image(_myPointSprite,0,0,100,100);
		g.translate(_myTextureOffset);
		
		_myBrightness = 10 *_cEnergyDensity / (_myFactor  * _cPointSize * _cPointSize);
		g.clear();
		
		g.pointSprite(_myPointSprite);
		g.pointSize(_cPointSize);
		
		g.color(_myBrightness);
		g.blend(CCBlendMode.ADD);

		_myParticles.renderer().display(g);
		g.noPointSprite();
		g.popMatrix();
		g.popAttribute();
		_myDensityBuffer.endDraw(g);
	
		_mySobelFilter.display(g);
	}
	
	public void update (CCAnimator theAnimator) {
		_myDefaultRenderer.update(theAnimator);
	}
}

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

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.render.CCGPUParticlePointRenderer;

/**
 * This class calculates a density texture from a particles object, that can be used as force texture input to 
 * simulate forces between the particles.
 * 
 * @author max goettner
 * @demo cc.creativecomputing.gpu.particles.demo.CCParticlesThermodynamicModel TODO: ??
 */

public class CCGPUParticlesDensity {
	
	
		@CCControl(name = "point size", min = 1, max = 30)
		public int _cPointSize = 1;
		
		@CCControl(name = "sprite shape")
		public CCDensitySpriteShape _cSpriteType = CCDensitySpriteShape.NONE;
		
		@CCControl(name = "energy density", min=0f, max=1f)
		public float _cEnergyDensity = 1f;
	
	
	public enum CCDensitySpriteShape {
		DISTANCE, SINUS, NONE
	}
	
	private CCParticles _myParticles;
	private CCGPUParticlePointRenderer _myDefaultRenderer;
	private CCShaderBuffer _myDensityBuffer;
	private CCTexture2D    _myPointSprite;
	private CCGraphics	   _myGraphics;
	
	private CCDensitySpriteShape   _mySpriteShape;
	private float 		   _myBrightness;
	private float		   _myFactor;
	
	private int _myWidth;
	private int _myHeight;
	
	public CCGPUParticlesDensity (CCParticles theParticles, CCGraphics theGraphics) {
		this(theParticles, theGraphics, CCDensitySpriteShape.DISTANCE, 1, theParticles.width(), theParticles.height());
	}
	
	public CCGPUParticlesDensity (CCParticles theParticles, CCGraphics theGraphics, int width, int height) {
		this(theParticles, theGraphics, CCDensitySpriteShape.DISTANCE, 1, width, height);
	}
	
	public CCGPUParticlesDensity (CCParticles theParticles, CCGraphics theGraphics, CCDensitySpriteShape theSpriteShape, int thePointSize, int width, int height) {
		_myParticles = theParticles;
		_myGraphics = theGraphics;
		
		_myWidth = width;
		_myHeight = height;
		
		_myDensityBuffer = new CCShaderBuffer (width, height);
		_myDensityBuffer.clear();
		
		_mySpriteShape = theSpriteShape;
		initPointSprite();
		
		_myDefaultRenderer = new CCGPUParticlePointRenderer();
		_myDefaultRenderer.setup(_myParticles);
	}
	
	
	public void initPointSprite() {
	/**
	 *	Calc point sprite envelope and sum for later normalization. 
	 */
		int spriteSize = 13;
		float sum = 0;
		float val = 1f;
		_myPointSprite = new CCTexture2D (spriteSize,spriteSize);
		
		for (int i=0; i<spriteSize; i++) {
			for (int j=0; j<spriteSize; j++) {
				switch (_mySpriteShape) {
				case SINUS:
					val = CCMath.cos(CCMath.PI*CCMath.dist(i, j, spriteSize/2, spriteSize/2)/spriteSize);
					sum += val;
					break;
				case DISTANCE:
					val = CCMath.dist(i, j, spriteSize/2, spriteSize/2);
					sum += val;
					break;
				case NONE:
					sum += val;
					break;
				default:
					break;
				}
				_myPointSprite.setPixel(i, j, new CCColor(val));
			}
		}
		
		_myFactor = sum / (spriteSize*spriteSize);
	}
	
	public CCTexture2D getDensity() {
		return _myDensityBuffer.attachment(0);
	}
	
	public void update (float theDeltaTime) {
		
		_myDefaultRenderer.update(theDeltaTime);
		
		_myGraphics.pushMatrix();
		_myGraphics.pushAttribute();
		_myGraphics.blend();
		_myBrightness = 10 *_cEnergyDensity / (_myFactor  * _cPointSize * _cPointSize);
		_myDensityBuffer.clear();
		
		_myGraphics.pointSprite(_myPointSprite);
		_myGraphics.pointSize(_cPointSize);
		
		_myGraphics.color(_myBrightness);
		_myGraphics.blendMode(CCBlendMode.ADD);
		_myDensityBuffer.beginDraw();
		
		_myGraphics.translate(_myWidth/2, _myHeight/2);
		
		_myDefaultRenderer.draw(_myGraphics);
		_myDensityBuffer.endDraw();
		_myGraphics.noPointSprite();
		_myGraphics.popMatrix();
		_myGraphics.popAttribute();
	}
}

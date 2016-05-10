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
package cc.creativecomputing.graphics.shader.imaging;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author info
 *
 */
public class CCGPUEdgeBlender {
	
	@CCProperty(name = "range", min = 0, max = 400)
	private int _cBlendRange = 0;
		
	@CCProperty(name = "power", min = 0.1f, max = 10)
	private float _cBlendPower = 0;
		
	@CCProperty(name = "break", min = 0f, max = 1f)
	private float _cBlendBreak = 0;
		
	@CCProperty(name = "red gamma", min = 1f, max = 3f)
	private float _cRedGamma = 0;
		
	@CCProperty(name = "green gamma", min = 1f, max = 3f)
	private float _cGreenGamma = 0;
		
	@CCProperty(name = "blue gamma", min = 1f, max = 3f)
	private float _cBlueGamma = 0;

//	private static class CCGPUEdgeBlendShader extends CCCGShader{
//		private CGparameter _myBlendStartParameter;
//		private CGparameter _myBlendEndParameter;
//		private CGparameter _myBlendPowerParameter;
//		private CGparameter _myBlendBreakParameter;
//		
//		private CGparameter _myRedGammaParameter;
//		private CGparameter _myGreenGammaParameter;
//		private CGparameter _myBlueGammaParameter;
//
//		/**
//		 * @param theG
//		 * @param theVertexShaderFile
//		 * @param theFragmentShaderFile
//		 */
//		public CCGPUEdgeBlendShader(CCGraphics theG, final String theFile) {
//			super(null, CCNIOUtil.classPath(CCGPUEdgeBlender.class,theFile));
//			_myBlendStartParameter = fragmentParameter("blendStart");
//			_myBlendEndParameter = fragmentParameter("blendEnd");
//			_myBlendPowerParameter = fragmentParameter("blendPower");
//			_myBlendBreakParameter = fragmentParameter("blendBreak");
//
//			_myRedGammaParameter = fragmentParameter("rGamma");
//			_myGreenGammaParameter = fragmentParameter("gGamma");
//			_myBlueGammaParameter = fragmentParameter("bGamma");
//			load();
//		}
//		
//		public void blendStart(final float theBlendStart) {
//			parameter(_myBlendStartParameter, theBlendStart);
//		}
//		
//		public void blendEnd(final float theBlendEnd) {
//			parameter(_myBlendEndParameter, theBlendEnd);
//		}
//		
//		public void blendPower(final float theBlendPower) {
//			parameter(_myBlendPowerParameter, theBlendPower);
//		}
//		
//		public void blendBreak(final float theBlendBreak) {
//			parameter(_myBlendBreakParameter, theBlendBreak);
//		}
//		
//		public void gamma(final float theRedGamma, final float theGreenGamma, final float theBlueGamma) {
//			parameter(_myRedGammaParameter, theRedGamma);
//			parameter(_myGreenGammaParameter, theGreenGamma);
//			parameter(_myBlueGammaParameter, theBlueGamma);
//		}
//	}
//	
//	private abstract class CCGPUEdgeBlendDrawMode{
//		protected CCGPUEdgeBlendShader _myEdgeBlendShader;
//		
//		public CCGPUEdgeBlendDrawMode(CCGraphics g, final String theFile){
//			_myEdgeBlendShader = new CCGPUEdgeBlendShader(g, theFile);
//		}
//		
//		public abstract void draw(CCGraphics g);
//	}
//	
//	private class CCGPUHorizontalEdgeBlendDrawMode extends CCGPUEdgeBlendDrawMode{
//		
//		 
//		private float _myX1;
//		private float _myX2;
//		private float _myX3;
//		private float _myX4;
//		private float _myX5;
//		
//		private float _myY1;
//		private float _myY2;
//		
//		private float _myTexX1;
//		private float _myTexX2;
//		private float _myTexX3;
//		private float _myTexX4;
//		
//		private float _myTexY1;
//		private float _myTexY2;
//		
//		public CCGPUHorizontalEdgeBlendDrawMode(CCGraphics g){
//			super(
//				g,
//				_myBlendTexture.target() == CCTextureTarget.TEXTURE_2D ?	
//				"edgeblend/horizontal.fp" :
//				"edgeblend/horizontal_RECT.fp"
//			);
//		}
//		
//		private void updateBlendRange(){
//			_myX1 = -(_myBlendTexture.width() + _cBlendRange) / 2;
//			_myX2 = -_cBlendRange;
//			_myX3 = 0;
//			_myX4 = _cBlendRange;
//			_myX5 = (_myBlendTexture.width() + _cBlendRange) / 2;
//			
//			_myY1 = -_myBlendTexture.height()/2;
//			_myY2 = _myBlendTexture.height()/2;
//			
//			_myTexX1 = 0;
//			_myTexX2 = (_myBlendTexture.width() - _cBlendRange) / 2f;
//			_myTexX3 = (_myBlendTexture.width() + _cBlendRange) / 2f;
//			_myTexX4 = _myBlendTexture.width();
//			
//			_myTexY1 = 0;
//			_myTexY2 = _myBlendTexture.height();
//			
//			if(_myBlendTexture.target() == CCTextureTarget.TEXTURE_2D) {
//				_myTexX1 /= _myBlendTexture.width();
//				_myTexX2 /= _myBlendTexture.width();
//				_myTexX3 /= _myBlendTexture.width();
//				_myTexX4 /= _myBlendTexture.width();
//				
//				_myTexY1 /= _myBlendTexture.height();
//				_myTexY2 /= _myBlendTexture.height();
//			}
//		}
//
//		@Override
//		public void draw(CCGraphics g) {
//			updateBlendRange();
//			_myEdgeBlendShader.gamma(_cRedGamma, _cGreenGamma, _cBlueGamma);
//			
//			g.texture(_myBlendTexture);
//			g.beginShape(CCDrawMode.QUADS);
//			g.vertex(_myX1,_myY1, _myTexX1, _myTexY1);
//			g.vertex(_myX2,_myY1, _myTexX2, _myTexY1);
//			g.vertex(_myX2,_myY2, _myTexX2, _myTexY2);
//			g.vertex(_myX1,_myY2, _myTexX1, _myTexY2);
//
//			g.vertex(_myX4,_myY1,_myTexX3, _myTexY1);
//			g.vertex(_myX5,_myY1,_myTexX4, _myTexY1);
//			g.vertex(_myX5,_myY2,_myTexX4, _myTexY2);
//			g.vertex(_myX4,_myY2,_myTexX3, _myTexY2);
//			g.endShape();
//			
//			_myEdgeBlendShader.start();
//			_myEdgeBlendShader.blendPower(_cBlendPower);
//			_myEdgeBlendShader.blendBreak(_cBlendBreak);
//			
//			_myEdgeBlendShader.blendStart(_myTexX3);
//			_myEdgeBlendShader.blendEnd(_myTexX2);
//			g.beginShape(CCDrawMode.QUADS);
//			g.vertex(_myX2,_myY1,_myTexX2, _myTexY1);
//			g.vertex(_myX3,_myY1,_myTexX3, _myTexY1);
//			g.vertex(_myX3,_myY2,_myTexX3, _myTexY2);
//			g.vertex(_myX2,_myY2,_myTexX2, _myTexY2);
//			g.endShape();
//			
//			_myEdgeBlendShader.blendStart(_myTexX2);
//			_myEdgeBlendShader.blendEnd(_myTexX3);
//			g.beginShape(CCDrawMode.QUADS);
//			g.vertex(_myX3,_myY1,_myTexX2, _myTexY1);
//			g.vertex(_myX4,_myY1,_myTexX3, _myTexY1);
//			g.vertex(_myX4,_myY2,_myTexX3, _myTexY2);
//			g.vertex(_myX3,_myY2,_myTexX2, _myTexY2);
//			g.endShape();
//			_myEdgeBlendShader.end();
//
//			g.noTexture();
//		}
//	}
//	
//	private class CCGPUVerticalEdgeBlendDrawMode extends CCGPUEdgeBlendDrawMode{
//		
//		private float _myX1;
//		private float _myX2;
//		
//		private float _myY1;
//		private float _myY2;
//		private float _myY3;
//		private float _myY4;
//		private float _myY5;
//		
//		private float _myTexX1;
//		private float _myTexX2;
//		
//		private float _myTexY1;
//		private float _myTexY2;
//		private float _myTexY3;
//		private float _myTexY4;
//		
//		public CCGPUVerticalEdgeBlendDrawMode(CCGraphics g){
//			super(
//				g,
//				_myBlendTexture.target() == CCTextureTarget.TEXTURE_2D ?	
//				"edgeblend/vertical.fp" :
//				"edgeblend/vertical_RECT.fp"
//			);
//		}
//		
//		private void updateBlendRange(){
//			_myX1 = -_myBlendTexture.width()/2;
//			_myX2 = _myBlendTexture.width()/2;
//
//			_myY1 = -(_myBlendTexture.height() + _cBlendRange) / 2;
//			_myY2 = -_cBlendRange;
//			_myY3 = 0;
//			_myY4 = _cBlendRange;
//			_myY5 = (_myBlendTexture.height() + _cBlendRange) / 2;
//			
//			_myTexX1 = 0;
//			_myTexX2 = _myBlendTexture.width();
//			
//			_myTexY1 = 0;
//			_myTexY2 = (_myBlendTexture.height() - _cBlendRange) / 2f;
//			_myTexY3 = (_myBlendTexture.height() + _cBlendRange) / 2f;
//			_myTexY4 = _myBlendTexture.height();
//			
//			if(_myBlendTexture.target() == CCTextureTarget.TEXTURE_2D) {
//				_myTexX1 /= _myBlendTexture.width();
//				_myTexX2 /= _myBlendTexture.width();
//				
//				_myTexY1 /= _myBlendTexture.height();
//				_myTexY2 /= _myBlendTexture.height();
//				_myTexY3 /= _myBlendTexture.height();
//				_myTexY4 /= _myBlendTexture.height();
//			}
//		}
//
//		@Override
//		public void draw(CCGraphics g) {
//			updateBlendRange();
//			_myEdgeBlendShader.gamma(_cRedGamma, _cGreenGamma, _cBlueGamma);
//			
//			g.texture(_myBlendTexture);
//			g.beginShape(CCDrawMode.QUADS);
//			g.vertex(_myX1,_myY1, _myTexX1, _myTexY1);
//			g.vertex(_myX2,_myY1, _myTexX2, _myTexY1);
//			g.vertex(_myX2,_myY2, _myTexX2, _myTexY2);
//			g.vertex(_myX1,_myY2, _myTexX1, _myTexY2);
//
//			g.vertex(_myX1,_myY4, _myTexX1, _myTexY3);
//			g.vertex(_myX2,_myY4, _myTexX2, _myTexY3);
//			g.vertex(_myX2,_myY5, _myTexX2, _myTexY4);
//			g.vertex(_myX1,_myY5, _myTexX1, _myTexY4);
//			g.endShape();
//			
//			_myEdgeBlendShader.start();
//			_myEdgeBlendShader.blendPower(_cBlendPower);
//			_myEdgeBlendShader.blendBreak(_cBlendBreak);
//			
//			_myEdgeBlendShader.blendStart(_myTexY2);
//			_myEdgeBlendShader.blendEnd(_myTexY3);
//			g.beginShape(CCDrawMode.QUADS);
//			g.vertex(_myX1,_myY2, _myTexX1, _myTexY2);
//			g.vertex(_myX2,_myY2, _myTexX2, _myTexY2);
//			g.vertex(_myX2,_myY3, _myTexX2, _myTexY3);
//			g.vertex(_myX1,_myY3, _myTexX1, _myTexY3);
//			g.endShape();
//			
//			_myEdgeBlendShader.blendStart(_myTexY3);
//			_myEdgeBlendShader.blendEnd(_myTexY2);
//			g.beginShape(CCDrawMode.QUADS);
//			g.vertex(_myX1,_myY3, _myTexX1, _myTexY2);
//			g.vertex(_myX2,_myY3, _myTexX2, _myTexY2);
//			g.vertex(_myX2,_myY4, _myTexX2, _myTexY3);
//			g.vertex(_myX1,_myY4, _myTexX1, _myTexY3);
//			g.endShape();
//			_myEdgeBlendShader.end();
//
//			g.noTexture();
//		}
//	}
//	
//	
//	private CCTexture2D _myBlendTexture;
//	
//	public static  enum CCGPUEdgeBlendDirection{
//		HORIZONTAL, VERTICAL;
//	}
//	
//	private CCGPUEdgeBlendDrawMode _myDrawMode;
//	private CCGPUEdgeBlendDrawMode _myHorizontalDrawMode;
//	private CCGPUEdgeBlendDrawMode _myVerticalDrawMode;
//	
//	public CCGPUEdgeBlender(final CCGraphics g, final CCTexture2D theBlendTexture, final CCGPUEdgeBlendDirection theBlendDirection) {
//		_myBlendTexture = theBlendTexture;
//		_myHorizontalDrawMode = new CCGPUHorizontalEdgeBlendDrawMode(g);
//		_myVerticalDrawMode = new CCGPUVerticalEdgeBlendDrawMode(g);
//		blendDirection(theBlendDirection);
//	}
//	
//	public void blendTexture() {
//		
//	}
//	
//	public void blendDirection(final CCGPUEdgeBlendDirection theBlendDirection){
//		switch(theBlendDirection){
//		case HORIZONTAL:
//			_myDrawMode = _myHorizontalDrawMode;
//			break;
//		case VERTICAL:
//			_myDrawMode = _myVerticalDrawMode;
//			break;
//		}
//	}
//	
//	public float blendRange() {
//		return _cBlendRange;
//	}
//	
//	public void update(final float theDeltaTime){
//		
//	}
//	
//	public void draw(CCGraphics g) {
//		g.pushAttribute();
//		g.noBlend();
//		g.color(255);
//		
//		_myDrawMode.draw(g);
//		g.popAttribute();
//	}
}

/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
package cc.creativecomputing.simulation.particles.blends;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.simulation.particles.CCParticle;

public class CCTextureBlend extends CCBlend{
	
	private int _myWidth;
	private int _myHeight;

	private CCShaderBuffer _myBlendBuffer;

	private String _myBlendTextureParameter;
	private String _myChannelAmountParameter;
	private String _myGlobalAmountParameter;
	private String _myChannelBlendRangeParameter;
	
	@CCProperty(name = "r amount", min = 0, max = 1)
	private double _cChannelRAmount = 0;
	@CCProperty(name = "g amount", min = 0, max = 1)
	private double _cChannelGAmount = 0;
	@CCProperty(name = "b amount", min = 0, max = 1)
	private double _cChannelBAmount = 0;
	@CCProperty(name = "global amount", min = 0, max = 1)
	private double _cGlobalAmount = 0;

	
	@CCProperty(name = "channel blend range", min = 0, max = 1)
	private double _cChannelBlendRange = 0;

	private CCGLProgram _myInitValueShader;
	
	public CCTextureBlend() {
		super("texture");
		
		_myBlendTextureParameter = parameter("blendTexture");
		_myChannelAmountParameter = parameter("channelAmounts");
		_myChannelBlendRangeParameter = parameter("channelBlendRange");
		_myGlobalAmountParameter = parameter("globalAmount");
	}
	
	public CCTexture2D texture() {
		return _myBlendBuffer.attachment(0);
	}
	
	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;

		_myInitValueShader = new CCGLWriteDataShader();
		
		_myBlendBuffer = new CCShaderBuffer(32,4,_myWidth, _myHeight);
		_myBlendBuffer.beginDraw(g);
		g.clear();
		_myBlendBuffer.endDraw(g);
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myBlendTextureParameter, _myBlendBuffer.attachment(0));
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myChannelAmountParameter, _cChannelRAmount, _cChannelGAmount, _cChannelBAmount);
		_myShader.uniform1f(_myChannelBlendRangeParameter, _cChannelBlendRange);
		_myShader.uniform1f(_myGlobalAmountParameter, _cGlobalAmount);
	}
	
	private CCGraphics g;
	
	public void beginSetBlends(CCGraphics g){
		this.g = g;
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myInitValueShader.start();
		_myBlendBuffer.beginDraw(g);
		g.beginShape(CCDrawMode.POINTS);
	}
	
	public void addBlend(CCParticle theParticle, double theR, double theG, double theB){
		g.textureCoords4D(0, theR, theG, theB, 1d);
		g.vertex(theParticle.x(), theParticle.y());
	}
	
	public void endSetBlends(CCGraphics g){
		g.endShape();
		_myBlendBuffer.endDraw(g);
		_myInitValueShader.end();
		g.popAttribute();
	}
}

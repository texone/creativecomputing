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
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * This force creates a texture based force field, every pixel of the
 * texture holds a direction which acts as a force on the particle. 
 * To read out the force from the texture it can be placed and scaled
 * on the xy plane. The xy coords of the particles will than be used to
 * read the force from the texture.
 * @author christian riekoff
 *
 */
public class CCTextureForceField2D extends CCForce{
	
	protected CCTexture2D _myTexture;
	
	@CCProperty(name = "texture scale")
	private CCVector2 _myTextureScale;
	@CCProperty(name = "texture offset")
	private CCVector2 _myTextureOffset;
	
	@CCProperty(name = "force scale", min = -1, max = 1)
	private CCVector3 _myForceScale = new CCVector3(1,1,1);
	
	private String _myTextureParameter;
	private String _myTextureScaleParameter;
	private String _myTextureOffsetParameter;
	private String _myTextureSizeParameter;
	private String _myForceScaleParameter;
	
	public CCTextureForceField2D(
		final CCTexture2D theTexture,
		final CCVector2 theTextureScale,
		final CCVector2 theTextureOffset
	){
		super("textureForceField");
		_myTexture = theTexture;
		
		_myTextureScale = new CCVector2(theTextureScale);
		_myTextureOffset = new CCVector2(theTextureOffset);

		_myTextureParameter = parameter("forceFieldTexture");
		_myTextureScaleParameter = parameter("textureScale");
		_myTextureOffsetParameter = parameter("textureOffset");
		_myTextureSizeParameter = parameter("textureSize");
		_myForceScaleParameter = parameter("forceScale");
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myTextureParameter, _myTexture);
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		
		_myShader.uniform2f(_myTextureScaleParameter, _myTextureScale.x , _myTextureScale.y);
		_myShader.uniform2f(_myTextureOffsetParameter, -_myTextureOffset.x, -_myTextureOffset.y);
	
		_myShader.uniform2f(_myTextureSizeParameter, _myTexture.width(), _myTexture.height());
		_myShader.uniform3f(_myForceScaleParameter, _myForceScale);
	}
	
	public boolean addToForceArray(){
		return true;
	}
	
	public void texture(final CCTexture2D theTexture){
		_myTexture = theTexture;
		_myShader.setTextureUniform(_myTextureParameter, theTexture);
	}
	
	public CCTexture2D texture(){
		return _myTexture;
	}
	
	public CCVector2 textureScale() {
		return _myTextureScale;
	}
	
	public CCVector2 textureOffset() {
		return _myTextureOffset;
	}
	
	public void display(CCGraphics g) {
		g.pushMatrix();
	
		g.scale(_myTextureScale.x , _myTextureScale.y);
		g.translate(-_myTextureOffset.x, -_myTextureOffset.y);
		
		g.texture(_myTexture);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0);
		g.vertex(0,0);
		g.textureCoords2D(1, 0);
		g.vertex(1,0);
		g.textureCoords2D(1, 1);
		g.vertex(1,1);
		g.textureCoords2D(0, 1);
		g.vertex(0,1);
		g.endShape();
		g.noTexture();
		g.popMatrix();
	}
}

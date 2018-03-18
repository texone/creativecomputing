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
package cc.creativecomputing.simulation.particles.constraints;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector3;


public class CCGPUShapeConstraint extends CCConstraint{
	
	private CCTexture2D _myTexture;
	private CCVector3 _myScale;
	private CCVector3 _myOffset;
	
	private String _myTextureParameter;
	private String _myTextureSizeParameter;
	private String _myScaleParameter;
	private String _myOffsetParameter;
	
	@CCProperty(name="friction", min=0, max=1)
	public void setFriction (float theFriction) {
		this.friction(theFriction);
	}
	@CCProperty(name="resilience", min=0, max=1)
	public void setResilience (float theResilience) {
		this.resilience(theResilience);
	}
	@CCProperty(name="minimal velocity", min=0, max=1)
	public void setVelocity (float theVelocity) {
		this.minimalVelocity(theVelocity);
	}
	
	public CCGPUShapeConstraint(
		final CCTexture2D theTexture, final CCVector3 theScale, final CCVector3 theOffset,
		final float theResilience, final float theFriction, final float theMinimalVelocity
	) {
		super("shapeConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myTexture = theTexture;
		_myScale = theScale;
		_myOffset = theOffset;
	}
	

	public void setupParameter(final int theWidth, final int theHeight){
		_myTextureParameter = parameter("texture");
		_myTextureSizeParameter = parameter("textureSize");
		_myScaleParameter = parameter("scale");
		_myOffsetParameter = parameter("offset");
			
//		texture(_myTexture);
//		textureScale(_myScale);
//		textureOffset(_myOffset);
	}

//	public void texture(final CCTexture2D theTexture) {
//		_myVelocityShader.texture(_myTextureParameter, theTexture.id());
//		_myVelocityShader.parameter(_myTextureSizeParameter, theTexture.width(), theTexture.height());
//	}
//	
//	public void textureScale(final CCVector3 theTextureScale) {
//		_myVelocityShader.parameter(_myScaleParameter, theTextureScale);
//	}
//	
//	public void textureScale(final float theXScale, final float theYScale, final float theZScale){
//		_myVelocityShader.parameter(_myScaleParameter, theXScale, theYScale, theZScale);
//	}
//	
//	public void textureOffset(final CCVector3 theTextureOffset) {
//		_myVelocityShader.parameter(_myOffsetParameter, theTextureOffset);
//	}
//	
//	public void textureOffset(final float theXOffset, final float theYOffset, final float theZOffset) {
//		_myVelocityShader.parameter(_myOffsetParameter, theXOffset, theYOffset, theZOffset);
//	}
}

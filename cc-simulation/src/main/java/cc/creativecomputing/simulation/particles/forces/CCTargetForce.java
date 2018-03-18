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
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.particles.CCParticle;

public class CCTargetForce extends CCForce {
	private CCShaderBuffer _myTargetBuffer;

	private String _myTargetPositionTextureParameter;
	private String _myCenterParameter;
	private String _myScaleParameter;
	private String _myLookAheadParameter;
	private String _myMaxForceParameter;
	private String _myNearDistanceParameter;
	private String _myNearMaxForceParameter;

	private int _myWidth;
	private int _myHeight;
	
	private double _myScale;
	@CCProperty(name = "lookahead", min = 0, max = 50)
	private double _myLookAhead = 0;
	@CCProperty(name = "max force", min = 0, max = 10)
	private double _myMaxForce;
	
	private CCVector3 _myCenter = new CCVector3();
	
	@CCProperty(name = "near distance", min = 0, max = 200)
	private double _myNearDistance;
	@CCProperty(name = "near max force", min = 0, max = 10)
	private double _myNearMaxForce;

	private CCGLProgram _myInitValueShader;

	public CCTargetForce() {
		super("target");
		_myScale = 1f;
		
		_myTargetPositionTextureParameter = parameter("targetPositionTexture");
		_myCenterParameter = parameter("center");
		_myScaleParameter = parameter("scale");
		_myLookAheadParameter = parameter("lookAhead");
		_myMaxForceParameter = parameter("maxForce");
		_myNearDistanceParameter = parameter("nearDistance");
		_myNearMaxForceParameter = parameter("nearMaxForce");
	}
	
	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;

		_myInitValueShader = new CCGLWriteDataShader();
		
		_myTargetBuffer = new CCShaderBuffer(16,4,_myWidth, _myHeight);
		_myTargetBuffer.beginDraw(g);
		g.clear();
		_myTargetBuffer.endDraw(g);
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myTargetPositionTextureParameter, _myTargetBuffer.attachment(0));
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_myScaleParameter, _myScale);
		_myShader.uniform1f(_myLookAheadParameter, _myLookAhead);
		_myShader.uniform1f(_myMaxForceParameter, _myMaxForce);
		_myShader.uniform3f(_myCenterParameter, _myCenter);
		_myShader.uniform1f(_myNearDistanceParameter, _myNearDistance);
		_myShader.uniform1f(_myNearMaxForceParameter, _myNearMaxForce);
//		_myShader.uniform2f(_myTextureSizeParameter, _myTexture.width(), _myTexture.height());
//		_myShader.uniform1f(_myExponentParameter, _myExponent);
	}
	
	public void scale(double theScale){
		_myScale = theScale;
	}
	
	public void lookAhead(double theLookAhead) {
		_myLookAhead = theLookAhead;
	}
	
	public void maxForce(double theMaxForce) {
		_myMaxForce = theMaxForce;
	}

	public void center(final CCVector3 theCenter) {
		_myCenter.set(theCenter);
	}

	public void center(final double theX, final double theY, final double theZ) {
		_myCenter.set(theX, theY, theZ);
	}
	
	public void nearDistance(double theNearMaxDistance) {
		_myNearDistance = theNearMaxDistance;
	}
	
	public void nearMaxForce(double theNearMaxForce) {
		_myNearMaxForce = theNearMaxForce;
	}

	public CCShaderBuffer targetBuffer(){
		return _myTargetBuffer;
	}
	
	private CCGraphics g;
	
	public void beginSetTargets(CCGraphics g){
		this.g = g;
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myInitValueShader.start();
		_myTargetBuffer.beginDraw(g);
		g.beginShape(CCDrawMode.POINTS);
	}
	
	public void addTarget(CCParticle theParticle){
		g.textureCoords4D(0, theParticle.target());
		g.vertex(theParticle.x() + 0.5f, theParticle.y() + 0.5f);
	}
	
	public void addTarget(double theX, double theY, double theZ, double theW, int theTexX, int theTexY){
		g.textureCoords4D(0, theX, theY, theZ, theW);
		g.vertex(theTexX + 1.5f, theTexY + 1.5f);
	}
	
	public void endSetTargets(CCGraphics g){
		g.endShape();
		_myTargetBuffer.endDraw(g);
		_myInitValueShader.end();
		g.popAttribute();
	}

}

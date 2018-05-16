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
package cc.creativecomputing.simulation.particles.emit.gpu;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCGroupEmitter extends CCEmitter{
	
	public static class CCParticleGroup{
		public int id;
		public int start;
		private int _mySize;
		public double progress;
		public double emitMax;
		public CCVector3 position = new CCVector3();
		
		public CCParticleGroup(int theID, int theStart, int theSize) {
			id = theID;
			start = theStart;
			_mySize = theSize;
		}
	}
	
	private int _myWidth;
	private int _myHeight;

	private CCGLProgram _myInitValueShader;

	private CCShaderBuffer _myGroupActivationBuffer;
	private List<CCParticleGroup> _myGroups = new ArrayList<>();
	
	private String _myGroupActivationParameter;
	private String _myGroupPositionParameter;
	private String _myStartVelocityParameter;
	private String _myOffsetParameter;
	private String _myRandomPositionParameter;
	private String _myRandomVelocityParameter;
	
	@CCProperty(name = "start velocity", min = -100, max = 100)
	private CCVector3 _cStartVelocity = new CCVector3();
	@CCProperty(name = "offset", min = -200, max = 200)
	private CCVector3 _cOffset = new CCVector3();
	
	@CCProperty(name = "random velocity", min = 0, max = 100)
	private double _cRandomVelocity = 0;
	@CCProperty(name = "random position", min = 0, max = 100)
	private double _cRandomPosition = 0;

	public CCGroupEmitter() {
		super("group");

		_myGroupActivationParameter = parameter("groupActivationTexture");
		_myGroupPositionParameter = parameter("groupPositionTexture");

		_myStartVelocityParameter = parameter("startVelocity");
		_myOffsetParameter = parameter("offset");
		_myRandomPositionParameter = parameter("randomPosition");
		_myRandomVelocityParameter = parameter("randomVelocity");
	}
	
	private int _myNumberOfParticles;

	@Override
	public void setSize(CCGraphics g, int theWidth, int theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;

		_myInitValueShader = new CCGLWriteDataShader();
		
		_myNumberOfParticles = theWidth * theHeight;
		
		_myGroupActivationBuffer = new CCShaderBuffer(32, 4, 2, _myWidth, _myHeight);
		_myGroupActivationBuffer.beginDraw(g);
		g.clear();
		_myGroupActivationBuffer.endDraw(g);
		
	}
	
	@Override
	public void setShader(CCGLProgram theProgram) {
		super.setShader(theProgram);
		_myShader.setTextureUniform(_myGroupActivationParameter, _myGroupActivationBuffer.attachment(0));
		_myShader.setTextureUniform(_myGroupPositionParameter, _myGroupActivationBuffer.attachment(1));
	}

	@Override
	public void preDisplay(CCGraphics g) {
		g.pushAttribute();
		g.noBlend();
		g.strokeWeight(1);
		_myInitValueShader.start();
		_myGroupActivationBuffer.beginDraw(g);
		g.clear();
		double i = 0.5;
		int x = 0;
		g.beginShape(CCDrawMode.LINES);
		for(CCParticleGroup myGroup:_myGroups) {
			int mySize = myGroup._mySize;
			int myDrawSize = (int)(myGroup._mySize * myGroup.emitMax);
			while(x + mySize > _myWidth) {
				int mySize0 = _myWidth - x;

				g.textureCoords4D(0, 1d,myGroup.progress,1d,1d);
				g.textureCoords3D(1, myGroup.position);
				g.vertex(x, i);
				g.vertex(x + CCMath.constrain(mySize0, 0, myDrawSize), i);
				i++;
				x = 0;
				mySize -= mySize0;
				myDrawSize -= mySize0;
			}

			g.textureCoords4D(0, 1d,myGroup.progress,1d,1d);
			g.textureCoords3D(1, myGroup.position);
			g.vertex(x, i);
			g.vertex(x + CCMath.constrain(mySize, 0, myDrawSize), i);
			x += mySize;
		}
		g.endShape();
		_myGroupActivationBuffer.endDraw(g);
		_myInitValueShader.end();
		g.popAttribute();
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();

		_myShader.uniform3f(_myStartVelocityParameter, _cStartVelocity);
		_myShader.uniform3f(_myOffsetParameter, _cOffset);
		_myShader.uniform1f(_myRandomPositionParameter, _cRandomPosition);
		_myShader.uniform1f(_myRandomVelocityParameter, _cRandomVelocity);
	}
	
	private int _myLastGroupStart = 0;
	
	public CCParticleGroup createGroup(int theSize) {
		int mySize = CCMath.min(theSize, _myNumberOfParticles - _myLastGroupStart);
		if(_myLastGroupStart >= _myNumberOfParticles)return null;
		CCParticleGroup myResult = new CCParticleGroup(_myGroups.size(), _myLastGroupStart, mySize);
		_myLastGroupStart += mySize;
		_myGroups.add(myResult);
		return myResult;
	}
	
	public List<CCParticleGroup> groups() {
		return _myGroups;
	}

	public CCTexture2D activationTexture() {
		return _myGroupActivationBuffer.attachment(0);
	}
}

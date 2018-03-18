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
package cc.creativecomputing.simulation.particles.render;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.simulation.particles.CCParticles;

/**
 * @author christianriekoff
 *
 */
public abstract class CCParticleRenderer {
	
	protected CCShaderBuffer _myEnvelopeData;
	
	private CCGLWriteDataShader _myWriteDataShader;
	
	protected String _myEnvelopeTextureParameter;
	 
	protected List<CCEnvelope> _myEnvelopes = new ArrayList<>();
	
	public CCParticleRenderer(){

		_myWriteDataShader = new CCGLWriteDataShader();
		
		_myEnvelopeTextureParameter = "lifeTimeBlends";
		_myEnvelopeData = new CCShaderBuffer(100,100);
	}
	
	public abstract void setup(CCParticles theParticles);

	public abstract void update(final CCAnimator theAnimator);
	
	public void updateData(CCGraphics g){
		_myEnvelopeData.beginDraw(g);
		g.clear();
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myWriteDataShader.start();
		g.beginShape(CCDrawMode.POINTS);
		float y = 0;
		for(CCEnvelope myEnvelope:_myEnvelopes){
			for(int i = 0; i < 100; i++){
				double myVal = myEnvelope.value(i / 100d);
				g.textureCoords4D(0, myVal, myVal, myVal, 1d);
				g.vertex(i + 0.5, y + 0.5);
			}
			y++;
		}
		g.endShape();
		_myWriteDataShader.end();
		g.popAttribute();
		_myEnvelopeData.endDraw(g);
	}
	
	public abstract void display(CCGraphics g);
	
	public abstract CCMesh mesh();
}

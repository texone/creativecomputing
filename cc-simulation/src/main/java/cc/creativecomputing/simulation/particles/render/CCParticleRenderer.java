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
package cc.creativecomputing.simulation.particles.render;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.forces.CCForce;

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

	public abstract void update(final CCGLTimer theAnimator);
	
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
	
	public abstract CCVBOMesh mesh();
}

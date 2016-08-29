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

import java.nio.FloatBuffer;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 *
 */
public class CCGPUIndexParticleCurveEmitter extends CCGPUIndexParticleEmitter{
	
	private CCCGShader _myCurveEmitShader;
	private CGparameter _myRadiusParameter;
	private CGparameter _myOffsetParameter;
	private CGparameter _myScaleParameter;
	private CGparameter _myOutputScaleParameter;

	private float _myOffset = 0;
	private float _myScale = 1;
	private float _myOutputScale = 1;
	private float _myRadius = 1;
	
	private float _mySpeed = 1;

	public CCGPUIndexParticleCurveEmitter(CCParticles theParticles, int theStart, int theNumberParticles) {
		super(theParticles, theStart, theNumberParticles);
		_myCurveEmitShader = new CCCGShader(
			null,
			new String[] {
				CCIOUtil.classPath(CCParticles.class,"shader/simplex.fp"),
				CCIOUtil.classPath(CCParticles.class, "shader/emit/curvefield_emit.fp")
			}
		);
		_myRadiusParameter = _myCurveEmitShader.fragmentParameter("radius");
		_myOffsetParameter = _myCurveEmitShader.fragmentParameter("offset");
		_myScaleParameter = _myCurveEmitShader.fragmentParameter("scale");
		_myOutputScaleParameter = _myCurveEmitShader.fragmentParameter("outputScale");
		_myCurveEmitShader.load();
		
		CCGPUNoise.attachFragmentNoise(_myCurveEmitShader);
	}

	public CCGPUIndexParticleCurveEmitter(CCParticles theParticles) {
		super(theParticles);
	}
	
	public void scale(float theScale) {
		_myScale = theScale;
	}

	public void outputScale(float theOutputScale) {
		_myOutputScale = theOutputScale;
	}
	
	public void radius(float theRadius) {
		_myRadius = theRadius;
	}

	public void speed(float theSpeed) {
		_mySpeed = theSpeed;
	}
	
	@Override
	public void update(float theDeltaTime) {
		super.update(theDeltaTime);
		
		_myOffset += theDeltaTime * _mySpeed;

		_myCurveEmitShader.parameter(_myOffsetParameter, _myOffset);
		_myCurveEmitShader.parameter(_myOutputScaleParameter, _myOutputScale);
		_myCurveEmitShader.parameter(_myScaleParameter, _myScale);
		_myCurveEmitShader.parameter(_myRadiusParameter, _myRadius);
	}
	
	@Override
	public void fillPositionData(FloatBuffer theBuffer, List<CCGPUParticle> theParticles) {
		int i = 0;
		for (CCGPUParticle myParticle:theParticles){
			_myFillArray[i * 3 + 0] = myParticle.position().x;
			_myFillArray[i * 3 + 1] = CCMath.random();
			_myFillArray[i * 3 + 2] = CCMath.random(CCMath.TWO_PI);
			i++;
		}
		theBuffer.put(_myFillArray, 0, theParticles.size() * 3);
	}
	
	@Override
	public void transferEmitData(CCGraphics g) {
		_myParticles.dataBuffer().beginDraw();
		_myCurveEmitShader.start();
		
		_myEmitMesh.draw(g);
		
		_myCurveEmitShader.end();
		_myParticles.dataBuffer().endDraw();
	}

}

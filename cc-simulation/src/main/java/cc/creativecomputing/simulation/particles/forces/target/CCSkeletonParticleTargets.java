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
package cc.creativecomputing.simulation.particles.forces.target;

import java.nio.FloatBuffer;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.model.collada.CCColladaSkeletonSkin;
import cc.creativecomputing.skeleton.CCSkeleton;

/**
 * @author christianriekoff
 *
 */
public class CCSkeletonParticleTargets {

	private CCSkeleton _mySkeleton;
	private CCColladaSkeletonSkin _mySkin;
	
	private FloatBuffer _myPositionBuffer;
	private FloatBuffer _myWeightBuffer;
	private FloatBuffer _myWeightIndexBuffer;
	
	private CCVBOMesh _myMesh;
	
	private CCGLSLShader _myWeightsShader;

	private CCShaderBuffer _myTargetTexture;
	
	private CCGraphics g;

	public CCSkeletonParticleTargets(
		CCApp theApp, 
		CCColladaSkeletonSkin theSkin,
		CCSkeleton theSkeleton,
		int theXRes, int theYRes
	) {
		g = theApp.g;
		
		_mySkin = theSkin; 
		_myPositionBuffer = _mySkin.mesh().vertices();
		_myWeightBuffer = _mySkin.weights();
		_myWeightIndexBuffer = _mySkin.weightIndices();
		
		int myNumberOfPoints = theXRes * theYRes;
		
		_myTargetTexture = new CCShaderBuffer(32, 3, theXRes, theYRes);
		_myTargetTexture.attachment(0).wrap(CCTextureWrap.REPEAT);
		FloatBuffer myTargetIds = FloatBuffer.allocate(myNumberOfPoints * 2);
		FloatBuffer myPositions = FloatBuffer.allocate(myNumberOfPoints * 3);
		FloatBuffer myWeights = FloatBuffer.allocate(myNumberOfPoints * 4);
		FloatBuffer myWeightIndicess = FloatBuffer.allocate(myNumberOfPoints * 4);
		
		for(int x = 0; x < theXRes;x++) {
			for(int y = 0; y < theYRes;y++) {
				myTargetIds.put(x);
				myTargetIds.put(y);
				
				float[] myVertex = randomPoint();
				myPositions.put(myVertex[0]);
				myPositions.put(myVertex[1]);
				myPositions.put(myVertex[2]);
				
				myWeights.put(myVertex[3]);
				myWeights.put(myVertex[4]);
				myWeights.put(myVertex[5]);
				myWeights.put(myVertex[6]);
				
				myWeightIndicess.put(myVertex[7]);
				myWeightIndicess.put(myVertex[8]);
				myWeightIndicess.put(myVertex[9]);
				myWeightIndicess.put(myVertex[10]);
			}
		}
		
		myTargetIds.rewind();
		myPositions.rewind();
		myWeights.rewind();
		myWeightIndicess.rewind();

		_myMesh = new CCVBOMesh(CCDrawMode.POINTS);
		_myMesh.vertices(myTargetIds, 2);
		_myMesh.textureCoords(0, myPositions, 3);
		_myMesh.textureCoords(1, myWeights, 4);
		_myMesh.textureCoords(2, myWeightIndicess, 4);
		
		_myWeightsShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "particle_weights_vert.glsl"),
			CCIOUtil.classPath(this, "particle_weights_frag.glsl")
		);
		_myWeightsShader.load();
		
		_mySkeleton = theSkeleton;
	}
	
	public void skeleton(CCSkeleton theSkeleton) {
		_mySkeleton = theSkeleton;
	}
	
	public CCShaderBuffer targets() {
		return _myTargetTexture;
	}
	
	public void update(final float theDeltaTime) {
		_myTargetTexture.beginDraw();
		g.clearColor(0);
		g.clear();
		if(_mySkeleton != null) {
			_myWeightsShader.start();
			_myWeightsShader.uniformMatrix4fv("joints", _mySkin.skinningMatrices(_mySkeleton));
			_myMesh.draw(g);
			_myWeightsShader.end();
		}
		_myTargetTexture.endDraw();
	}
	
	private float[] randomPoint() {
		int myTriangleIndex = (int)CCMath.random(_myPositionBuffer.limit() / 3 / 3) * 3;
		
		_myPositionBuffer.position(myTriangleIndex * 3);
		float myP0X = _myPositionBuffer.get();
		float myP0Y = _myPositionBuffer.get();
		float myP0Z = _myPositionBuffer.get();

		float myP1X = _myPositionBuffer.get();
		float myP1Y = _myPositionBuffer.get();
		float myP1Z = _myPositionBuffer.get();

		float myP2X = _myPositionBuffer.get();
		float myP2Y = _myPositionBuffer.get();
		float myP2Z = _myPositionBuffer.get();
		
		float myP01X = myP1X - myP0X;
		float myP01Y = myP1Y - myP0Y;
		float myP01Z = myP1Z - myP0Z;
		
		float myP02X = myP2X - myP0X;
		float myP02Y = myP2Y - myP0Y;
		float myP02Z = myP2Z - myP0Z;
	
		float myBlend1 = CCMath.random();
		float myBlend2 = CCMath.random();
		
		float[] myResult = new float[11];
				
		myResult[0] = myP0X + myP01X * myBlend1 + myP02X * myBlend2;
		myResult[1] = myP0Y + myP01Y * myBlend1 + myP02Y * myBlend2;
		myResult[2] = myP0Z + myP01Z * myBlend1 + myP02Z * myBlend2;
		
		_myWeightIndexBuffer.position(myTriangleIndex * 4);
		_myWeightBuffer.position(myTriangleIndex * 4);
		
		myResult[3] = _myWeightBuffer.get();
		myResult[4] = _myWeightBuffer.get();
		myResult[5] = _myWeightBuffer.get();
		myResult[6] = _myWeightBuffer.get();

		myResult[7] = _myWeightIndexBuffer.get();
		myResult[8] = _myWeightIndexBuffer.get();
		myResult[9] = _myWeightIndexBuffer.get();
		myResult[10] = _myWeightIndexBuffer.get();
		
		return myResult;
	}
	
	public void drawTargets(CCGraphics g) {
		g.color(255);
		g.image(_myTargetTexture.attachment(0),0,0);
	}
}

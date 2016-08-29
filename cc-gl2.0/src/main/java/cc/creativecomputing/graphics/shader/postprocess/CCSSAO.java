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
package cc.creativecomputing.graphics.shader.postprocess;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCSSAO extends CCPostProcessEffect{
	@CCProperty(name = "sample radius", min = 0, max = 20)
	private float sampleRadius = 0.4f;
	
	@CCProperty(name = "intensity", min = 0, max = 20)
	float intensity = 2.5f;

	@CCProperty(name = "scale", min = 0, max = 1f)
	float scale = 0.34f;

	@CCProperty(name = "bias", min = 0, max = 1)
	float bias = 0.05f;

	@CCProperty(name = "jitter", min = 0, max = 100)
	float jitter = 64.0f;

	@CCProperty(name = "self occlusion", min = 0, max = 1)
	float selfOcclusion = 0.12f;
	
	private CCGLProgram	mSSAOShader;
	private CCTexture2D		mSSAORandTexture;
	
	private CCShaderBuffer	mFbo;
	
	public CCSSAO() {
		// Ambient Occlusion
		 mSSAOShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "postProcess_vert.glsl"),
			CCNIOUtil.classPath(this, "ssao.glsl")
		 );
		 
		 mSSAORandTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "randomNormals.png")));
		 mSSAORandTexture.wrap(CCTextureWrap.REPEAT);
		 mSSAORandTexture.textureFilter(CCTextureFilter.NEAREST);
	}
	
	@Override
	public void initialize(int theWidth, int theHeight) {
		mFbo = new CCShaderBuffer(theWidth, theHeight, CCTextureTarget.TEXTURE_2D);
	}
	
	@Override
	public void apply(CCGeometryBuffer theGeometryBuffer, CCGraphics g){

		g.texture(0, theGeometryBuffer.positions());
		g.texture(1, theGeometryBuffer.normals());
		g.texture(2, mSSAORandTexture);

		mSSAOShader.start();

		mSSAOShader.uniform1i( "positions", 0 );
		mSSAOShader.uniform1i( "normals", 1 );
		mSSAOShader.uniform1i( "random", 2 );

		mSSAOShader.uniform1f( "sampleRadius", sampleRadius );
		mSSAOShader.uniform1f( "intensity", intensity );
		mSSAOShader.uniform1f( "scale", scale /25f);
		mSSAOShader.uniform1f( "bias", bias );
		mSSAOShader.uniform1f( "jitter", jitter );
		mSSAOShader.uniform1f( "selfOcclusion", selfOcclusion );
		mSSAOShader.uniform2f( "screenSize", mFbo.width(), mFbo.height());
		mSSAOShader.uniform2f( "invScreenSize", 1.0f /  mFbo.width(), 1.0f / mFbo.height());

		g.color(255);
		mFbo.draw();
////		GL2 gl = CCGraphics.currentGL();
////		gl.glBegin(GL2.GL_QUADS);
////		gl.glTexCoord2f(0, 0);
////		gl.glVertex2f(0, 0);
////		gl.glTexCoord2f(1f, 0);
////		gl.glVertex2f(mFbo.width(), 0);
////		gl.glTexCoord2f(1f, 1f);
////		gl.glVertex2f(mFbo.width(), mFbo.height());
////		gl.glTexCoord2f(0, 1f);
////		gl.glVertex2f(0, mFbo.height());
////		gl.glEnd();

		mSSAOShader.end();

		g.noTexture();
	}

	public CCTexture2D content() {
		return mFbo.attachment(0);
	}
}

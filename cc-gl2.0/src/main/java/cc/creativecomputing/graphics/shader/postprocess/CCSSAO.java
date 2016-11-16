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
import cc.creativecomputing.core.logging.CCLog;
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
public class CCSSAO extends CCPostProcessEffect {
	@CCProperty(name = "sample radius", min = 0, max = 20)
	private double sampleRadius = 0.4f;

	@CCProperty(name = "intensity", min = 0, max = 20)
	private double intensity = 2.5f;

	@CCProperty(name = "scale", min = 0, max = 1f)
	private double scale = 0.34f;

	@CCProperty(name = "bias", min = 0, max = 1)
	private double bias = 0.05f;

	@CCProperty(name = "jitter", min = 0, max = 100)
	private double jitter = 64.0f;

	@CCProperty(name = "self occlusion", min = 0, max = 1)
	private double selfOcclusion = 0.12f;

	@CCProperty(name = "shader")
	private CCGLProgram _mySSAOShader;
	private CCTexture2D _mySSAORandomTexture;

	private CCShaderBuffer _myShaderBuffer;

	public CCSSAO() {
		super("ssao");
		// Ambient Occlusion
		_mySSAOShader = new CCGLProgram(
			null,
			CCNIOUtil.classPath(this, "ssao.glsl")
		);

		_mySSAORandomTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "randomNormals.png")));
		_mySSAORandomTexture.wrap(CCTextureWrap.REPEAT);
		_mySSAORandomTexture.textureFilter(CCTextureFilter.NEAREST);
	}

	@Override
	public void initialize(int theWidth, int theHeight) {
		_myShaderBuffer = new CCShaderBuffer(theWidth, theHeight, CCTextureTarget.TEXTURE_2D);
	}

	@Override
	public void apply(CCGeometryBuffer theGeometryBuffer, CCGraphics g) {

		CCLog.info("app:" );
		g.texture(0, theGeometryBuffer.positions());
		g.texture(1, theGeometryBuffer.normals());
		g.texture(2, _mySSAORandomTexture);

		_mySSAOShader.start();

		_mySSAOShader.uniform1i("positions", 0);
		_mySSAOShader.uniform1i("normals", 1);
		_mySSAOShader.uniform1i("random", 2);

		_mySSAOShader.uniform1f("sampleRadius", sampleRadius);
		_mySSAOShader.uniform1f("intensity", intensity);
		_mySSAOShader.uniform1f("scale", scale / 25f);
		_mySSAOShader.uniform1f("bias", bias);
		_mySSAOShader.uniform1f("jitter", jitter);
		_mySSAOShader.uniform1f("selfOcclusion", selfOcclusion);
		_mySSAOShader.uniform2f("screenSize", _myShaderBuffer.width(), _myShaderBuffer.height());
		_mySSAOShader.uniform2f("invScreenSize", 1.0f / _myShaderBuffer.width(), 1.0f / _myShaderBuffer.height());

		g.color(255);
		_myShaderBuffer.draw();

		_mySSAOShader.end();

		g.noTexture();
	}

	public CCTexture2D content() {
		return _myShaderBuffer.attachment(0);
	}
}

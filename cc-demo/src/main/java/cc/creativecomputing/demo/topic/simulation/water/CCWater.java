package cc.creativecomputing.demo.topic.simulation.water;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2;

public class CCWater {
	
	private CCGLProgram _myDropShader;
	private CCGLProgram _myUpdateShader;
	private CCGLProgram _myNormalShader;
	private CCGLProgram _mySphereShader;
	
	private CCShaderBuffer textureA;
	private CCShaderBuffer textureB;
	
	public CCWater() {
		_myDropShader = new CCGLProgram(CCNIOUtil.classPath(this, "water_vertex.glsl"), CCNIOUtil.classPath(this, "water_drop_shader.glsl"));
		_myUpdateShader = new CCGLProgram(CCNIOUtil.classPath(this, "water_vertex.glsl"), CCNIOUtil.classPath(this, "water_update_shader.glsl"));
		_myNormalShader = new CCGLProgram(CCNIOUtil.classPath(this, "water_vertex.glsl"), CCNIOUtil.classPath(this, "water_normal_shader.glsl"));
		_mySphereShader = new CCGLProgram(CCNIOUtil.classPath(this, "water_vertex.glsl"), CCNIOUtil.classPath(this, "water_sphere_shader.glsl"));
		
		//var filter = GL.Texture.canUseFloatingPointLinearFiltering() ? gl.LINEAR : gl.NEAREST;
		//this.textureA = new GL.Texture(256, 256, { type: gl.FLOAT, filter: filter });
		textureA = new CCShaderBuffer(256, 256, CCTextureTarget.TEXTURE_2D);
		textureB = new CCShaderBuffer(256, 256, CCTextureTarget.TEXTURE_2D);
		
		// this.plane = GL.Mesh.plane();
	}
	
	public CCTexture2D textureA() {
		return textureA.attachment(0);
	}
	
	public CCTexture2D textureB() {
		return textureB.attachment(0);
	}

	public void addDrop(CCGraphics g, double x, double y, double radius, double strength) {
		g.texture(textureA.attachment(0));
		_myDropShader.start();
		_myDropShader.uniform2f("center", x, y);
		_myDropShader.uniform1f("radius", radius);
		_myDropShader.uniform1f("strength", strength);
		textureB.draw(g);
		_myDropShader.end();
		g.noTexture();
		
		CCShaderBuffer _myTMP = textureA;
		textureA = textureB;
		textureB = _myTMP;
	}

	public void moveSphere(CCGraphics g, CCVector2 oldCenter, CCVector2 newCenter, double radius) {
		g.texture(textureA.attachment(0));
		_mySphereShader.start();
		_mySphereShader.uniform2f("oldCenter", oldCenter);
		_mySphereShader.uniform1f("newCenter", radius);
		_mySphereShader.uniform1f("radius", radius);
		textureB.draw(g);
		_mySphereShader.end();
		g.noTexture();

		CCShaderBuffer _myTMP = textureA;
		textureA = textureB;
		textureB = _myTMP;
	}

	public void stepSimulation(CCGraphics g) {
		g.texture(textureA.attachment(0));
		_myUpdateShader.start();
		_myUpdateShader.uniform2f("delta", 1d / textureA.width(), 1d / textureA.height());
		textureB.draw(g);
		_myUpdateShader.end();
		g.noTexture();

		CCShaderBuffer _myTMP = textureA;
		textureA = textureB;
		textureB = _myTMP;
	}

	public void updateNormals(CCGraphics g) {
		g.texture(textureA.attachment(0));
		_myNormalShader.start();
		_myNormalShader.uniform2f("delta", 1d / textureA.width(), 1d / textureA.height());
		textureB.draw(g);
		_myNormalShader.end();
		g.noTexture();

		CCShaderBuffer _myTMP = textureA;
		textureA = textureB;
		textureB = _myTMP;	
	}
}

package cc.creativecomputing.demo.topic.pbr;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.primitives.CCSphereMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCBasicPBR extends CCGL2Adapter {
	
	@CCProperty(name = "light shader")
	private CCGLProgram _cLightShader;
	@CCProperty(name = "camera controller")
	private CCCameraController _cCameraController;
	

	@CCProperty(name = "color 0")
	private CCVector3 _cColor0 = new CCVector3(300,300,300);
	@CCProperty(name = "color 1")
	private CCVector3 _cColor1 = new CCVector3(300,300,300);
	@CCProperty(name = "color 2")
	private CCVector3 _cColor2 = new CCVector3(300,300,300);
	@CCProperty(name = "color 3")
	private CCVector3 _cColor3 = new CCVector3(300,300,300);

	@CCProperty(name = "pos 0")
	private CCVector3 _cPos0 = new CCVector3(-30,30,30);
	@CCProperty(name = "pos 1")
	private CCVector3 _cPos1 = new CCVector3(30,30,30);
	@CCProperty(name = "pos 2")
	private CCVector3 _cPos2 = new CCVector3(-30,-30,30);
	@CCProperty(name = "pos 3")
	private CCVector3 _cPos3 = new CCVector3(30,-30,30);
	
	private CCSphereMesh _cSphereMesh;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cLightShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "pbr_vertex.glsl"),
			CCNIOUtil.classPath(this, "pbr_fragment.glsl")
		);
		
		_cCameraController = new CCCameraController(this, g, 100);
		
		_cSphereMesh = new CCSphereMesh(25, 50);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	int nrRows    = 7;
    int nrColumns = 7;
    double spacing = 55;

	@Override
	public void display(CCGraphics g) {
		g.clearColor(0.1, 0.1, 0.1, 1.0);
		g.clear();
		_cCameraController.camera().draw(g);
		_cLightShader.start();
		_cLightShader.uniform3f("camPos", 0,0,0);
		for (int row = 0; row < nrRows; ++row) {
			_cLightShader.uniform1f("metallic", (float)row / (float)nrRows);
            for (int col = 0; col < nrColumns; ++col) {
                // we clamp the roughness to 0.025 - 1.0 as perfectly smooth surfaces (roughness of 0.0) tend to look a bit off
                // on direct lighting.
            	_cLightShader.uniform1f("roughness", CCMath.clamp((float)col / (float)nrColumns, 0.05f, 1.0f));
            	_cLightShader.uniform3fv("lightColors", _cColor0, _cColor1, _cColor2, _cColor3);
            	_cLightShader.uniform3fv("lightPositions", _cPos0, _cPos1, _cPos2, _cPos3);
                g.pushMatrix();
            	g.translate(
                    (float)(col - (nrColumns / 2)) * spacing, 
                    (float)(row - (nrRows / 2)) * spacing, 
                    0.0f
                );
//                shader.setMat4("model", model);
                _cSphereMesh.draw(g);
                g.popMatrix();
            }
        }
		_cLightShader.end();
	}

	public static void main(String[] args) {

		CCBasicPBR demo = new CCBasicPBR();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

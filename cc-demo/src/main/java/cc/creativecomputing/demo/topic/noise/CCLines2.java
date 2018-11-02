package cc.creativecomputing.demo.topic.noise;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCLines2 extends CCGL2Adapter {
	
	private CCVBOMesh _myMesh;
	
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	@CCProperty(name = "attributes")
	private CCDrawAttributes _cAttributes = new CCDrawAttributes();
	
	
	@CCProperty(name = "LinesShader")
	private CCGLProgram _cLinesShader;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, 1000000);
		
		for(int i = 0; i < _myMesh.numberOfVertices();i++) {
			_myMesh.addVertex(CCMath.norm(i, 0, _myMesh.numberOfVertices() - 1), CCMath.random());
		}
		
		_cCameraController = new CCCameraController(this, g, 100);
		
					
		_cLinesShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "lines2_vertex.glsl"),
			CCNIOUtil.classPath(this, "lines2_fragment.glsl")
		);
	}
	
	private double _myTime;

	@Override
	public void update(CCAnimator theAnimator) {
		_myTime = theAnimator.time();
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);
		_cAttributes.start(g);
		_cLinesShader.start();
		_cLinesShader.uniform1f("time", _myTime);
		_myMesh.draw(g);
		_cLinesShader.end();
		_cAttributes.end(g);
	}

	public static void main(String[] args) {

		CCLines2 demo = new CCLines2();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 1200);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

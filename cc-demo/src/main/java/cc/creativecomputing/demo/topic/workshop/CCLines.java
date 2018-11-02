package cc.creativecomputing.demo.topic.workshop;

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
import jogamp.opengl.ThreadingImpl;

public class CCLines extends CCGL2Adapter {
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	@CCProperty(name = "LineSHader")
	private CCGLProgram _cLineSHader;
	
	private CCVBOMesh _myMesh;
	
	@CCProperty(name = "attributes")
	private CCDrawAttributes _cAttributes = new CCDrawAttributes();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cCameraController = new CCCameraController(this, g, 100);
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, 1000000);
		
		for(int i = 0; i < _myMesh.numberOfVertices(); i ++) {
			_myMesh.addVertex(CCMath.norm(i, 0, _myMesh.numberOfVertices()), CCMath.random());
		}
		
		
		_cLineSHader = new CCGLProgram(
			CCNIOUtil.classPath(this, "lines_vertex.glsl"),
			CCNIOUtil.classPath(this, "lines_fragment.glsl")
		);
	}
	
	private double _myTime = 0;

	@Override
	public void update(CCAnimator theAnimator) {
		_myTime = theAnimator.time();
	}

	@Override
	public void display(CCGraphics g) {
		_cCameraController.camera().draw(g);
		
		g.clear();
		_cAttributes.start(g);
		_cLineSHader.start();
		_cLineSHader.uniform1f("time", _myTime);
		_myMesh.draw(g);
		_cLineSHader.end();
		_cAttributes.end(g);
	}

	public static void main(String[] args) {

		CCLines demo = new CCLines();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

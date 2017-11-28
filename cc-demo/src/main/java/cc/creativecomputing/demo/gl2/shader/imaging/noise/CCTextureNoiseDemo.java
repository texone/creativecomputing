package cc.creativecomputing.demo.gl2.shader.imaging.noise;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.imaging.filter.CCNormalMap;
import cc.creativecomputing.graphics.shader.imaging.noise.CCNoiseGenerator;
import cc.creativecomputing.io.CCNIOUtil;

public class CCTextureNoiseDemo extends CCGL2Adapter {

	@CCProperty(name = "noise")
	private CCNoiseGenerator _cVoronoise;

	@CCProperty(name = "normalmap")
	private CCNormalMap _cNormalMap;
	
	private static enum CCDrawTex{
		VORONOI,
		NORMALMAP
	}
	
	@CCProperty(name = "draw tex")
	private CCDrawTex _cDrawTex = CCDrawTex.VORONOI;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cVoronoise = new CCNoiseGenerator(g.width(), g.height());
		
		_cNormalMap = new CCNormalMap(g.width(), g.height());
		_cNormalMap.inputChannel(0, _cVoronoise.output());
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cVoronoise.display(g);
		_cNormalMap.display(g);
		g.ortho2D();
		switch(_cDrawTex){
		case VORONOI:
			g.image(_cVoronoise.output(), 0, 0);
			break;
		case NORMALMAP:
			g.image(_cNormalMap.output(), 0, 0);
			break;
		}
		
	}

	public static void main(String[] args) {

		CCTextureNoiseDemo demo = new CCTextureNoiseDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1680, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

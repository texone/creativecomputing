package cc.creativecomputing.demo.effectables;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;

public class CCEffectableTri extends CCGL2Adapter {
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);
		
		int dim = 2;
		int edgeLength = 100;
		int offset = dim / 2 * edgeLength;
		
		g.beginShape(CCDrawMode.LINES);
		for(int x = 0; x <= dim;x++) {
			for(int y = 0; y <= dim;y++) {
				for(int z = 0; z <= dim;z++) {
					if(x < dim) {
						g.vertex(x * edgeLength - offset,y * edgeLength - offset,z * edgeLength - offset);
						g.vertex((x + 1) * edgeLength - offset,y * edgeLength - offset,z * edgeLength - offset);
					}
					
					g.vertex(x * edgeLength - offset,y * edgeLength - offset,z * edgeLength - offset);
					g.vertex((x + 1) * edgeLength - offset,(y + 1) * edgeLength - offset,z * edgeLength - offset);
					
					if(y < dim) {
						g.vertex(x * edgeLength - offset,y * edgeLength - offset,z * edgeLength - offset);
						g.vertex(x * edgeLength - offset,(y + 1) * edgeLength - offset,z * edgeLength - offset);
					}
					
					if(z < dim) {
						g.vertex(x * edgeLength - offset,y * edgeLength - offset,z * edgeLength - offset);
						g.vertex(x * edgeLength - offset,y * edgeLength - offset,(z + 1) * edgeLength - offset);
					}
				}
			}
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCEffectableTri demo = new CCEffectableTri();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

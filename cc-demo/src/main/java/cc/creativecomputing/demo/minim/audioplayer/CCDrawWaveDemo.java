package cc.creativecomputing.demo.minim.audioplayer;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioSample;
import cc.creativecomputing.sound.CCSoundIO;

/**
 * This sketch demonstrates the difference between linearly spaced averages and
 * logarithmically spaced averages.
 * <p>
 * From top to bottom:<br/>
 * - The full spectrum.<br/>
 * - The spectrum grouped into 30 linearly spaced averages.<br/>
 * - The spectrum grouped logarithmically into 10 octaves, each split into 3
 * bands.
 * <p>
 * For more information about Minim and additional features, visit
 * http://code.compartmental.net/minim/
 */
public class CCDrawWaveDemo extends CCGL2Adapter {

	CCSoundIO minim;
	CCAudioSample song;
	
	@CCProperty(name = "scale", min = 1, max = 100)
	private int _cScale = 1;
	
	
	@Override
	public void start(CCAnimator theAnimator) {
		minim = new CCSoundIO();
		
		song = CCSoundIO.loadSample(CCNIOUtil.dataPath("sound/fair1939.wav"));
	
		// loop the file
		
		
		CCLog.info("start");
	}
	
	

	@Override
	public void init(CCGraphics g) {
		
	
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		// use the mix buffer to draw the waveforms.
		g.pushMatrix();
		g.translate(-g.width() / 2, -g.height() / 4);
		CCLog.info(song.getChannel(0).length);
		g.color(255);
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		for (int i = 0; i < song.getChannel(0).length - _cScale && i / _cScale < g.width(); i += _cScale) {
			float value0 = 0;
			float value1 = 0;
			for(int j = 0;j<_cScale;j++){
				 value0 = CCMath.max(song.getChannel(0)[i + j], value0);
				 value1 = CCMath.min(song.getChannel(0)[i + j], value1);
			}
//			g.line(i / _cScale, 50 - song.getChannel(0)[i] * 50, i / _cScale + 1, 50 - song.getChannel(0)[i + _cScale] * 10);
			g.vertex(i / _cScale,  value0 * 50);
			g.vertex(i / _cScale,  value1 * 50);
		}
		g.endShape();
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCDrawWaveDemo demo = new CCDrawWaveDemo();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

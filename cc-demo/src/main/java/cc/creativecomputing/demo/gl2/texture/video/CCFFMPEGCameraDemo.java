package cc.creativecomputing.demo.gl2.texture.video;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCIOUtil;
//import cc.creativecomputing.video.ffmpeg.FFMPEGVideoPlayer;

public class CCFFMPEGCameraDemo extends CCGL2Adapter {
//	FFMPEGVideoPlayer videoPlayer;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
//		videoPlayer = FFMPEGVideoPlayer.fromFile("C:\\Users\\chris\\dev\\creativecomputing\\cc-demo\\data\\videos\\sintel_trailer-1080p.mp4");
//        videoPlayer.start();
	}

	@Override
	public void update(CCAnimator theAnimator) {
//		 videoPlayer.next();
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
	}

	public static void main(String[] args) {

		CCFFMPEGCameraDemo demo = new CCFFMPEGCameraDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

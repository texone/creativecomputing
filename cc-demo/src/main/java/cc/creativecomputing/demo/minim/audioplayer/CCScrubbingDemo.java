package cc.creativecomputing.demo.minim.audioplayer;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.gl.app.events.CCMouseAdapter;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioPlayer;
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
public class CCScrubbingDemo extends CCGL2Adapter {

	CCSoundIO minim;
	CCAudioPlayer song;
	
	
	@CCProperty(name = "spectrum scale", min = 0, max = 10)
	private double _cSpectrumScale = 10;
	
	@CCProperty(name = "average scale", min = 0, max = 10)
	private double _cAverageScale = 10;
	
	int mouseX, mouseY;
	
	@Override
	public void start(CCAnimator theAnimator) {
		minim = new CCSoundIO();
		
		song = minim.loadFile(CCNIOUtil.dataPath("sound/fair1939.wav"), 512);
		
		// loop the file
		
		
		CCLog.info("start");
	}
	
	@CCProperty(name = "rewind")
	private boolean _cRewind = false;
	@CCProperty(name = "forward")
	private boolean _cForward = false;
	
	@CCProperty(name = "play")
	public void play(boolean thePlay){
		if ( !thePlay )
	    {
	      
	        song.pause();
	    } else
	      {
	        song.loop();
	      }
	}

	@Override
	public void init(CCGraphics g) {
		
		mouseListener().add(new CCMouseAdapter() {
			@Override
			public void mouseMoved(CCMouseEvent theMouseEvent) {
				mouseX = theMouseEvent.x();
				mouseY = theMouseEvent.y();
				
				CCLog.info(mouseX + " : " + mouseY);
			}
		});
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		// if the rewind button is currently being pressed
	    if (_cRewind)
	    {
	      // get the current song position
	      int pos = song.position();
	      // if it greater than 200 milliseconds
	      if ( pos > 200 )
	      {
	        // rewind the song by 200 milliseconds
	        song.skip(-200);
	      }
	      else
	      {
	        // if the song hasn't played more than 100 milliseconds
	        // just rewind to the beginning
	        song.rewind();
	      }
	    }
	    
	 // if the forward button is currently being pressed
	    if (_cForward)
	    {
	      // get the current position of the song
	      int pos = song.position();
	      // if the song's position is more than 40 milliseconds from the end of the song
	      if ( pos < song.length() - 40 )
	      {
	        // forward the song by 40 milliseconds
	        song.skip(40);
	      }
	      else
	      {
	        // otherwise, cue the song at the end of the song
	        song.cue( song.length() );
	      }
	      // start the song playing
	      song.play();
	    }
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		// use the mix buffer to draw the waveforms.
		g.pushMatrix();
		g.translate(-g.width() / 2, -g.height() / 2);
		float centerFrequency = 0;

		g.color(255);
		  for (int i = 0; i < song.bufferSize() - 1;  i++){
		    g.line(i, 50 - song.left.get(i)*50, i+1, 50 - song.left.get(i+1)*10);
		  }
		  // draw the position in the song
		  // the position is in milliseconds,
		  // to get a meaningful graphic, we need to map the value to the range [0, width]
		  float x = CCMath.map(song.position(), 0, song.length(), 0, g.width());
		  g.color(255, 0, 0);
		  g.line(x, 50 - 20, x, 50 + 20);

		g.popMatrix();
	}

	public static void main(String[] args) {
		CCScrubbingDemo demo = new CCScrubbingDemo();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

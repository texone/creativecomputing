package cc.creativecomputing.minim.demos.audioplayer;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyEvent.CCKeyCode;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.minim.CCMinimConnector;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;

/**
 * This sketch demonstrates how to use the <code>loop</code> method of a
 * <code>Playable</code> class. The class used here is <code>AudioPlayer</code>,
 * but you can also loop an <code>AudioSnippet</code>. When you call
 * <code>loop()</code> it will make the <code>Playable</code> playback in an
 * infinite loop. If you want to make it stop looping you can call
 * <code>play()</code> and it will finish the current loop and then stop. Press
 * 'l' to start the player looping.
 * 
 */
public class Play extends CCApp {

	Minim minim;
	AudioPlayer groove;

	@Override
	public void setup() {
		minim = new Minim(new CCMinimConnector());
		groove = minim.loadFile(CCIOUtil.dataPath("bodyScan_selectItemMainMenu-combi.wav"), 2048);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();

		g.color(255);

		for (int i = 0; i < groove.bufferSize() - 1; i++) {
			g.line(i, 50 + groove.left.get(i) * 50, i + 1, 50 + groove.left.get(i + 1) * 50);
			g.line(i, 150 + groove.right.get(i) * 50, i + 1, 150 + groove.right.get(i + 1) * 50);
		}
	}

	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		if (theKeyEvent.keyCode() == CCKeyCode.VK_P)
			groove.play();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(Play.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

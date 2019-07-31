package cc.creativecomputing.demo.topic.simulation.yellowtail;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;

/**
 * Yellowtail by Golan Levin (www.flong.com).
 * 
 * Click, drag, and release to create a kinetic gesture.
 * 
 * Yellowtail (1998-2000) is an interactive software system for the gestural
 * creation and performance of real-time abstract animation. Yellowtail repeats
 * a user's strokes end-over-end, enabling simultaneous specification of a
 * line's shape and quality of movement. Each line repeats according to its own
 * period, producing an ever-changing and responsive display of lively,
 * worm-like textures.
 */
public class CCYellowTail extends CCGL2Adapter {

	private List<CCGesture> _myGestures = new ArrayList<>();
	final int minMove = 0; // Minimum travel for a new point
	CCGesture currentGesture;

	boolean mousePressed = false;

	@CCProperty(name = "load")
	void loadStrokes() {
		_myGestures.clear();
		CCDataElement myXML = CCXMLIO.createXMLElement(CCNIOUtil.dataPath("strokes.xml"));
		for (CCDataElement myStrokeXML : myXML) {
			CCGesture myStroke = new CCGesture(width, height);
			for (CCDataElement myPointXML : myStrokeXML) {
				// if(myPointXML.getFloat("x") == 0 && myPointXML.getFloat("y") == 0)continue;
				myStroke.addPoint(myPointXML.doubleAttribute("x"), myPointXML.doubleAttribute("y"),
						myPointXML.doubleAttribute("p"));
			}
			_myGestures.add(myStroke);
		}
	}
	@CCProperty(name = "screencapture")
	private CCScreenCaptureController _cScreenCaptureController;

	private int width;
	private int height;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cScreenCaptureController = new CCScreenCaptureController(this);
		
		currentGesture = null;

		mousePressed().add(e -> {
			currentGesture = new CCGesture(g.width(), g.height());
			currentGesture.addPoint(e.x(), e.y());

			mousePressed = true;
		});

		mouseReleased().add(e -> {
			mousePressed = false;
			_myGestures.add(currentGesture);
			currentGesture = null;
		});

		mouseDragged().add(e -> {
			if (currentGesture == null)
				return;
			if (currentGesture.distToLast(e.x(), e.y()) <= minMove)
				return;

			currentGesture.addPoint(e.x(), e.y());
			currentGesture.smooth();
			currentGesture.compile();

		});

		keyPressed().add(e -> {

			switch (e.keyCode()) {
			case VK_SPACE:
				_myGestures.clear();
				break;
			default:
				break;
			}
		});
		
		width = g.width();
		height = g.height();

		loadStrokes();
	}

	@CCProperty(name = "update")
	public boolean _cUpdate = false;

	@CCProperty(name = "progress", min = 0, max = 1)
	public double progress = 0;

	@Override
	public void update(CCAnimator theAnimator) {
		// if(_cUpdate)
		for (int i = 0; i < _myGestures.size() * progress; i++) {
			_myGestures.get(i).update(theAnimator);
		}
		// _myGestures.forEach(gesture -> gesture.update(theAnimator));
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
//		g.polygonMode(CCPolygonMode.LINE);
		g.color(255, 100);
		_myGestures.forEach(gesture -> gesture.display(g));

		if (currentGesture != null)
			currentGesture.display(g);
		;
	}

	public static void main(String[] args) {

		CCYellowTail demo = new CCYellowTail();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1800, 1368);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

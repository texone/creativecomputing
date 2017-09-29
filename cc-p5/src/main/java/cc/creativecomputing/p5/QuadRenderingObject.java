package cc.creativecomputing.p5;


import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import processing.core.PApplet;

public class QuadRenderingObject extends PApplet {
	
	private CCP5Controller _myController;
	
	@CCProperty(name = "x", desc = "x value for the rectangle", min = 0, max = 500)
	private float _cX = 300;
	@CCProperty(name = "y")
	private float _cY = 300;
	@CCProperty(name = "width")
	private float _cWidth = 300;
	@CCProperty(name = "height")
	private float _cHeight = 300;

	public void setup() {

		noStroke();
		fill(0, 100);
		_myController = new CCP5Controller(this);
	}

	public void draw() {
		background(255);
		rect(_cX, _cY, _cWidth, _cHeight);
	}

	public void settings() {
		size(800, 600);
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { QuadRenderingObject.class.getName() };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}

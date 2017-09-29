package cc.creativecomputing.p5;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.spline.CCBezierSpline;
import cc.creativecomputing.math.spline.CCSpline;
import processing.core.PApplet;

public class ColorRendering extends PApplet {
	
	private CCP5Controller _myController;
	
	@CCProperty(name = "color")
	private CCColor _cColor = new CCColor();

	@CCProperty(name = "gradient")
	private CCGradient _cGradient = new CCGradient();
	
	public void setup() {
		_myController = new CCP5Controller(this);
	}

	public void draw() {
		background(255);
		
		noStroke();
		fill(_cColor.toInt());
		rect(100,100,width - 200, 100);
		noFill();
		for(int x = 100; x < width - 100; x++) {
			double myBlend = norm(x, 100, width - 100);
			stroke(_cGradient.color(myBlend).toInt());
			line(x, 300, x, 500);
		}
	}

	public void settings() {
		size(800, 600);
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { ColorRendering.class.getName() };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}

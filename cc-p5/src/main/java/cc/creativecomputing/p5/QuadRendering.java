package cc.creativecomputing.p5;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import processing.core.PApplet;

public class QuadRendering extends PApplet {
	
	private CCP5Controller _myController;
	
	private class Quad{
		@CCProperty(name = "x", min = 0, max = 500)
		private float _cX = random(0,500);
		@CCProperty(name = "y", min = 0, max = 500)
		private float _cY = random(0,500);
        @CCProperty(name = "width", min = 0, max = 200)
		private float _cWidth = random(0,200);
        @CCProperty(name = "height", min = 0, max = 200)
		private float _cHeight = random(0,200);
		@CCProperty(name = "color")
		private CCColor _cColor = new CCColor();
		
		
		public void draw() {
			fill(_cColor.toInt());
			rect(_cX, _cY, _cWidth, _cHeight);
		}
	}
	
	private class QuadCont {
		  @CCProperty(name = "quad")
		    private Quad _cQuad = new Quad();
		}
	
	@CCProperty(name = "container")
	  private QuadCont _cContainer = new QuadCont();
	

	@CCProperty(name = "gradient")
	private CCGradient _cGradient = new CCGradient();
	
	public void setup() {
		
		
		_myController = new CCP5Controller(this);
	}

	public void draw() {
		background(255);
		_cContainer._cQuad.draw();
	}

	public void settings() {
		size(800, 600);
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { QuadRendering.class.getName() };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}

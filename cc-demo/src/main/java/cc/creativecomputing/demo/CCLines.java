package cc.creativecomputing.demo;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCLines extends CCGL2Adapter{
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	
	@Override
	public void init(CCGraphics g) {
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	CCVector2 r2d(CCVector2 x, double a)
    {
	    a *=  CCMath.TWO_PI;
	    return new CCVector2(
	    	CCMath.cos(a) * x.x + CCMath.sin(a) * x.y, 
	    	CCMath.cos(a) * x.y - CCMath.sin(a) * x.x
        );
    }
	
	CCVector3 sphere(double a, double b) {
		a =  -(a - 0.5) * CCMath.PI;
		b *=  CCMath.TWO_PI;
		double x = CCMath.sin(b) * CCMath.cos(a); 
		double y = CCMath.sin(a);
		double z = CCMath.cos(b) * CCMath.cos(a);
    	
    	return new CCVector3(x,y,z);
	}
	
	@Override
	public void display(CCGraphics g) {
		_cCameraController.camera().draw(g);
		g.clearColor(CCColor.BLACK);
		g.clear();

		g.color(CCColor.RED);
		g.pushMatrix();
		g.pointSize(2);
		g.scale(200);
		g.beginShape(CCDrawMode.POINTS);
		for(float x = 0; x <= 1; x+=0.02) {
			for(float y = 0; y <= 1; y+=0.02) {
				g.vertex(x,y);
			}
		}
		g.endShape();
		
		g.color(CCColor.BLUE);
		g.beginShape(CCDrawMode.POINTS);
		for(float x = 0; x <= 1; x+=0.02) {
			for(float y = 0; y <= 1; y+=0.02) {
				CCVector3 pw = new CCVector3(0,0,1);
				CCVector2 yz = r2d(pw.yz(), - (y - 0.5) * 0.5);
				pw.y = yz.x;
				pw.z = yz.y;
				CCVector2 xz = r2d(pw.xz(), x);
				pw.x = xz.x;
				pw.z = xz.y;
				
				g.vertex(pw);
			}
		}
		g.endShape();
		
		g.color(CCColor.GREEN);
		g.beginShape(CCDrawMode.POINTS);
		for(float x = 0; x <= 1; x+=0.02) {
			for(float y = 0; y <= 1; y+=0.02) {
				g.vertex(sphere(y,x));
			}
		}
		g.endShape();
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		
		
		CCLines demo = new CCLines();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

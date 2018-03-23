package cc.creativecomputing.demo.mapping;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCRay3;
import cc.creativecomputing.math.CCVector3;

class CCQuadMapping{
	@CCProperty(name = "res", min = 30, max = 360)
	private int res = 30;
	
	@CCProperty(name = "rings", min = 4, max = 40)
	private int rings = 10;
	
	@CCProperty(name = "parts", min = 1, max = 10)
	private int parts = 1;
	
	@CCProperty(name = "connections", min = 2, max = 5)
	private int connections = 2;
	
	@CCProperty(name = "element alpha", min = 0, max = 1)
	private double elementAlpha = 0.5;

	@CCProperty(name = "alpha", min = 0, max = 1)
	private double alpha = 0.5;
	
	@CCProperty(name = "distance 0", min = 0, max = 2000)
	private double _cDistance0 = 0;
	
	@CCProperty(name = "distance 1", min = 0, max = 2000)
	private double _cDistance1 = 200;
	
	@CCProperty(name = "radius 0", min = 0, max = 200)
	private double _cRadius0;
	@CCProperty(name = "radius 1", min = 0, max = 200)
	private double _cRadius1;
	@CCProperty(name = "random 0", min = 0, max = 2)
	private double _cRandom;
	
	private class CCRing{
		CCPlane _myPlane = new CCPlane();
		
		List<CCVector3> _myPoints = new ArrayList<>();
		
		private double _myY0;
		private double _myY1;
		
		private double _myZ0;
		private double _myZ1;
		
		public void update(){
			_myPoints.clear();
			for(double i = 0; i <= res;i++){
				double myBlend = i / res;
				
				double y = CCMath.blend(_myY0, _myY1, myBlend);
				
				CCRay3 myRay0 = new CCRay3(new CCVector3(0,0,0), new CCVector3(200,y,_myZ0));
				CCRay3 myRay1 = new CCRay3(new CCVector3(0,0,0), new CCVector3(200,y,_myZ1));
					
				_myPoints.add(myRay0.intersectsPlane(_myPlane));
				_myPoints.add(myRay1.intersectsPlane(_myPlane));
				
				
			}
		}
		
		private void pointLines(CCGraphics g, CCVector3 thePoint0, CCVector3 thePoint1){
			if(thePoint0 == null || thePoint1 == null)return;
			g.vertex(0, thePoint0.y, thePoint0.z);
			g.vertex(thePoint0);

			g.vertex(0, thePoint1.y, thePoint1.z);
			g.vertex(thePoint1);
		}
		
		public void draw(CCGraphics g){
			if(_myPoints.size() < 2)return;
			g.color(1, elementAlpha);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(CCVector3 myPoint:_myPoints){
				if(myPoint == null)continue;
				g.vertex(myPoint);
			}
			g.endShape();

			g.color(1, alpha);
			int step = _myPoints.size() / connections;
			g.beginShape(CCDrawMode.LINES);
			for(int i = 0; i < _myPoints.size() - 1;i+=step){
				pointLines(g,  _myPoints.get(i), _myPoints.get(i+1));
			}
			pointLines(g,  
				_myPoints.get(_myPoints.size() - 1),
				_myPoints.get(_myPoints.size() - 2)
			);
			g.endShape();
		}
	}
	
	private List<CCRing> _myPlanes = new ArrayList<>();
	
	public void update(CCAnimator theAnimator) {
		if (theAnimator.time() > 5) {
			_myPlanes.clear();
			CCMath.randomSeed(0);
			for (double r = 0; r < rings; r++) {
				double myBlend = r / (rings - 1d);
				double myBlend0 = r / (rings);
				double myBlend1 = (r + 1) / (rings);
				double d = CCMath.blend(_cDistance0, _cDistance1, myBlend);
				double myAdd = CCMath.random();
				for (double p = 0; p < parts; p++) {
					CCRing myRing = new CCRing();
					myRing._myZ0 = CCMath.blend(-_cRadius1/2, _cRadius1/2, p / parts);
					myRing._myZ1 = CCMath.blend(-_cRadius1/2, _cRadius1/2,(p + 1) / parts);
					myRing._myPlane.setOriginNormal(new CCVector3(d,0,0), new CCVector3(1,0,0).addLocal(new CCVector3().randomize().multiply(_cRandom)));
					myRing._myY0 = CCMath.blend(-_cRadius1/2, _cRadius1/2, myBlend0);
					myRing._myY1 = CCMath.blend(-_cRadius1/2, _cRadius1/2, myBlend1);
					myRing.update();
					_myPlanes.add(myRing);
				}
			}
		}
	}
	
	public void display(CCGraphics g){
		double myRadius = CCMath.max(_cRadius0, _cRadius1);
		g.line(0,0,0,200,-myRadius, -myRadius);
		g.line(0,0,0,200, myRadius, -myRadius);
		g.line(0,0,0,200, myRadius,  myRadius);
		g.line(0,0,0,200,-myRadius,  myRadius);
		for(CCRing myRing:_myPlanes){
			myRing.draw(g);
		}
	}
	
}
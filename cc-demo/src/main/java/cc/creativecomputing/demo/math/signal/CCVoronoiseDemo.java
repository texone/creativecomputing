package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

//This is a procedural pattern that has 2 parameters, that generalizes cell-noise,
//perlin-noise and voronoi, all of which can be written in terms of the former as:
//
//cellnoise(x) = pattern(0,0,x)
//perlin(x) = pattern(0,1,x)
//voronoi(x) = pattern(1,0,x)
//
//From this generalization of the three famouse patterns, a new one (which I call
//"Voronoise") emerges naturally. It's like perlin noise a bit, but within a jittered
//grid like voronoi):
//
//voronoise(x) = pattern(1,1,x)
//
//Not sure what one would use this generalization for, because it's slightly slower
//than perlin or voronoise (and certainly much slower than cell noise), and in the
//end as a shading TD you just want one or another depending of the type of visual
//features you are looking for, I can't see a blending being needed in real life.
//But well, if only for the math fun it was worth trying. And they say a bit of
//mathturbation can be healthy anyway!


//Use the mouse to blend between different patterns:

//ell noise    u=0,v=0
//voronoi      u=1,v=0
//perlin noise u=0,v1=
//voronoise    u=1,v=1

//More info here: http://iquilezles.org/www/articles/voronoise/voronoise.htm
public class CCVoronoiseDemo extends CCGL2Adapter {
	
//	CCVector3 hash3( CCVector2 p ){
//		CCVector3 q = new CCVector3(
//			p.dot(new CCVector2(127.1,311.7)),
//	        p.dot(new CCVector2(269.5,183.3)),
//	        p.dot(new CCVector2(419.2,371.9))
//		);
//	    return fract(CCMath.sin(q) * 43758.5453);
//	}
//
//	float noise01( CCVector2 x, CCVector3 uv ){
//		CCVector2 p = floor(x);
//		CCVector2 f = fract(x);
//	    
//	    float k = 1.0+63.0*pow(1.0-uv.y,4.0);
//	    
//	    double va = 0.0;
//	    double wt = 0.0;
//	    for( int j=-2; j<=2; j++ )
//	        for( int i=-2; i<=2; i++ )
//	        {
//	        	CCVector2 g = vec2( float(i),float(j) );
//	        	CCVector3 o = hash3( p.add(g) )*vec3(uv.x,uv.x,1.0);
//	        	CCVector2 r = g - f + o.xy;
//	            float d = dot(r,r);
//	            float ww = pow( 1.0-smoothstep(0.0,1.6,sqrt(d)), k );
//	            va += o.z * ww;
//	            wt += ww;
//	        }
//	    
//	    return va/wt;
//	}

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCVoronoiseDemo demo = new CCVoronoiseDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}



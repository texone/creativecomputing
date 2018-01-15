package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.spline.CCSpline;

public class CCEnvelopeDemo extends CCGL2Adapter{
	
	public interface CCRealtimeGraph extends CCCompileObject{
		void draw(CCGraphics g);
	}
	
	@CCProperty(name = "envelope")
	private CCEnvelope _myEnvelope = new CCEnvelope();
	
	@CCProperty(name = "catmulrom spline")
	private CCSpline _myCatmulromSpline = new CCCatmulRomSpline(1.0, false);
	
	@CCProperty(name = "clear color")
	private CCColor _myClearColor = new CCColor();
	
	@CCProperty(name = "vector")
	private CCVector3 _cVector = new CCVector3();
	
	@CCProperty(name = "stringr")
	private String _myString = "texone";
	
	@CCProperty(name = "text x")
	private float _cTextX = 0;
	
	@CCProperty(name = "draw text")
	private boolean _cDrawText = true;
	
	@CCProperty(name = "draw mode")
	private CCDrawMode _myDrawMode = CCDrawMode.POINTS;
	
	@CCProperty(name = "texture asset")
	private CCTexture2DAsset _myAsset;
	
	@CCProperty(name = "diagram x", min = 0, max = 1000)
	private double _cDiagramX = 0;

	@CCProperty(name = "diagram y", min = 0, max = 500)
	private double _cDiagramY = 0;
	
	@CCProperty(name = "diagram width", min = 0, max = 200)
	private double _cDiagramWitdh = 0;

	@CCProperty(name = "diagram height", min = 0, max = 200)
	private double _cDiagramHeight = 0;
	
	
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(CCGraphics g) {

		
		
		_myAsset = new CCTexture2DAsset(glContext());
		
//		for(CCRealtimeGraph myGraph:_myRealTimeGraph.instances()){
//			CCLog.info(myGraph);
//			if(myGraph == null)return;
//			myGraph.draw(g);
//		}
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
	}
	
	@CCProperty(name = "trigger")
	public void triggerDemo(){
		CCLog.info("EVENT");
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(_myClearColor);
		g.clear();
		
		g.rect(_cDiagramX, _cDiagramY, _cDiagramWitdh, _cDiagramHeight);
		
//		for(CCRealtimeGraph myGraph:_myRealTimeGraph.instances()){
////			CCLog.info(myGraph);
//			if(myGraph == null)continue;
//			myGraph.draw(g);
//		}
//		g.color(255);
//		
//		g.ortho();
//		g.color(1d);
//		g.beginShape(_myDrawMode);
//		for(int i = 0; i < 1000;i++){
//			g.vertex(i,CCMath.random(200,400));// _myEnvelope.value(i / 1000f) * 200
//		}
//		g.endShape();
		
//		if(_cDrawText)g.text(_myString,_cTextX,0);
//		
////		if(_myAsset == null)return;
////		g.image(_myAsset.value(), 0, 0);
//		g.color(255);
////		g.rect(-g.width() / 4, - g.height() / 4, _cRectWidth, g.height() / 2);
//		
////		CCLog.info(g.width + ":" + g.height);
	}
	
	public static void main(String[] args) {
		
		
		CCEnvelopeDemo demo = new CCEnvelopeDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

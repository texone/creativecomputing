package cc.creativecomputing.demo.protocol.serial.hpgl;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCLine2;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL.CCHPPaperFormat;

public class CCWhiteHouse extends CCGL2Adapter {
	
	@CCProperty(name = "HP")
	private CCHPGL _cHPGL;
	
	private CCImage _myImage;
	private CCTexture2D _myTExture;

	private List<CCLine2> _myLines = new ArrayList<>();
	
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 0;
	@CCProperty(name = "line alpha", min = 0, max = 1)
	private double _cLineAlpha = 1;
	
	@CCProperty(name ="line per frame")
	private int _cLinesPerFrame = 10;
	@CCProperty(name ="radius")
	private int _cRadius = 10;
	@CCProperty(name = "pow", min = 0, max = 10)
	private double _cPow = 0;
	@CCProperty(name = "min", min = 0, max = 1)
	private double _cMin = 0;
	@CCProperty(name = "max", min = 0, max = 1)
	private double _cMax = 0;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cHPGL = new CCHPGL(CCHPPaperFormat.A4);
		_myImage = CCImageIO.newImage(CCNIOUtil.dataPath("textures/whitehouse.jpg"));
		_myTExture = new CCTexture2D(_myImage);
	}
	
	@CCProperty(name = "reset")
	public void reset(){
		_myLines.clear();
	}

	@Override
	public void update(CCAnimator theAnimator) {
		//,200,,7400
		if(_cHPGL.isConnected()){
//			_cHPGL.lineTo((int)CCMath.random(430,10430), (int)CCMath.random(200,7400));

			_cHPGL.line(CCMath.random(430,10430), CCMath.random(200,7400), CCMath.random(430,10430), CCMath.random(200,7400));
			
//			int radius = (int)CCMath.random(1000);
//			int x = (int)CCMath.random(430 + radius,10430 - radius);
//			int y = (int)CCMath.random(200 + radius,7400 - radius);
//			_cHPGL.circle(x, y, radius);
		}
		
		for(int i = 0; i < _cLinesPerFrame;i++ ){
			double x = CCMath.random(0, _myTExture.width());
			double y = CCMath.random(0, _myTExture.height());
			
			double myBrightness = CCMath.pow(CCMath.saturate(CCMath.norm(_myImage.getPixel(x, y).brightness(), _cMin, _cMax)), _cPow);
			
			if(CCMath.random() > myBrightness){
				_myLines.add(new CCLine2(x,y,x + CCMath.random(-_cRadius,_cRadius),y + CCMath.random(-_cRadius,_cRadius)));
			}
		}
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho2D();
		g.color(1d);
		g.image(_myTExture, 0,0);
		g.color(1d, _cAlpha);
		g.rect(0, 0,1600,1067);
		
		g.color(0d,_cLineAlpha);
		for(CCLine2 myLine:_myLines){
			g.line(myLine.start(), myLine.end());
		}
	}

	public static void main(String[] args) {

		CCWhiteHouse demo = new CCWhiteHouse();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1600, 1067);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}


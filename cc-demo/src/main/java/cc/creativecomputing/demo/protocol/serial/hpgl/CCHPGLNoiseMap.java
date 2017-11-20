package cc.creativecomputing.demo.protocol.serial.hpgl;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.signal.CCMixSignal;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL.CCHPGLCommand;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL.CCHPPaperFormat;

public class CCHPGLNoiseMap extends CCGL2Adapter {
	
	@CCProperty(name = "HP")
	private CCHPGL _cHPGL;
	@CCProperty(name = "signal")
	private CCMixSignal _cSignal = new CCMixSignal();
	@CCProperty(name = "xRes")
	private int _cXRes = 100;
	@CCProperty(name = "yRes")
	private int _cyRes = 100;
	@CCProperty(name = "xspace")
	private double _cXSpace = 10;
	@CCProperty(name = "ySpace")
	private double _cYSpace = 10;
	@CCProperty(name = "displace")
	private double _cDisplace = 10;
	
	@CCProperty(name = "x move")
	private double _cXMove = 0;
	@CCProperty(name = "y move")
	private double _cYMove = 0;
	@CCProperty(name = "speed")
	private int _cSpeed = 1;
	
	private List<List<CCVector2>> _myLines = new ArrayList<>();
	private List<CCVector2> _myCurrentVertices;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cHPGL = new CCHPGL(CCHPPaperFormat.A4);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		//,200,,7400
//		if(_cHPGL.isConnected()){
////			_cHPGL.lineTo((int)CCMath.random(430,10430), (int)CCMath.random(200,7400));
//
//			_cHPGL.line(CCMath.random(430,10430), CCMath.random(200,7400), CCMath.random(430,10430), CCMath.random(200,7400));
//			
////			int radius = (int)CCMath.random(1000);
////			int x = (int)CCMath.random(430 + radius,10430 - radius);
////			int y = (int)CCMath.random(200 + radius,7400 - radius);
////			_cHPGL.circle(x, y, radius);
//		}
		
		if(_myCurrentVertices == null && _myLines.size() > 0){
			_myCurrentVertices = _myLines.remove(0);
			CCVector2 myStart = _myCurrentVertices.remove(0);
			_cHPGL.moveTo(myStart.x * 10, myStart.y * 10);
		}
		if(_myCurrentVertices == null)return;
		
		CCLog.info(_cHPGL.isConnected() + ":" + _myCurrentVertices.size());
		
		_cHPGL.velocitySelect(_cSpeed);

		_cHPGL.write(CCHPGLCommand.VS, 0, _cSpeed);
		if(_cHPGL.isConnected() && _myCurrentVertices.size() > 0){
			CCVector2 v0 = _myCurrentVertices.remove(0);
			
			_cHPGL.lineTo(v0.x * 10, v0.y * 10);
		}
		
		if(_myCurrentVertices.size() == 0){
			_myCurrentVertices = null;
		}
	}
	
	boolean _cPlot = false;
	
	@CCProperty(name = "plot")
	public void plot(){
		_cPlot = true;
	}
	
	private void vertex(CCGraphics g, int x, int y){
		double myX = x * _cXSpace;
		double myY = y * _cYSpace;
		double myDisplaceX = (_cSignal.value(myX, myY) * 2 - 1) * _cDisplace;
		double myDisplaceY = (_cSignal.value(myX, myY, 1000) * 2 - 1) * _cDisplace;
		myX += myDisplaceX;
		myY += myDisplaceY;
		g.vertex(myX + 43 + _cXMove, myY + 20 + _cYMove);
		if(_cPlot)myLine.add(new CCVector2(myX + 43 + _cXMove, myY + 20 + _cYMove));
	}
	
	List<CCVector2> myLine;

	@Override
	public void display(CCGraphics g) {
		g.clearColor(1d);
		g.clear();
		
		g.ortho();
		g.color(0d);
		for(int y = 0;y < _cyRes;y++){
			g.beginShape(CCDrawMode.LINE_STRIP);
			if(_cPlot)myLine = new ArrayList<>();
			if(y%2 == 0){
				for(int x = 0;x < _cXRes;x++){
					vertex(g, x, y);
				}
			}else{
				for(int x = _cXRes - 1;x >= 0;x--){
					vertex(g, x, y);
				}
			}
			g.endShape();
			if(_cPlot)_myLines.add(myLine);
		}
		_cPlot = false;
	}

	public static void main(String[] args) {

		CCHPGLNoiseMap demo = new CCHPGLNoiseMap();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1086, 760);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}


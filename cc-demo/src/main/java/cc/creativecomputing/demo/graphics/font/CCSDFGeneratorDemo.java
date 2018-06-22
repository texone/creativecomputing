package cc.creativecomputing.demo.graphics.font;

import java.awt.image.BufferedImage;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCFontSettings;
import cc.creativecomputing.graphics.font.CCSignedDistanceField;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.util.CCDistanceFieldGenerator;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCSDFGeneratorDemo extends CCGL2Adapter {
	
	private CCImage _myImage;
	private CCImage _myFontImage;
	private CCTexture2D _myTexture;
	
	
	@CCProperty(name = "spread", min = 1, max = 20)
	private double _cSpread = 4;
	
	

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myImage = new CCImage(600, 300);
		
		
		_myTexture = new CCTexture2D(_myImage);
		
		String myFontName = "Arial-Black";
		float mySize = 32;
		
		CCFontSettings mySettings = new CCFontSettings(myFontName, mySize, true, CCCharSet.REDUCED_CHARSET);
		CCTextureMapFont myFont = CCFontIO.createTextureMapFont(mySettings);
		_myFontImage = myFont.image();
	}
	
	
	
	

	@Override
	public void update(CCAnimator theAnimator) {
		double[][] myImage = new double[_myImage.width()][_myImage.height()];
		for(int x = 0; x < _myImage.width();x++) {
			for(int y = 0; y < _myImage.height();y++) {
//				if(x % 40 > 20 && y % 40 > 20)_myImage.setPixel(x, y, new CCColor(1d,1d));
//				else _myImage.setPixel(x, y, new CCColor(1d,0d));
				myImage[x][y] = _myFontImage.getPixel(x, y).r;
;				_myImage.setPixel(x, y, new CCColor(_myFontImage.getPixel(x, y).r));
			}
		}
		CCSignedDistanceField mySDF = new CCSignedDistanceField();
		double[][] myOut = new double[_myImage.width()][_myImage.height()];
		mySDF.buildDistanceField(myOut, _cSpread, myImage, _myImage.width(), _myImage.height());
		
		for(int x = 0; x < _myImage.width();x++) {
			for(int y = 0; y < _myImage.height();y++) {
//				if(x % 40 > 20 && y % 40 > 20)_myImage.setPixel(x, y, new CCColor(1d,1d));
//				else _myImage.setPixel(x, y, new CCColor(1d,0d));
				
				_myImage.setPixel(x, y, new CCColor(1d, myOut[x][y]));
//;				_myImage.setPixel(x, y, );
			}
		}
//		CCDistanceFieldGenerator myGenerator = new CCDistanceFieldGenerator();
//		myGenerator.spread(_cSpread);
//		_myImage = myGenerator.generateDistanceField(_myImage);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(255,0,0);
		g.clear();
		_myTexture.data(_myImage);
		g.image(_myTexture, -_myTexture.width()/2, -_myTexture.height() / 2);
	}

	public static void main(String[] args) {

		CCSDFGeneratorDemo demo = new CCSDFGeneratorDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

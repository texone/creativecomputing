package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCEnttypoDemo extends CCGLApp{
	
	private CCTextField _myTextureField;
	private CCTextField _myVectorField;
	
	private CCTextureMapFont _myFont;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		
		_myTextureField = new CCTextField(_myFont = new CCTextureMapFont(CCCharSet.ENTYPO,CCNIOUtil.dataPath("fonts/entypo.ttf"), 40), "")
			.position(0, -100)
			.align(CCTextAlign.LEFT)
			.fontSize(40);
		
		_myVectorField = new CCTextField(new CCVectorFont(CCCharSet.ENTYPO,CCNIOUtil.dataPath("fonts/entypo.ttf"), 40), "")
			.position(100, -100)
			.align(CCTextAlign.LEFT)
			.fontSize(40);
			
	}
	
	private double _myRotation = 0;
	
	private int myChar;
	
	private int frame = 0;
	
	@Override
	public void update(CCGLTimer theTimer) {
//		double myFontSize = CCMath.map(CCMath.sin(theTimer.time()), -1, 1, 50, 150);
//		_myRotation = CCMath.map(CCMath.sin(theTimer.time()), -1, 1, -15, 15);
//		_myTextFieldLeft.fontSize(myFontSize);
//		_myTextFieldCenter.fontSize(myFontSize);
//		_myTextFieldRight.fontSize(myFontSize);
		myChar = (frame / 10) % CCCharSet.ENTYPO.chars().length;
		frame++;
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(1.0f, 0.0f, 0.0f, 0.0f);
		g.clear();
		g.pushMatrix();
		g.rotate(_myRotation);
		g.translate(0,0);
		g.color(1d);
		g.line(-g.width()/2, -100, g.width()/2, -100);
		g.line(-g.width()/2, 0, g.width()/2, 0);
		g.line(-g.width()/2, 100, g.width()/2, 100);
		CCLog.info(myChar);
		_myTextureField.text(CCEntypoIcon.ICON_CHEVRON_LEFT.text);
		_myVectorField.text(CCCharSet.ENTYPO.chars()[myChar] + "");
		_myTextureField.draw(g);
		_myVectorField.draw(g);
		g.line(0, -50, _myTextureField.width(), -50);
		g.popMatrix();
		
		g.image(_myFont.texture(), -g.width()/2,0);
	}
	
	public static void main(String[] args) {
		CCEnttypoDemo myDemo = new CCEnttypoDemo();
		myDemo.run();
	}
}

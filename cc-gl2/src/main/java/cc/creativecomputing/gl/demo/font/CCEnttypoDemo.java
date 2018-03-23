package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCEnttypoDemo extends CCGLApp{
	
	private CCTextField _myTextureField;
	private CCTextField _myDescField;
	
	private CCTextureMapFont _myFont;
	
	private int _myStartChar = 0;
	
	@Override
	public void setup() {
		
		_myTextureField = new CCTextField(_myFont = new CCTextureMapFont(CCCharSet.ENTYPO,CCNIOUtil.dataPath("fonts/entypo.ttf"), 40), "")
			.position(0, -100)
			.align(CCTextAlign.LEFT)
			.fontSize(40);
		
		_myDescField = new CCTextField(_myFont = new CCTextureMapFont(CCCharSet.REDUCED,CCNIOUtil.dataPath("fonts/Lato/Lato-Bold.ttf"), 20, 2,2), "")
				.position(0, -100)
				.align(CCTextAlign.LEFT)
				.fontSize(20);
		
		keyReleaseEvents.add(event -> {
			switch(event.key){
			case KEY_LEFT:
				_myStartChar -= (g.height() - 60) / 50;
				_myStartChar = CCMath.max(_myStartChar, 0);
				break;
			case KEY_RIGHT:
				_myStartChar += (g.height() - 60) / 50;
				_myStartChar = CCMath.min(_myStartChar, CCEntypoIcon.values().length);
				break;
			}
		});
			
	}
	
	private double _myRotation = 0;
	
	
	@Override
	public void update(CCGLTimer theTimer) {
//		double myFontSize = CCMath.map(CCMath.sin(theTimer.time()), -1, 1, 50, 150);
//		_myRotation = CCMath.map(CCMath.sin(theTimer.time()), -1, 1, -15, 15);
//		_myTextFieldLeft.fontSize(myFontSize);
//		_myTextFieldCenter.fontSize(myFontSize);
//		_myTextFieldRight.fontSize(myFontSize);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
		g.ortho2D();
		g.clear();
		g.pushMatrix();
		g.rotate(_myRotation);
		g.translate(0,0);
		g.color(1d);
		int myCounter = _myStartChar;
		for(int x = 20; x < g.width() - 20;x += 500) {
			for(int y = 40; y < g.height()-20;y += 50) {
				if(myCounter >= CCEntypoIcon.values().length) continue;
				_myTextureField.position(x, y);
				_myTextureField.text(CCEntypoIcon.values()[myCounter].text);
				_myTextureField.draw(g);
				
				_myDescField.position(x + 60,y + 10);
				_myDescField.text(CCEntypoIcon.values()[myCounter] + "");
				_myDescField.draw(g);
				myCounter++;
			}
		}
		g.line(0, -50, _myTextureField.width(), -50);
		g.popMatrix();
		
//		g.image(_myFont.texture(), -g.width()/2,0);
	}
	
	public static void main(String[] args) {
		CCEnttypoDemo myDemo = new CCEnttypoDemo();
		myDemo.width = 1800;
		myDemo.height = 1000;

		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}

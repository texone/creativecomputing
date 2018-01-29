package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCOutlineChar;
import cc.creativecomputing.graphics.font.CCVectorChar;
import cc.creativecomputing.io.CCNIOUtil;

public class CCFontCharDemo extends CCGLApp{

	private CCFont<?> _myFont;
	private CCVectorChar _myFillChar;
	private CCOutlineChar _myOutlineChar;
	
	private double _myScale;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		_myFont = new CCFont(null,CCNIOUtil.dataPath("fonts/Raleway/Raleway-Regular.ttf"));
		_myFillChar = _myFont.fill('t');
		_myOutlineChar = _myFont.outline('t');
		_myScale = _myFont.scaleForPixelHeight(50);
		
		_myMainWindow.keyCharEvents.add((c) -> {
			_myFillChar = _myFont.fill(c);
			_myOutlineChar = _myFont.outline(c);
		});
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(1.0f, 0.0f, 0.0f, 0.0f);
		g.clear();
		g.translate(0,0);
		g.color(1d);
		g.line(-g.width() / 2, 0, g.width() / 2, 0);
		g.line(0, -g.height() / 2, 0, g.height() / 2);

		g.line(100, 0, 100, 240);
		g.line(-g.width() / 2, _myFont.descent() * _myScale, g.width() / 2, _myFont.descent() * _myScale);
		g.line(-g.width() / 2, _myFont.ascent() * _myScale, g.width() / 2, _myFont.ascent() * _myScale);
		
		_myFillChar.draw(g, 0, 0, 0, _myScale);
		
		g.color(0f,0f,1f);
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(_myFillChar.boundx0() * _myScale, _myFillChar.boundy0() * _myScale);
		g.vertex(_myFillChar.boundx0() * _myScale, _myFillChar.boundy1() * _myScale);
		g.vertex(_myFillChar.boundx1() * _myScale, _myFillChar.boundy1() * _myScale);
		g.vertex(_myFillChar.boundx1() * _myScale, _myFillChar.boundy0() * _myScale);
		g.endShape();
		
		g.color(0f,0f,1f);
		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(_myFont.boundingBox()[0] * _myScale, _myFont.boundingBox()[1] * _myScale);
		g.vertex(_myFont.boundingBox()[0] * _myScale, _myFont.boundingBox()[3] * _myScale);
		g.vertex(_myFont.boundingBox()[2] * _myScale, _myFont.boundingBox()[3] * _myScale);
		g.vertex(_myFont.boundingBox()[2] * _myScale, _myFont.boundingBox()[1] * _myScale);
		g.endShape();
		
		_myOutlineChar.draw(g, 100, 0, 0, _myScale);
		CCLog.info(_myScale, _myFillChar.advanceWidth(), _myFillChar.leftSideBearing());
	}
	
	public static void main(String[] args) {
		CCFontCharDemo myDemo = new CCFontCharDemo();
		myDemo.run();
	}
}

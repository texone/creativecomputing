package cc.creativecomputing.topic.simulation.drops;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.modulation.CCColumnRowRingSource;
import cc.creativecomputing.effects.modulation.CCColumnRowSpiralSource;
import cc.creativecomputing.effects.modulation.CCPositionSource;
import cc.creativecomputing.effects.modulation.CCXYEuclidianDistanceSource;
import cc.creativecomputing.effects.modulation.CCXYManhattanDistanceSource;
import cc.creativecomputing.effects.modulation.CCXYRadialSource;
import cc.creativecomputing.effects.modulation.CCXYSignalSource;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCFontSettings;
import cc.creativecomputing.graphics.font.CCTextureMapChar;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCMultiFontText.CCTextGridLinePart;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2;

public class CCRainText {
	
	private class CCRainCharEffectable extends CCEffectable {

		private CCTextGridLinePart _myGridLine;
		private int _myCharID;
		
		private double _myBlend;
		
		private CCTextureMapChar myChar;
		
		public CCRainCharEffectable(int theId, CCTextGridLinePart theGridLine, int theColumn, int theRow) {
			super(theId);
			
			CCTextureMapFont myFont = (CCTextureMapFont)theGridLine.font();
			myChar = (CCTextureMapChar)theGridLine.charByIndex(theColumn);
			
			column(theColumn);
			row(theRow);
			_myGridLine = theGridLine;
			_myCharID = theColumn;
		}
		
		
		@Override
		public void apply(double... theValues) {
			_myBlend = theValues[0];
			if(_myCharID == 0)CCLog.info(_myCharID,_myBlend);
		}
		
		public void draw(CCGraphics g) {
			g.color(1);
			_cFontShader.uniform1f("noiseAmt", _cNoiseEnvelope.interpolate(_myBlend) * 4);
			_cFontShader.uniform1f("alphaAmp", _cAmpEnvelope.interpolate(_myBlend));
			g.pushMatrix();
			g.translate(0,_cNoiseEnvelope.interpolate(_myBlend) * -200,0);
			_myFont.beginText(g);
			_myGridLine.drawChar(g, _myCharID);
			_myFont.endText(g);
			g.popMatrix();
		}
	}
	
	public class CCRainEffectableText extends CCText{
		
		private CCFont<?> _myFont;
		
		private List<CCRainCharEffectable> _myChars = new ArrayList<>();

		/**
		 * @param theFont
		 */
		public CCRainEffectableText(CCFont<?> theFont) {
			super(theFont);
			_myFont = theFont;
		}
		
		public void update(double theDeltaTime) {
	
		}
		
		@Override
		public void breakText() {
			super.breakText();
			_myChars.clear();
			int myLine = 0;
			int myID = 0;
			for(CCTextGridLinePart myGridLines:_myTextGrid.gridLines()) {
				double myY = myGridLines.y();
				for(int i = 0; i < myGridLines.myNumberOfChars();i++) {
					double myX = myGridLines.x(i);	
					if(myGridLines.charByIndex(i)== null)continue;
					if(myGridLines.charByIndex(i).getChar() == ' ')continue;
					if(myGridLines.charByIndex(i).getChar() == '\n')continue;
					CCRainCharEffectable myEffectable = new CCRainCharEffectable(myID++, myGridLines, i, myLine);
					myEffectable.position().x = myX + position().x;
					myEffectable.position().y = myY + position().y;
					_myChars.add(myEffectable);
				}
				myLine++;
			}
		}
		
		@Override
		public void draw(CCGraphics g) {
			for(CCRainCharEffectable myEffectable:_myChars) {
				myEffectable.draw(g);
			}

		}
	}

	private CCTextureMapFont _myFont;
	
	private CCRainEffectableText _myText;
	
	@CCProperty(name = "effects")
	private CCEffectManager<CCRainCharEffectable> _myEffectManager;
	
	private CCShaderBuffer _myFontContext;
	
	@CCProperty(name = "font shader")
	private CCGLProgram _cFontShader;
	
	@CCProperty(name = "font composit shader")
	private CCGLProgram _cFontCompositeShader;
	
	@CCProperty(name = "noise Envelope")
	private CCEnvelope _cNoiseEnvelope = new CCEnvelope();
	
	
	@CCProperty(name = "AmpEnvelope")
	private CCEnvelope _cAmpEnvelope = new CCEnvelope();
	
	public CCRainText(CCRainDropSimulation theSimulation) {
		String myFont = "Tahoma Bold";
		float mySize = 32;
		CCFontSettings mySettings = new CCFontSettings(myFont, mySize, true, CCCharSet.REDUCED_CHARSET);
		mySettings.doSDF(true);
		mySettings.sdfSpread(8);
		_myFont = CCFontIO.createTextureMapFont(mySettings);
		
		_myText = new CCRainEffectableText(_myFont);
		_myText.size(mySize);
		_myText.width(300);
//		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.text("WATER AQUA");
		_myText.text("Weniger als 1% des Wassers auf \nder Erde kann als Trinkwasser \ngenutzt werden.");
		_myText.position(100,100);
		
		_myEffectManager = new CCEffectManager<>(_myText._myChars, "a");
		_myEffectManager.addIdSources(CCEffectable.COLUMN_SOURCE, CCEffectable.ROW_SOURCE);
		_myEffectManager.addRelativeSources(
			new CCColumnRowRingSource(),
			new CCColumnRowSpiralSource(),
			new CCPositionSource("position"),
			new CCXYEuclidianDistanceSource("euclidian", 200, new CCVector2()),
			new CCXYManhattanDistanceSource("manhattan", 200, 200, new CCVector2()),
			new CCXYRadialSource("radial", new CCVector2()),
			new CCXYSignalSource("signal")
		);
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("offset2", new CCOffsetEffect());
		
		_cFontShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "sdf_vertex.glsl"),
			CCNIOUtil.classPath(this, "sdf_fragment.glsl")
		);
		_myFontContext  = new CCShaderBuffer((int)theSimulation.width(),(int) theSimulation.height(), CCTextureTarget.TEXTURE_2D);
		

		_cFontCompositeShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "font_composite_vertex.glsl"),
			CCNIOUtil.classPath(this, "font_composite_fragment.glsl")
		);
	}
	
	
	public void update(CCGLTimer theAnimator) {
		_myEffectManager.update(theAnimator);
	}
	
	public void preDisplay(CCGraphics g) {
		_myFontContext.beginDraw(g);
		g.clearColor(255,0,0);
		g.clear();
		g.clearColor(0);
		g.pushMatrix();
		g.scale(3);
		_cFontShader.start();
		_cFontShader.uniform1i("fontTexture", 0);
		_myText.position(0,200);
		_myText.draw(g);
		_cFontShader.end();
		g.popMatrix();
		_myFontContext.endDraw(g);
	}
	
	public void display(CCGraphics g) {
		g.texture(0,_myFontContext.attachment(0));
		_cFontCompositeShader.start();
		_cFontCompositeShader.uniform1i("fontTexture", 0);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0);
	    g.vertex(0,0);
	    g.textureCoords2D(1, 0);
	    g.vertex(g.width(),0);
	    g.textureCoords2D(1, 1);
	    g.vertex(g.width(),g.height());
	    g.textureCoords2D(0, 1);
	    g.vertex(10,g.height());
	    g.endShape();
		_cFontCompositeShader.end();
		g.noTexture();
	}
}

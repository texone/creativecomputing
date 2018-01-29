package cc.creativecomputing.gl.demo.font.tocheck;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.markup.CCMarkUpBoldElement;
import cc.creativecomputing.io.markup.CCMarkUpDocument;
import cc.creativecomputing.io.markup.CCMarkUpElement;
import cc.creativecomputing.io.markup.CCMarkUpFormat;
import cc.creativecomputing.io.markup.CCMarkUpHeadingElement;
import cc.creativecomputing.io.markup.CCMarkUpItalicElement;
import cc.creativecomputing.io.markup.CCMarkUpLineBreakElement;
import cc.creativecomputing.io.markup.CCMarkUpParagraphElement;
import cc.creativecomputing.io.markup.CCMarkUpTextElement;

import import static org.lwjgl.stb.STBImageWrite.

public class CCMarkUpDemo extends CCGL2Adapter {
	
	private abstract class CCMarkUpElementView {
	
		public CCMarkUpElementView(int theX, int theY){
			
		}
		
		public abstract void display(CCGraphics g);
	}
	
	private class CCMarkUpTextView extends CCMarkUpElementView{
		
		private CCText _myText;

		public CCMarkUpTextView(int theX, int theY) {
			super(theX, theY);
			_myText = new CCText();
			_myText.position(theX, theY);
			_myText.dimension(400, 400);
			_myText.lineBreak(CCLineBreakMode.BLOCK);
		}

		@Override
		public void display(CCGraphics g) {
			_myText.draw(g);
		}
		
	}
	
	private class CCMarkUpImageView extends CCMarkUpElementView{
		
		private CCTexture2D _myTexture;
		
		private int _myX;
		private int _myY;

		public CCMarkUpImageView(int theX, int theY) {
			super(theX, theY);
			_myX = theX;
			_myY = theY;
		}

		@Override
		public void display(CCGraphics g) {
			g.image(_myTexture, _myX, _myY);
		}
		
	}
	
	
	private class CCMarkUpView{
		
		
		
		private List<CCMarkUpElementView> _myElements = new ArrayList<>();
		
		private CCVectorFont _myFont;
		private CCVectorFont _myBoldFont;
		private CCVectorFont _myItalicFont;
		private CCVectorFont _myBoldItalicFont;
		
		private int _myY = 0;
		
		private CCMarkUpTextView _myCurrentView;
		
		private StringBuffer _myCurrentText = new StringBuffer();
		
		private CCVectorFont _myCurrentFont;
		
		public CCMarkUpView(String theFontName, CCMarkUpDocument theDocument){
			_myFont = CCFontIO.createVectorFont(theFontName, 12);
			_myBoldFont = CCFontIO.createVectorFont(theFontName + "-Bold", 12);
			_myItalicFont = CCFontIO.createVectorFont(theFontName + "-Italic", 12);
			_myBoldItalicFont = CCFontIO.createVectorFont(theFontName + "-BoldItalic", 12);
			
			_myCurrentFont = _myFont;
			
			handleElement(theDocument);
		}
		
		private void createHeadingView(CCMarkUpHeadingElement theHeadline){
			finishParagraph();
			
			CCMarkUpTextView myTextView = new CCMarkUpTextView(20,_myY);
			int mySize = 12;
			switch(theHeadline.level()){
			case 1:
				mySize = 20;
				break;
			case 2:
				mySize = 18;
				break;
			case 3:
				mySize = 16;
				break;
			case 4:
				mySize = 14;
				break;
			}
			myTextView._myText.addText(theHeadline.value(), _myBoldFont, mySize);
			myTextView._myText.breakText();
			
			_myElements.add(myTextView);
			_myY -= myTextView._myText.height();
		}
		
		private void finishText(){
			if(_myCurrentView == null)return;
			if(_myCurrentText.toString().length() == 0)return;
			CCLog.info("+" + _myCurrentText.toString() + "+");
			_myCurrentView._myText.addText(_myCurrentText.toString(), _myCurrentFont);
			_myCurrentText = new StringBuffer();
		}
		
		private void finishParagraph(){
			if(_myCurrentView == null)return;
			
			finishText();
				
			_myCurrentView._myText.breakText();
			_myY -= _myCurrentView._myText.height();
				
			_myElements.add(_myCurrentView);
			_myCurrentView = null;
			
		}
		
		private void handleParagraph(CCMarkUpParagraphElement theParagraph){
			finishParagraph();
			_myCurrentView = new CCMarkUpTextView(20,_myY);
			
			handleElement(theParagraph);
		}
		
		private void handleItalic(CCMarkUpItalicElement theElement){
			finishText();

			CCVectorFont myLastFont = _myCurrentFont;
			if(_myCurrentFont == _myBoldFont){
				_myCurrentFont = _myBoldItalicFont;
			}else{
				_myCurrentFont = _myItalicFont;
			}
			handleElement(theElement);
			finishText();
			_myCurrentFont = myLastFont;
		}
		
		private void handleBold(CCMarkUpBoldElement theElement){
			finishText();

			CCVectorFont myLastFont = _myCurrentFont;
			if(_myCurrentFont == _myItalicFont){
				_myCurrentFont = _myBoldItalicFont;
			}else{
				_myCurrentFont = _myBoldFont;
			}
			handleElement(theElement);
			finishText();
			_myCurrentFont = myLastFont;
		}
		
		private void handleElement(CCMarkUpElement theElement){
			for(CCMarkUpElement myElement:theElement){
				if(myElement.getClass() == CCMarkUpHeadingElement.class){
					createHeadingView((CCMarkUpHeadingElement)myElement);
				}else if(myElement.getClass() == CCMarkUpParagraphElement.class){
					handleParagraph((CCMarkUpParagraphElement)myElement);
				}else if(myElement.getClass() == CCMarkUpItalicElement.class){
					handleItalic((CCMarkUpItalicElement)myElement);
				}else if(myElement.getClass() == CCMarkUpBoldElement.class){
					handleBold((CCMarkUpBoldElement)myElement);
				}else if(myElement.getClass() == CCMarkUpTextElement.class){
					_myCurrentText.append(((CCMarkUpTextElement)myElement).value());
				}else if(myElement.getClass() == CCMarkUpLineBreakElement.class){
					_myCurrentText.append("\n");
				}else{
					CCLog.info(myElement.getClass());
				}
			}
		}
		
		public void display(CCGraphics g){
			for(CCMarkUpElementView myView:_myElements){
				myView.display(g);
			}
		}
	}
	
	
	
	private CCMarkUpView _myMarkUpView;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myMarkUpView = new CCMarkUpView("HelveticaNeue", new CCMarkUpFormat().load(CCNIOUtil.dataPath("font/markup.txt"), true));
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);
		g.pushMatrix();
		g.translate(-g.width()/2, g.height()/2 - 50);
		_myMarkUpView.display(g);
		g.popMatrix();
	}

	public static void main(String[] args) {
		
//		CCFontIO.printFontList();

		CCMarkUpDemo demo = new CCMarkUpDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

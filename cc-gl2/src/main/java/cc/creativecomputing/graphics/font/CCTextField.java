package cc.creativecomputing.graphics.font;

import java.util.ArrayList;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCTextField {
	
	public static class CCPlacedTextChar{
		public double x;
		public double width;
		public double y;
		
		public final CCChar charObject;
		
		public CCPlacedTextChar(CCChar theCharObject, double theX, double theWidth, double theY){
			charObject = theCharObject;
			x = theX;
			width = theWidth;
			y = theY;
		}
	}
	
	public static class CCTextCharGrid extends ArrayList<CCPlacedTextChar>{
		
	}
	
	public CCVector2 boundMin(){
		return _myBoundMin;
	}
	
	public CCVector2 boundMax(){
		return _myBoundMax;
	}
	
	protected CCFont<?> _myFont;
	
	private CCVector3 _myPosition;
	
	protected String _myText;
	
	protected CCTextCharGrid _myCharGrid = new CCTextCharGrid();
	
	private CCTextAlign _myAlign = CCTextAlign.LEFT;
	
	protected double _myWidth;
	protected double _myHeight;
	
	protected CCVector2 _myBoundMin;
	protected CCVector2 _myBoundMax;
	
	private double _myFontSize;
	protected double _myDrawCorrectionScale;
	protected double _myBreakCorrectionScale;
	
	public CCTextField(CCFont<?> theFont, String theText){
		_myFont = theFont;
		_myPosition = new CCVector3();
		fontSize(theFont.size());
		text(theText);
	}
	
	public CCTextField(String theText){
		_myPosition = new CCVector3();
		text(theText);
	}
	
	public CCTextCharGrid charGrid(){
		return _myCharGrid;
	}
	
	public CCTextField fontSize(double theFontSize){
		if(theFontSize == _myFontSize)return this;
		_myFontSize = theFontSize;
		_myDrawCorrectionScale = _myFont.scaleForSize(_myFontSize);
		_myBreakCorrectionScale = _myFont.scaleForPixelHeight(_myFontSize);
		breakText();
		return this;
	}
	
	public double fontSize() {
		return _myFontSize;
	}
	
	/**
	 * Returns the coordinate above the baseline the font extends.
	 * <p>
	 * in scaled coordinates, based on the textSize of the text
	 * @return the coordinate above the baseline the font extends
	 * 
	 * @see CCFont#ascent()
	 */
	public double ascent(){
		return _myFont.ascent() * _myBreakCorrectionScale;
	}
	
	/**
	 * Returns the coordinate below the baseline the font extends (i.e. it is typically negative)
	 * <p>
	 * in scaled coordinates, based on the textSize of the text
	 * @return the coordinate above the baseline the font extends
	 * 
	 * @see CCFont#descent()
	 */
	public double descent(){
		return _myFont.descent() * _myBreakCorrectionScale;
	}
	
	/**
	 * Returns the spacing between one row's descent and the next row's ascent.
	 * <p>
	 * in scaled coordinates, based on the textSize of the text
	 * @return the coordinate above the baseline the font extends
	 * 
	 * @see CCFont#linegap()
	 */
	public double linegap(){
		return _myFont.linegap() * _myBreakCorrectionScale;
	}
	
	/**
	 * Returns the vertical advance *ascent - *descent + *lineGap
	 * <p>
	 * in scaled coordinates, based on the textSize of the text
	 * @return the coordinate above the baseline the font extends
	 * 
	 * @see CCFont#verticalAdvance()
	 */
	public double verticalAdvance(){
		return _myFont.verticalAdvance() * _myBreakCorrectionScale;
	}
	
	public CCTextField align(CCTextAlign theAlign){
		_myAlign = theAlign;
		breakText();
		return this;
	}
	
	public CCTextAlign align() {
		return _myAlign;
	}
	
	public void breakText(){
		_myHeight = 0;
		if(_myText == null)return;
		if(_myFont == null)return;
		_myCharGrid.clear();
		double myX = 0;
		double myY = 0;
		char myLastChar = ' ';
		int myLineStartIndex = 0;
		int myLineEndIndex = 0;
		double myWidth = 0;
		_myText = _myText.replaceAll(" +\n", " \n");
		for (char myChar:_myText.toCharArray()) {
			if(myChar == '\n'){
				myWidth = myX;
				_myWidth = CCMath.max(myWidth, _myWidth);
				myX = 0;
				myY -= _myFont.verticalAdvance() * _myBreakCorrectionScale;
				_myHeight += _myFont.verticalAdvance() * _myBreakCorrectionScale;
				for(int i = myLineStartIndex; i < myLineEndIndex;i++){
					_myCharGrid.get(i).x += alignCorrection(myWidth);
				}
				myLineStartIndex = myLineEndIndex;
				continue;
			}
			if(myX > 0){
        		myX += _myFont.kernAdvance(myLastChar, myChar) * _myBreakCorrectionScale;
        	}
        	CCChar myCharObject = _myFont.fontChar(myChar);
        	double myCharWidth = myCharObject.advanceWidth() * _myBreakCorrectionScale;
        	_myCharGrid.add(new CCPlacedTextChar(myCharObject, myX, myCharWidth, myY));
        	
        	myX += myCharObject.advanceWidth() * _myBreakCorrectionScale;
        	myLastChar = myChar;
        	myLineEndIndex++;
        }
		myWidth = myX;
		for(int i = myLineStartIndex; i < myLineEndIndex;i++){
			_myCharGrid.get(i).x += alignCorrection(myWidth);
		}
		_myWidth = CCMath.max(myWidth, _myWidth);
		_myHeight += _myFont.verticalAdvance() * _myBreakCorrectionScale;
		
		updateBounds();
	}
	
	protected void updateBounds(){
		_myBoundMax = new CCVector2(_myPosition.x + _myWidth, _myPosition.y + _myFont.ascent() * _myBreakCorrectionScale);
		_myBoundMin = new CCVector2(_myPosition.x, _myBoundMax.y - _myHeight);
	}
	
	protected double alignCorrection(double theWidth){
		switch(_myAlign){
		case RIGHT:
			return -theWidth;
		case CENTER:
			return -theWidth / 2;
		}
		return 0;
	}
	
	public double width(){
		return _myWidth;
	}
	
	public double height(){
		return _myHeight;
	}
	
	public CCTextField text(String theText){
		_myText = theText;
		breakText();
		return this;
	}
	
	public String text(){
		return _myText;
	}
	
	public CCTextField font(CCFont<?> theFont){
		_myFont = theFont;
		breakText();
		return this;
	}
	
	public CCFont<?> font(){
		return _myFont;
	}
	
	public void lineBreak(CCLineBreakMode _myLineBreakMode) {
		// TODO Auto-generated method stub
		
	}
	
	public CCLineBreakMode lineBreak() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CCTextField position(double theX, double theY){
		_myPosition.set(theX, theY, 0);
		return this;
	}
	
	public CCVector3 position(){
		return _myPosition;
	}

	public void draw(CCGraphics g){
		_myFont.beginText(g);
		for (CCPlacedTextChar myChar:_myCharGrid) {
			myChar.charObject.drawVertices(g, _myPosition.x + myChar.x, _myPosition.y + myChar.y, _myPosition.z, _myDrawCorrectionScale);
        }
		_myFont.endText(g);
	}
}

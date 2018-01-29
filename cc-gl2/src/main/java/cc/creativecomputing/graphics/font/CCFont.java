package cc.creativecomputing.graphics.font;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTVertex;
import org.lwjgl.system.MemoryStack;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCFont<CharType extends CCChar> {
	
	private CCVectorFontTesselator _myTesselator;

	protected int _myBezierDetail = 30;
    
    protected int _myAscent;
    protected int _myDescent;
    protected int _myLineGap;
    protected int _myVerticalAdvance;
    
    protected final STBTTFontinfo _myInfo;
	
	protected int _myCharCount;
	protected CCCharSet _myCharSet;
	protected int _myCharCodes[];
	protected int[] _myAsciiLookUpTable;
	protected int[] _myCharLookUpTable;
	protected CharType[] _myChars;
	
	private final ByteBuffer _myFontData;
	
	protected int _mySize = 1;
	
	protected CCFont(CCFont theFont){
		_myInfo = theFont._myInfo;
		_myFontData = theFont._myFontData;
		
		_myAscent = theFont._myAscent;
		_myDescent = theFont._myDescent;
		_myLineGap = theFont._myLineGap;
		_myVerticalAdvance = theFont._myVerticalAdvance;
		
		_myCharSet = theFont._myCharSet;
		_myCharCount = _myCharSet.size();
		_myTesselator = theFont._myTesselator;
		
		_myCharCodes = theFont._myCharCodes;
		_myAsciiLookUpTable = theFont._myAsciiLookUpTable;
	}
	
	public CCFont(final CCCharSet theCharSet, Path theFontPath){
		_myInfo = STBTTFontinfo.create();
		_myFontData = CCNIOUtil.loadBytes(theFontPath);
       
		stbtt_InitFont(_myInfo, _myFontData);
		
		try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(_myInfo, pAscent, pDescent, pLineGap);

            _myAscent = pAscent.get(0);
            _myDescent = pDescent.get(0);
            _myLineGap = pLineGap.get(0);
        }
        _myVerticalAdvance = _myAscent - _myDescent + _myLineGap;
		
		if(theCharSet == null){
			_myCharSet = CCCharSet.REDUCED;
		}else{
			_myCharSet = theCharSet;
		}
		
		_myCharCount = _myCharSet.size();
		
		_myTesselator = new CCVectorFontTesselator();
		int myMaxGlyphIndex = 0;
		for (int i = 0; i < _myCharCount; i++) {
			char myChar = _myCharSet.chars()[i];
			myMaxGlyphIndex = CCMath.max(myMaxGlyphIndex, findGlyphIndex(myChar));
		}

		_myCharCodes = new int[myMaxGlyphIndex + 1];
		_myAsciiLookUpTable = new int[128];
		for (int i = 0; i < _myCharCount; i++) {
			char myChar = _myCharSet.chars()[i];
			int myGlyphIndex = findGlyphIndex(myChar);
			if (myChar < 128){
				_myAsciiLookUpTable[myChar] = myGlyphIndex;
			}
			
			_myCharCodes[myGlyphIndex] = myChar;
		}
	}
	
	/**
	 * Get index for the char (convert from unicode to bagel charset).
	 * @return index into arrays or -1 if not found
	 */
	public int index(final char theChar){
		// degenerate case, but the find function will have trouble
		// if there are somehow zero chars in the lookup
		if (_myCharCodes.length == 0)
			return -1;
		
		// quicker lookup for the ascii chars
		if (theChar < 128)
			return _myAsciiLookUpTable[theChar];
		
		return findGlyphIndex(theChar);
	}
	
	public CharType fontChar(int theGlyphIndex){
		return _myChars[theGlyphIndex];
	}
	
	public CharType fontChar(char theChar){
		return fontChar(index(theChar));
	}
	
	public int charCount(){
		return _myCharCount;
	}
	
	public CCCharSet charSet(){
		return _myCharSet;
	}
	
	/**
	 * Returns the coordinate above the baseline the font extends.
	 * <p>
	 * in unscaled coordinates, so you must multiply by the scale factor for a given size
	 * look for {@linkplain #scaleForPixelHeight(double)}
	 * @return the coordinate above the baseline the font extends
	 * 
	 * @see #scaleForPixelHeight(double)
	 */
	public int ascent(){
		return _myAscent;
	}
	
	/**
	 * Returns the coordinate below the baseline the font extends (i.e. it is typically negative)
	 * <p>
	 * in unscaled coordinates, so you must multiply by the scale factor for a given size
	 * look for {@linkplain #scaleForPixelHeight(double)}
	 * @return the coordinate below the baseline the font extends
	 */
	public int descent(){
		return _myDescent;
	}
	
	/**
	 * Returns the spacing between one row's descent and the next row's ascent.
	 * <p>
	 * in unscaled coordinates, so you must multiply by the scale factor for a given size
	 * look for {@linkplain #scaleForPixelHeight(double)}
	 * @return the spacing between one row's descent and the next row's ascent.
	 */
	public int linegap(){
		return _myLineGap;
	}
	
	/**
	 * Returns the vertical advance *ascent - *descent + *lineGap
	 * <p>
	 * in unscaled coordinates, so you must multiply by the scale factor for a given size
	 * look for {@linkplain #scaleForPixelHeight(double)}
	 * @return the vertical advance
	 */
	public int verticalAdvance(){
		return _myVerticalAdvance;
	}
	
	public double width(char theChar){
		return _myChars[index(theChar)].advanceWidth();
	}
	
	public double width(int theGlyphIndex){
		return _myChars[theGlyphIndex].advanceWidth();
	}
	
	public double width(String theChar, int theStart, int theEnd){
		double myResult = 0;
		for(int i = theStart; i < theEnd; i++){
			myResult += width(theChar.charAt(i));
		}
		return myResult;
	}
	
	public double scale(){
		return scaleForPixelHeight(_mySize);
	}
	
	public double scaleForSize(double theSize){
		return 1;
	}
	
	public double size(){
		return _mySize;
	}
	
	public ByteBuffer fontData(){
		return _myFontData;
	}
	
	public void beginText(CCGraphics g){
	}

    public void endText(CCGraphics g){
	}

	protected void createChars() {}
	
	//////////////////////////////////////////////////////////////////////////////
	//
	//FONT LOADING
	//
	//

	/**
	 * This function will determine the number of fonts in a font file.  TrueType
	 * collection (.ttc) files may contain multiple fonts, while TrueType font
	 * (.ttf) files only contain one font. The number of fonts can be used for
	 * indexing with the previous function where the index is between zero and one
	 * less than the total fonts. If an error occurs, -1 is returned.
	 * @return
	 */
	public int numberOfFonts(){
		return stbtt_GetNumberOfFonts(_myFontData);
	}

	/**
	 * Each .ttf/.ttc file may have more than one font. Each font has a sequential
	 * index number starting from 0. Call this function to get the font offset for
	 * a given index; it returns -1 if the index is out of range. A regular .ttf
	 * file will only define one font and it always be at offset 0, so it will
	 * return '0' for index 0, and -1 for all other indices.
	 * @param theIndex
	 * @return
	 */
	public int fontOffset(int theIndex){
		return stbtt_GetFontOffsetForIndex(_myFontData, theIndex);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// CHARACTER TO GLYPH-INDEX CONVERSION
	
	/**
	 * If you're going to perform multiple operations on the same character
	 * and you want a speed-up, call this function with the character you're
	 * going to process, then use glyph-based functions instead of the
	 * codepoint-based functions.
	 * @param theChar
	 * @return
	 */
	public int findGlyphIndex(char theChar){
		return stbtt_FindGlyphIndex(_myInfo, theChar);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// CHARACTER PROPERTIES
	//
	
	/**
	 * Computes a scale factor to produce a font whose "height" is 'pixels' tall.
	 * Height is measured as the distance from the highest ascender to the lowest
	 * descender; in other words, it's equivalent to calling stbtt_GetFontVMetrics
	 * and computing: <code>scale = pixels / (ascent - descent)</code>
	 * so if you prefer to measure height by the ascent only, use a similar calculation.
	 * @param thePixels
	 * @return
	 */
	public double scaleForPixelHeight(double thePixels){
		return thePixels / (_myAscent - _myDescent);
	}
	
	/**
	 * Computes a scale factor to produce a font whose EM size is mapped to
	 * 'pixels' tall.
	 * @param thePixels
	 * @return
	 */
	public double scaleForMappingEmToPixels(double thePixels){
		return stbtt_ScaleForMappingEmToPixels(_myInfo, (float)thePixels);
	}

	/**
	 * the bounding box around all possible characters
	 * @return
	 */
	public int[] boundingBox(){
		try(MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer x0 = stack.mallocInt(1);
			IntBuffer y0 = stack.mallocInt(1);
			IntBuffer x1 = stack.mallocInt(1);
			IntBuffer y1 = stack.mallocInt(1);
			stbtt_GetFontBoundingBox(_myInfo, x0, y0, x1, y1);
			return new int[]{x0.get(),y0.get(),x1.get(),y1.get()};
		}
	}
	
	
	/**
	 * leftSideBearing is the offset from the current horizontal position to the left edge of the character
	 * advanceWidth is the offset from the current horizontal position to the next horizontal position
	 * these are expressed in unscaled coordinates
	 * @param theIndex
	 * @return
	 */
	public int[] hMetrics(int theIndex){
		try(MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer advanceWidth = stack.mallocInt(1);
			IntBuffer leftSideBearing = stack.mallocInt(1);
			stbtt_GetGlyphHMetrics(_myInfo, theIndex, advanceWidth, leftSideBearing);
			return new int[]{advanceWidth.get(),leftSideBearing.get()};
		}
	}
	
	public int[] hMetrics(char theIndex){
		return hMetrics(findGlyphIndex(theIndex));
	}

	/**
	 * an additional amount to add to the 'advance' value between ch1 and ch2
	 * @param theGlyphIndex0
	 * @param theGlyphIndex1
	 * @return
	 */
	public int kernAdvance(int theGlyphIndex0, int theGlyphIndex1){
		return stbtt_GetGlyphKernAdvance(_myInfo, theGlyphIndex0, theGlyphIndex1);
	}
	
	/**
	 * an additional amount to add to the 'advance' value between ch1 and ch2
	 * @param theChar0
	 * @param theChar1
	 * @return
	 */
	public double kernAdvance(char theChar0, char theChar1){
		return stbtt_GetCodepointKernAdvance(_myInfo, theChar0, theChar1) * scale();
	}
	
	/**
	 * Gets the bounding box of the visible part of the glyph, in unscaled coordinates
	 * @param codepoint
	 * @return
	 */
	public int[] boundingBox(int index){
		try(MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer x0 = stack.mallocInt(1);
			IntBuffer y0 = stack.mallocInt(1);
			IntBuffer x1 = stack.mallocInt(1);
			IntBuffer y1 = stack.mallocInt(1);
			stbtt_GetGlyphBox(_myInfo, index, x0, y0, x1, y1);
			return new int[]{x0.get(),y0.get(),x1.get(),y1.get()};
		}
	}
	
	public int[] boundingBox(char theChar){
		return boundingBox(findGlyphIndex(theChar));
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// GLYPH SHAPES (you probably don't need these, but they have to go before
	// the bitmaps for C declaration-order reasons)
	//
	
	/**
	 * Checks if the glyph has data, for example a space needs nothing to be drawn 
	 * @param theIndex the glyph index
	 * @return returns <code>true</code> if nothing is drawn for this glyph
	 */
	public boolean isGlyphEmpty(int theIndex){
		return stbtt_IsGlyphEmpty(_myInfo, theIndex);
	}
	
	
	
	/**
	 * returns # of vertices and fills *vertices with the pointer to them
	 * these are expressed in "unscaled" coordinates
	 * 
	 * The shape is a series of contours. Each one starts with
	 * a STBTT_moveto, then consists of a series of mixed
	 * STBTT_lineto and STBTT_curveto segments. A lineto
	 * draws a line from previous end point to its x,y; a curveto
	 * draws a quadratic bezier from previous endpoint to
	 * its x,y, using cx,cy as the bezier control point.
	 * @param index
	 * @param theFunction
	 * @return
	 */
	private STBTTVertex.Buffer glyphShape(int theGlyph){
		try (MemoryStack stack = stackPush()) {
			PointerBuffer myVertices = stack.callocPointer(1);
			int myNumberOfVertices =  stbtt_GetGlyphShape(_myInfo, theGlyph, myVertices);
			STBTTVertex.Buffer myVertexBuffer = STBTTVertex.create(myVertices.get(0),myNumberOfVertices);
			return myVertexBuffer;
		}
	}
	
	protected STBTTVertex.Buffer shape(char theChar){
		return glyphShape(findGlyphIndex(theChar));
	}

	public CCVectorChar fill(char theChar){
		int theGlyphCode = findGlyphIndex(theChar);
		
		final CCVectorChar myVectorChar = new CCVectorChar(
			theChar, 
			theGlyphCode, 
			boundingBox(theGlyphCode),
			hMetrics(theGlyphCode)
		);

		STBTTVertex.Buffer myBuffer = glyphShape(theGlyphCode);
		
		if(myBuffer == null){
			return myVectorChar;
		}
		_myTesselator.beginPolygon(myVectorChar);

		int lastX = 0;
		int lastY = 0;
		boolean myFirstContour = true;

		
		while (myBuffer.hasRemaining()) {
			STBTTVertex myVertex = myBuffer.get();
			switch (myVertex.type()) {
				case STBTT_vmove: // 1 point (2 vars) in textPoints
					if(!myFirstContour){
						_myTesselator.endContour();
					}
					myFirstContour = false;
					_myTesselator.beginContour();
				case STBTT_vline: // 1 point
					_myTesselator.vertex(myVertex.x(), myVertex.y(), 0);
					break;

				case STBTT_vcurve: // 2 points
					for (int j = 1; j < _myBezierDetail; j++) {
						float t = (float) j / _myBezierDetail;
						_myTesselator.vertex(
							CCMath.bezierPoint(lastX, myVertex.cx(), myVertex.x(), myVertex.x(), t), 
							CCMath.bezierPoint(lastY, myVertex.cy(), myVertex.y(), myVertex.y(), t), 
							0
						);
					}
					break;
				case STBTT_vcubic: // 3 points
					for (int j = 1; j < _myBezierDetail; j++) {
						float t = (float) j / _myBezierDetail;
						_myTesselator.vertex(
							CCMath.bezierPoint(lastX, myVertex.cx(), myVertex.cx1(), myVertex.x(), t), 
							CCMath.bezierPoint(lastY, myVertex.cy(), myVertex.cy1(), myVertex.y(), t), 
							0
						);
					}

					break;
			}
			lastX = myVertex.x();
			lastY = myVertex.y();
		}
		_myTesselator.endContour();
		_myTesselator.endPolygon();
		
		return myVectorChar;
	}
	
	public CCOutlineChar outline(char theChar){
		int theGlyphCode = findGlyphIndex(theChar);
		
		final CCOutlineChar myOutlineChar = new CCOutlineChar(
			theChar, 
			theGlyphCode, 
			boundingBox(theGlyphCode),
			hMetrics(theGlyphCode)
		);
		
		STBTTVertex.Buffer myBuffer = glyphShape(theGlyphCode);
		if(myBuffer == null){
			return myOutlineChar;
		}
		
		int lastX = 0;
		int lastY = 0;
		boolean myFirstContour = true;
		
		while (myBuffer.hasRemaining()) {
			STBTTVertex myVertex = myBuffer.get();
			switch (myVertex.type()) {
				case STBTT_vmove: // 1 point (2 vars) in textPoints
					if(!myFirstContour){
						myOutlineChar.endPath();
					}
					myFirstContour = false;
					myOutlineChar.beginPath();
				case STBTT_vline: // 1 point				
					myOutlineChar.addVertex(new CCVector2(myVertex.x(), myVertex.y()));
					break;
				case STBTT_vcurve: // 2 points
					for (int j = 1; j < _myBezierDetail; j++) {
						double t = (double) j / _myBezierDetail;
						myOutlineChar.addVertex(new CCVector2(
							CCMath.bezierPoint(lastX, myVertex.cx(), myVertex.cx(), myVertex.x(), t), 
							CCMath.bezierPoint(lastY, myVertex.cy(), myVertex.cy(), myVertex.y(), t)
						));
					}
					break;
				case STBTT_vcubic: // 3 points
					for (int j = 1; j < _myBezierDetail; j++) {
						double t = (double) j / _myBezierDetail;
						myOutlineChar.addVertex(new CCVector2(
							CCMath.bezierPoint(lastX, myVertex.cx(), myVertex.cx1(), myVertex.x(), t), 
							CCMath.bezierPoint(lastY, myVertex.cy(), myVertex.cy1(), myVertex.y(), t)
						));
					}
					break;
			}
			lastX = myVertex.x();
			lastY = myVertex.y();
		}
		myOutlineChar.endPath();
		return myOutlineChar;
	}
	
	public CC3DChar char3D(char theChar, double theDepth){
		CCVectorChar myVectorChar = fill(theChar);
		CCOutlineChar myOutlineChar = outline(theChar);
		return new CC3DChar(myVectorChar, myOutlineChar, theDepth);
	}
	
	public static void main(String[] args) {
		CCFont myFont = new CCFont(CCCharSet.REDUCED,CCNIOUtil.dataPath("Roboto-Bold.ttf"));
		
		for(int i = 0; i < myFont.charCount();i++){
			char c = myFont.charSet().chars()[i];
			CCLog.info(c, (int)c, myFont.findGlyphIndex(c), myFont.index(c), myFont.boundingBox(c), myFont.isGlyphEmpty(myFont.findGlyphIndex(c)), myFont.shape(c));
		}
		long time = System.nanoTime();
		for(int i = 0; i < myFont.charCount();i++){
			char c = myFont.charSet().chars()[i];
			for(int j = 0; j < 100;j++){
			 myFont.findGlyphIndex(c);
			}
		}
		long dif0 = System.nanoTime() - time;
		time = System.nanoTime();
		for(int i = 0; i <  myFont.charCount();i++){
			char c = myFont.charSet().chars()[i];
			for(int j = 0; j < 100;j++){
			 myFont.index(c);
			}
		}
		long dif1 = System.nanoTime() - time;
		
		CCLog.info(dif0, dif1);
		CCLog.info(myFont.boundingBox());
		CCLog.info(STBTT_vmove,STBTT_vline,STBTT_vcurve,STBTT_vcubic);
		CCLog.info(myFont.charCount());
	}

	public double height() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}

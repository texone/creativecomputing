package cc.creativecomputing.graphics.font;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRanges;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackRange;
import org.lwjgl.stb.STBTTPackedchar;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.math.CCQuad2f;

/**
 * NEW TEXTURE BAKING API
 * 
 * This provides options for packing multiple fonts into one atlas, not
 * perfectly but better than nothing.
 * @author christianr
 *
 */
public class CCFontImage extends CCImage{

	private STBTTPackContext _myPackContext = null;
	
	public CCFontImage(int theWidth, int theHeight){
		super(theWidth, theHeight, CCPixelInternalFormat.ALPHA, CCPixelFormat.ALPHA, CCPixelType.UNSIGNED_BYTE);
	}
	
	/**
	 * Initializes a packing context stored in the passed-in stbtt_pack_context.
	 * Future calls using this context will pack characters into the bitmap passed
	 * in here: a 1-channel bitmap that is width * height. 
	 * @param theWidth
	 * @param theHeight
	 * @param theStride distance from one row to the next (or 0 to mean they are packed tightly together).
	 * @param thePadding amount of padding to leave between each
	 * character (normally you want '1' for bitmaps you'll use as textures with
	 * bilinear filtering).
	 * @return <code>false</code> on failure, <code>true</code> on success.
	 */
	public boolean packBegin(int theStride, int thePadding){
		_myPackContext = STBTTPackContext.malloc();
		
		return stbtt_PackBegin(_myPackContext, (ByteBuffer)buffer(), _myWidth, _myHeight, 0, 1, NULL);
	}
	
	public ByteBuffer data(){
		return (ByteBuffer)buffer();
	}
	
	public boolean packBegin(){
		return packBegin(0, 1);
	}
	
	/**
	 * Cleans up the packing context and frees all memory.
	 */
	public void packEnd(){
		stbtt_PackEnd(_myPackContext);
		_myPackContext.close();
		_myPackContext = null;
	}
	
	/**
	 * Creates character bitmaps from multiple ranges of characters stored in
	 * ranges. This will usually create a better-packed bitmap than multiple
	 * calls to stbtt_PackFontRange. Note that you can call this multiple
	 * times within a single PackBegin/PackEnd.
	 * @return
	 */
	public boolean packFontRanges(CCFont theFont, STBTTPackRange.Buffer theRanges){
		return stbtt_PackFontRanges(_myPackContext, theFont.fontData(), 0, theRanges);
	}
	
	/**
	 * Creates character bitmaps from the font_index'th font found in fontdata (use
	 * font_index=0 if you don't know what that is). It creates num_chars_in_range
	 * bitmaps for characters with unicode values starting at first_unicode_char_in_range
	 * and increasing. Data for how to render them is stored in chardata_for_range;
	 * pass these to stbtt_GetPackedQuad to get back renderable quads.
	 * <p>
	 * font_size is the full height of the character from ascender to descender,
	 * as computed by stbtt_ScaleForPixelHeight. To use a point size as computed
	 * by stbtt_ScaleForMappingEmToPixels, wrap the point size in STBTT_POINT_SIZE()
	 * and pass that result as 'font_size':
	 *    ...,                  20 , ... // font max minus min y is 20 pixels tall
	 *    ..., STBTT_POINT_SIZE(20), ... // 'M' is 20 pixels tall
	 * @param theFontSize
	 * @param theFirstChar
	 * @return
	 */
	public boolean packFontRange(CCFont theFont, double theFontSize, int theFirstChar, STBTTPackedchar.Buffer theCharData){
		return stbtt_PackFontRange(_myPackContext, theFont.fontData(), 0, (float)theFontSize, theFirstChar, theCharData);
	}
	
	public STBTTPackedchar.Buffer packFont(CCFont theFont, int theFirstChar, double theFontSize){
		STBTTPackedchar.Buffer myResult = STBTTPackedchar.malloc(theFont.charCount() );
		if(!packFontRange(theFont, theFontSize, theFirstChar, myResult)){
			myResult.free();
			return null;
		}
		return myResult;
	}
	
	public STBTTPackedchar.Buffer packFont(CCFont theFont, double theFontSize){
		return packFont(theFont, 0, theFontSize);
	}

	/**
	 * Oversampling a font increases the quality by allowing higher-quality subpixel
	 * positioning, and is especially valuable at smaller text sizes.
	 * <p>
	 * This function sets the amount of oversampling for all following calls to
	 * stbtt_PackFontRange(s) or stbtt_PackFontRangesGatherRects for a given
	 * pack context. The default (no oversampling) is achieved by h_oversample=1
	 * and v_oversample=1. The total number of pixels required is
	 * h_oversample*v_oversample larger than the default; for example, 2x2
	 * oversampling requires 4x the storage of 1x1. For best results, render
	 * oversampled textures with bilinear filtering. 
	 * <p>
	 * To use with PackFontRangesGather etc., you must set it before calls
	 * call to PackFontRangesGatherRects.
	 * @param theHOversample
	 * @param theVOversample
	 */
	public void oversampling(int theHOversample, int theVOversample){
		stbtt_PackSetOversampling(_myPackContext, theHOversample, theVOversample);
	}
	
	private final STBTTAlignedQuad q  = STBTTAlignedQuad.malloc();
    private final FloatBuffer      xb = memAllocFloat(1);
    private final FloatBuffer      yb = memAllocFloat(1);
	
    
    public static class CCFontQuad{
    	public final double x0;
    	public final double y0;
    	public final double x1;
    	public final double y1;

    	public final double s0;
    	public final double t0;
    	public final double s1;
    	public final double t1;
    	
    	public final double width;
    	public final double height;
    	
    	CCFontQuad(STBTTAlignedQuad theQuad, double theWidth){
    		x0 = theQuad.x0();
    		y0 = theQuad.y0();
    		x1 = theQuad.x1();
    		y1 = theQuad.y1();
    		
    		s0 = theQuad.s0();
    		t0 = theQuad.t0();
    		s1 = theQuad.s1();
    		t1 = theQuad.t1();
    		
    		width = theWidth;
    		height = y1 - y0;
    	}
    }
    /**
     * 
     * @param theCharData
     * @param theChar character to display
     * @param theX current position in screen pixel space
     * @param theY current position in screen pixel space
     */
	public CCFontQuad quad(STBTTPackedchar.Buffer theCharData, int theChar){
		xb.put(0, 0f);
        yb.put(0, 0f);
		stbtt_GetPackedQuad(theCharData, _myWidth, _myHeight, theChar, xb, yb, q, false);
		
		return new CCFontQuad(q,xb.get(0));
	}
	
	private CCTexture2D _myFontTexture;
	
	public CCTexture2D texture(){
		if(_myFontTexture == null){
			_myFontTexture = new CCTexture2D(this);
		}
		return _myFontTexture;
	}
	
//	STBTT_DEF void stbtt_GetBakedQuad(const stbtt_bakedchar *chardata, int pw, int ph,  // same data as above
//            int char_index,             // character to display
//            float *xpos, float *ypos,   // pointers to current position in screen pixel space
//            stbtt_aligned_quad *q,      // output: quad to draw
//            int opengl_fillrule);       // true if opengl fill rule; false if DX9 or earlier
////Call GetBakedQuad with char_index = 'character - first_char', and it
////creates the quad you need to draw and advances the current position.
////
////The coordinate system used assumes y increases downwards.
////
////Characters will extend both above and below the current position;
////see discussion of "BASELINE" above.
////
////It's inefficient; you might want to c&p it and optimize it.
	
}
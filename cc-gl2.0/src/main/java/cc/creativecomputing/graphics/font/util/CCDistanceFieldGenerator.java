package cc.creativecomputing.graphics.font.util;

import java.awt.image.BufferedImage;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

/**
 * Generates a signed distance field image from a binary (black/white) source image.
 * 
 * <p> Signed distance fields are used in Team Fortress 2 by Valve to enable
 * sharp rendering of bitmap fonts even at high magnifications,
 * using nothing but alpha testing so at no extra runtime cost.
 * 
 * <p> The technique is described in the SIGGRAPH 2007 paper
 * "Improved Alpha-Tested Magniﬁcation for Vector Textures and Special Effects" by Chris Green:
 * <a href="http://www.valvesoftware.com/publications/2007/SIGGRAPH2007_AlphaTestedMagnification.pdf">
 * http://www.valvesoftware.com/publications/2007/SIGGRAPH2007_AlphaTestedMagnification.pdf
 * </a>
 * 
 * @author Thomas ten Cate
 */
public class CCDistanceFieldGenerator{
	
	private double _mySpread = 1;
	
	public CCDistanceFieldGenerator() {
		
	}
	
	/**
	 * Sets the spread of the distance field. The spread is the maximum distance in pixels
	 * that we'll scan while for a nearby edge. The resulting distance is also normalized
	 * by the spread.
	 * 
	 * @param theSpread a positive number
	 */
	public void spread(double theSpread) {
		_mySpread = CCMath.max(1, theSpread);
	}
	
	/** @see #spread(double) */
	public double spread() {
		return _mySpread;
	}
	
	/**
	 * Caclulate the squared distance between two points
	 * 
	 * @param theX1 The x coordinate of the first point
	 * @param theY1 The y coordiante of the first point
 	 * @param theX2 The x coordinate of the second point
	 * @param theY2 The y coordinate of the second point
	 * @return The squared distance between the two points
	 */
	private static int squareDist(final int theX1, final int theY1, final int theX2, final int theY2){
		final int mydx = theX1 - theX2;
		final int mydy = theY1 - theY2;
		return mydx*mydx + mydy*mydy;
	}
	
	/**
	 * Returns the signed distance for a given point.
	 * 
	 * For points "inside", this is the distance to the closest "outside" pixel.
	 * For points "outside", this is the <em>negative</em> distance to the closest "inside" pixel.
	 * If no pixel of different color is found within a radius of {@code spread}, returns
	 * the {@code -spread} or {@code spread}, respectively.
	 * 
	 * @param theCenterX the x coordinate of the center point 
	 * @param theCenterY the y coordinate of the center point
	 * @param bitmap the array representation of an image, {@code true} representing "inside"
	 * @return the signed distance 
	 */
	private double findSignedDistance(final int theCenterX, final int theCenterY, boolean[][] bitmap, CCImage theInput){
		final int myWidth = bitmap.length;
		final int myHeight = bitmap[0].length;
		final boolean myBase = bitmap[theCenterX][theCenterY];
		
		final int myDelta = CCMath.ceil(_mySpread);
		final int myStartX = CCMath.max(0, theCenterX - myDelta);
		final int myStartY = CCMath.max(0, theCenterY - myDelta);
		final int myEndX  = CCMath.min(myWidth - 1, theCenterX + myDelta);
		final int myEndY = CCMath.min(myHeight - 1, theCenterY + myDelta);

		int closestSquareDist = myDelta * myDelta;
		
		for (int x = myStartX; x <= myEndX; x++){
			for (int y = myStartY; y <= myEndY; y++){
				if (myBase == bitmap[x][y])continue;
				
				final int squareDist = squareDist(theCenterX, theCenterY, x, y);
				if (squareDist < closestSquareDist){
					closestSquareDist = squareDist;
				}
			}
		}
		//double myDensity = theInput.getPixel(theCenterX, theCenterY).r;
		double closestDist = CCMath.sqrt(closestSquareDist);
		if(myBase) {
			return CCMath.min(closestDist  -1, _mySpread) / _mySpread;
		}
		return -CCMath.min(closestDist , _mySpread) / _mySpread;
//		return myBase ? _mySpread : -_mySpread;
	}
	
	/**
	 * For a distance as returned by {@link #findSignedDistance}, returns the corresponding "RGB" (really ARGB) color value.
	 *  
	 * @param theSignedDistance the signed distance of a pixel
	 * @return an ARGB color value suitable for {@link BufferedImage#setRGB}.
	 */
	private CCColor distanceToRGB(double theSignedDistance) {
		double myAlpha = CCMath.saturate(0.5 + 0.5 * (theSignedDistance));
		
//		if(theSignedDistance < 0)return CCColor.BLUE;
//		return CCColor.GREEN;
		return new CCColor(1d, myAlpha);
	}
	
	/**
	 * Process the image into a distance field.
	 * <p>
	 * The input image should be binary (black/white), but if not, see {@link #isInside(int)}.
	 * <p>
	 * Opaque pixels more than {@link #spread} away in the output image from white remain opaque;
	 * transparent pixels more than {@link #spread} away in the output image from black remain transparent.
	 * In between, we get a smooth transition from opaque to transparent, with an alpha value of 0.5
	 * when we are exactly on the edge.
	 * 
	 * @param theInput the image to process. 
	 * @return the distance field image
	 */
	public CCImage generateDistanceField(CCImage theInput) {
		// Note: coordinates reversed to mimic storage of BufferedImage, for memory locality
		
		final boolean[][] myBitmap = new boolean[theInput.width()][theInput.height()];
		for (int x = 0; x < theInput.width(); x++) {
			for (int y = 0; y < theInput.height(); y++) {
				CCColor myPixel = theInput.getPixel(x, y);
				myBitmap[x][y] = myPixel.r > 0.5;
//				if(x % 40 > 20 && y % 40 > 20)myBitmap[x][y] = true;
			}
		}
		
		CCImage myOutput = new CCImage(theInput.width(), theInput.height());
		
		for (int x = 0; x < myOutput.width(); x++){
			for (int y = 0; y < myOutput.height(); y++){
				int myCenterX = x;
				int myCenterY = y;
				double signedDistance = findSignedDistance(myCenterX, myCenterY, myBitmap, theInput);
				//signedDistance -= theInput.getPixel(x, y).r - 0.5;
				myOutput.setPixel(x, y, distanceToRGB(signedDistance));
//				myOutput.setPixel(x, y, new CCColor(theInput.getPixel(x, y).r));
			}
		}
		
		return myOutput;
	}
}
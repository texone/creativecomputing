package cc.creativecomputing.geometry.surface;

import java.util.ArrayList;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.random.CCRandom;

public class CCImageTargets {
	
	private final CCImage _myImage;
	
	public CCImageTargets(final CCImage theImage) {
		
		_myImage = theImage;
	}
	
	public CCImage image() {
		return _myImage;
	}
	
	public ArrayList<CCVector3> createAndDistributePointsOnImage(final int theNumber, final int theNumberOfTries) {
		final ArrayList<CCVector3> _myTargets = new ArrayList<CCVector3>();
		
		final double myCoveredImageArea = countBrightImagePixels() / (double)(_myImage.width() * _myImage.height());
		
		if(myCoveredImageArea < 0.01) {
			CCLog.warn("The count of active pixels in the image is very low.");
		}
		
		final double myRadius =  CCMath.sqrt(myCoveredImageArea/(theNumber*CCMath.PI));
		
		final CCRandom myRandom = new CCRandom();
				
		for(int i = 0; i < theNumber; i++) {
						
			CCVector3 myBestResult = null;
			
			int myTryCount = 0;
			int myLastCollisionCount = Integer.MAX_VALUE;
			
			
			/* Try different successful positions */
			while(myTryCount < theNumberOfTries) {
				
				final CCVector3  myPosition = new CCVector3 (myRandom.random(), myRandom.random());
				
				int myCollisionCount = 0;
				
				
				/* Is the point suitable? */
				if(getImageValue(myPosition) > 0.5f ) {
					myTryCount++;
					
					/* Check with brute force the collisions against all others */
					for(final CCVector3 myOtherTarget: _myTargets) {
			
						final double myDistance = myPosition.distance(myOtherTarget);
												
						if(myDistance * 0.5 < myRadius) {
							myCollisionCount++;
						}
					}	
					
					if(myCollisionCount < myLastCollisionCount) {
						myBestResult = myPosition;
						myLastCollisionCount = myCollisionCount;
					}								
				}
			}
			
			if(myBestResult == null)continue;
			
			_myTargets.add(myBestResult.multiply(_myImage.width(), _myImage.height(),1));
		}
		return _myTargets;
	}
	
	
	
	/**
	 * Reads the brightness from the attached image. 
	 *
	 * @param theX normalized image coordinate [0-1]
	 * @param theY normalized image coordinate [0-1]
	 * @return normalized brightness value [0-1]
	 */
	private double getImageValue(CCVector3 thePosition) {
		final int myX = CCMath.floor(thePosition.x * _myImage.width());
		final int myY = CCMath.floor(thePosition.y * _myImage.height());

		return _myImage.getPixel(myX, myY).r;
	}


	private int countBrightImagePixels() {
		int myCount = 0;
		for (int x = 0; x < _myImage.width(); x++) {
			for (int y = 0; y < _myImage.height(); y++) {
				if (_myImage.getPixel(x, y).r > 0.5f)
					myCount++;
			}
		}
		return myCount;
	}
}

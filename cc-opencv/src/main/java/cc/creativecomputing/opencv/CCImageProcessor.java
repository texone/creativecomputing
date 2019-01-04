package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;

public abstract class CCImageProcessor {
	
	/**
	 * Various border types
	 * @author chris
	 *
	 */
	public static enum CCBorderType{
		/**
		 * iiiiii|abcdefgh|iiiiiii
		 */
		CONSTANT ( BORDER_CONSTANT  ),
		/**
		 * aaaaaa|abcdefgh|hhhhhhh
		 */
		REPLICATE ( BORDER_REPLICATE  ),
		/**
		 * fedcba|abcdefgh|hgfedcb
		 */
		REFLECT( BORDER_REFLECT   ),
		/**
		 * cdefgh|abcdefgh|abcdefg
		 */
		WRAP ( BORDER_WRAP  ),
		/**
		 * gfedcb|abcdefgh|gfedcba
		 */
		REFLECT_101 ( BORDER_REFLECT_101  ),
		/**
		 * uvwxyz|abcdefgh|ijklmno
		 */
		TRANSPARENT ( BORDER_TRANSPARENT  ),
		/**
		 * 
		 */
		ISOLATED ( BORDER_ISOLATED  );
		
		public final int id;
		
		private CCBorderType(int theID) {
			id = theID;
		}
	}
	
	@CCProperty(name = "bypass")
	protected boolean _cBypass = true;
	
	public abstract void implementation(Mat theSource);
	
	public void process(Mat theSource) {
		if(_cBypass)return;
		
		
		implementation(theSource);
	}
	
	public void preDisplay(CCGraphics g) {
		
	}
}

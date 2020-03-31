package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;

public abstract class CCImageOperation {
	
	
	@CCProperty(name = "bypass")
	protected boolean _cBypass = true;
	
	public abstract Mat implementation(Mat theSource0, Mat theSource1);
	
	public Mat process(Mat theSource0, Mat theSource1) {
		if(_cBypass)return theSource0;
		
		
		return implementation(theSource0,theSource1);
	}
	
	public void preDisplay(CCGraphics g) {
		
	}
}

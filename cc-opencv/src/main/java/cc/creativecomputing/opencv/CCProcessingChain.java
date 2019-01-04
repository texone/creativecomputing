package cc.creativecomputing.opencv;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.core.CCProperty;

public class CCProcessingChain extends CCImageProcessor{
	
	@CCProperty(name = "processors")
	private Map<String, CCImageProcessor> _cProcessorMap = new LinkedHashMap<>();

	@Override
	public void implementation(Mat theSource) {
		_cProcessorMap.forEach((k,v)->v.process(theSource));
	}

}

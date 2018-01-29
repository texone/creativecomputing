package cc.creativecomputing.graphics.shader.imaging.noise;

import cc.creativecomputing.graphics.shader.imaging.CCSimpleImageFilter;
import cc.creativecomputing.io.CCNIOUtil;

public class CCNoiseGenerator extends CCSimpleImageFilter{

	public CCNoiseGenerator(int theWidth, int theHeight) {
		super(theWidth, theHeight, CCNIOUtil.classPath(CCNoiseGenerator.class, "voronoise.glsl"));
	}

}

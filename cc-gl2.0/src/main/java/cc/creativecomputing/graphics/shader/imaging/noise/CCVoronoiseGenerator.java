package cc.creativecomputing.graphics.shader.imaging.noise;

import cc.creativecomputing.graphics.shader.imaging.CCSimpleImageFilter;
import cc.creativecomputing.io.CCNIOUtil;

public class CCVoronoiseGenerator extends CCSimpleImageFilter{

	public CCVoronoiseGenerator(int theWidth, int theHeight) {
		super(theWidth, theHeight, CCNIOUtil.classPath(CCVoronoiseGenerator.class, "voronoise.glsl"));
	}

}

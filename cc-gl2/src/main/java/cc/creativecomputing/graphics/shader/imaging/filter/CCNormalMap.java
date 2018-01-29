package cc.creativecomputing.graphics.shader.imaging.filter;

import cc.creativecomputing.graphics.shader.imaging.CCSimpleImageFilter;
import cc.creativecomputing.io.CCNIOUtil;

public class CCNormalMap extends CCSimpleImageFilter{
	public CCNormalMap(int theWidth, int theHeight) {
		super(theWidth, theHeight, CCNIOUtil.classPath(CCNormalMap.class, "normalmap.glsl"));
	}
}

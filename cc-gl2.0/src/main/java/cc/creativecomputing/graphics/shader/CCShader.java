package cc.creativecomputing.graphics.shader;

import java.nio.file.Path;

import cc.creativecomputing.io.CCNIOUtil;

public abstract class CCShader {
	/**
	 * Takes the given files and merges them to one String. 
	 * This method is used to combine the different shader sources and get rid of the includes
	 * inside the shader files.
	 * @param thePaths
	 * @return
	 */
	protected String buildSource(final Path...thePaths) {
		StringBuffer myBuffer = new StringBuffer();
		
		for(Path myPath:thePaths) {
			myBuffer.append(CCNIOUtil.loadString(myPath));
			myBuffer.append("\n");
		}
		
		return myBuffer.toString();
	}
}

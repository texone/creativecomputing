package cc.creativecomputing.demo.topic.geometry;

import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.math.CCLine3;

public abstract class CCLineRenderer extends CCVBOMesh{

	public CCLineRenderer(List<CCLine3> theLines, CCDrawMode theDrawMode) {
		super(theDrawMode);
		
		prepareMesh(theLines);
	}
	
	public abstract void prepareMesh(List<CCLine3> theLines);
	
	//public  void renderLines(int nbLines, float* pLines, float radius, GLuint texture, float screenRatio);

}

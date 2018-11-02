package cc.creativecomputing.demo.topic.geometry;

import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCLine3;

public class CCLineRendererSimple extends CCLineRenderer{

	public CCLineRendererSimple(List<CCLine3> theLines) {
		super(theLines, CCDrawMode.LINES);
	}

	@Override
	public void prepareMesh(List<CCLine3> theLines) {
		prepareVertexData(theLines.size() * 2, 3);
		
		for(CCLine3 myLine:theLines) {
			addVertex(myLine.start());
			addVertex(myLine.end());
		}
	}

	
}

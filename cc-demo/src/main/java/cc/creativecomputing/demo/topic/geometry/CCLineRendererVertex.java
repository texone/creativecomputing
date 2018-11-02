package cc.creativecomputing.demo.topic.geometry;

import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCLine3;

public class CCLineRendererVertex extends CCLineRenderer{
	
	@CCProperty(name = "shader")
	private CCGLProgram _cShader;

	public CCLineRendererVertex(List<CCLine3> theLines) {
		super(theLines, CCDrawMode.QUADS);
		
		_cShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "lines_vertex_vertex.glsl"),
			CCNIOUtil.classPath(this, "lines_vertex_fragment.glsl")
		);
	}

	@Override
	public void prepareMesh(List<CCLine3> theLines) {
		prepareVertexData(theLines.size() * 12, 3);
		prepareTextureCoordData(theLines.size() * 12, 0, 3);
		prepareTextureCoordData(theLines.size() * 12, 1, 4);
		
		for(CCLine3 myLine:theLines) {
//			addTextureCoords(1, 1.0f, 1.0f, 1.0f, 0.0f);
//			addTextureCoords(1, 1.0f,-1.0f, 1.0f, 1.0f);
//			addTextureCoords(1, 0.0f, 1.0f, 0.5f, 0.0f);
//			addTextureCoords(1, 0.0f,-1.0f, 0.5f, 1.0f);
//			
//			addTextureCoords(1, 0.0f,-1.0f, 0.5f, 0.0f);
//			addTextureCoords(1, 0.0f, 1.0f, 0.5f, 1.0f);
//			addTextureCoords(1, 1.0f,-1.0f, 0.0f, 0.0f);
//			addTextureCoords(1, 1.0f, 1.0f, 0.0f, 1.0f);

			addTextureCoords(0, myLine.end());
			addTextureCoords(0, myLine.end());
			addTextureCoords(0, myLine.end());
			addTextureCoords(0, myLine.end());

			addTextureCoords(1, 1.0f, 1.0f, 1.0f, 0.0f);
			addTextureCoords(1, 1.0f,-1.0f, 1.0f, 1.0f);
			addTextureCoords(1, 0.0f,-1.0f, 0.5f, 1.0f);
			addTextureCoords(1, 0.0f, 1.0f, 0.5f, 0.0f);

			addVertex(myLine.start());
			addVertex(myLine.start());
			addVertex(myLine.start());
			addVertex(myLine.start());

			addTextureCoords(0, myLine.end());
			addTextureCoords(0, myLine.end());
			addTextureCoords(0, myLine.start());
			addTextureCoords(0, myLine.start());
			
			addTextureCoords(1, 0.0f, 1.0f, 0.5f, 0.0f);
			addTextureCoords(1, 0.0f,-1.0f, 0.5f, 1.0f);
			addTextureCoords(1, 0.0f, 1.0f, 0.5f, 1.0f);
			addTextureCoords(1, 0.0f,-1.0f, 0.5f, 0.0f);
			
			addVertex(myLine.start());
			addVertex(myLine.start());
			addVertex(myLine.end());
			addVertex(myLine.end());
			
			addTextureCoords(1, 0.0f,-1.0f, 0.5f, 0.0f);
			addTextureCoords(1, 0.0f, 1.0f, 0.5f, 1.0f);
			addTextureCoords(1, 1.0f, 1.0f, 0.0f, 1.0f);
			addTextureCoords(1, 1.0f,-1.0f, 0.0f, 0.0f);
			
			addVertex(myLine.end());
			addVertex(myLine.end());
			addVertex(myLine.end());
			addVertex(myLine.end());
			
			addTextureCoords(0, myLine.start());
			addTextureCoords(0, myLine.start());
			addTextureCoords(0, myLine.start());
			addTextureCoords(0, myLine.start());
		}
	}

	@Override
	public void draw(CCGraphics g) {
		_cShader.start();
		_cShader.uniform1f("radius", 10);
		_cShader.uniform1f("invScrRatio", g.aspectRatio());
		super.draw(g);
		_cShader.end();
	}
}

package cc.creativecomputing.simulation.particles.render;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.simulation.particles.CCParticle;
import cc.creativecomputing.simulation.particles.CCParticles;

public class CCParticleTriangleRenderer extends CCParticleRenderer{
	
	@CCProperty(name = "shader")
	protected CCGLProgram _myShader;
	
	protected CCParticles _myParticles;
	
	protected CCVBOMesh _myMesh;
	
	protected int _myTriangleCount;
	
	private CCShaderBuffer _myBuffer;
	
	private CCGLProgram _myInitValueShader;
	
	public CCParticleTriangleRenderer(int theTriangleCount, Path theVertexShader, Path theFragmentShader) {
		super("triangles");
		_myShader = new CCGLProgram(theVertexShader, theFragmentShader);
		_myTriangleCount = theTriangleCount;
		
		_myInitValueShader = new CCGLWriteDataShader();
	}
	
	public CCParticleTriangleRenderer(int theTriangleCount) {
		this(
			theTriangleCount,
			CCNIOUtil.classPath(CCDisplayShader.class, "triangles_vertex.glsl"),
			CCNIOUtil.classPath(CCDisplayShader.class, "triangles_fragment.glsl")
		);
	}
	
	private void resetTextures(CCGraphics g) {
		_myBuffer.beginDraw(g);
		_myInitValueShader.start();
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords3D(-1f, -1f, 0);
		g.vertex(0,0);
		g.vertex(_myBuffer.width(),0);
		g.vertex(_myBuffer.width(),_myBuffer.height());
		g.vertex(0,_myBuffer.height());
		g.endShape();
		_myInitValueShader.end();
		_myBuffer.endDraw(g);
	}
	
	private boolean _myInit = true;
	private int _myIndex = 0;
	
	@Override
	public void preDisplay(CCGraphics g) {
		super.preDisplay(g);
		
		if(_myInit) {
			resetTextures(g);
			_myInit = false;
		}
		
		if(_myIndex > _myBuffer.width() * _myBuffer.height())return;
		
		CCMesh myMesh = new CCMesh(CCDrawMode.POINTS, _myTriangles.size());
		g.beginShape(CCDrawMode.POINTS);
		for(CCParticle myParticle:_myTriangles) {
			myMesh.addVertex(_myIndex %  _myBuffer.width(), _myIndex /  _myBuffer.width());
			myMesh.addTextureCoords(0, myParticle.x(), myParticle.y(), 0, 1);
			myMesh.addTextureCoords(1, myParticle.texCoords().x, myParticle.texCoords().y, 0, 1);
			
			_myIndex++;
		}
			
		g.endShape();
		
		g.noBlend();
		_myBuffer.beginDraw(g);
		_myInitValueShader.start();
		myMesh.draw(g);
		_myInitValueShader.end();
		g.clearColor(0);
		_myBuffer.endDraw(g);
		

		_myTriangles.clear();
	}

	@Override
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, _myParticles.size() * _myTriangleCount);
		CCLog.info("TRIANGLES", _myParticles.size() * _myTriangleCount);
		_myMesh.prepareVertexData(3);
	
		for(int y = 0; y < _myParticles.height();y++) {
			for(int x = 0; x < _myParticles.width() * _myTriangleCount;x++) {
				_myMesh.addVertex(x,y,0);
			}
		}
		
		_myBuffer = new CCShaderBuffer(32, 3, 2,  _myParticles.width() * _myTriangleCount, _myParticles.height());
	}
	
	private List<CCParticle> _myTriangles = new ArrayList<>();
	
	public void addTriangle(CCParticle theA, CCParticle theB, CCParticle theC) {
		if(theA == null || theB == null || theC == null)return;
		_myTriangles.add(theA);
		_myTriangles.add(theB);
		_myTriangles.add(theC);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}
	
	private CCTexture2D _myTexture;
	
	public void texture(CCTexture2D theTexture) {
		_myTexture = theTexture;
	}

	@Override
	public void display(CCGraphics g) {
		if(!_cIsActive)return;
		_cAttributes.start(g);
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(2, _myParticles.dataBuffer().attachment(3));
		g.texture(3, _myBuffer.attachment(0));
		g.texture(4, _myBuffer.attachment(1));
		if(_myTexture != null)g.texture(5, _myTexture);
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("colors", 2);
		_myShader.uniform1i("triangleIDs", 3);
		_myShader.uniform1i("texCoords", 4);
		if(_myTexture != null)_myShader.uniform1i("texture", 5);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height());
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		_cAttributes.end(g);
	}

	@Override
	public CCMesh mesh() {
		return _myMesh;
	}
}

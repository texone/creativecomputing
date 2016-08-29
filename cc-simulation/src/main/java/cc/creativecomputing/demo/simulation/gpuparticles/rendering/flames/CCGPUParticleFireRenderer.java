package cc.creativecomputing.demo.simulation.gpuparticles.rendering.flames;
import javax.media.opengl.GL2;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.random.CCRandom;
import cc.creativecomputing.simulation.particles.CCGPUParticleSort;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.render.CCGPUIndexedParticleRenderer;

public class CCGPUParticleFireRenderer extends CCGPUIndexedParticleRenderer{
	
	
	private class CCNoiseControl{
		@CCControl(name = "octaves", min = 1, max = 10)
		private int octaves = 4; 
		@CCControl(name = "gain", min = 0, max = 1)
		private float gain = 0.5f; 
		@CCControl(name = "lacunarity", min = 0, max = 10)
		private float lacunarity = 2f; 
		
		@CCControl(name = "speed x", min = -10, max = 10)
		private float speedX = 0f; 
		@CCControl(name = "speed y", min = -10, max = 10)
		private float speedY = 1.0f; 
		@CCControl(name = "speed z", min = -10, max = 10)
		private float speedZ = 0f; 
		@CCControl(name = "speed gain", min = 0, max = 2)
		private float speedGain = 0.5f; 
	}
	
	@CCControl(name = "noise", column = 1)
	private CCNoiseControl _cNoiseControl = new CCNoiseControl();
	
	private CCGPUParticleSort _mySort;
	private CCGraphics _myGraphics;
	private CCTexture3D _myFlameTexture;

	private CCTexture2D _myRandomTexture;
	private CCRandom _myRandom = new CCRandom();
	
	private float _myPointSpriteRatio;
	
	private float _myMinSpeed = 1;
	private float _myMaxSpeed = 1;
	
	private float _myNoisePow = 1;
	private float _myNoiseBlend = 1;
	private float _myNoiseScale = 1;
		
	public CCGPUParticleFireRenderer(CCGraphics theGraphics, CCTexture3D theFlameTexture) {
		super(
			CCIOUtil.classPath(CCGPUParticleFireRenderer.class, "fire_vertex.glsl"),
			CCIOUtil.classPath(CCGPUParticleFireRenderer.class, "fire_fragment.glsl")
		);
		_myGraphics = theGraphics;
		_myFlameTexture = theFlameTexture;
		
		_myPointSpriteRatio = _myFlameTexture.width() / (float)_myFlameTexture.height();
		
		CCColor[][] myBaseColorMap = new CCColor[256][256];
		
		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				myBaseColorMap[x][y] = new CCColor(_myRandom.random(),0,0,0);
			}
		}

		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				int x2 = (x + 37) % 256;
				int y2 = (y + 17) % 256;
				myBaseColorMap[x2][y2].g = myBaseColorMap[x][y].r;
			}
		}
		
		CCTextureData myData = new CCTextureData(256,256);
		for(int x = 0; x < myData.width(); x++){
			for(int y = 0; y < myData.height(); y++){
				myData.setPixel(x, y, myBaseColorMap[x][y]);
			}
		}
		
		_myRandomTexture = new CCTexture2D(myData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
	}
		
	@Override
	public void setup(CCParticles theParticles) {
		_myParticles = theParticles;
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS, _myParticles.size() * 4);
		_myMesh.prepareVertexData(4);
		_myMesh.prepareTextureCoordData(0, 4);
			
		for(int x = 0; x < _myParticles.width();x++) {
			for(int y = 0; y < _myParticles.height();y++) {
				_myMesh.addVertex(x,y,-1,-1);
				_myMesh.addVertex(x,y, 1,-1);
				_myMesh.addVertex(x,y, 1, 1);
				_myMesh.addVertex(x,y,-1, 1);
				float myRand1 = CCMath.random();
				float myRand2 = CCMath.random();
				_myMesh.addTextureCoords(0, 0f, 1f, myRand1, myRand2);
				_myMesh.addTextureCoords(0, 1f, 1f, myRand1, myRand2);
				_myMesh.addTextureCoords(0, 1f, 0f, myRand1, myRand2);
				_myMesh.addTextureCoords(0, 0f, 0f, myRand1, myRand2);
			}
		}
			
		_mySort = new CCGPUParticleSort(_myGraphics, theParticles);
	}
		
	public CCGPUParticleSort sort(){
		return _mySort;
	}
	
	float _myTime = 0;
		
	@Override
	public void update(float theDeltaTime) {
//		CCLog.info("SORT");
//		for(int i = 0; i < 10;i++){
//			_mySort.update(theDeltaTime);
//		}
		_myTime += theDeltaTime;
	}
	
	@CCControl(name = "min speed", min = 0, max = 10)
	public void minSpeed(float theSpeed){
		_myMinSpeed = theSpeed;
	}
	
	@CCControl(name = "max speed", min = 0, max = 10)
	public void maxSpeed(float theSpeed){
		_myMaxSpeed = theSpeed;
	}
	
	@CCControl(name = "noise pow", min = 0, max = 10)
	public void noisePow(float theNoiseNoisePow){
		_myNoisePow = theNoiseNoisePow;
	}
	
	@CCControl(name = "noise blend amount", min = 0, max = 2)
	public void noiseBlendAmount(float theNoiseNoisePow){
		_myNoiseBlend = theNoiseNoisePow;
	}
	
	@CCControl(name = "noisescale", min = 0, max = 10)
	public void noiseScale(float theNoiseNoisePow){
		_myNoiseScale = theNoiseNoisePow;
	}
		
	@Override
	public void draw(CCGraphics g) {
		g.gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
		_myShader.start();
		g.texture(0, _myParticles.dataBuffer().attachment(0));
		g.texture(1, _myParticles.dataBuffer().attachment(1));
		g.texture(2, _mySort.indices().attachment(0));
		g.texture(3, _myFlameTexture);
		g.texture(4, _myRandomTexture);
		_myShader.uniform1i("positions", 0);
		_myShader.uniform1i("infos", 1);
		_myShader.uniform1i("indices", 2);
		_myShader.uniform1f("tanHalfFOV", CCMath.tan(g.camera().fov()) * g.height);
		_myShader.uniform2f("pointSize", _myPointsize, _myPointsize / _myPointSpriteRatio);
		_myShader.uniform1i("pointSprite", 3);
		_myShader.uniform1f("minSpeed", _myMinSpeed);
		_myShader.uniform1f("maxSpeed", _myMaxSpeed);
		
		_myShader.uniform1f("time", _myTime);
		_myShader.uniform1i("randomTexture",4);
		_myShader.uniform1i("octaves", _cNoiseControl.octaves);
		_myShader.uniform1f("gain", _cNoiseControl.gain);
		_myShader.uniform1f("lacunarity", _cNoiseControl.lacunarity);
		
		_myShader.uniform3f("noiseMovement", _cNoiseControl.speedX, _cNoiseControl.speedY, _cNoiseControl.speedZ);
		_myShader.uniform1f("speedGain", _cNoiseControl.speedGain);
		_myShader.uniform1f("noisePow", _myNoisePow);
		_myShader.uniform1f("noiseBlendAmount", _myNoiseBlend);
		_myShader.uniform1f("noiseScale", _myNoiseScale);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		g.gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE) ;
	}
}
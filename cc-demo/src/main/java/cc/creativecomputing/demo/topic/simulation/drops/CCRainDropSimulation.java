package cc.creativecomputing.demo.topic.simulation.drops;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCRainDropSimulation {
	
	private double globalTimeScale = 1;
	
	@CCProperty(name = "raining")
	private boolean _cRaining = true;
	
	public CCRainOptions options;

	public double _myWidth;
	public double _myHeight;

	public double _myScale;
	
	
	@CCProperty(name = "rain shader")
	private CCGLProgram _cRainShader;

	private CCShaderBuffer _myRainContext;
	
	private CCTexture2D _myForeground;
	private CCTexture2D _myBackground;
	
	
	@CCProperty(name = "drops")
	private CCRainDrops _myDrops;
	@CCProperty(name = "droplets")
	private CCRainDroplets _myDroplets;
	@CCProperty(name = "text")
	private CCRainText _myText;

	public CCRainDropSimulation(
		double theWidth, 
		double theHeight, 
		double theScale, 
		CCRainOptions theOptions
	) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myScale = theScale;

		options = theOptions;
		
		_myRainContext = new CCShaderBuffer((int)_myWidth,(int) _myHeight, CCTextureTarget.TEXTURE_2D);
		
		_cRainShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "rain-vertex.glsl"),
			CCNIOUtil.classPath(this, "rain-fragment.glsl")
		);

		_myForeground = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "weather/texture-rain-fg.png")));
		_myBackground = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "weather/texture-rain-bg.png")));
		
		_myDrops = new CCRainDrops(this);
		_myDroplets = new CCRainDroplets(this);
		_myText = new CCRainText(this);
		
	}
	
	public CCRainDropSimulation(
		double theWidth, 
		double theHeight, 
		double theScale
	) {
		this(theWidth, theHeight, theScale, new CCRainOptions());
	}
	
	public CCRainDropSimulation(
		double theWidth, 
		double theHeight
	) {
		this(theWidth, theHeight, 1);
	}
	
	public CCRainDrops drops() {
		return _myDrops;
	}
	
	public double width() {
		return _myWidth;
	}
	
	public double height() {
		return _myHeight;
	}
	
	public double scale() {
		return _myScale;
	}
	
	public boolean active() {
		return _cRaining;
	}

	private double area() {
		return _myWidth * _myHeight / _myScale;
	}

	public double areaMultiplier() {
		return CCMath.sqrt(area() / (1024 * 768));
	}
	
	public double globalTimeScale() {
		return globalTimeScale;
	}
	
	public void clear(){
		_myDrops.clear();
		_myDroplets.clear();
	}
	
	private double timeScale = 0;
	
	public void update(CCAnimator theAnimator){
		timeScale = theAnimator.deltaTime() * 30 * globalTimeScale;

		_myDroplets.update(timeScale);
		_myDrops.update(timeScale);
		_myText.update(theAnimator);
	}

	public void preDisplay(CCGraphics g) {
		_myDroplets.preDisplay(g);
		_myText.preDisplay(g);
		
		_myRainContext.beginDraw(g);
		g.clear();
		g.pushMatrix();
		g.ortho();
		g.image(_myDroplets.texture(), 0, 0);
		
		_myText.display(g);
		_myDrops.display(g);
		g.popMatrix();
		_myRainContext.endDraw(g);
	}
	

	public void draw(CCGraphics g) {
		preDisplay(g);
		
		g.pushMatrix();
		g.ortho2D();
//		g.image(_myRainContext.attachment(0), 0, 0);
		
		g.texture(0, _myRainContext.attachment(0));
		g.texture(1, _myForeground);
		g.texture(2, _myBackground);
		
		_cRainShader.start();
		_cRainShader.uniform1i("waterMap", 0);
		_cRainShader.uniform1i("textureFg", 1);
		_cRainShader.uniform1i("textureBg", 2);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0);
	    g.vertex(10,10);
	    g.textureCoords2D(1, 0);
	    g.vertex(g.width() - 10,10);
	    g.textureCoords2D(1, 1);
	    g.vertex(g.width() - 10,g.height() - 10);
	    g.textureCoords2D(0, 1);
	    g.vertex(10,g.height() -10);
	    g.endShape();
		_cRainShader.end();
		g.noTexture();
		
		
		g.popMatrix();
	}
}

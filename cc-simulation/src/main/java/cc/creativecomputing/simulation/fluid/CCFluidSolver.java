package cc.creativecomputing.simulation.fluid;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.events.CCMouseSimpleInfo;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShaderNoise;
import cc.creativecomputing.graphics.texture.CCGLSwapBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCFluidSolver {
	
	private CCFluidGrid grid;
	private CCFluidTime time;
	private CCVector2 windowSize;
	
	public CCGLSwapBuffer velocity;
	public CCGLSwapBuffer density;
	public CCGLSwapBuffer velocityDivergence;
	private CCGLSwapBuffer velocityVorticity;
	public CCGLSwapBuffer pressure;
	
	private CCVector3 source = new CCVector3(0.8, 0.0, 0.0);
	public CCVector3 ink = new CCVector3(0.0, 0.06, 0.19);
	
	@CCProperty(name = "apply viscosity")
	private boolean applyViscosity = false;
	@CCProperty(name = "viscosity", min = 0, max = 1)
	private double viscosity = 0.3;
	
	@CCProperty(name = "apply vorticity")
	private boolean applyVorticity = false;
	@CCProperty(name = "curl", min = 0, max = 1)
	private double curl = 0.3;
	
	private CCGLProgram advect;
	private CCFluidJacobi diffuse;
	private CCGLProgram divergence;
	private CCFluidJacobi poissonPressureEq;
	private CCGLProgram gradient;
	private CCGLProgram splat;
	
	private CCGLProgram noiseField;
	
	private CCTexture2D _myRandomTexture;
	
	@CCProperty(name = "noise scale", min = 0, max = 100)
	private double _myNoiseScale = 1;
	@CCProperty(name = "noise octaves", min = 0, max = 5)
	private int _cNoiseOctaves = 1;
	@CCProperty(name = "noise gain", min = 0, max = 1)
	private double _cNoiseGain = 0.5;
	@CCProperty(name = "noise lacunarity", min = 0, max = 4)
	private double _cNoiseLacunarity = 2;
	@CCProperty(name = "noise amount", min = 0, max = 1)
	private double _cNoiseAmount = 0;
	@CCProperty(name = "noise border", min = 0, max = 1)
	private double _cNoiseBorder = 0;
	@CCProperty(name = "noise fade", min = -1, max = 1)
	private double _cNoiseFade = 0;
	@CCProperty(name = "noise dissipation", min = 0, max = 1)
	private double _cNoiseDissipation = 0;
	
	private CCVector3 _myNoiseOffset = new CCVector3();
	
	private CCGLProgram boundary;

	@CCProperty(name  = "color radius", min = 0, max = 1)
	private double colorRadius = 0.01;
	@CCProperty(name  = "velocity radius", min = 0, max = 1)
	private double velocityRadius = 0.01;
	
	private CCGLProgram vorticity;
	private CCGLProgram vorticityConfinement;
	
	//0.9 very fast 0.998 slow 1 none
	@CCProperty(name = "dissipation", min = 0.9, max = 1, digits = 4)
	private double _myDissipation = 0.998;
	
	private static enum CCFluidDrawMode{
		DENSITY,
		VELOCITY,
		DIVERGENCE,
		PRESSURE
	}
	
	@CCProperty(name = "fluid draw mode")
	private CCFluidDrawMode _myFluidDrawMode = CCFluidDrawMode.DENSITY;
	
	private CCFluidDisplay displayScalar;
	private CCFluidDisplay displayVector;

	public CCFluidSolver(CCFluidGrid theGrid, CCFluidTime theTime, CCVector2 theWindowSize) {
		this.grid = theGrid;
		this.time = theTime;
		this.windowSize = theWindowSize;

		// slabs
		int w = (int)grid.size.x;
		int h = (int)grid.size.y;
	        
		this.velocity = new CCGLSwapBuffer(w, h, CCTextureTarget.TEXTURE_2D);
		this.density = new CCGLSwapBuffer(w, h, CCTextureTarget.TEXTURE_2D);
		this.velocityDivergence = new CCGLSwapBuffer(w, h, CCTextureTarget.TEXTURE_2D);
		this.velocityVorticity = new CCGLSwapBuffer(w, h, CCTextureTarget.TEXTURE_2D);
		this.pressure = new CCGLSwapBuffer(w, h, CCTextureTarget.TEXTURE_2D);

		// slab operations
		advect = new CCGLProgram(null, CCNIOUtil.classPath(this, "advect.fs"));
		this.diffuse = new CCFluidJacobi(theGrid, CCNIOUtil.classPath(this, "jacobivector.fs"));
		divergence = new CCGLProgram(null, CCNIOUtil.classPath(this, "divergence.fs"));
		this.poissonPressureEq = new CCFluidJacobi(theGrid, CCNIOUtil.classPath(this, "jacobivector.fs"));
		gradient = new CCGLProgram(null, CCNIOUtil.classPath(this, "gradient.fs"));
		splat = new CCGLProgram(null, CCNIOUtil.classPath(this, "splat.fs"));
		vorticity = new CCGLProgram(null, CCNIOUtil.classPath(this, "vorticity.fs"));
		vorticityConfinement = new CCGLProgram(null, CCNIOUtil.classPath(this, "vorticityforce.fs"));
		noiseField = new CCGLProgram(null, CCNIOUtil.classPath(this, "noisefield.fs"));
		
		_myRandomTexture = new CCTexture2D(CCGLShaderNoise.randomData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
		
		boundary = new CCGLProgram(null, CCNIOUtil.classPath(this, "boundary.fs"));
		
		displayScalar = new CCFluidDisplay(CCNIOUtil.classPath(this, "displayscalar.fs"));
		displayVector = new CCFluidDisplay(CCNIOUtil.classPath(this, "displayvector.fs"));
	}
	
	private void advect(CCGraphics g, CCGLSwapBuffer theTarget, double theDissipation){
		g.texture(0,velocity.attachment(0));
        g.texture(1,theTarget.attachment(0));
        advect.start();
        advect.uniform1i("velocity", 0);
        advect.uniform1i("advected", 1);
        advect.uniform2f("gridSize", grid.size);
        advect.uniform1f("gridScale", grid.scale);
        advect.uniform1f("timestep", time.step);
        advect.uniform1f("dissipation", theDissipation);
        theTarget.draw();
        advect.end();
        g.noTexture();
        
        theTarget.swap();
	}
	
	private double epsilon = 2.4414e-4;
	
	private void vorticity(CCGraphics g){
		if(!applyVorticity)return;
		
		g.texture(0,velocity.attachment(0));
		vorticity.start();
		vorticity.uniform1i("velocity", 0);
		vorticity.uniform2f("gridSize", grid.size);
		vorticity.uniform1f("gridScale", grid.scale);
        velocityVorticity.draw();
        vorticity.end();
        g.noTexture();
        velocityVorticity.swap();
        
        g.texture(0,velocity.attachment(0));
		g.texture(1,velocityVorticity.attachment(0));
		vorticityConfinement.start();
		vorticityConfinement.uniform1i("velocity", 0);
		vorticityConfinement.uniform1i("vorticity", 1);
		vorticityConfinement.uniform2f("gridSize", grid.size);
		vorticityConfinement.uniform1f("gridScale", grid.scale);
		vorticityConfinement.uniform1f("timestep", time.step);
		vorticityConfinement.uniform1f("epsilon", epsilon);
		vorticityConfinement.uniform2f("curl", curl * grid.scale, curl * grid.scale);
		velocity.draw();
        vorticityConfinement.end();
        g.noTexture();
        velocity.swap();
	}
	
	private void divergence(CCGraphics g){
		g.texture(0, velocity.attachment(0));
		divergence.start();
		divergence.uniform1i("velocity", 0);
		divergence.uniform2f("gridSize", grid.size);
		divergence.uniform1f("gridScale", grid.scale);
		velocityDivergence.draw();
		divergence.end();
		g.noTexture();

		velocityDivergence.swap();
	}
	
	private void gradient(CCGraphics g){
		g.texture(0,pressure.attachment(0));
        g.texture(1,velocity.attachment(0));
        gradient.start();
        gradient.uniform1i("p", 0);
        gradient.uniform1i("w", 1);
        gradient.uniform2f("gridSize", grid.size);
        gradient.uniform1f("gridScale", grid.scale);
        velocity.draw();
        gradient.end();
        g.noTexture();
        velocity.swap();
	}
	
	// solve poisson equation and subtract pressure gradient
	private void project(CCGraphics g) {
		divergence(g);

		// 0 is our initial guess for the poisson equation solver
		pressure.clear();

		poissonPressureEq.alpha = -grid.scale * grid.scale;
		poissonPressureEq.compute(
				g,
				this.pressure.attachment(0),
				this.velocityDivergence.attachment(0),
				this.pressure
			);

		gradient(g);
	}
	
	private void renderLine(
		CCGraphics g, 
		double theX0, double theY0,
		double theX1, double theY1, 
		double theOffsetX, double theOffsetY
	){
		
        boundary.uniform2f("gridOffset", theOffsetX, theOffsetY);
		g.beginShape(CCDrawMode.LINES);
		g.vertex(theX0, theY0);
		g.vertex(theX1, theY1);
        g.endShape();
	}
	
	private void boundary(CCGraphics g, double scale, CCGLSwapBuffer theBuffer){
		g.texture(0, theBuffer.attachment(0));
		boundary.start();
		boundary.uniform1i("read", 0);
		boundary.uniform2f("gridSize", grid.size);
		boundary.uniform1f("scale", scale);

		double x0 = 0.5;
		double x1 = windowSize.x - 0.5;
		double y0 = 0.5;
		double y1 = windowSize.y - 0.5;
		theBuffer.beginDraw();
        renderLine(g, x0, y0, x0, y1,  1,  0); // left
        renderLine(g, x1, y0, x1, y1, -1,  0); // right
        renderLine(g, x0, y1, x1, y1,  0,  1); // bottom
        renderLine(g, x0, y0, x1, y0,  0, -1); // top
        theBuffer.endDraw();
        boundary.end();
        g.noTexture();
	}
	
	public CCVector3 noiseOffset(){
		return _myNoiseOffset;
	}
	
	private void noise(CCGraphics g){

        g.texture(0,velocity.attachment(0));
        g.texture(1, _myRandomTexture);
        noiseField.start();
        noiseField.uniform2f("gridSize", grid.size);
		noiseField.uniform1i("velocity", 0);
		noiseField.uniform1i("randomTexture", 1);
		
		
		noiseField.uniform3f("offset", _myNoiseOffset);
		noiseField.uniform1f("scale", _myNoiseScale);

		noiseField.uniform1i("octaves", _cNoiseOctaves);
		noiseField.uniform1f("gain", _cNoiseGain);
		noiseField.uniform1f("lacunarity", _cNoiseLacunarity);

		noiseField.uniform1f("noiseAmount", _cNoiseAmount);
		
		noiseField.uniform1f("minX", _cNoiseBorder);
		noiseField.uniform1f("maxX", _cNoiseBorder + _cNoiseFade);

		noiseField.uniform1f("dissipation", _cNoiseDissipation);

        velocity.draw();
        noiseField.end();
        g.noTexture();
        velocity.swap();
		
		
	}
	
	public void step(CCGraphics g) {
		// we only want the quantity carried by the velocity field to be
		// affected by the dissipation

		noise(g);
		
		advect(g, velocity, 1);
		advect(g, density, _myDissipation);
		vorticity(g);

		if (applyViscosity && viscosity > 0) {
			double s = this.grid.scale;
			this.diffuse.alpha = (s * s) / (this.viscosity * this.time.step);
			this.diffuse.beta = 4 + this.diffuse.alpha;
			this.diffuse.compute(g, this.velocity.attachment(0), this.velocity.attachment(0), this.velocity);
		}

		project(g);
	}
	
	private void splat(CCGraphics g, CCGLSwapBuffer theTarget, CCVector3 theInput, CCVector2 thePosition, double theRadius){
		g.texture(0,theTarget.attachment(0));
        splat.start();
        splat.uniform1i("read", 0);
        splat.uniform2f("gridSize", grid.size);
        splat.uniform3f("color", theInput);
        splat.uniform2f("point", thePosition);
        splat.uniform1f("radius", theRadius);
//        theTarget.draw(thePosition.x - theRadius, thePosition.y - theRadius, thePosition.x + theRadius, thePosition.y + theRadius);
        theTarget.draw();
        splat.end();
        g.noTexture();
        theTarget.swap();
	}
	
	public void addColor(CCGraphics g, CCVector2 thePosition, CCColor theColor, double theRadius){
		CCVector2 point = new CCVector2();

		point.set(thePosition.x, this.windowSize.y - thePosition.y);
		// normalize to [0, 1] and scale to grid size
		point.x *= grid.size.x / windowSize.x;
		point.y *= grid.size.y / windowSize.y;
	     
		splat(g, density, new CCVector3(theColor.r,theColor.g,theColor.b), point, theRadius);
	}
	
	public void addColor(CCGraphics g, CCVector2 thePosition, CCColor theColor){
		addColor(g, thePosition, theColor, colorRadius);
	}
	
	public void addForce(CCGraphics g, CCVector2 thePosition, CCVector2 theForce, double theRadius){
		CCVector2 point = new CCVector2();

		point.set(thePosition.x, this.windowSize.y - thePosition.y);
		// normalize to [0, 1] and scale to grid size
		point.x *= grid.size.x / windowSize.x;
		point.y *= grid.size.y / windowSize.y;
		
		splat(g, velocity, new CCVector3(theForce.x, theForce.y, 0), point, theRadius);	
	}
	
	public void addForce(CCGraphics g, CCVector2 thePosition, CCVector2 theForce){
		addForce(g, thePosition, theForce, velocityRadius);
	}

	public void addForces(CCGraphics g, CCMouseSimpleInfo mouse) {
		CCVector2 point = new CCVector2();
		CCVector3 force = new CCVector3();
	     
		CCVector2 motion = mouse.motion;

		point.set(mouse.position.x, this.windowSize.y - mouse.position.y);
		// normalize to [0, 1] and scale to grid size
		point.x *= grid.size.x / windowSize.x;
		point.y *= grid.size.y / windowSize.y;

		force.set(motion.x, -motion.y, 0);
	     
		splat(g, velocity, force, point, colorRadius);	
		splat(g, density, source, point, velocityRadius);
	}

	public void display(CCGraphics g, int theX, int theY, int theWidth, int theHeight) {
		CCFluidDisplay display = null;
		CCTexture2D read = null;
			 
		switch (_myFluidDrawMode) {
		case VELOCITY:
			display = displayVector;
			display.scaleNegative();
			read = velocity.attachment(0);
			break;
		case DENSITY:
			display = displayVector;
			display.scale.set(1,1,1);
			display.bias.set(0, 0, 0);
			read = density.attachment(0);
			break;
		case DIVERGENCE:
			display = displayScalar;
			display.scaleNegative();
			read = velocityDivergence.attachment(0);
			break;
		case PRESSURE:
			display = displayScalar;
			display.scaleNegative();
			read = pressure.attachment(0);
			break;
		}
		display.display(g, read, theX, theY, theWidth, theHeight);
		
	}
	
	public void display(CCGraphics g){
		display(g,  -g.width()/2, -g.height()/2, g.width(), g.height());
	}

}

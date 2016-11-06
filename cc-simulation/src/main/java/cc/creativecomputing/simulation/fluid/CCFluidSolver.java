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
	
	private CCFluidGrid _myGrid;
	private CCVector2 _myWindowSize;
	
	public CCGLSwapBuffer _myVelocityData;
	public CCGLSwapBuffer _myDensityData;
	public CCGLSwapBuffer _myDivergenceData;
	private CCGLSwapBuffer _myVorticityData;
	public CCGLSwapBuffer _myPressureData;
	
	private CCVector3 source = new CCVector3(0.8, 0.0, 0.0);
	public CCVector3 ink = new CCVector3(0.0, 0.06, 0.19);
	
	@CCProperty(name = "step", min = 0, max = 1)
	public double _cStep = 1;
	@CCProperty(name = "apply viscosity")
	private boolean applyViscosity = false;
	@CCProperty(name = "viscosity", min = 0, max = 1)
	private double viscosity = 0.3;
	
	@CCProperty(name = "apply vorticity")
	private boolean _myApplyVorticity = false;
	@CCProperty(name = "curl", min = 0, max = 1)
	private double _cCurl = 0.3;
	
	private CCGLProgram _myAdvectProgram;
	private CCFluidJacobi _myDiffuseProgram;
	private CCGLProgram _myDivergenceProgram;
	private CCFluidJacobi _myPoissonPressureEq;
	private CCGLProgram _myGradientProgram;
	private CCGLProgram _mySplatProgram;
	
	private CCGLProgram _myNoiseFieldProgram;
	
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
	
	private CCGLProgram _myBoundaryProgram;

	@CCProperty(name  = "color radius", min = 0, max = 1)
	private double colorRadius = 0.01;
	@CCProperty(name  = "velocity radius", min = 0, max = 1)
	private double velocityRadius = 0.01;
	
	private CCGLProgram _myVorticityProgram;
	private CCGLProgram _myVorticityConfinementProgram;
	
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
	
	private CCFluidDisplay _myScalarDisplayProgram;
	private CCFluidDisplay _myVectorDisplayProgram;

	public CCFluidSolver(CCFluidGrid theGrid, CCVector2 theWindowSize) {
		_myGrid = theGrid;
		_myWindowSize = theWindowSize;

		// slabs
		int _myWidth = (int)_myGrid.size.x;
		int _myHeight = (int)_myGrid.size.y;
	        
		_myVelocityData = new CCGLSwapBuffer(_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myDensityData = new CCGLSwapBuffer(_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myDivergenceData = new CCGLSwapBuffer(_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myVorticityData = new CCGLSwapBuffer(_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myPressureData = new CCGLSwapBuffer(_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);

		// slab operations
		_myAdvectProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "advect.fs"));
		_myDiffuseProgram = new CCFluidJacobi(theGrid, CCNIOUtil.classPath(this, "jacobivector.fs"));
		_myDivergenceProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "divergence.fs"));
		_myPoissonPressureEq = new CCFluidJacobi(theGrid, CCNIOUtil.classPath(this, "jacobivector.fs"));
		_myGradientProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "gradient.fs"));
		_mySplatProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "splat.fs"));
		_myVorticityProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "vorticity.fs"));
		_myVorticityConfinementProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "vorticityforce.fs"));
		_myNoiseFieldProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "noisefield.fs"));
		
		_myRandomTexture = new CCTexture2D(CCGLShaderNoise.randomData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
		
		_myBoundaryProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "boundary.fs"));
		
		_myScalarDisplayProgram = new CCFluidDisplay(CCNIOUtil.classPath(this, "displayscalar.fs"));
		_myVectorDisplayProgram = new CCFluidDisplay(CCNIOUtil.classPath(this, "displayvector.fs"));
	}
	
	private void advect(CCGraphics g, CCGLSwapBuffer theTarget, double theDissipation){
		g.texture(0,_myVelocityData.attachment(0));
        g.texture(1,theTarget.attachment(0));
        _myAdvectProgram.start();
        _myAdvectProgram.uniform1i("velocity", 0);
        _myAdvectProgram.uniform1i("advected", 1);
        _myAdvectProgram.uniform2f("gridSize", _myGrid.size);
        _myAdvectProgram.uniform1f("gridScale", _myGrid.scale);
        _myAdvectProgram.uniform1f("timestep", _cStep);
        _myAdvectProgram.uniform1f("dissipation", theDissipation);
        theTarget.draw();
        _myAdvectProgram.end();
        g.noTexture();
        
        theTarget.swap();
	}
	
	private static double EPSILON = 2.4414e-4;
	
	private void vorticity(CCGraphics g){
		if(!_myApplyVorticity)return;
		
		g.texture(0,_myVelocityData.attachment(0));
		_myVorticityProgram.start();
		_myVorticityProgram.uniform1i("velocity", 0);
		_myVorticityProgram.uniform2f("gridSize", _myGrid.size);
		_myVorticityProgram.uniform1f("gridScale", _myGrid.scale);
        _myVorticityData.draw();
        _myVorticityProgram.end();
        g.noTexture();
        _myVorticityData.swap();
        
        g.texture(0,_myVelocityData.attachment(0));
		g.texture(1,_myVorticityData.attachment(0));
		_myVorticityConfinementProgram.start();
		_myVorticityConfinementProgram.uniform1i("velocity", 0);
		_myVorticityConfinementProgram.uniform1i("vorticity", 1);
		_myVorticityConfinementProgram.uniform2f("gridSize", _myGrid.size);
		_myVorticityConfinementProgram.uniform1f("gridScale", _myGrid.scale);
		_myVorticityConfinementProgram.uniform1f("timestep", _cStep);
		_myVorticityConfinementProgram.uniform1f("epsilon", EPSILON);
		_myVorticityConfinementProgram.uniform2f("curl", _cCurl * _myGrid.scale, _cCurl * _myGrid.scale);
		_myVelocityData.draw();
        _myVorticityConfinementProgram.end();
        g.noTexture();
        _myVelocityData.swap();
	}
	
	private void divergence(CCGraphics g){
		g.texture(0, _myVelocityData.attachment(0));
		_myDivergenceProgram.start();
		_myDivergenceProgram.uniform1i("velocity", 0);
		_myDivergenceProgram.uniform2f("gridSize", _myGrid.size);
		_myDivergenceProgram.uniform1f("gridScale", _myGrid.scale);
		_myDivergenceData.draw();
		_myDivergenceProgram.end();
		g.noTexture();

		_myDivergenceData.swap();
	}
	
	private void gradient(CCGraphics g){
		g.texture(0,_myPressureData.attachment(0));
        g.texture(1,_myVelocityData.attachment(0));
        _myGradientProgram.start();
        _myGradientProgram.uniform1i("p", 0);
        _myGradientProgram.uniform1i("w", 1);
        _myGradientProgram.uniform2f("gridSize", _myGrid.size);
        _myGradientProgram.uniform1f("gridScale", _myGrid.scale);
        _myVelocityData.draw();
        _myGradientProgram.end();
        g.noTexture();
        _myVelocityData.swap();
	}
	
	// solve poisson equation and subtract pressure gradient
	private void project(CCGraphics g) {
		divergence(g);

		// 0 is our initial guess for the poisson equation solver
		_myPressureData.clear();

		_myPoissonPressureEq.alpha = -_myGrid.scale * _myGrid.scale;
		_myPoissonPressureEq.compute(
			g,
			_myPressureData.attachment(0),
			_myDivergenceData.attachment(0),
			_myPressureData
		);

		gradient(g);
	}
	
	private void renderLine(
		CCGraphics g, 
		double theX0, double theY0,
		double theX1, double theY1, 
		double theOffsetX, double theOffsetY
	){
		
        _myBoundaryProgram.uniform2f("gridOffset", theOffsetX, theOffsetY);
		g.beginShape(CCDrawMode.LINES);
		g.vertex(theX0, theY0);
		g.vertex(theX1, theY1);
        g.endShape();
	}
	
	private void boundary(CCGraphics g, double scale, CCGLSwapBuffer theBuffer){
		g.texture(0, theBuffer.attachment(0));
		_myBoundaryProgram.start();
		_myBoundaryProgram.uniform1i("read", 0);
		_myBoundaryProgram.uniform2f("gridSize", _myGrid.size);
		_myBoundaryProgram.uniform1f("scale", scale);

		double x0 = 0.5;
		double x1 = _myWindowSize.x - 0.5;
		double y0 = 0.5;
		double y1 = _myWindowSize.y - 0.5;
		theBuffer.beginDraw();
        renderLine(g, x0, y0, x0, y1,  1,  0); // left
        renderLine(g, x1, y0, x1, y1, -1,  0); // right
        renderLine(g, x0, y1, x1, y1,  0,  1); // bottom
        renderLine(g, x0, y0, x1, y0,  0, -1); // top
        theBuffer.endDraw();
        _myBoundaryProgram.end();
        g.noTexture();
	}
	
	public CCVector3 noiseOffset(){
		return _myNoiseOffset;
	}
	
	private void noise(CCGraphics g){

        g.texture(0,_myVelocityData.attachment(0));
        g.texture(1, _myRandomTexture);
        _myNoiseFieldProgram.start();
        _myNoiseFieldProgram.uniform2f("gridSize", _myGrid.size);
		_myNoiseFieldProgram.uniform1i("velocity", 0);
		_myNoiseFieldProgram.uniform1i("randomTexture", 1);
		
		_myNoiseFieldProgram.uniform3f("offset", _myNoiseOffset);
		_myNoiseFieldProgram.uniform1f("scale", _myNoiseScale);

		_myNoiseFieldProgram.uniform1i("octaves", _cNoiseOctaves);
		_myNoiseFieldProgram.uniform1f("gain", _cNoiseGain);
		_myNoiseFieldProgram.uniform1f("lacunarity", _cNoiseLacunarity);

		_myNoiseFieldProgram.uniform1f("noiseAmount", _cNoiseAmount);
		
		_myNoiseFieldProgram.uniform1f("minX", _cNoiseBorder);
		_myNoiseFieldProgram.uniform1f("maxX", _cNoiseBorder + _cNoiseFade);

		_myNoiseFieldProgram.uniform1f("dissipation", _cNoiseDissipation);

        _myVelocityData.draw();
        _myNoiseFieldProgram.end();
        g.noTexture();
        _myVelocityData.swap();
	}
	
	public void step(CCGraphics g) {
		// we only want the quantity carried by the velocity field to be
		// affected by the dissipation

		noise(g);
		
		advect(g, _myVelocityData, 1);
		advect(g, _myDensityData, _myDissipation);
		vorticity(g);

		if (applyViscosity && viscosity > 0) {
			double s = _myGrid.scale;
			_myDiffuseProgram.alpha = (s * s) / (viscosity * _cStep);
			_myDiffuseProgram.beta = 4 + _myDiffuseProgram.alpha;
			_myDiffuseProgram.compute(g, _myVelocityData.attachment(0), _myVelocityData.attachment(0), _myVelocityData);
		}

		project(g);
	}
	
	private void splat(CCGraphics g, CCGLSwapBuffer theTarget, CCVector3 theInput, CCVector2 thePosition, double theRadius){
		g.texture(0,theTarget.attachment(0));
        _mySplatProgram.start();
        _mySplatProgram.uniform1i("read", 0);
        _mySplatProgram.uniform2f("gridSize", _myGrid.size);
        _mySplatProgram.uniform3f("color", theInput);
        _mySplatProgram.uniform2f("point", thePosition);
        _mySplatProgram.uniform1f("radius", theRadius);
//        theTarget.draw(thePosition.x - theRadius, thePosition.y - theRadius, thePosition.x + theRadius, thePosition.y + theRadius);
        theTarget.draw();
        _mySplatProgram.end();
        g.noTexture();
        theTarget.swap();
	}
	
	public void addColor(CCGraphics g, CCVector2 thePosition, CCColor theColor, double theRadius){
		CCVector2 point = new CCVector2();

		point.set(thePosition.x, _myWindowSize.y - thePosition.y);
		// normalize to [0, 1] and scale to grid size
		point.x *= _myGrid.size.x / _myWindowSize.x;
		point.y *= _myGrid.size.y / _myWindowSize.y;
	     
		splat(g, _myDensityData, new CCVector3(theColor.r,theColor.g,theColor.b), point, theRadius);
	}
	
	public void addColor(CCGraphics g, CCVector2 thePosition, CCColor theColor){
		addColor(g, thePosition, theColor, colorRadius);
	}
	
	public void addForce(CCGraphics g, CCVector2 thePosition, CCVector2 theForce, double theRadius){
		CCVector2 point = new CCVector2();

		point.set(thePosition.x, _myWindowSize.y - thePosition.y);
		// normalize to [0, 1] and scale to grid size
		point.x *= _myGrid.size.x / _myWindowSize.x;
		point.y *= _myGrid.size.y / _myWindowSize.y;
		
		splat(g, _myVelocityData, new CCVector3(theForce.x, theForce.y, 0), point, theRadius);	
	}
	
	public void addForce(CCGraphics g, CCVector2 thePosition, CCVector2 theForce){
		addForce(g, thePosition, theForce, velocityRadius);
	}

	public void addForces(CCGraphics g, CCMouseSimpleInfo mouse) {
		CCVector2 point = new CCVector2();
		CCVector3 force = new CCVector3();
	     
		CCVector2 motion = mouse.motion;

		point.set(mouse.position.x, _myWindowSize.y - mouse.position.y);
		// normalize to [0, 1] and scale to grid size
		point.x *= _myGrid.size.x / _myWindowSize.x;
		point.y *= _myGrid.size.y / _myWindowSize.y;

		force.set(motion.x, -motion.y, 0);
	     
		splat(g, _myVelocityData, force, point, colorRadius);	
		splat(g, _myDensityData, source, point, velocityRadius);
	}

	public void display(CCGraphics g, int theX, int theY, int theWidth, int theHeight) {
		CCFluidDisplay display = null;
		CCTexture2D read = null;
			 
		switch (_myFluidDrawMode) {
		case VELOCITY:
			display = _myVectorDisplayProgram;
			display.scaleNegative();
			read = _myVelocityData.attachment(0);
			break;
		case DENSITY:
			display = _myVectorDisplayProgram;
			display.scale.set(1,1,1);
			display.bias.set(0, 0, 0);
			read = _myDensityData.attachment(0);
			break;
		case DIVERGENCE:
			display = _myScalarDisplayProgram;
			display.scaleNegative();
			read = _myDivergenceData.attachment(0);
			break;
		case PRESSURE:
			display = _myScalarDisplayProgram;
			display.scaleNegative();
			read = _myPressureData.attachment(0);
			break;
		}
		display.display(g, read, theX, theY, theWidth, theHeight);
		
	}
	
	public void display(CCGraphics g){
		display(g,  -g.width()/2, -g.height()/2, g.width(), g.height());
	}

}

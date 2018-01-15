package cc.creativecomputing.simulation.fluid;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.events.CCMouseSimpleInfo;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShaderUtil;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
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
	public CCGLSwapBuffer _myTemperatureData;
	public CCGLSwapBuffer _myDivergenceData;
	private CCGLSwapBuffer _myVorticityData;
	public CCGLSwapBuffer _myPressureData;
	
	public CCShaderBuffer _myBoundBuffer;
	
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
	
	@CCProperty(name = "apply buoyancy")
	private boolean _cApplyBuoyancy = false;
	
	@CCProperty(name = "ambient temperature")
	private double _cAmbientTemperature = 0;
	@CCProperty(name = "buoyancy", min = 0, max = 1)
	private double _cBuoyancy = 1;
	@CCProperty(name = "buoyancy weight", min = 0, max = 1)
	private double _cBuoyancyWeight = 0.05;
	
	private CCGLProgram _myAdvectProgram;
	private CCFluidJacobi _myDiffuseProgram;
	private CCGLProgram _myDivergenceProgram;
	private CCFluidJacobi _myPoissonPressureEq;
	private CCGLProgram _myGradientProgram;
	private CCGLProgram _mySplatProgram;
	private CCGLProgram _myBuoyancyProgram;
	
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

	@CCProperty(name  = "color radius", min = 0, max = 1)
	private double colorRadius = 0.01;
	@CCProperty(name  = "velocity radius", min = 0, max = 1)
	private double velocityRadius = 0.01;
	@CCProperty(name  = "temperature radius", min = 0, max = 1)
	private double _cTemperatureRadius = 0.01;
	
	private CCGLProgram _myVorticityProgram;
	private CCGLProgram _myVorticityConfinementProgram;
	
	//0.9 very fast 0.998 slow 1 none
	@CCProperty(name = "color dissipation", min = 0.9, max = 1, digits = 4)
	private double _myColorDissipation = 0.998;
	@CCProperty(name = "velocity dissipation", min = 0.9, max = 1, digits = 4)
	private double _myVelocityDissipation = 0.998;
	@CCProperty(name = "temperature dissipation", min = 0.9, max = 1, digits = 4)
	private double _myTemperatureDissipation = 0.998;
	
	private enum CCFluidDrawMode{
		DENSITY,
		VELOCITY,
		DIVERGENCE,
		PRESSURE,
		BOUNDS,
		TEMPERATURE
	}
	
	@CCProperty(name = "fluid draw mode")
	private CCFluidDrawMode _myFluidDrawMode = CCFluidDrawMode.DENSITY;
	
	private CCFluidDisplay _myScalarDisplayProgram;
	private CCFluidDisplay _myVectorDisplayProgram;

	public CCFluidSolver(CCGraphics g, CCFluidGrid theGrid, CCVector2 theWindowSize) {
		_myGrid = theGrid;
		_myWindowSize = theWindowSize;

		// slabs
		int _myWidth = (int)_myGrid.size.x;
		int _myHeight = (int)_myGrid.size.y;
	        
		_myVelocityData = new CCGLSwapBuffer(g, _myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myDensityData = new CCGLSwapBuffer(g,_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myTemperatureData = new CCGLSwapBuffer(g,_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myDivergenceData = new CCGLSwapBuffer(g,_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myVorticityData = new CCGLSwapBuffer(g,_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myPressureData = new CCGLSwapBuffer(g,_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		
		_myBoundBuffer = new CCShaderBuffer(_myWidth, _myHeight, CCTextureTarget.TEXTURE_2D);
		_myBoundBuffer.clear(g);
		

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
		_myBuoyancyProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "buoyancy.fs"));
		
		_myRandomTexture = new CCTexture2D(CCGLShaderUtil.randomRGBAData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
		
		_myScalarDisplayProgram = new CCFluidDisplay(CCNIOUtil.classPath(this, "displayscalar.fs"));
		_myVectorDisplayProgram = new CCFluidDisplay(CCNIOUtil.classPath(this, "displayvector.fs"));
	}
	
	public CCShaderBuffer bounds(){
		return _myBoundBuffer;
	}
	
	private void advect(CCGraphics g, CCGLSwapBuffer theTarget, double theDissipation){
		g.texture(0,_myVelocityData.attachment(0));
        g.texture(1,theTarget.attachment(0));
        g.texture(2, _myBoundBuffer.attachment(0));
        _myAdvectProgram.start();
        _myAdvectProgram.uniform1i("velocity", 0);
        _myAdvectProgram.uniform1i("advected", 1);
        _myAdvectProgram.uniform1i("bounds", 2);
        _myAdvectProgram.uniform2f("gridSize", _myGrid.size);
        _myAdvectProgram.uniform1f("gridScale", _myGrid.scale);
        _myAdvectProgram.uniform1f("timestep", _cStep);
        _myAdvectProgram.uniform1f("dissipation", theDissipation);
        theTarget.draw(g);
        _myAdvectProgram.end();
        g.noTexture();
        
        theTarget.swap();
	}
	
	private void buoyancy(CCGraphics g){
		if(!_cApplyBuoyancy)return;
		
		g.texture(0, _myVelocityData.attachment(0));
		g.texture(1, _myTemperatureData.attachment(0));
		g.texture(2, _myDensityData.attachment(0));
		_myBuoyancyProgram.start();
		_myBuoyancyProgram.uniform1i("velocity", 0);
		_myBuoyancyProgram.uniform1i("temperature", 1);
		_myBuoyancyProgram.uniform1i("density", 2);
		_myBuoyancyProgram.uniform2f("gridSize", _myGrid.size);
		_myBuoyancyProgram.uniform1f("gridScale", _myGrid.scale);
		_myBuoyancyProgram.uniform1f("timestep", _cStep);

		_myBuoyancyProgram.uniform1f("ambientTemperature", _cAmbientTemperature);
		_myBuoyancyProgram.uniform1f("sigma", _cBuoyancy);
		_myBuoyancyProgram.uniform1f("kappa", _cBuoyancyWeight);
		_myVelocityData.draw(g);
		_myBuoyancyProgram.end();
		g.noTexture();
		
		_myVelocityData.swap();
	}
	
	private static double EPSILON = 2.4414e-4;
	
	private void vorticity(CCGraphics g){
		if(!_myApplyVorticity)return;
		
		g.texture(0,_myVelocityData.attachment(0));
		_myVorticityProgram.start();
		_myVorticityProgram.uniform1i("velocity", 0);
		_myVorticityProgram.uniform2f("gridSize", _myGrid.size);
		_myVorticityProgram.uniform1f("gridScale", _myGrid.scale);
        _myVorticityData.draw(g);
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
		_myVelocityData.draw(g);
        _myVorticityConfinementProgram.end();
        g.noTexture();
        _myVelocityData.swap();
	}
	
	private void divergence(CCGraphics g){
		g.texture(0, _myVelocityData.attachment(0));
        g.texture(1, _myBoundBuffer.attachment(0));
		_myDivergenceProgram.start();
		_myDivergenceProgram.uniform1i("velocity", 0);
        _myGradientProgram.uniform1i("bounds", 1);
		_myDivergenceProgram.uniform2f("gridSize", _myGrid.size);
		_myDivergenceProgram.uniform1f("gridScale", _myGrid.scale);
		_myDivergenceData.draw(g);
		_myDivergenceProgram.end();
		g.noTexture();

		_myDivergenceData.swap();
	}
	
	private void gradient(CCGraphics g){
		g.texture(0,_myPressureData.attachment(0));
        g.texture(1,_myVelocityData.attachment(0));
        g.texture(2, _myBoundBuffer.attachment(0));
        _myGradientProgram.start();
        _myGradientProgram.uniform1i("p", 0);
        _myGradientProgram.uniform1i("w", 1);
        _myGradientProgram.uniform1i("bounds", 2);
        _myGradientProgram.uniform2f("gridSize", _myGrid.size);
        _myGradientProgram.uniform1f("gridScale", _myGrid.scale);
        _myVelocityData.draw(g);
        _myGradientProgram.end();
        g.noTexture();
        _myVelocityData.swap();
	}
	
	// solve poisson equation and subtract pressure gradient
	private void project(CCGraphics g) {
		divergence(g);

		// 0 is our initial guess for the poisson equation solver
		_myPressureData.clear(g);

		_myPoissonPressureEq.alpha = -_myGrid.scale * _myGrid.scale;
		_myPoissonPressureEq.compute(
			g,
			_myPressureData.attachment(0),
			_myDivergenceData.attachment(0),
			_myPressureData
		);

		gradient(g);
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

        _myVelocityData.draw(g);
        _myNoiseFieldProgram.end();
        g.noTexture();
        _myVelocityData.swap();
	}
	
	public void clearBounds(CCGraphics g){
		_myBoundBuffer.beginDraw(g);
		g.clear();
		g.color(255,0,0);
		g.line(_myGrid.size.x - 0.5, 0.5, _myGrid.size.x - 0.5, _myGrid.size.y - 0.5);
		g.line(0.5, 0.5, _myGrid.size.x - 0.5, 0.5);
		g.line(0.5,	_myGrid.size.y - 0.5, _myGrid.size.x - 0.5,_myGrid.size.y - 0.5);
		g.line(0.5, 0.5, 0.5, _myGrid.size.y - 0.5);
		_myBoundBuffer.endDraw(g);
	}
	
	public void step(CCGraphics g) {
		// we only want the quantity carried by the velocity field to be
		// affected by the dissipation
		noise(g);
		
		advect(g, _myVelocityData, _myVelocityDissipation);
		advect(g, _myDensityData, _myColorDissipation);
		advect(g, _myTemperatureData, _myTemperatureDissipation);
		buoyancy(g);
		
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
        theTarget.draw(g);
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
	
	public void addTemperature(CCGraphics g, CCVector2 thePosition, double theTemperature, double theRadius){
		CCVector2 point = new CCVector2();

		point.set(thePosition.x, _myWindowSize.y - thePosition.y);
		// normalize to [0, 1] and scale to grid size
		point.x *= _myGrid.size.x / _myWindowSize.x;
		point.y *= _myGrid.size.y / _myWindowSize.y;
		
		splat(g, _myTemperatureData, new CCVector3(theTemperature, theTemperature, 0), point, theRadius);	
	}
	
	public void addTemperature(CCGraphics g, CCVector2 thePosition, double theTemperature){
		addTemperature(g, thePosition, theTemperature, _cTemperatureRadius);
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
			display.scale.set(-1,-1,-1);
			display.bias.set(1, 1, 1);
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
		case BOUNDS:
			display = _myVectorDisplayProgram;
			display.scale.set(1,1,1);
			display.bias.set(0, 0, 0);
			read = _myBoundBuffer.attachment(0);
			break;
		case TEMPERATURE:
			display = _myVectorDisplayProgram;
			display.scale.set(1,1,1);
			display.bias.set(0,0,0);
			read = _myTemperatureData.attachment(0);
			break;
		}
		display.display(g, read, theX, theY, theWidth, theHeight);
		
	}
	
	public void display(CCGraphics g){
		display(g,  -g.width()/2, -g.height()/2, g.width(), g.height());
	}

	public static void main(String[] args) {
		CCLog.info(CCNIOUtil.classPath(CCFluidSolver.class, "divergence.fs"));
	}
}

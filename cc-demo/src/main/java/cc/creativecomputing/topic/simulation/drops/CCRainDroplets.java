package cc.creativecomputing.topic.simulation.drops;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.interpolate.CCInterpolators;
import cc.creativecomputing.topic.simulation.drops.CCRainDrops.CCDrop;

public class CCRainDroplets {
	
	private int  dropletsCounter = 0;
	private int textureCleaningIterations = 0;

	@CCProperty(name = "limit", min = 0, max = 100)
	private double _cDropletsRate = 50;
	
	@CCProperty(name = "droplet cleaning radius", min = 0.0, max = 1)
	private double _cDropletsCleaningRadiusMultiplier = 0.4;

	@CCProperty(name = "min droplet size", min = 0.1, max = 10)
	private double _cMinDropletSize = 2;
	@CCProperty(name = "max droplet size", min = 0.1, max = 10)
	private double _cMaxDropletSize = 4;
	
	@CCProperty(name = "min droplet depth", min = 0, max = 1)
	private double _cMinDropletDepth = 0;
	@CCProperty(name = "max droplet depth", min = 0, max = 1)
	private double _cMaxDropletDepth = 1;

	private CCRainDropSimulation _mySimulation;
	
	private CCRainDrops _myDrops;

	private CCShaderBuffer _myBuffer;
	
	public CCRainDroplets(CCRainDropSimulation theSimulation) {
		_mySimulation = theSimulation;
		_myDrops = _mySimulation.drops();
		
		_myBuffer = new CCShaderBuffer(
			(int)_mySimulation.width(),
			(int) _mySimulation.height()
		);
	}
	
	public CCTexture2D texture() {
		return _myBuffer.attachment(0);
	}
	
	private double _myDeltaTime;
	
	public void update(double theDeltaTime) {
		if (textureCleaningIterations > 0) {
			textureCleaningIterations -= 1 * theDeltaTime;
		}
		_myDeltaTime = theDeltaTime;
		if (!_mySimulation.active())
			return;

		dropletsCounter += _cDropletsRate * theDeltaTime * _mySimulation.areaMultiplier();
	} 
	
	public void clear(){
		 textureCleaningIterations = 50;
	}
	
	private void clearDroplets(CCGraphics g) {
		g.color(0);
		for(CCDrop myDrop:_myDrops.drops) {
			if (myDrop.killed) continue;
			if(!myDrop.moved())continue;
			if(_cDropletsRate <= 0)continue;
			
			g.ellipse(
				myDrop.x, 
				myDrop.y, 
				myDrop.r * _cDropletsCleaningRadiusMultiplier
			);
		}
	}
	
	public void preDisplay(CCGraphics g) {
		if(!_mySimulation.active())return;

		_myBuffer.beginDraw(g);
		g.color(0,0,0,0.01d * _myDeltaTime);
		g.rect(0, 0, g.width(), g.height());

		clearDroplets(g);

		_myDrops.beginDisplay(g);
		
		for(int i = 0; i < dropletsCounter;i++) {
			dropletsCounter--;

			double myScaleRand = CCMath.random(0, 1, CCInterpolators.QUADRATIC_POW);
			_myDrops.drawRandomDrop(
				g, 
				CCMath.blend(_cMinDropletSize, _cMaxDropletSize, myScaleRand), 
				CCMath.blend(_cMinDropletDepth, _cMaxDropletDepth, myScaleRand)
			);
		}
		
		_myDrops.endDisplay(g);
		
		_myBuffer.endDraw(g);
	}
	
	public void display(CCGraphics g) {
		
	}
}

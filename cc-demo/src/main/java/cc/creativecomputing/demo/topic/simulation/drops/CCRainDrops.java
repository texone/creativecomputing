package cc.creativecomputing.demo.topic.simulation.drops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.interpolate.CCInterpolators;

public class CCRainDrops {
	
	@CCProperty(name = "min radius", min = 10, max = 100)
	private double _cMinRadius = 10;
	@CCProperty(name = "max radius", min = 10, max = 100)
	private double _cMaxRadius = 40;
	private double maxDrops = 900;
	private double rainChance = 0.3;
	private double rainLimit = 3;
	
	private double dropletsRate = 50;
	
	
	@CCProperty(name = "min droplet size", min = 0.1, max = 10)
	private double _cMinDropletSize = 2;
	@CCProperty(name = "max droplet size", min = 0.1, max = 10)
	private double _cMaxDropletSize = 4;
	
	@CCProperty(name = "droplet cleaning radius", min = 0.0, max = 1)
	private double _cDropletsCleaningRadiusMultiplier = 0.4;
	
	private double globalTimeScale = 1;
	private double trailRate = 1;
	private boolean autoShrink = true;
	private double[] spawnArea = { -0.1, 0.95 };
	private double[] trailScaleRange = {0.2,0.45 };
	private double collisionRadius = 0.45;
	private double collisionRadiusIncrease = 0.01;
	private double dropFallMultiplier = 1;
	private double collisionBoostMultiplier = 0.05;
	private double collisionBoost = 1;
	
	public class CCDrop {
		double x = 0;
		double y = 0;
		
		double r = 0;
		
		double momentum = 0;
		
		double spreadX = 0;
		double spreadY = 0;
		
		double momentumX = 0;
		double lastSpawn = 0;
		double nextSpawn = 0;
		CCDrop parent = null;
		boolean isNew = true;
		boolean killed = false;
		double shrink = 0;
		
		/*
		 *  x:random(this.width/this.scale),
		          y:random((this.height/this.scale)*this.spawnArea[0],(this.height/this.scale)*this.spawnArea[1]),
		          r:r,
		          momentum:1+((r-this.minR)*0.1)+random(2),
		          spreadX:1.5,
		          spreadY:1.5,
		 */
		public CCDrop() {
			
		}
		
		public CCDrop(double theX, double theY, double theR, double theMomentum, double theSpreadX, double theSpreadY) {
			x = theX;
			y = theY;
			
			r = theR;
			
			momentum = theMomentum;
			
			spreadX = theSpreadX;
			spreadY = theSpreadY;
		}
		
		public CCDrop(double theX, double theY, double theR, double theSpreadY, CCDrop theParent ) {
			x = theX;
			y = theY;
			r = theR;
			spreadY = theSpreadY;
			parent = theParent;
		}
		
		public CCDrop(double theX, double theY, double theR) {
			x = theX;
			y = theY;
			r = theR;
		}
		
		public boolean moved() {
			return momentum > 0;
		}
		
		private void updateTrails(double theDeltaTime, List<CCDrop> newDrops) {
			if (!_cRaining) return;
			
			lastSpawn += momentum * theDeltaTime * trailRate;
			
			if (lastSpawn <= nextSpawn) return;

			if (!belowDropLimit()) return;
			
			newDrops.add(
				new CCDrop(
					x + (CCMath.random(-r, r) * 0.1), // x
					y - (r * 0.01), // y:
					r * CCMath.random(trailScaleRange[0], trailScaleRange[1]), // r:
					momentum * 0.1, // spreadY:
					this // parent:
				)
			);

			r *= CCMath.pow(0.97, theDeltaTime);
			lastSpawn = 0;
			nextSpawn = CCMath.random(_cMinRadius, _cMaxRadius) - (momentum * 2 * trailRate) + (_cMaxRadius - r);
		}
		
		private void checkCollision(double theDeltaTime, int theStartIndex) {
			boolean checkCollision = (moved() || isNew) && !killed;
			isNew = false;

			if (!checkCollision) return;
			
			for(int j = theStartIndex + 1; j < drops.size();j++) {
				CCDrop drop2 = drops.get(j);
				// basic check
				if (this != drop2 && r > drop2.r && parent != drop2 && drop2.parent != this && !drop2.killed) {
					double dx = drop2.x - x;
					double dy = drop2.y - y;
					double d = Math.sqrt((dx * dx) + (dy * dy));
						// if it's within acceptable distance
					if (d < (r + drop2.r) * (collisionRadius + (momentum * collisionRadiusIncrease * theDeltaTime))) {
						
						double r1 = r;
						double r2 = drop2.r;
						double a1 = CCMath.PI * (r1 * r1);
						double a2 = CCMath.PI * (r2 * r2);
						double targetR = CCMath.sqrt((a1 + (a2 * 0.8)) / CCMath.PI);
						if (targetR > _cMaxRadius) {
							targetR = _cMaxRadius;
						}
						r = targetR;
						momentumX += dx * 0.1;
						spreadX = 0;
						spreadY = 0;
						drop2.killed = true;
						momentum = Math.max(drop2.momentum, Math.min(40, momentum + (targetR * collisionBoostMultiplier) + collisionBoost));
					}
				}
			}
		}
		
		private void draw(CCGraphics g) {
			double x0 = x - (r * scaleX * (spreadX + 1));
			double y0 = y - (r * scaleY * (spreadY + 1));
			double w = r * 2 * scaleX * (spreadX + 1);
			double h = r * 2 * scaleY * (spreadY + 1);

			double d = CCMath.saturate(((r - _cMinRadius) / deltaR()) * 0.9);
			d *= 1 / (((spreadX + spreadY) * 0.5) + 1);
			
			g.textureCoords4D(1, d, 0, 0, 0);
			g.textureCoords2D(0, 0);
			g.vertex(x0, y0);
			g.textureCoords2D(1, 0);
			g.vertex(x0 + w, y0);
			g.textureCoords2D(1, 1);
			g.vertex(x0 + w, y0 + h);
			g.textureCoords2D(0, 1);
			g.vertex(x0, y0 + h);
		}
	}
	
	@CCProperty(name = "raining")
	private boolean _cRaining = true;
	
	public CCRainOptions options;

	public double _myWidth;
	public double _myHeight;

	double scaleX=1;
	double scaleY=1.5;
	public double _myScale;
	
	public CCTexture2D _myDropAlphaTexture;
	public CCTexture2D _myDropColorTexture;
	
	@CCProperty(name = "shader")
	private CCGLProgram _cDropShader;
	
	@CCProperty(name = "rain shader")
	private CCGLProgram _cRainShader;

	public List<CCDrop> drops;

	private int textureCleaningIterations = 0;
	
	private CCShaderBuffer _myDropletContext;
	private CCShaderBuffer _myRainContext;
	
	private CCTexture2D imageFg;
	private CCTexture2D imageBg;

	public CCRainDrops(
		double theWidth, 
		double theHeight, 
		double theScale, 
		CCRainOptions theOptions
	) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		_myScale = theScale;
		_myDropAlphaTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "drop-alpha.png")));
		_myDropColorTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "drop-color.png")));

		options = theOptions;
		drops = new ArrayList<>();
		
		_myDropletContext = new CCShaderBuffer((int)_myWidth,(int) _myHeight);
		_myRainContext = new CCShaderBuffer((int)_myWidth,(int) _myHeight, CCTextureTarget.TEXTURE_2D);
		
		_cDropShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "drop-vertex.glsl"), 
			CCNIOUtil.classPath(this, "drop-fragment.glsl")
		);
		_cRainShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "rain-vertex.glsl"),
			CCNIOUtil.classPath(this, "rain-fragment.glsl")
		);
		
		imageFg = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "weather/texture-rain-fg.png")));
		imageBg = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "weather/texture-rain-bg.png")));
	}
	
	public CCRainDrops(
		double theWidth, 
		double theHeight, 
		double theScale
	) {
		this(theWidth, theHeight, theScale, new CCRainOptions());
	}
	
	public CCRainDrops(
		double theWidth, 
		double theHeight
	) {
		this(theWidth, theHeight, 1);
	}

	private double deltaR() {
		return _cMaxRadius - _cMinRadius;
	}

	private double area() {
		return _myWidth * _myHeight / _myScale;
	}

	private double areaMultiplier() {
		return CCMath.sqrt(area() / (1024 * 768));
	}

	private boolean belowDropLimit() {
		return (drops.size() < maxDrops * areaMultiplier());
	}
	
	public List<CCDrop> updateRain(double theDeltaTime) {
		List<CCDrop> myResult = new ArrayList<>();
		if (!_cRaining)
			return myResult;
		
		double limit = rainLimit * theDeltaTime * areaMultiplier();
		int count = 0;

		while (CCMath.chance(rainChance * theDeltaTime * areaMultiplier()) && count < limit) {
			count++;
			double r = CCMath.random(_cMinRadius, _cMaxRadius, CCInterpolators.CUBIC_POW);

			if (!belowDropLimit()) continue;
			
			myResult.add(
				new CCDrop(
					CCMath.random(_myWidth / _myScale), // x
					CCMath.random((_myHeight / _myScale) * spawnArea[0], (_myHeight / _myScale) * spawnArea[1]), // y
					r, 
					1 + ((r - _cMinRadius) * 0.1) + CCMath.random(2),
					1.5, // spreadX:
					1.5 // spreadY));
				)
			);
		}

		return myResult;
	}
	
	 private void clearTexture(){
		 textureCleaningIterations = 50;
	 }
	
	public void clearDrops(){
		for(CCDrop drop:drops) {
			drop.shrink = 0.1 + CCMath.random(0.5);
		}
		clearTexture();
//		    drops.forEach((drop)=>{
//		      setTimeout(()=>{
//		        drop.shrink=0.1+(random(0.5));
//		      },random(1200))
//		    })
		  
	}
	

	private int  dropletsCounter = 0;
	
	public void updateDroplets(double theDeltaTime) {
		if (textureCleaningIterations > 0) {
			textureCleaningIterations -= 1 * theDeltaTime;
			// dropletsCtx.globalCompositeOperation="destination-out";
			// dropletsCtx.fillStyle="rgba(0,0,0,"+(0.05*timeScale)+")";
			// dropletsCtx.fillRect(0,0,width *
			// dropletsPixelDensity,height*dropletsPixelDensity);
		}
		if (!_cRaining)
			return;

		dropletsCounter += dropletsRate * theDeltaTime * areaMultiplier();
	}
	
	public void updateDrops(double theDeltaTime) {

		List<CCDrop> newDrops = updateRain(theDeltaTime);

		Collections.sort(drops, (a, b) -> 
			 Double.compare(
				(a.y * (_myWidth / _myScale)) + a.x, 
				(b.y * (_myWidth / _myScale)) + b.x
			)
		);
		
		for (int i = 0; i < drops.size();i++) {
			CCDrop drop = drops.get(i);
			if (drop.killed) continue;
			
			// update gravity
			// (chance of drops "creeping down")
			if (CCMath.chance((drop.r - (_cMinRadius * dropFallMultiplier)) * (0.1 / deltaR()) * theDeltaTime)) {
				drop.momentum += CCMath.random((drop.r / _cMaxRadius) * 4);
			}
			// clean small drops
			if (autoShrink && drop.r <= _cMinRadius && CCMath.chance(0.05 * theDeltaTime)) {
				drop.shrink += 0.01;
			}
			// update shrinkage
			drop.r -= drop.shrink * theDeltaTime;
			if (drop.r <= 0)
				drop.killed = true;

			// update trails
			drop.updateTrails(theDeltaTime, newDrops);
			
			// normalize spread
			drop.spreadX *= Math.pow(0.4, theDeltaTime);
			drop.spreadY *= Math.pow(0.7, theDeltaTime);

			// update position
			if (drop.moved() && !drop.killed) {
				drop.y += drop.momentum * globalTimeScale;
				drop.x += drop.momentumX * globalTimeScale;
				if (drop.y > (_myHeight / _myScale) + drop.r) {
					drop.killed = true;
				}
			}

			// collision
			drop.checkCollision(theDeltaTime, i);

			// slowdown momentum
			drop.momentum -= Math.max(1, (_cMinRadius * 0.5) - drop.momentum) * 0.1 * theDeltaTime;
			if (drop.momentum < 0)
				drop.momentum = 0;
			drop.momentumX *= Math.pow(0.7, theDeltaTime);

			if (!drop.killed) {
				newDrops.add(drop);
			}
		}

		drops = newDrops;
	}
	
	private double timeScale = 0;
	
	public void update(CCAnimator theAnimator){
		timeScale = theAnimator.deltaTime() * 30;//theAnimator.deltaTime();
		timeScale *= globalTimeScale;

		updateDroplets(timeScale);
		updateDrops(timeScale);
	}
	
	private void clearDroplets(CCGraphics g) {
		g.color(0);
		for(CCDrop myDrop:drops) {
			if (myDrop.killed) continue;
			if(!myDrop.moved())continue;
			if(dropletsRate <= 0)continue;
			
			g.ellipse(
				myDrop.x, 
				myDrop.y, 
				myDrop.r * _cDropletsCleaningRadiusMultiplier
			);
		}
	}
	
	public void drawDrops(CCGraphics g) {
		g.color(1d);
		
		g.texture(0, _myDropColorTexture);
		g.texture(1, _myDropAlphaTexture);
		_cDropShader.start();
		_cDropShader.uniform1i("color", 0);
		_cDropShader.uniform1i("alpha", 1);
		
		g.pushMatrix();
		g.scale(_myScale);
		g.beginShape(CCDrawMode.QUADS);
		for(CCDrop myDrop:drops) {
			myDrop.draw(g);
		}
		g.endShape();
		g.popMatrix();
		
		_cDropShader.end();
		g.noTexture();
	}
	
	private void drawDroplets(CCGraphics g) {
		if(!_cRaining)return;

		_myDropletContext.beginDraw(g);
		g.color(0,0,0,0.01d * timeScale);
		g.rect(0, 0, g.width(), g.height());

		clearDroplets(g);

		g.color(1d);
		g.pushMatrix();
		
		g.texture(0, _myDropColorTexture);
		g.texture(1, _myDropAlphaTexture);
		_cDropShader.start();
		_cDropShader.uniform1i("color", 0);
		_cDropShader.uniform1i("alpha", 1);
		
		g.scale(_myScale);
		g.beginShape(CCDrawMode.QUADS);
		for(int i = 0; i < dropletsCounter;i++) {
			dropletsCounter--;
			new CCDrop(
				CCMath.random(_myWidth/_myScale),
				CCMath.random(_myHeight/_myScale),
				CCMath.random(_cMinDropletSize, _cMaxDropletSize, CCInterpolators.QUADRATIC_POW)
			).draw(g);
		}
		g.endShape();
		
		_cDropShader.end();
		g.noTexture();
		
		g.popMatrix();
		_myDropletContext.endDraw(g);
	}
	
	public void preDisplay(CCGraphics g) {
		drawDroplets(g);
		_myRainContext.beginDraw(g);
		g.clear();
		g.pushMatrix();
		g.ortho();
		g.image(_myDropletContext.attachment(0), 0, 0);
		drawDrops(g);
		g.popMatrix();
		_myRainContext.endDraw(g);
	}
	
	private boolean renderShadow = false;
	private double minRefraction = 256;
	private double maxRefraction = 512;
	private double brightness = 1.1;
	private double alphaMultiply = 6;
	private double alphaSubtract = 3;
	private double parallaxBg = 5;
	private double parallaxFg = 40;
	

	private double parallaxX = 1;
	private double parallaxY = 1;
	
	private void displayRain(CCGraphics g) {
		g.texture(0, _myRainContext.attachment(0));
		g.texture(1, imageFg);
		g.texture(2, imageBg);
		
		_cRainShader.start();
		_cRainShader.uniform2f("resolution", _myWidth, _myHeight);
		_cRainShader.uniform1f("textureRatio",(double)imageBg.width()/imageBg.height());
		_cRainShader.uniform1i("waterMap", 0);
		_cRainShader.uniform1i("textureFg", 1);
		_cRainShader.uniform1i("textureBg", 2);
		//_cShader.uniform1i("renderShine",this.imageShine==null?false:true);
		_cRainShader.uniform1i("renderShadow",renderShadow ? 1 : 0);
		_cRainShader.uniform1f("minRefraction",minRefraction);
		_cRainShader.uniform1f("refractionDelta",maxRefraction-minRefraction);
		_cRainShader.uniform1f("brightness",brightness);
		_cRainShader.uniform1f("alphaMultiply",alphaMultiply);
		_cRainShader.uniform1f("alphaSubtract",alphaSubtract);
		_cRainShader.uniform1f("parallaxBg",parallaxBg);
		_cRainShader.uniform1f("parallaxFg",parallaxFg);
		_cRainShader.uniform2f("parallax", parallaxX,parallaxY);
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
	}

	public void draw(CCGraphics g) {
		preDisplay(g);
		g.pushMatrix();
		g.ortho2D();
//		g.image(_myRainContext.attachment(0), 0, 0);
		
		displayRain(g);
		
		
		g.popMatrix();
		
		
	}
}

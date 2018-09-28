package cc.creativecomputing.demo.topic.simulation.drops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.interpolate.CCInterpolators;

public class CCRainDrops {
	
	@CCProperty(name = "min radius", min = 10, max = 100)
	private double _cMinRadius = 10;
	@CCProperty(name = "max radius", min = 10, max = 100)
	private double _cMaxRadius = 40;
	
	private double maxDrops = 9000;
	
	@CCProperty(name = "chance", min = 0, max = 0.5)
	private double _cChance = 0.3;
	@CCProperty(name = "limit", min = 0, max = 10)
	private double _cLimit = 3;

	@CCProperty(name = "trail rate", min = 0, max = 10)
	private double trailRate = 1;
	
	@CCProperty(name = "limit", min = 0, max = 1)
	private double _cTrailScaleMin = 0.2;
	@CCProperty(name = "limit", min = 0, max = 1)
	private double _cTrailScaleMax = 0.45;
	
	private double[] spawnArea = { -0.1, 0.95 };
	
	private double dropFallMultiplier = 1;
	
	private double collisionRadius = 0.45;
	private double collisionRadiusIncrease = 0.0002;
	private double collisionBoostMultiplier = 0.05;
	private double collisionBoost = 1;
	

	double scaleX=1;
	double scaleY=1.5;
	

	private boolean autoShrink = true;
	
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
			if (!_mySimulation.active()) return;
			
			lastSpawn += momentum * theDeltaTime * trailRate;
			
			if (lastSpawn <= nextSpawn) return;

			if (!belowDropLimit()) return;
			
			newDrops.add(
				new CCDrop(
					x + (CCMath.random(-r, r) * 0.1), // x
					y - (r * 0.01), // y:
					r * CCMath.random(_cTrailScaleMin, _cTrailScaleMax), // r:
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
	
	public List<CCDrop> drops= new ArrayList<>();
	
	private final CCRainDropSimulation _mySimulation;

	@CCProperty(name = "shader")
	private CCGLProgram _cDropShader;
	
	public CCTexture2D _myDropAlphaTexture;
	public CCTexture2D _myDropColorTexture;
	
	private double _myWidth;
	private double _myHeight;
	private double _myScale;
	
	public CCRainDrops(CCRainDropSimulation theSimulation) {
		_mySimulation = theSimulation;
		_myWidth = _mySimulation.width();
		_myHeight = _mySimulation.height();
		_myScale = _mySimulation.scale();
		
		_cDropShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "drop-vertex.glsl"), 
			CCNIOUtil.classPath(this, "drop-fragment.glsl")
		);

		_myDropAlphaTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "drop-alpha.png")));
		_myDropColorTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "drop-color.png")));
	}

	public void clear() {
		for(CCDrop drop:drops) {
			drop.shrink = 0.1 + CCMath.random(0.5);
		}
	}
	
	private boolean belowDropLimit() {
		return (drops.size() < maxDrops * _mySimulation.areaMultiplier());
	}
	
	public List<CCDrop> updateRain(double theDeltaTime) {
		List<CCDrop> myResult = new ArrayList<>();
		if (!_mySimulation.active())
			return myResult;
		
		double limit = _cLimit * theDeltaTime * _mySimulation.areaMultiplier();
		int count = 0;

		while (CCMath.chance(_cChance * theDeltaTime * _mySimulation.areaMultiplier()) && count < limit) {
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
	
	private double deltaR() {
		return _cMaxRadius - _cMinRadius;
	}
	
	public void update(double theDeltaTime) {

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
				drop.y += drop.momentum * _mySimulation.globalTimeScale();
				drop.x += drop.momentumX * _mySimulation.globalTimeScale();
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
	
	public void beginDisplay(CCGraphics g) {

		g.pushMatrix();
		g.scale(_myScale);
		g.color(1d);
		
		g.texture(0, _myDropColorTexture);
		g.texture(1, _myDropAlphaTexture);
		_cDropShader.start();
		_cDropShader.uniform1i("color", 0);
		_cDropShader.uniform1i("alpha", 1);
	}
	
	public void endDisplay(CCGraphics g) {
		_cDropShader.end();
		g.noTexture();
		g.popMatrix();
	}
	
	public void drawRandomDrop(CCGraphics g, double theSize, double theDepth) {
		_cDropShader.uniform1f("depthAdd", theDepth);
		g.beginShape(CCDrawMode.QUADS);
		new CCDrop(
			CCMath.random(_myWidth/_myScale),
			CCMath.random(_myHeight/_myScale),
			theSize
		).draw(g);
		g.endShape();
		_cDropShader.uniform1f("depthAdd", 0);
	}
	
	public void display(CCGraphics g) {
		beginDisplay(g);
		
		g.beginShape(CCDrawMode.QUADS);
		for(CCDrop myDrop:drops) {
			myDrop.draw(g);
		}
		g.endShape();
		
		endDisplay(g);
	}
}

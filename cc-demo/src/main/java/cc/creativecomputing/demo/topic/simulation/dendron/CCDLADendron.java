package cc.creativecomputing.demo.topic.simulation.dendron;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

/**
 * public class dendron extends PApplet {// Dendron by Golan Levin
 * http://www.flong.com/projects/dendron
 * Java 1.1 version, October 2001
 * Processing-135 version, January 2008
 * @author christianr
 *
 */
public class CCDLADendron extends CCGL2Adapter {
	
	/*
	 * Here we introduce an inner class called Particle. The Dendron applet will
	 * use an array of several tens of thousands of these Particles to simulate
	 * Diffusion-Limited Aggregation. Each Particle is defined by a
	 * two-dimensional position and velocity, and offers a method by which the
	 * particle can be reset() or updated.
	 */

	private class Particle {
		final double A = 0.9f;
		final double B = 1.0f - A;

		double px, py; // Particle position
		double vx, vy; // Particle velocity
		double dx, dy; // Handy offsets
		int used;
		boolean active;

		public Particle() {
			reset();
			reset2();
		}

		public void reset() {
			// Reset the Particle to a new random location
			// and a zeroed-out velocity.
			px = Math.random() * (double) rasterWidth;
			py = Math.random() * (double) rasterHeight;
			vx = 0;
			vy = 0;
		}

		public void reset2() {
			active = true;
			used = 0;
		}

		public void update() {
			// This method advances a Particle to its next
			// location, based on its current velocity, and
			// also evolves the Particle's velocity somewhat
			// in order to produce a smoothed-out drunk walk.

			// Coarsely integrate the current velocity
			// in order to update the Particle's position.
			px += vx;
			py += vy;

			// Do a bounds check to make sure that the Particle
			// stays within the bounds of the raster area.
			if (px > rasterWidth) {
				px -= rasterWidth;
			} else if (px < 0) {
				px += rasterWidth;
			}
			if (py > rasterHeight) {
				py -= rasterHeight;
			} else if (py < 0) {
				py += rasterHeight;
			}

			// Compute a random deflection for the Particle,
			// and modify its velocity slightly in that direction.
			dx = (Math.random() - 0.5f) * maxParticleVelocity;
			dy = (Math.random() - 0.5f) * maxParticleVelocity;
			vx = A * vx + B * dx;
			vy = A * vy + B * dy;

		}

	}
	
	// ---------------------------------------------------------------------------
	CCColor myPalette[];
	int rasterWidth; // The width of the applet's pixel field.
	int rasterHeight; // The height of the applet's pixel field.
	byte rasterPixels[]; // The array of bytes containing the pixels.
	int numberOfRasterPixels; // The size (length) of this byte array.
	final int UNSIGN = 0xFF;
	
	// ----------------------------------------------------------------------------
	/*
	 * Declare the variables and objects we'll be using in the Diffusion-Limited
	 * Aggregation (DLA) simulation. Our simulation will consist of a set of
	 * Particles which move around the raster
	 */

	int numberOfParticles;
	Particle particleArray[];
	double aggregationThreshold = 10;
	double maxParticleVelocity = 20;
	byte DRAW_BYTE = (byte) 129;// 97;
	byte ACCRETE_BYTE = (byte) 49;// 65;
	int blurFrequency = 4;

	CCFont<?> F;
	
	/**
	 * Create the palette we'll use to display the pixels.
	 * This 256-color palette blends between the background and foreground
	 * colors that were specified in the HTML parameter tags. We assume that
	 * readAppletParameters() has already been called, which reads these
	 * parameters into the myBackgroundColor and myForegroundColor objects.
	 * <p>
	 * Fetch the individual color components
	 */
		private void createCustomPalette() {
			// 
			// 
			// 
			// 
			// 

			// 
			CCColor myBackgroundColor = new CCColor(50, 30, 16);
			CCColor myForegroundColor = CCColor.parseFromInteger(0xffB0FFD0);

			

			// Fill the red, green and blue arrays with values that
			// blend from myBackgroundColor to myForegroundColor.

			for (int i = 0; i < 256; i++) {

				double f = 0.10f + CCMath.pow(i / 255.0f, 0.90f);
				if (i == 0) {
					f = 0.0f;
				}

				// Use non-linear color scaling to get a more interesting palette
				double percent = (double) (i / 255.0f);
				double rPercent = CCMath.pow(percent, 0.50f) + f;
				double gPercent = CCMath.pow(percent, 0.40f) + f;
				double bPercent = CCMath.pow(percent, 0.38f) + f;
				rPercent = CCMath.min(1, rPercent);
				gPercent = CCMath.min(1, gPercent);
				bPercent = CCMath.min(1, bPercent);

				// Create the actual color component values
				int r = (int) (myBackgroundColor.r + rPercent * (myForegroundColor.r - myBackgroundColor.r));
				int g = (int) (myBackgroundColor.g + gPercent * (myForegroundColor.g - myBackgroundColor.g));
				int b = (int) (myBackgroundColor.b + bPercent * (myForegroundColor.b - myBackgroundColor.b));

				myPalette[i] = new CCColor(r, g, b);
			}
		}
		
		/**
		 * Initialize the objects and properties of the DLA simulation,
		 * namely, a very large array of (invisible) particles.
		 * <p>
		 *  I determined empirically that the applet works well when
		 *  there are approximately 0.1 Particles per pixel. Larger applets
		 *  will have simulations which are correspondingly slower to compute.
		 */
		private void initializeSimulation() {
			final double particlesPerPixel = 0.1f;
			numberOfParticles = (int) (numberOfRasterPixels * particlesPerPixel);

			// Construct the array containing the Particles.
			particleArray = new Particle[numberOfParticles];
			for (int i = 0; i < numberOfParticles; i++) {
				particleArray[i] = new Particle();
			}
		}

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		F = CCFontIO.createTextureMapFont("arial", 24);

		rasterWidth = g.width();
		rasterHeight = g.height();
		numberOfRasterPixels = rasterWidth * rasterHeight;
		rasterPixels = new byte[numberOfRasterPixels];
		for (int i = 0; i < numberOfRasterPixels; i++) {
			rasterPixels[i] = (byte) (0);
		}

		myPalette = new CCColor[256];
		createCustomPalette();
		initializeSimulation();
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	/**
	 * This method reassigns the brightness of a pixel to be a weighted
	 * average of itself with the pixels in the 3x3 neighborhood around it.
	 * If its neighbors are dark, it will become darker; if its neighbors
	 * are lighter, it will become lighter.
	 */
		public void blur2(int locC) {
			

			// First, we'll do a check to make sure that we can blur this pixel.
			// We'll hit problems if the pixel's upper or lower neighbors lie
			// outside the bounds of the rasterPixels array.
			if ((locC >= (rasterWidth + 1)) && (locC < (numberOfRasterPixels - rasterWidth - 1))) {

				// We compute the array indices of the neighbors in our 3x3
				// region, numbering them from 1 through 9 as follows:
				// 1 2 3
				// 4 5 6
				// 7 8 9
				int loc1 = locC - rasterWidth - 1; // North West
				int loc2 = locC - rasterWidth; // North
				int loc3 = locC - rasterWidth + 1; // North East
				int loc4 = locC - 1; // West
				int loc5 = locC; // Center
				int loc6 = locC + 1; // East
				int loc7 = locC + rasterWidth - 1; // South West
				int loc8 = locC + rasterWidth; // South
				int loc9 = locC + rasterWidth + 1; // South East

				// Now we fetch the values of the nine pixels out of the
				// rasterPixels byte array. We need to do some unaesthetic
				// bitwise math with the UNSIGN constant in order to extract
				// the bytes as unsigned integers (from 0...255). Otherwise Java
				// will (by default) cast bytes to signed integers (-128...127).
				int val1 = UNSIGN & rasterPixels[loc1];
				int val2 = UNSIGN & rasterPixels[loc2];
				int val3 = UNSIGN & rasterPixels[loc3];
				int val4 = UNSIGN & rasterPixels[loc4];
				int val5 = UNSIGN & rasterPixels[loc5];
				int val6 = UNSIGN & rasterPixels[loc6];
				int val7 = UNSIGN & rasterPixels[loc7];
				int val8 = UNSIGN & rasterPixels[loc8];
				int val9 = UNSIGN & rasterPixels[loc9];

				// Now we use a 3x3 Gaussian blurring convolution kernel
				// to determine the weighted average of the pixels. This kernel has
				// the following multiplicative weights assigned to each location:
				// 1 2 1
				// 2 4 2
				// 1 2 1

				int sum = (1 * val1 + 2 * val2 + 1 * val3 + 2 * val4 + 4 * val5 + 2 * val6 + 1 * val7 + 2 * val8
						+ 1 * val9);

				int weightedAverage = sum / 16;
				rasterPixels[locC] = (byte) (weightedAverage);
			}
		}

		/**
		 * This method blurs the 3x3 neighborhood of pixels centered around the
		 * pixel whose index in the rasterArray is locC. In order to improve
		 * speed, this method was written without the security checks which
		 * would ensure that the locC index -- and the indices of its neighbors
		 * -- were within the bounds of the rasterArray. Calling this method on
		 * a location which is too close to the edge of the rasterArray may
		 * cause an ArrayIndexOutOfBoundsException.
		 * 
		 * We blur the pixels of the 3x3 region in a certain order -- from the
		 * center towards the edges -- in order to minimize any asymmetric
		 * effects.
		 */
		private void blurNeighborhood(int locC) {
			

			// Define the indices of the North (upper)
			// and South (lower) neighboring pixels.
			int locN = locC - rasterWidth;
			int locS = locC + rasterWidth;

			// Finally, blur the corner pixels: NW, NE, SW, SE.
			blur2(locN - 1);
			blur2(locN + 1);
			blur2(locS - 1);
			blur2(locS + 1);

			// Now blur the north, south, west and east pixels
			blur2(locN);
			blur2(locS);
			blur2(locC - 1);
			blur2(locC + 1);

			// Blur the center pixel first
			blur2(locC);
		}
		
		
		// It's good to include some functions which can quickly restore our applet
		// to its original (startup) condition.

		private void clearRaster() {
			for (int i = 0; i < numberOfRasterPixels; i++) {
				rasterPixels[i] = (byte) 0;
			}
		}

		private void resetParticles() {
			for (int i = 0; i < numberOfParticles; i++) {
				particleArray[i].reset();
				particleArray[i].reset2();
			}
		}
	
	/**
	 * diffusion-limited aggregation:
	 * have a whole bunch of particles which start some distance away
	 * each does a drunk walk. if it encounters a part of the structure,
	 * it sticks to it, adding on to the structure.
	 * <p>
	 * slow the growth over time by raising the aggregation threshold.
	 */
		private void computeSimulation() {
		
			aggregationThreshold += 0.11f;

			Particle P;
			int sum;
			int x, y, loc;
			int locc, locn, locs;

			final int minX = 1;
			final int minY = 1;
			final int maxX = rasterWidth - 2;
			final int maxY = rasterHeight - 2;

			int count = 0;
			for (int i = 0; i < numberOfParticles; i++) {
				(P = particleArray[i]).update();

				x = (int) P.px;
				y = (int) P.py;

				if ((x > minX) && (x < maxX) && (y > minY) && (y < maxY)) {

					locn = (locc = (loc = y * rasterWidth + x) - 1) - rasterWidth;
					locs = locc + rasterWidth;

					sum = (UNSIGN & rasterPixels[locn++]) + (UNSIGN & rasterPixels[locn++]) + (UNSIGN & rasterPixels[locn])
							+ (UNSIGN & rasterPixels[locc++]) + (UNSIGN & rasterPixels[locc++])
							+ (UNSIGN & rasterPixels[locc]) + (UNSIGN & rasterPixels[locs++])
							+ (UNSIGN & rasterPixels[locs++]) + (UNSIGN & rasterPixels[locs]);

					if (sum >= aggregationThreshold) {
						rasterPixels[loc] = (byte) Math.min(255, (UNSIGN & rasterPixels[loc]) + ACCRETE_BYTE);

						if ((count++) % blurFrequency == 0) {
							if (CCMath.random(1) < 0.5f) {
								blurNeighborhood(loc + rasterWidth);
							} else {
								blurNeighborhood(loc + 1);
							}
							blurNeighborhood(loc);

						} else {
							blur2(loc);
						}

						P.reset();

					}
				}
			}

			if (((double) count / (double) numberOfParticles) > 0.936f) {
				clearRaster();
				resetParticles();
			}
		}
		
		boolean clicked = false;
		long clickedMillis = 0;

	private void drawText(CCGraphics g) {

		double alp = 0.0f;
		if (clicked == false) {
			alp = 255.0f;
		}
		if ((clicked == true) && (animator().time() < 1.5)) {
			double frac = animator().time() / 1.5;
			frac = CCMath.pow(frac, 0.5f);
			frac = CCMath.min(1.0f, frac);
			alp = 255.0f * (1.0f - frac);
		}

		if (alp > 0) {
			g.color(176, 245, 218, alp);
			g.textAlign(CCTextAlign.CENTER);
			g.textFont(F);
			g.text("dendron by golan levin (2001, 2008)\nhttp://www.flong.com\nclick, drag, wait\n", g.width() * 0.5f,
					g.height() * 0.4f);
		}
	}
	
	@Override
	public void display(CCGraphics g) {
		computeSimulation();
		g.loadPixels();

		int row;
		int index = 0;
		int c;
		for (int y = 0; y < rasterHeight; y++) {
			row = y * rasterWidth;
			for (int x = 0; x < rasterWidth; x++) {
				index = row + x;
//				g.pixels[index] = myPalette[UNSIGN & rasterPixels[index]];
			}
		}
		g.updatePixels();

		drawText(g);
	}

	public static void main(String[] args) {

		CCDLADendron demo = new CCDLADendron();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}



//
//	
//
//	
//
//	
//
//	// --------------------------------------------------------------------------
//	public void draw() {
//		
//
//	}
//
//	
//
//	
//
//	// --------------------------------------------------------------------------
//	public void mousePressed() {
//		aggregationThreshold = 1;
//		createLine(mouseX - 1, mouseY, mouseX, mouseY);
//		if (clicked == false) {
//			clickedMillis = millis();
//		}
//		clicked = true;
//	}
//
//	// --------------------------------------------------------------------------
//	public void mouseDragged() {
//		createLine(pmouseX, pmouseY, mouseX, mouseY);
//	}
//
//	
//
//	
//
//	public void keyPressed() {
//		clearRaster();
//		resetParticles();
//		clicked = false;
//	}
//
//	
//
//	
//
//	
//
//	// --------------------------------------------------------------------------
//	public void createLine(int x0, int y0, int x1, int y1) {
//		// This method takes the x- and y-coordinates of two points,
//		// and draws a line between them (in the raster of pixels)
//		// by depositing the DRAW_BYTE value into the rasterPixels array.
//		// This is a simple method, but not very efficient at all; for a
//		// faster technique, please investigate Bresenham's method.
//
//		x0 = constrain(x0, 0, rasterWidth - 1);
//		y0 = constrain(y0, 0, rasterHeight - 1);
//		x1 = constrain(x1, 0, rasterWidth - 1);
//		y1 = constrain(y1, 0, rasterHeight - 1);
//
//		double dx = x1 - x0;
//		double dy = y1 - y0;
//		double dh = (double) (Math.sqrt(dx * dx + dy * dy));
//
//		int x, y;
//		int ym, xm, index;
//		double percent;
//
//		for (int i = 0; i < dh; i++) {
//			percent = (double) i / dh;
//			x = (int) (x0 + percent * dx);
//			y = (int) (y0 + percent * dy);
//
//			ym = y % rasterHeight;
//			xm = x % rasterWidth;
//			index = ym * rasterWidth + xm;
//			rasterPixels[index] = (byte) (((UNSIGN & rasterPixels[index]) + DRAW_BYTE));
//		}
//
//	}
//
//	// --------------------------------------------------------------------------
//	
//
//	static public void main(String args[]) {
//		PApplet.main(new String[] { "dendron" });
//	}

package cc.creativecomputing.graphics.font;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCSignedDistanceField {

	/**
	 * Maximum number of distance transform passes
	 */
	public static int SDF_MAX_PASSES = 10; //
	/**
	 * Controls how much smaller the neighbor value must be to consider, too
	 * small slack increase iteration count.
	 */
	public static double SDF_SLACK = 0.001f;
	/**
	 * Big value used to initialize the distance field.
	 */
	public static double SDF_BIG = 1e+37f;

	/**
	 * This function converts the antialiased image where each pixel represents
	 * coverage (box-filter sampling of the ideal, crisp edge) to a distance
	 * field with narrow band radius of sqrt(2). This is the fastest way to turn
	 * antialised image to contour texture. This function is good if you don't
	 * need the distance field for effects (i.e. fat outline or dropshadow).
	 * Input and output buffers must be different.
	 * 
	 * @param out Output of the distance transform, one byte per pixel.
	 * @param img Input image, one byte per pixel.
	 * @param width Width if the image.
	 * @param height Height if the image.
	 */
	public void coverageToDistanceField(double[][] out, double[][] img, int width, int height) {

		// Zero out borders
		for (int x = 0; x < width; x++) {
			out[x][0] = 0;
			out[x][height - 1] = 0;
		}
		for (int y = 1; y < height; y++) {
			out[0][y] = 0;
			out[width - 1][y] = 0;
		}

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {

				// Skip flat areas.
				if (img[x][y] == 1) {
					out[x][y] = 1;
					continue;
				}
				if (img[x][y] == 0) {
					// Special handling for cases where full opaque pixels are
					// next to full transparent pixels.
					// See: https://github.com/memononen/SDF/issues/2
					boolean he = img[x - 1][y] == 1 || img[x + 1][y] == 1;
					boolean ve = img[x][y - 1] == 1 || img[x][y + 1] == 1;
					if (!he && !ve) {
						out[x][y] = 0;
						continue;
					}
				}

				double gx = -img[x - 1][y - 1] - CCMath.SQRT2 * img[x - 1][y] - img[x - 1][y + 1] + img[x + 1][y - 1] + CCMath.SQRT2 * img[x + 1][y] + img[x + 1][y + 1];
				double gy = -img[x - 1][y - 1] - CCMath.SQRT2 * img[x][y - 1] - img[x + 1][y - 1] + img[x - 1][y + 1] + CCMath.SQRT2 * img[x][y + 1] + img[x + 1][y + 1];
				double a = img[x][y];
				gx = CCMath.abs(gx);
				gy = CCMath.abs(gy);

				double d;
				// double glen;
				if (gx < 0.0001f || gy < 0.000f) {
					d = (0.5f - a) * CCMath.SQRT2;
				} else {
					double glen = gx * gx + gy * gy;
					glen = 1.0f / CCMath.sqrt(glen);
					gx *= glen;
					gy *= glen;
					if (gx < gy) {
						double temp = gx;
						gx = gy;
						gy = temp;
					}
					double a1 = 0.5f * gy / gx;
					if (a < a1) { // 0 <= a < a1
						d = 0.5f * (gx + gy) - CCMath.sqrt(2.0f * gx * gy * a);
					} else if (a < (1.0 - a1)) { // a1 <= a <= 1-a1
						d = (0.5f - a) * gx;
					} else { // 1-a1 < a <= 1
						d = -0.5f * (gx + gy) + CCMath.sqrt(2.0f * gx * gy * (1.0f - a));
					}
				}
				d *= 1.0f / CCMath.SQRT2;
				out[x][y] = CCMath.saturate(0.5f - d);
			}
		}
	}

	private static double edged(double gx, double gy, double a) {
		double df, a1;
		if ((gx == 0) || (gy == 0)) {
			// Either A) gu or gv are zero, or B) both
			// Linear approximation is A) correct or B) a fair guess
			df = 0.5 - a;
		} else {
			// Everything is symmetric wrt sign and transposition,
			// so move to first octant (gx>=0, gy>=0, gx>=gy) to
			// avoid handling all possible edge directions.
			gx = CCMath.abs(gx);
			gy = CCMath.abs(gy);
			if (gx < gy) {
				double temp = gx;
				gx = gy;
				gy = temp;
			}
			a1 = 0.5f * gy / gx;
			if (a < a1) { // 0 <= a < a1
				df = 0.5 * (gx + gy) - CCMath.sqrt(2.0 * gx * gy * a);
			} else if (a < (1.0 - a1)) { // a1 <= a <= 1-a1
				df = (0.5 - a) * gx;
			} else { // 1-a1 < a <= 1
				df = -0.5 * (gx + gy) + CCMath.sqrt(2.0 * gx * gy * (1.0 - a));
			}
		}
		return df;
	}

	private static double distsqr(CCVector2 a, CCVector2 b) {
		double dx = b.x - a.x, dy = b.y - a.y;
		return dx * dx + dy * dy;
	}

	/**
	 * Sweep-and-update Euclidean distance transform of an antialised image for
	 * contour textures. Based on edtaa3func.c by Stefan Gustavson.
	 * <p>
	 * White (255) pixels are treated as object pixels, zero pixels are treated
	 * as background. An attempt is made to treat antialiased edges correctly.
	 * The input image must have pixels in the range [0,255], and the
	 * antialiased image should be a box-filter sampling of the ideal, crisp
	 * edge. If the antialias region is more than 1 pixel wide, the result from
	 * this transform will be inaccurate. Pixels at image border are not
	 * calculated and are set to 0.
	 * <p>
	 * The output distance field is encoded as bytes, where 0 = radius (outside)
	 * and 255 = -radius (inside). Input and output can be the same buffer.
	 * 
	 * @param theOut Output of the distance transform, one byte per pixel.
	 * @param theRadius The radius of the distance field narrow band in pixels.
	 * @param theImage Input image, one byte per pixel.
	 * @param theWidth Width if the image.
	 * @param theHeight Height if the image.
	 * @return
	 */
	public int buildDistanceField(double[][] out, double radius, double[][] img, int width, int height) {

		double[][] tdist = new double[width][height];
		CCVector2[][] tpt = new CCVector2[width][height];

		// Initialize buffers
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tpt[x][y] = new CCVector2();
				tdist[x][y] = SDF_BIG;
			}
		}

		// Calculate position of the anti-aliased pixels and distance to the
		// boundary of the shape.
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				CCVector2 c = new CCVector2(x, y);

				// Skip flat areas.
				if (img[x][y] == 1)
					continue;
				if (img[x][y] == 0) {
					// Special handling for cases where full opaque pixels are
					// next to full transparent pixels.
					// See: https://github.com/memononen/SDF/issues/2
					boolean he = img[x - 1][y] == 1 || img[x + 1][y] == 1;
					boolean ve = img[x][y - 1] == 1 || img[x][y + 1] == 1;
					if (!he && !ve)
						continue;
				}

				// Calculate gradient direction

				double gx = -img[x - 1][y - 1] - CCMath.SQRT2 * img[x - 1][y] - img[x - 1][y + 1] + img[x + 1][y - 1] + CCMath.SQRT2 * img[x + 1][y] + img[x + 1][y + 1];
				double gy = -img[x - 1][y - 1] - CCMath.SQRT2 * img[x][y - 1] - img[x + 1][y - 1] + img[x - 1][y + 1] + CCMath.SQRT2 * img[x][y + 1] + img[x + 1][y + 1];
				if (CCMath.abs(gx) < 0.001f && CCMath.abs(gy) < 0.001f)
					continue;
				double glen = gx * gx + gy * gy;
				if (glen > 0.0001f) {
					glen = 1.0f / CCMath.sqrt(glen);
					gx *= glen;
					gy *= glen;
				}

				// Find nearest point on contour.
				double d = edged(gx, gy, img[x][y]);
				tpt[x][y].x = x + gx * d;
				tpt[x][y].y = y + gy * d;
				tdist[x][y] = distsqr(c, tpt[x][y]);
			}
		}

		// Calculate distance transform using sweep-and-update.
		for (int pass = 0; pass < SDF_MAX_PASSES; pass++) {
			int changed = 0;

			// Bottom-left to top-right.
			for (int y = 1; y < height - 1; y++) {
				for (int x = 1; x < width - 1; x++) {
					boolean ch = false;
					CCVector2 c = new CCVector2(x, y);
					CCVector2 pt = null;
					double pd = tdist[x][y], d;
					// (-1,-1)
					if (tdist[x - 1][y - 1] < pd) {
						d = distsqr(c, tpt[x - 1][y - 1]);
						if (d + SDF_SLACK < pd) {
							pt = tpt[x - 1][y - 1];
							pd = d;
							ch = true;
						}
					}
					// (0,-1)
					if (tdist[x][y - 1] < pd) {
						d = distsqr(c, tpt[x][y - 1]);
						if (d + SDF_SLACK < pd) {
							pt = tpt[x][y - 1];
							pd = d;
							ch = true;
						}
					}
					// (1,-1)
					if (tdist[x + 1][y - 1] < pd) {
						d = distsqr(c, tpt[x + 1][y - 1]);
						if (d + SDF_SLACK < pd) {
							pt = tpt[x + 1][y - 1];
							pd = d;
							ch = true;
						}
					}
					// (-1,0)
					if (tdist[x - 1][y] < tdist[x][y]) {
						d = distsqr(c, tpt[x - 1][y]);
						if (d + SDF_SLACK < pd) {
							pt = tpt[x - 1][y];
							pd = d;
							ch = true;
						}
					}
					if (ch) {
						tpt[x][y] = pt;
						tdist[x][y] = pd;
						changed++;
					}
				}
			}

			// Top-right to bottom-left.
			for (int y = height - 2; y > 0; y--) {
				for (int x = width - 2; x > 0; x--) {
					boolean ch = false;
					CCVector2 c = new CCVector2(x, y);
					CCVector2 pt = null;
					double pd = tdist[x][y], d;
					// (1,0)
					if (tdist[x + 1][y] < pd) {
						d = distsqr(c, tpt[x + 1][y]);
						if (d + SDF_SLACK < pd) {
							pt = tpt[x + 1][y];
							pd = d;
							ch = true;
						}
					}
					// (-1,1)
					if (tdist[x - 1][y + 1] < pd) {
						d = distsqr(c, tpt[x - 1][y + 1]);
						if (d + SDF_SLACK < pd) {
							pt = tpt[x - 1][y + 1];
							pd = d;
							ch = true;
						}
					}
					// (0,1)
					if (tdist[x][y + 1] < pd) {
						d = distsqr(c, tpt[x][y + 1]);
						if (d + SDF_SLACK < pd) {
							pt = tpt[x][y + 1];
							pd = d;
							ch = true;
						}
					}
					// (1,1)
					if (tdist[x + 1][y + 1] < pd) {
						d = distsqr(c, tpt[x + 1][y + 1]);
						if (d + SDF_SLACK < pd) {
							pt = tpt[x + 1][y + 1];
							pd = d;
							ch = true;
						}
					}
					if (ch) {
						tpt[x][y] = pt;
						tdist[x][y] = pd;
						changed++;
					}
				}
			}

			if (changed == 0)
				break;
		}

		// Map to good range.
		double scale = 1.0 / radius;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double d = CCMath.sqrt(tdist[x][y]) * scale;
				if (img[x][y] > 0.5)
					d = -d;
				out[x][y] = CCMath.saturate(0.5f - d * 0.5f);
			}
		}

		return 1;
	}
}

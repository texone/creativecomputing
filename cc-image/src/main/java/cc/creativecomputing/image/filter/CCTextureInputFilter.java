/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.image.filter;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

public abstract class CCTextureInputFilter implements BufferedImageOp {

	public BufferedImage filter(BufferedImage theImage) {
		return filter(theImage, null);
	}

	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
		if (dstCM == null)
			dstCM = src.getColorModel();
		return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
	}

	public Rectangle2D getBounds2D(BufferedImage src) {
		return new Rectangle(0, 0, src.getWidth(), src.getHeight());
	}

	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
		if (dstPt == null)
			dstPt = new Point2D.Double();
		dstPt.setLocation(srcPt.getX(), srcPt.getY());
		return dstPt;
	}

	public RenderingHints getRenderingHints() {
		return null;
	}

	/**
	 * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
	 * penalty of BufferedImage.getRGB unmanaging the image.
	 * @param image   a BufferedImage object
	 * @param x       the left edge of the pixel block
	 * @param y       the right edge of the pixel block
	 * @param width   the width of the pixel array
	 * @param height  the height of the pixel array
	 * @param pixels  the array to hold the returned pixels. May be null.
	 * @return the pixels
	 * @see #setRGB
	 */
	public int[] getRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
			return (int[]) image.getRaster().getDataElements(x, y, width, height, pixels);
		return image.getRGB(x, y, width, height, pixels, 0, width);
	}

	/**
	 * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
	 * penalty of BufferedImage.setRGB unmanaging the image.
	 * @param image   a BufferedImage object
	 * @param x       the left edge of the pixel block
	 * @param y       the right edge of the pixel block
	 * @param width   the width of the pixel array
	 * @param height  the height of the pixel array
	 * @param pixels  the array of pixels to set
	 * @see #getRGB
	 */
	public void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
		int type = image.getType();
		if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
			image.getRaster().setDataElements(x, y, width, height, pixels);
		else
			image.setRGB(x, y, width, height, pixels, 0, width);
	}
}

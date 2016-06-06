package cc.creativecomputing.model.svg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import cc.creativecomputing.io.xml.CCXMLElement;

class CCSVGRadialGradient extends CCSVGGradient {
	double cx, cy, r;

	public CCSVGRadialGradient(CCSVGGroup parent) {
		super(parent);

		
	}
	
	@Override
	public void read(CCXMLElement theSVG) {
		super.read(theSVG);
		this.cx = CCSVGIO.getDoubleWithUnit(theSVG, "cx");
		this.cy = CCSVGIO.getDoubleWithUnit(theSVG, "cy");
		this.r = CCSVGIO.getDoubleWithUnit(theSVG, "r");

		String transformStr = theSVG.attribute("gradientTransform");

		if (transformStr != null) {
			double t[] = CCSVGIO.parseTransform(transformStr).get(null);
			this.transform = new AffineTransform(t[0], t[3], t[1], t[4],
					t[2], t[5]);

			Point2D t1 = transform.transform(new Point2D.Double(cx, cy),
					null);
			Point2D t2 = transform.transform(new Point2D.Double(cx + r, cy),
					null);

			this.cx = (double) t1.getX();
			this.cy = (double) t1.getY();
			this.r = (double) (t2.getX() - t1.getX());
		}
	}
	
	@Override
	public String svgTag() {
		return "radialGradient";
	}
}
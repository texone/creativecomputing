package cc.creativecomputing.model.svg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import cc.creativecomputing.io.xml.CCDataElement;

class CCSVGLinearGradient extends CCSVGGradient {
	double x1, y1, x2, y2;

	public CCSVGLinearGradient(CCSVGGroup parent) {
		super(parent);		
	}
	
	@Override
	public void read(CCDataElement theSVG) {
		super.read(theSVG);
		
		this.x1 = CCSVGIO.getDoubleWithUnit(theSVG, "x1");
		this.y1 = CCSVGIO.getDoubleWithUnit(theSVG, "y1");
		this.x2 = CCSVGIO.getDoubleWithUnit(theSVG, "x2");
		this.y2 = CCSVGIO.getDoubleWithUnit(theSVG, "y2");

		String transformStr = theSVG.attribute("gradientTransform");

		if (transformStr != null) {
			double t[] = CCSVGIO.parseTransform(transformStr).get(null);
			this.transform = new AffineTransform(t[0], t[3], t[1], t[4],
					t[2], t[5]);

			Point2D t1 = transform.transform(new Point2D.Double(x1, y1), null);
			Point2D t2 = transform.transform(new Point2D.Double(x2, y2), null);

			this.x1 = (double) t1.getX();
			this.y1 = (double) t1.getY();
			this.x2 = (double) t2.getX();
			this.y2 = (double) t2.getY();
		}
	}
	
	@Override
	public String svgTag() {
		return "linearGradient";
	}
}
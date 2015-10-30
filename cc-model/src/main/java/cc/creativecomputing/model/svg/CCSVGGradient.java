package cc.creativecomputing.model.svg;

import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.List;

import cc.creativecomputing.core.util.CCArrayUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.math.spline.CCLinearSpline;

class CCSVGGradient extends CCSVGElement {
	AffineTransform transform;

	double[] offset;
	int[] color;
	int count;

	CCSVGGradient(CCSVGGroup parent, CCXMLElement theSVG) {
		super(parent);

		offset = new double[theSVG.countChildren()];
		color = new int[theSVG.countChildren()];
		
		count = 0;
		
		// <stop offset="0" style="stop-color:#967348"/>
		for (CCXMLElement elem:theSVG) {
			String name = elem.name();
			if (name.equals("stop")) {
				String offsetAttr = elem.attribute("offset");
				double div = 1.0f;
				if (offsetAttr.endsWith("%")) {
					div = 100.0f;
					offsetAttr = offsetAttr.substring(0, offsetAttr.length() - 1);
				}
				
				offset[count] = Double.parseDouble(offsetAttr) / div;
				String style = elem.attribute("style");
				HashMap<String, String> styles = CCSVGIO.parseStyleAttributes(style);

				String colorStr = styles.get("stop-color");
				if (colorStr == null)
					colorStr = "#000000";
				
				String opacityStr = styles.get("stop-opacity");
				
				if (opacityStr == null)
					opacityStr = "1";
				
				int tupacity = (int) (Double.parseDouble(opacityStr) * 255);
				color[count] = (tupacity << 24) | Integer.parseInt(colorStr.substring(1), 16);
				count++;
			}
		}

		offset = CCArrayUtil.subset(offset, 0, count);
		color = CCArrayUtil.subset(color, 0, count);
	}
	

	
	@Override
	public void drawImplementation(CCGraphics g, boolean theFill) {}

	@Override
	public List<CCLinearSpline> contours() {
		// TODO Auto-generated method stub
		return null;
	}
}
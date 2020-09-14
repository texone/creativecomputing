package cc.creativecomputing.demo.math.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class RectangleSeparationStrategy implements RectangleStrategy {

	public List<Rectangle> rectangles;

	public RectangleSeparationStrategy(List<Rectangle> _rectangles) {
		rectangles = _rectangles;


	}

	
	
	 private CCVector2 translate_vector(Rectangle rect) {

			CCVector2 overlapSum = new CCVector2();
			for(Rectangle r :rectangles) {
				if(r == rect)continue;
				if(!rect.overlap(r))continue;
				
				overlapSum.addLocal(rect.center_vec(r));
			}

	        return overlapSum.normalize();
	 }
	 
	 private boolean avoid() {
		 List<CCVector2> offsets = new ArrayList<>();
			CCVector2 offsetSum = new CCVector2();
			for(int i = 0; i < rectangles.size();i++) {
				offsets.add( translate_vector(rectangles.get(i)).normalize());
			}
			            
			for(int i = 0; i < rectangles.size();i++) {
				rectangles.get(i).left +=offsets.get(i).x;
				rectangles.get(i).top  +=offsets.get(i).y;
				
				offsetSum.x += offsets.get(i).x;
				offsetSum.y += offsets.get(i).y;
			}
			CCLog.info(offsetSum.length());
			return offsetSum.length() > 0;
	 }

	     

	@Override
	public void step() {
		while(avoid()) {
			
		}
	}
}

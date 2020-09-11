package cc.creativecomputing.demo.math.util;

import java.util.Collections;
import java.util.List;

import cc.creativecomputing.math.CCMath;

/**
 * This strategy starts at the top, works it's way down, shifting rectangles downward as needed
 * so they don't overlap previous rectangles above
 * @author chris
 *
 */
public class RectangleTopDownStrategy implements RectangleStrategy{

	public List<Rectangle> rectangles;
	public int index;
	
	public RectangleTopDownStrategy(List<Rectangle> _rectangles) {
		rectangles = _rectangles;
		Collections.sort(rectangles, (r1, r2) -> Double.compare(r1.top, r2.top));
		index = 1;
		
	}
     
	public boolean overlaps_previous(int i) {
		Rectangle r = rectangles.get(i);
		for(int j = 0; j < i;j++) {
			Rectangle rect = rectangles.get(j);
			if(r.overlap(rect)) {
				return true;
			}
		}
        return false;
	}
        
	@Override
	public void step() {
		while (index < rectangles.size() && !overlaps_previous(index))
            index += 1;

        if (index >= rectangles.size())
            // No more overlaps, so exit without doing anything
            return;

        Rectangle current = rectangles.get(index);
        double translation = 0;
       
        // Find the maximum vertical overlap of this rectangle with all previous rectangles
        for(int j = 0; j < index;j++) {
			Rectangle r = rectangles.get(j);
			 if( current.overlap(r)) {
	                translation = CCMath.max(translation, CCMath.abs(r.bottom() - current.top));
			 }
        }
		

        current.top += translation;
		Collections.sort(rectangles, (r1, r2) -> Double.compare(r1.top, r2.top));
	}

}

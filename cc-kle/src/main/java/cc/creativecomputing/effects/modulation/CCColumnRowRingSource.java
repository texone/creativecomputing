package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.math.CCMath;

public class CCColumnRowRingSource extends CCModulationSource{

	public CCColumnRowRingSource() {
		super(
			"column row ring", 
			(effectManager, effectable) -> {
				int c = effectable.column();
				int r = effectable.row();
				int columns = effectManager.columns();
				int rows = effectManager.rows();
				int ring = CCMath.min(c, r, columns - 1 - c, rows - 1 - r);  
				
				double ringIndex = 0;
				
				if(ring == c){
					ringIndex = r - ring;
				}else if(ring == rows - 1 - r){
					ringIndex = (rows - ring * 2) + c - ring - 1;
				}else if(ring == columns - 1 - c){
					ringIndex = (rows - ring * 2) + (columns - ring * 2) + (rows - ring - r) - 2;
				}else{
					ringIndex = (rows - ring * 2) * 2 + (columns - ring * 2) + (columns - ring - c) - 3;
				}
				return ringIndex / ((rows - ring * 2) + (columns - ring * 2)) * 2d;
			}
		);
	}

}

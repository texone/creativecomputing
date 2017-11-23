package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.math.CCMath;

public class CCColumnRowSpiralSource extends CCModulationSource{

	public CCColumnRowSpiralSource() {
		super(
			"column row spiral", 
			(effectManager, effectable) -> {
				int c = effectable.column();
				int r = effectable.row();
				int columns = effectManager.columns();
				int rows = effectManager.rows();
				int ring = CCMath.min(c, r, columns - 1 - c, rows - 1 - r);  
				
				double spiralIndex = 0;
				if(ring == c){
					spiralIndex = r - ring;
				}else if(ring == rows - 1 - r){
					spiralIndex = (rows - ring * 2) + c - ring - 1;
				}else if(ring == columns - 1 - c){
					spiralIndex = (rows - ring * 2) + (columns - ring * 2) + (rows - ring - r) - 2;
				}else{
					spiralIndex = (rows - ring * 2) * 2 + (columns - ring * 2) + (columns - ring - c) - 3;
				}
				for(int i = 0; i < ring;i++){
					spiralIndex += ((rows - ring * 2) + (columns - ring * 2)) * 2d - 1;
				}
				return spiralIndex / ((rows - ring * 2) + (columns - ring * 2)) * 2d;
			}
		);
	}

}

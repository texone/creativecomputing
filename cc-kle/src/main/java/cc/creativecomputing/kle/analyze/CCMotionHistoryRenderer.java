package cc.creativecomputing.kle.analyze;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.time.CCMotionHistoryDataPoint;

public class CCMotionHistoryRenderer extends CCHistoryRenderer<CCMotionHistoryDataPoint>{

	public void draw3D(CCGraphics g, List<CCMotionHistoryDataPoint> theData, CCHistoryValueSettings<CCMotionHistoryDataPoint> theSettings){
		if(!theSettings._cShow)return;
		float mySize = CCMath.max(_cHistorySize, theData.size());
		if(_cDrawValues){
			g.beginShape(CCDrawMode.LINE_STRIP);
			float i = 0; 
			for(CCMotionHistoryDataPoint myData:new ArrayList<>(theData)){
				g.color(
					theSettings._cColor.r, 
					theSettings._cColor.g, 
					theSettings._cColor.b, 
					_cFadeOut ? (i / mySize) * _cAlpha : _cAlpha);
				i++;
				g.vertex(myData.position);
			}
			g.endShape();
		}
		if(_cDrawViolations){
			g.beginShape(CCDrawMode.POINTS);
			float i = 0; 
			for(CCMotionHistoryDataPoint myData:new ArrayList<>(theData)){
				if(theSettings.isOverMax(myData)){
					g.color(
						theSettings._cColor.r, 
						theSettings._cColor.g, 
						theSettings._cColor.b, 	
						_cFadeOut ? (i / mySize) * _cAlpha : _cAlpha
					);
					g.vertex(myData.position);
				}
				i++;
			}
			g.endShape();
		}
	}
	
	public void draw3D(CCGraphics g, List<CCMotionHistoryDataPoint> theData){
		for(CCHistoryValueSettings<CCMotionHistoryDataPoint> mySettings:_cValueSettings.values()){
			draw3D(g, theData, mySettings);
		}
	}
}

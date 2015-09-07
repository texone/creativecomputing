package cc.creativecomputing.kle.analyze;

import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;

public abstract class CCMotionHistoryGraph extends CCHistoryGraph<CCMotionData>{
	
	public CCMotionHistoryGraph(CCAnalyzeSettings theSettings) {
		super(theSettings);
	}

	public void draw3D(CCGraphics g, List<CCMotionData> theData){
		if(!_cShow)return;
		float mySize = CCMath.max(_mySettings._cHistorySize, theData.size());
		g.beginShape(CCDrawMode.LINE_STRIP);
		float i = 0; 
		for(CCMotionData myData:theData){
			g.color(
				_cColor.r, 
				_cColor.g, 
				_cColor.b, 
				_mySettings._cFadeOut ? (i / mySize) * _mySettings._cAlpha : _mySettings._cAlpha);
			i++;
			g.vertex(myData.position);
		}
		g.endShape();
		g.beginShape(CCDrawMode.POINTS);
		i = 0; 
		for(CCMotionData myData:theData){
			if(CCMath.abs(value(myData)) > _cMax){
				g.color(
					_cColor.r, 
					_cColor.g, 
					_cColor.b, 	
					_mySettings._cFadeOut ? (i / mySize) * _mySettings._cAlpha : _mySettings._cAlpha
				);
				g.vertex(myData.position);
			}
			i++;
		}
		g.endShape();
	}
}

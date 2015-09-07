package cc.creativecomputing.kle.analyze;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;


public class CCFloatHistoryGraph extends CCHistoryGraph<CCDoubleHistoryData>{
	
	@CCProperty(name = "graph")
	private CCAnalyzeSettings _cSettings;
	
	protected final List<CCDoubleHistoryData> data;
	
	public CCFloatHistoryGraph() {
		_mySettings = _cSettings = new CCAnalyzeSettings();
		data = new ArrayList<>();
	}
	
	public void reset(){
		data.clear();
	}
	
	public void update(double theDeltaTime){
		
	}
	
	public void addData(CCDoubleHistoryData theValue){
		while(data.size() >= _cSettings._cHistorySize){
			data.remove(0);
		}
		data.add(theValue);
	}
	
	public void addData(double theValue, double theTimeStep){
		addData(new CCDoubleHistoryData(theValue, theTimeStep));
	}
	
	public void drawCurves(CCGraphics g, double theHeight){
		drawCurves(g, data, theHeight);
	}

	@Override
	public double value(CCDoubleHistoryData theData) {
		return theData.value;
	}

}

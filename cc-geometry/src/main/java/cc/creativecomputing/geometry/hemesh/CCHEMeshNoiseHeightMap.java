package cc.creativecomputing.geometry.hemesh;

import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.signal.CCSimplexNoise;

public class CCHEMeshNoiseHeightMap {
	
	@CCProperty(name = "noise")
	private CCSimplexNoise _myNoise;
	@CCProperty(name = "amount", min = 0, max = 1000)
	private double _cAmount = 0;
	@CCProperty(name = "look up", min = 0, max = 1)
	private double _cLookUp = 0;
	
	public CCHEMeshNoiseHeightMap(){
		_myNoise = new CCSimplexNoise();
	}

	public void modify(List<CCHEFace> theFaces) {
		for(CCHEFace myFace:theFaces){
			CCVector3 myCentroid = myFace.centroid();
			
			double myNoiseHeight0 = _myNoise.value(myCentroid.x, myCentroid.y) * _cAmount;
			double myNoiseHeight1 = _myNoise.value(myCentroid.x + _cLookUp, myCentroid.y) * _cAmount;
			double myNoiseHeight2 = _myNoise.value(myCentroid.x, myCentroid.y + _cLookUp) * _cAmount;
			
			CCVector3 myNoiseNormal = CCVector3.normal(
				new CCVector3(myCentroid.x, myCentroid.y, myNoiseHeight0),
				new CCVector3(myCentroid.x + _cLookUp, myCentroid.y, myNoiseHeight1),
				new CCVector3(myCentroid.x, myCentroid.y + _cLookUp, myNoiseHeight2)
			);
			
			myFace.centroid().z = myNoiseHeight0;
			
			CCHEEdge myEdge = myFace.edge();
			do{
				myEdge.start().vector().z += myNoiseHeight0;
				myEdge = myEdge.next();
			}while(myEdge != null && myEdge != myFace.edge());

			myFace.rotateTo(myNoiseNormal);
		}
	}

}

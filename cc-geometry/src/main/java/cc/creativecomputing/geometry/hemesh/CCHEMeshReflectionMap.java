package cc.creativecomputing.geometry.hemesh;

import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.geometry.surface.CCImageTargets;
import cc.creativecomputing.geometry.surface.CCSurfaceQuad;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.signal.CCSimplexNoise;

public class CCHEMeshReflectionMap {
	
	@CCProperty(name = "noise")
	private CCSimplexNoise _myNoise;
	@CCProperty(name = "amount", min = 0, max = 1000)
	private double _cAmount = 0;
	
	private CCImageTargets _myImageTargets;
	private CCSurfaceQuad _mySurfaceQuad;
	
	public CCHEMeshReflectionMap(CCImage theTextureData, CCSurfaceQuad theSurfaceQuad){
		_myImageTargets = new CCImageTargets(theTextureData);
		_mySurfaceQuad = theSurfaceQuad;
	}
	
	/**
	 * Calculates a normal Vector in world space to satisfy a reflection from the light to the target.
	 * 
	 * @param theLightPosition
	 * @param theTargetPosition
	 * @return
	 */
	private CCVector3 calculateReflectionNormal(final CCVector3 theLightPosition, final CCVector3 theTargetPosition, final CCVector3 theFaceCentroid) {
		final CCVector3 myToLight = theLightPosition.subtract(theFaceCentroid);
		myToLight.normalizeLocal();

		final CCVector3 myToTarget = theTargetPosition.subtract(theFaceCentroid);
		myToTarget.normalizeLocal();


		final CCVector3 myResultNormal = new CCVector3(myToLight);
		myResultNormal.addLocal(myToTarget);
		myResultNormal.normalizeLocal();

		return myResultNormal;
	}

	public void modify(List<CCHEFace> theFaces) {
		List<CCVector3> myVector = _myImageTargets.createAndDistributePointsOnImage(theFaces.size(), 10);
		for(CCHEFace myFace:theFaces){
			CCVector3 myCentroid = myFace.centroid();
			
			double myNoiseHeight0 = _myNoise.value(myCentroid.x, myCentroid.y) * _cAmount;
			double myNoiseHeight1 = _myNoise.value(myCentroid.x + 0.1f, myCentroid.y) * _cAmount;
			double myNoiseHeight2 = _myNoise.value(myCentroid.x, myCentroid.y + 0.1f) * _cAmount;
			
			CCVector3 myNoiseNormal = CCVector3.normal(
				new CCVector3(myCentroid.x, myCentroid.y + 0.1f, myNoiseHeight2),
				new CCVector3(myCentroid.x + 0.1f, myCentroid.y, myNoiseHeight1),
				new CCVector3(myCentroid.x, myCentroid.y, myNoiseHeight0)
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

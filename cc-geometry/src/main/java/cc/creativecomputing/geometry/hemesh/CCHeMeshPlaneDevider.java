package cc.creativecomputing.geometry.hemesh;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCRay3;
import cc.creativecomputing.math.CCVector3;

public class CCHeMeshPlaneDevider implements CCHEMeshModifier{

	private CCPlane _myPlane;
	
	public CCHeMeshPlaneDevider(CCPlane thePlane){
		_myPlane = thePlane;
	}
	
	/**
	 * Add all all signs of the faces vertices sides to the plane, if all are negative or positive
	 * the sum is the negative or positive number of edges, if we have vertices on both sides the
	 * result is smaller than the number of edges, this way the result of division is either
	 * -1, 0 or 1
	 * @param theFace
	 * @return
	 */
	private int checkPlaneFaceRelation(CCHEFace theFace){
		int myRelationSum = 0;
		for(CCHEEdge myEdge:theFace.edges()){
			myRelationSum += CCMath.sign(_myPlane.pseudoDistance(myEdge.start().vector()));
		}
		return myRelationSum / theFace.numberOfEdges();
	}
	
	private void cutFace(CCHEFace theFace){
		CCHEEdge myCutInEdge = null;
		CCHEEdge myCutOutEdge = null;
		for(CCHEEdge myEdge:theFace.edges()){
			if(_myPlane.pseudoDistance(myEdge.start().vector()) >= 0 && _myPlane.pseudoDistance(myEdge.end().vector()) < 0){
				myCutInEdge = myEdge;
			}
			if(_myPlane.pseudoDistance(myEdge.start().vector()) < 0 && _myPlane.pseudoDistance(myEdge.end().vector()) > 0){
				myCutOutEdge = myEdge;
			}
		}
		
		if(myCutInEdge == null || myCutOutEdge == null){
			return;
		}
		
		CCVector3 myInVector = CCRay3.createFromLine(myCutInEdge.start().vector(), myCutInEdge.end().vector()).intersectsPlane(_myPlane);
		CCVector3 myOutVector = CCRay3.createFromLine(myCutOutEdge.start().vector(), myCutOutEdge.end().vector()).intersectsPlane(_myPlane);
		
		CCHEEdge myCutEdge = new CCHEEdge(new CCHEVertex(myInVector), theFace);
		
		myCutInEdge.next(myCutEdge);
		myCutOutEdge.start().vector().set(myOutVector);
		myCutEdge.next(myCutOutEdge);
		
		theFace.edge(myCutEdge);
	}

	@Override
	public void modify(CCHEMesh theMesh) {
		List<CCHEFace> myCutFaces = new ArrayList<>();
		List<CCHEFace> myPositivFaces = new ArrayList<>();
		List<CCHEFace> myNegativeFaces = new ArrayList<>();
		for(CCHEFace myFace:theMesh.faces()){
			switch(checkPlaneFaceRelation(myFace)){
			case -1:
				myNegativeFaces.add(myFace);
				break;
			case 0:
				myCutFaces.add(myFace);
				break;
			case 1:
				myPositivFaces.add(myFace);
				break;
			}
		}
		for(CCHEFace myFace:myNegativeFaces){
			theMesh.removeFace(myFace);
		}
		for(CCHEFace myFace:myCutFaces){
			cutFace(myFace);
		}
		System.out.println(myCutFaces.size()+ ":" + myPositivFaces.size() + ":" + myNegativeFaces.size());
	}
	

}

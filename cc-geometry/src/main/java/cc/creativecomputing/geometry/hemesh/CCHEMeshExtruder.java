package cc.creativecomputing.geometry.hemesh;

import java.util.ArrayList;
import java.util.List;

public class CCHEMeshExtruder implements CCHEMeshModifier{

	public CCHEMeshExtruder(){
		
	}
	
	private float _myScale = 1;
	
	public void scale(float theScale){
		_myScale = theScale;
	}
	
	private void buildNeighborFace(CCHEMesh theMesh, List<CCHEEdge> theSideEdges, CCHEEdge theEdge){
		if(theEdge.mark0())return;
		if(theEdge.pair() == null)return;
		if(theEdge.pair().next() == null)return;
		CCHEEdge myPair = theEdge.pair();
		
		theEdge.mark0(true);
		myPair.mark0(true);
		
		CCHEFace myFace = new CCHEFace();
		CCHEEdge myEdge0 = new CCHEEdge(theEdge.next().start(), myFace);
		CCHEEdge myEdge1 = new CCHEEdge(theEdge.start(), myFace);
		CCHEEdge myEdge2 = new CCHEEdge(myPair.next().start(), myFace);
		CCHEEdge myEdge3 = new CCHEEdge(myPair.start(), myFace);
		
		theMesh.addFace(myFace);
		myFace.edge(myEdge0);
		
		theEdge.pair(myEdge0);
		myEdge0.pair(theEdge);
		myPair.pair(myEdge2);
		myEdge2.pair(myPair);
		
		theSideEdges.add(myEdge1);
		theSideEdges.add(myEdge3);
		
		theMesh.addHalfEdge(myEdge0);
		theMesh.addHalfEdge(myEdge1);
		theMesh.addHalfEdge(myEdge2);
		theMesh.addHalfEdge(myEdge3);
		
		myEdge0.next(myEdge1);
		myEdge1.next(myEdge2);
		myEdge2.next(myEdge3);
		myEdge3.next(myEdge0);
	}
	
	private void buildCrossFace(CCHEMesh theMesh, CCHEEdge theEdge){
		if(theEdge.mark0())return;
		theEdge.mark0(true);
		if(theEdge.next().pair() == null)return;
		if(theEdge.next().pair().next().pair() == null)return;
		
		
		CCHEEdge myEdge1 = theEdge.next().pair().next().pair().next();

		if(myEdge1 == null)return;
		if(myEdge1.next().pair() == null)return;
		
		CCHEEdge myEdge2 = myEdge1.next().pair().next().pair().next();
		
		theEdge.mark0(true);
		myEdge1.mark0(true);
		myEdge2.mark0(true);
		
		CCHEFace myFace = new CCHEFace();
		theMesh.addFace(myFace);
		CCHEEdge myFaceEdge2 = new CCHEEdge(theEdge.next().start(), myFace);
		CCHEEdge myFaceEdge1 = new CCHEEdge(myEdge1.next().start(), myFace);
		CCHEEdge myFaceEdge0 = new CCHEEdge(myEdge2.next().start(), myFace);
		
		theMesh.addHalfEdge(myFaceEdge2);
		theMesh.addHalfEdge(myFaceEdge1);
		theMesh.addHalfEdge(myFaceEdge0);
		myFace.edge(myFaceEdge0);
		
		theEdge.pair(myFaceEdge2);
		myFaceEdge2.pair(theEdge);
		myEdge1.pair(myFaceEdge1);
		myFaceEdge1.pair(myEdge1);
		myEdge2.pair(myFaceEdge0);
		myFaceEdge0.pair(myEdge2);
		
		myFaceEdge0.next(myFaceEdge1);
		myFaceEdge1.next(myFaceEdge2);
		myFaceEdge2.next(myFaceEdge0);
		
	}
	
	private List<CCHEFace> _myOriginalFaces = new ArrayList<>();
	
	public List<CCHEFace> originalFaces(){
		return _myOriginalFaces;
	}
	
	public void modify(CCHEMesh theMesh){
		theMesh.vertices().clear();
		
		for(CCHEEdge myEdge:theMesh.edges()){
			CCHEVertex myVertex = new CCHEVertex(myEdge.start().vector().clone());
			myEdge.vertex(myVertex);
			theMesh.addVertex(myVertex);
		}
		
		_myOriginalFaces = new ArrayList<>(theMesh.faces());
		List<CCHEEdge> myOriginalEdges = new ArrayList<>(theMesh.edges());
		List<CCHEEdge> mySideEdges = new ArrayList<>();
		for(CCHEEdge myEdge:myOriginalEdges){
			buildNeighborFace(theMesh, mySideEdges, myEdge);
		}
		
		for(CCHEEdge mySideEdge: mySideEdges){
			buildCrossFace(theMesh, mySideEdge);
		}
		
		for(CCHEFace myFace:theMesh.faces()){
			myFace.updateCentroid();
			myFace.updateNormal();
		}
		
		for(CCHEFace myFace:_myOriginalFaces){
			myFace.scale(_myScale);
		}
	}
}

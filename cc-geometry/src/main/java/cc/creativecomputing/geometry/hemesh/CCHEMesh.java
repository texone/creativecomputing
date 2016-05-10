package cc.creativecomputing.geometry.hemesh;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCVector3;

public class CCHEMesh {

	private List<CCHEFace> _myFaces;
	
	private List<CCHEEdge> _myEdges;
	
	private List<CCHEVertex> _myVertices;
	
	public CCHEMesh(){
		_myFaces = new ArrayList<>();
		_myEdges = new ArrayList<>();
		_myVertices = new ArrayList<>();
	}
	
	public void addHalfEdge(CCHEEdge theEdge){
		_myEdges.add(theEdge);
	}
	
	public List<CCHEEdge> edges(){
		return _myEdges;
	}
	
	public void addVertex(CCHEVertex theVertex){
		_myVertices.add(theVertex);
	}
	
	public List<CCHEVertex> vertices(){
		return _myVertices;
	}
	
	public void addFace(CCHEFace theFace){
		_myFaces.add(theFace);
	}
	
	public void removeFace(CCHEFace theFace){
		_myFaces.remove(theFace);
		
		CCHEEdge myEdge = theFace.edge();
		
		if(theFace.edge().next() == null){
			_myEdges.remove(myEdge);
			return;
		}
		
		do{
			_myEdges.remove(myEdge);
			myEdge = myEdge.next();
		}while(myEdge.next() != null && myEdge != theFace.edge());
	}
	
	public List<CCHEFace> faces(){
		return _myFaces;
	}
	
	public CCHEVertex getVertex(CCVector3 theInput){
		for(CCHEVertex myVertex:_myVertices){
			if(myVertex.vector().equals(theInput)){
				return myVertex;
			}
		}
		CCHEVertex myNewVertex = new CCHEVertex(theInput);
		_myVertices.add(myNewVertex);
		return myNewVertex;
	}
	
//	public void drawEdges(CCGraphics g){
//		g.beginShape(CCDrawMode.LINES);
//		for(CCHEEdge myEdge:_myEdges){
//			if(myEdge.next() == null)continue;
////			if(!myEdge.mark0())continue;
////			if(myEdge.pair()!= null)continue;
//			g.color(255,0,0);
//			g.vertex(myEdge.start().vector());
//			g.color(0,255,0);
//			g.vertex(myEdge.next().start().vector());
//		}
//		g.endShape();
//	}
//	
//	public void drawFaces(CCGraphics g){
//		for(CCHEFace myFace:_myFaces){
////			if(myFace.edge() == null)continue;
////			CCHEEdge myEdge = myFace.edge();
////			if(myEdge.next() == null)continue;
//
//			g.beginShape(CCDrawMode.POLYGON);
//			for(CCHEEdge myEdge:myFace.edges()){
//				g.vertex(myEdge.start().vector());
//			}
////			do{
//////				g.color(255,0,0);
////				
//////				g.color(0,255,0);
//////				g.vertex(myEdge.next().start().vector());
////				myEdge = myEdge.next();
////				
////			}while(myEdge.next() != null && myEdge != myFace.edge());
//			g.endShape();
//		}
//	}
	
	public void closeFaces(){
		List<CCHEEdge> myEdges = new ArrayList<>();
		for(CCHEFace myFace:_myFaces){
			myEdges.clear();
			for(CCHEEdge myEdge:_myEdges){
				if(myEdge.face() == myFace && myEdge != myFace.edge()){
					myEdges.add(myEdge);
				}
			}
			System.out.println(myEdges.size());
		}
	}
}

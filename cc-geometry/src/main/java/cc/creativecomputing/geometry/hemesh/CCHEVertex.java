package cc.creativecomputing.geometry.hemesh;

import cc.creativecomputing.math.CCVector3;

public class CCHEVertex {
	 private CCVector3 _myVector;
     private CCHEEdge _myEdge;  // one of the half-edges emantating from the vertex
     
     public CCHEVertex(CCVector3 theVector, CCHEEdge theEdge){
    	 _myVector = theVector;
    	 _myEdge = theEdge;
     }
     
     public CCHEEdge edge(){
    	 return _myEdge;
     }
     
     public CCHEVertex(CCVector3 theVector){
    	 this(theVector, null);
     }
     
     public CCVector3 vector(){
    	 return _myVector;
     }
}

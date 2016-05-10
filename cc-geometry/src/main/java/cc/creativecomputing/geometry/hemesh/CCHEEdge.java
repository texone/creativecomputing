package cc.creativecomputing.geometry.hemesh;

public class CCHEEdge {
	private CCHEVertex _myVertex;   // vertex at the end of the half-edge
	private CCHEEdge _myPair;   // oppositely oriented adjacent half-edge 
    private CCHEFace _myFace;   // face the half-edge borders
    private CCHEEdge _myNext;   // next half-edge around the face
    private CCHEEdge _myPrevoius;
    
    private boolean _myMark0 = false;
    private boolean _myMark1 = false;
    
    public CCHEEdge(CCHEVertex theVertex, CCHEFace theFace){
    	_myVertex = theVertex;
    	_myFace = theFace;
    }
    
    public CCHEFace face(){
    	return _myFace;
    }
    
    public void face(CCHEFace theFace){
    	_myFace = theFace;
    }
    
    public void pair(CCHEEdge thePair){
    	_myPair = thePair;
    }
    
    public CCHEEdge pair(){
    	return _myPair;
    }
    
    public CCHEVertex start(){
    	return _myVertex;
    }
    
    public void vertex(CCHEVertex theVertex){
    	_myVertex = theVertex;
    }
    
    public CCHEVertex end(){
    	if(_myNext != null)return _myNext.start();
    	if(_myPair != null)return _myPair.start();
    	return null;
    }
    
    public boolean isNext(CCHEEdge theEdge){
    	return _myPair.start().vector().equals(theEdge.start().vector());
    }
    
    public boolean isPrevious(CCHEEdge theEdge){
    	return _myVertex.vector().equals(theEdge.pair().start().vector());
    }
    
    public CCHEEdge next(){
    	return _myNext;
    }
    
    public void next(CCHEEdge theEdge){
    	_myNext = theEdge;
    	theEdge._myPrevoius = this;
    }
    
    public CCHEEdge previous(){
    	return _myPrevoius;
    }
    
    public void previous(CCHEEdge theEdge){
    	_myPrevoius = theEdge;
    	theEdge._myNext = this;
    }
    
    void mark0(boolean theMark0){
    	_myMark0 = theMark0;
    }
    
    boolean mark0(){
    	return _myMark0;
    }
    
    void mark1(boolean theMark1){
    	_myMark0 = theMark1;
    }
    
    boolean mark1(){
    	return _myMark1;
    }
}

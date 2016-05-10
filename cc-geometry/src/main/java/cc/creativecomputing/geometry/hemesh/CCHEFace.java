package cc.creativecomputing.geometry.hemesh;

import java.util.Iterator;

import cc.creativecomputing.math.CCVector3;


public class CCHEFace {
	public static class CCHEEdgeIterator implements Iterator<CCHEEdge>, Iterable<CCHEEdge>{
		
		private CCHEFace _myFace;
		private CCHEEdge _myEdge;
		private boolean _myIsfirst;
		
		private CCHEEdgeIterator(CCHEFace theFace){
			_myFace = theFace;
			_myEdge = theFace.edge();
			_myIsfirst = true;
		}

		@Override
		public boolean hasNext() {
			return  _myEdge != null && (_myIsfirst || _myEdge != _myFace.edge());
		}

		@Override
		public CCHEEdge next() {
			_myIsfirst = false;
			CCHEEdge myResult = _myEdge;
			_myEdge = _myEdge.next();
			return myResult;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Iterator<CCHEEdge> iterator() {
			return this;
		}
		
		
	}
	
	private CCHEEdge _myEdge;  // one of the half-edges bordering the face
	private CCVector3 _myCentroid;
	private CCVector3 _myNormal;
	private int _myNumberOfEdges = 0;
	
	public CCHEFace(){
	}
	
	public CCHEEdge edge(){
		return _myEdge;
	}
	
	public void edge(CCHEEdge theEdge){
		_myEdge = theEdge;
	}
	
	public Iterable<CCHEEdge> edges(){
		return new CCHEEdgeIterator(this);
	}
	
	public double signedArea() {
		double area = 0;
		
		CCHEEdge myEdge = _myEdge;
		
		if(_myEdge.next() == null){
			return 0;
		}
		
		do{
			CCVector3 myA = myEdge.start().vector();
			CCVector3 myB = myEdge.next().start().vector();
			area += myA.x * myB.y;
			area -= myA.y * myB.x;
			myEdge = myEdge.next();
		}while(myEdge.next() != null && myEdge != _myEdge);
		
		area /= 2.0;

		return area;
	}
	
	public void updateNormal(){
		if(_myEdge.next() == null){
			_myNormal = new CCVector3(0f,1f,0f);
			return;
		}
		if(_myEdge.next().next() == null){
			_myNormal = new CCVector3(0f,1f,0f);
			return;
		}
		
		_myNormal = CCVector3.normal(
			_myEdge.next().next().start().vector(),
			_myEdge.next().start().vector(), 
			_myEdge.start().vector()
		);
	}
	
	//TODO centroid in face is just working 2D
	void updateCentroid() {
		_myCentroid = new CCVector3();
		
		if(_myEdge.next() == null){
			_myCentroid.set(_myEdge.start().vector());
			return;
		}

		double factor = 0;
		
		CCHEEdge myEdge = _myEdge;
		
		_myNumberOfEdges = 0;
		
		do{
			CCVector3 myA = myEdge.start().vector();
			CCVector3 myB = myEdge.next().start().vector();
			factor = (myA.x * myB.y - myB.x * myA.y);
			_myCentroid.x += (myA.x + myB.x) * factor;
			_myCentroid.y += (myA.y + myB.y) * factor;
			myEdge = myEdge.next();
			_myNumberOfEdges++;
		}while(myEdge.next() != null && myEdge != _myEdge);
		
		
		factor = 1 / (signedArea() * 6);
		_myCentroid.x *= factor;
		_myCentroid.y *= factor;
	}
	
	public int numberOfEdges(){
		return _myNumberOfEdges;
	}
	
	public CCVector3 centroid(){
		return _myCentroid;
	}
	
	public CCVector3 normal(){
		return _myNormal;
	}
	
	public void scale(double theScale){
		CCVector3 myCenter = centroid();
		CCHEEdge myEdge = _myEdge;
		do{
			CCVector3 myA = myEdge.start().vector();
			myA.subtractLocal(myCenter);
			myA.multiplyLocal(theScale);
			myA.addLocal(myCenter);
			myEdge = myEdge.next();
		}while(myEdge.next() != null && myEdge != _myEdge);
	}
	
	public void rotateTo(CCVector3 theDirection){
		CCVector3 myCenter = centroid();
		CCVector3 myDirection = normal();
		double myAngle = CCVector3.angle(theDirection, myDirection);

		CCVector3 myAxis = myDirection.cross(theDirection);
		myAxis.normalizeLocal();
		
//		CCMatrix4 myTransform = new CCMatrix4();

		PMatrix3D myTransform = new PMatrix3D();
		myTransform.rotate(myAngle,myAxis.x, myAxis.y, myAxis.z);

//		CCQuaternion myQuaternion = new CCQuaternion();
//		myQuaternion.fromVectorAndAngle(myAngle, myAxis.x, myAxis.y, myAxis.z);
//		myTransform.setRotationFromQuaternion(myQuaternion);

		CCHEEdge myEdge = _myEdge;
		do{
			CCVector3 myA = myEdge.start().vector();
			myA.subtractLocal(myCenter);
			myTransform.mult(myA, myA);
			myA.addLocal(myCenter);
			myEdge = myEdge.next();
		}while(myEdge.next() != null && myEdge != _myEdge);
		
		updateNormal();
	}
}

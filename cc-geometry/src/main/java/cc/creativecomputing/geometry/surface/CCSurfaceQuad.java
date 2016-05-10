package cc.creativecomputing.geometry.surface;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;


/**
 *
 */
public class CCSurfaceQuad {
	
//	/**
//	 * @param theVertices four vertices
//	 * @param theUvs four uv coordinates [0-1] in the same order as the vertices
//	 * @return
//	 */
//	public static CCSurfaceQuad fromFourVerticesAndUvs(final ArrayList<CCVector3> theVertices, final ArrayList<CCVector3> theUvs) {
//		
//		CCVector3 myPointOrigin = null;
//		CCVector3 myPointUMax = null;
//		CCVector3 myPointVMax = null;
//	
//		for(int i = 0; i < 4; i++) {
//			final CCVector3 myVertex = theVertices.get(i);
//			final CCVector3 myVertexUV = theUvs.get(i);
//
//			if( myVertexUV.x < 0.5 && myVertexUV.y < 0.5) {
//				myPointOrigin = new CCVector3(myVertex.x, myVertex.y, myVertex.z);
//			}
//			
//			if( myVertexUV.x > 0.5 && myVertexUV.y < 0.5) {
//				myPointUMax = new CCVector3(myVertex.x, myVertex.y, myVertex.z);
//			}
//			
//			if( myVertexUV.x < 0.5 && myVertexUV.y > 0.5) {
//				myPointVMax = new CCVector3(myVertex.x, myVertex.y, myVertex.z);
//			}
//		}
//		
//		if(myPointOrigin == null || myPointUMax == null || myPointVMax == null) {
//			System.out.println(" Uh Uh Uh"); // FIXME: detect UV inconsistencies earlier
//			return null;
//		}
//			
//		final CCVector3 myOrigin = myPointOrigin;	
//		
//		final CCVector3 myU = CCVecMath.subtract(myPointUMax, myPointOrigin);	
//		final float myWidth = myU.length();
//		myU.normalize();
//			
//		final CCVector3 myV = CCVecMath.subtract(myPointVMax, myPointOrigin);
//		final float myHeight = myV.length();
//		myV.normalize();
//			
//		return new CCSurfaceQuad(myOrigin, myU, myV, myWidth, myHeight);		
//	}
	
	protected final float _myWidth; // mm
	protected final float _myHeight; // mm
	
	
	/**
	 * World coordinates to quad tangent space (normalized [0-1])
	 */
	protected CCMatrix4x4 _myToLocalMatrix; 
	
	/**
	 * Tangent coordinates to quad tangent space (normalized [0-1])
	 */
	protected CCMatrix4x4 _myToWorldMatrix;
		
	
	public CCSurfaceQuad(float theWidth, float theHeight) {
		_myToWorldMatrix = new CCMatrix4x4();
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		updateMatrices();
	}
	
	
	public void updateMatrices() {
		
		//_myToWorldMatrix.transpose();
		
		_myToLocalMatrix = new CCMatrix4x4(_myToWorldMatrix);
		_myToLocalMatrix.invert();
	}
	
	public CCMatrix4x4 transform() {
		return _myToWorldMatrix;
	}
	
	/**
	 * @return width of the quad in world coordinates
	 */
	public double width() {
		return _myWidth;
	}
	
	/**
	 * @return height of the quad in world coordinates
	 */
	public double height() {
		return _myHeight;
	}
	
	public CCVector3 toWorldCoords(final CCVector3 thePosition) {
		return _myToWorldMatrix.applyPostVector(thePosition, null);
	}
	
	public CCVector3 toTangentCoords(final CCVector3 thePosition) {
		return _myToLocalMatrix.applyPostVector(thePosition, null);
	}
	
	public CCMatrix4x4 toWorldMatrix() {
		return  _myToWorldMatrix;
	}
	
	public CCMatrix4x4 toTangentMatrix() {
		return _myToLocalMatrix;
	}
	
	public void draw(CCGraphics g){
		g.pushMatrix();
		g.applyMatrix(_myToWorldMatrix);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0,0);
		g.vertex(_myWidth, 0);
		g.vertex(_myWidth, _myHeight);
		g.vertex(0, _myHeight);
		g.endShape();
		g.popMatrix();
	}
	
}

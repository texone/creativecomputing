package cc.creativecomputing.geometry.hemesh;

import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.util.CCTriangulator;
import cc.creativecomputing.math.CCVector3;

public class CCHEMeshTriangulator {

	private CCTriangulator _myTriangulator;
	
	public CCHEMeshTriangulator(){
		_myTriangulator = new CCTriangulator();
	}
	
	public void triangulate(CCHEMesh theMesh){
		for(CCHEFace myFace:theMesh.faces()){
			if(myFace.numberOfEdges() == 4){
				CCVector3 myV0 = myFace.edge().start().vector();
				CCVector3 myV1 = myFace.edge().next().start().vector();
				CCVector3 myV2 = myFace.edge().next().next().start().vector();
				CCVector3 myV3 = myFace.edge().next().next().next().start().vector();
				_myTriangulator.vertices().add(myV0);
				_myTriangulator.normals().add(myFace.normal());
				_myTriangulator.vertices().add(myV1);
				_myTriangulator.normals().add(myFace.normal());
				_myTriangulator.vertices().add(myV2);
				_myTriangulator.normals().add(myFace.normal());
				_myTriangulator.vertices().add(myV0);
				_myTriangulator.normals().add(myFace.normal());
				_myTriangulator.vertices().add(myV2);
				_myTriangulator.normals().add(myFace.normal());
				_myTriangulator.vertices().add(myV3);
				_myTriangulator.normals().add(myFace.normal());
			}else{
				_myTriangulator.beginPolygon();
				for(CCHEEdge myEdge:myFace.edges()){
					_myTriangulator.normal(myFace.normal());
					_myTriangulator.vertex(myEdge.start().vector());
				}
			_myTriangulator.endPolygon();
			}
		}
	}
	
	public List<CCVector3> toTriangleVertexList(CCHEMesh theMesh){
		_myTriangulator = new CCTriangulator();
		triangulate(theMesh);
		return _myTriangulator.vertices();
	}
	
	public CCVBOMesh toVBO(CCHEMesh theMesh){
		_myTriangulator = new CCTriangulator();
		triangulate(theMesh);
		
		CCVBOMesh myResult = new CCVBOMesh(CCDrawMode.TRIANGLES, _myTriangulator.vertices().size());
		myResult.vertices( _myTriangulator.vertices());
		myResult.normals(_myTriangulator.normals());
		
		return myResult;
	}
}

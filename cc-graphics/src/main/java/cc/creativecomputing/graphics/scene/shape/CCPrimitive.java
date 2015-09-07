package cc.creativecomputing.graphics.scene.shape;

import cc.creativecomputing.gl.data.CCGeometryData;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.graphics.scene.CCGeometry;

public abstract class CCPrimitive extends CCGeometry{
	
	protected CCGeometryData _myGeometryData;
	
	protected void allocate(GLDrawMode theDrawMode, int theNumberOfVertices, int theNumberOfIndices){
		_myData = _myGeometryData = new CCGeometryData(theDrawMode, theNumberOfVertices);
		_myGeometryData.allocateVertices();
		_myGeometryData.allocateNormals();
		_myGeometryData.allocateTextureCoords(0, 2);
		_myData.allocateIndices(theNumberOfIndices);
	}
	
	protected void allocate(int theNumberOfVertices, int theNumberOfIndices){
		allocate(GLDrawMode.TRIANGLES, theNumberOfVertices, theNumberOfIndices);
	}

	protected void createGeometry(){
		_myData.rewindBuffer();
		
		setGeometryData();
		setIndexData();

		_myData.rewindBuffer();
		
		updateWorldBound();
	}
	
	protected abstract void setGeometryData();
	
	protected abstract void setIndexData();
	
}

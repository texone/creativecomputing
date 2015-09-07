package cc.creativecomputing.gl.demo;

import cc.creativecomputing.gl.data.CCGeometryData;
import cc.creativecomputing.gl4.GLDrawMode;

public abstract class CCPrimitive{
	
	protected CCGeometryData _myGeometryData;
	
	protected void allocate(int theNumberOfVertices, int theNumberOfIndices){
		_myGeometryData = new CCGeometryData(GLDrawMode.TRIANGLES, theNumberOfVertices);
		_myGeometryData.allocateVertices();
		_myGeometryData.allocateNormals();
		_myGeometryData.allocateTextureCoords(0, 2);
		_myGeometryData.allocateIndices(theNumberOfIndices);
	}

	protected void createGeometry(){
		_myGeometryData.rewindBuffer();
		
		setGeometryData();
		setIndexData();

		_myGeometryData.rewindBuffer();
	}
	
	public CCGeometryData data(){
		return _myGeometryData;
	}
	
	protected abstract void setGeometryData();
	
	protected abstract void setIndexData();
	
}
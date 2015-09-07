package cc.creativecomputing.graphics.scene;


import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.gl.data.CCGeometryData;
import cc.creativecomputing.gl.data.GLCombinedBuffer;
import cc.creativecomputing.gl.data.GLMesh;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLUniform;
import cc.creativecomputing.gl4.GLUniformParameters;
import cc.creativecomputing.graphics.bounding.CCBoundingSphere;
import cc.creativecomputing.graphics.bounding.CCBoundingVolume;
import cc.creativecomputing.graphics.scene.effect.CCVisualEffect;
import cc.creativecomputing.graphics.scene.state.CCGlobalState;
import cc.creativecomputing.graphics.scene.state.CCGlobalState.CCGlobalStateType;
import cc.creativecomputing.math.CCMatrix3x3;
import cc.creativecomputing.math.CCMatrix4x4;

public class CCGeometry extends CCSpatial{

	protected CCGeometryData _myData;
	protected boolean _myIsUpdatedData;
	
	protected CCBoundingVolume modelBound;
	
	public CCGlobalState[] states = new CCGlobalState[CCGlobalStateType.values().length];
	
	public void updateModelState(){
		
	}
	
	public void meshData(CCGeometryData theMeshData){
		_myData = theMeshData;
		modelBound = null;
	}
	
	public CCGeometryData meshData(){
		return _myData;
	}
	
	protected void updateModelNormals (){
		
	}
	
	protected void updateWorldBound (){
		if(modelBound == null){
			modelBound = new CCBoundingSphere();
		    modelBound.computeFromPrimitives(_myData);
		}
		_myWorldBound = modelBound.transform(_myWorldTransform, _myWorldBound);
	}
	
	@Override
	public void getVisibleSet(CCCuller culler, boolean noCull) {
		culler.Insert(this);
	}
	
	@GLUniform(name = "ModelViewMatrix")
	private CCMatrix4x4 _myModelViewMatrix = new CCMatrix4x4();
	
	@GLUniform(name = "NormalMatrix")
	private CCMatrix3x3 _myNormalMatrix = new CCMatrix3x3();
	
	@GLUniform(name = "MVP")
	private CCMatrix4x4 _myModelViewProjectionMatrix = new CCMatrix4x4();
	
	@Override
	public void draw(CCRenderer theRenderer, CCVisualEffect theEffect) {
		_myModelMatrix.multiply(theRenderer.camera().viewMatrix(),_myModelViewMatrix);
		_myModelViewMatrix.matrix3(_myNormalMatrix);
		_myModelViewMatrix.multiply(theRenderer.camera().projectionMatrix(), _myModelViewProjectionMatrix);
		if(_myEffect == null) {
			if(theEffect == null)return;
			theEffect.draw(theRenderer, this);
		}else _myEffect.draw(theRenderer, this);
	}
	
	private Map<GLShaderProgram, GLUniformParameters> _myParameterMap = new HashMap<>();
	
	public GLUniformParameters parameters(GLShaderProgram theProgram){
		if(!_myParameterMap.containsKey(theProgram)){
			_myParameterMap.put(theProgram, theProgram.createUniformParameters(this));
		}
		return _myParameterMap.get(theProgram);
	}

	private GLMesh _myMesh;
	
	public void drawImplementation(CCRenderer theRenderer) {
		if(_myMesh == null){
			_myMesh = new GLMesh(_myData.drawMode(), new GLCombinedBuffer(_myData), effect().shader(0));
		}
		_myMesh.draw();
	}
}

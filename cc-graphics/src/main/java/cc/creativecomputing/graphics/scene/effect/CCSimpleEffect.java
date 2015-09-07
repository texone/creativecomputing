package cc.creativecomputing.graphics.scene.effect;

import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.graphics.scene.state.CCAlphaState;
import cc.creativecomputing.graphics.scene.state.CCCullState;
import cc.creativecomputing.graphics.scene.state.CCDepthState;
import cc.creativecomputing.graphics.scene.state.CCOffsetState;
import cc.creativecomputing.graphics.scene.state.CCStencilState;
import cc.creativecomputing.graphics.scene.state.CCVisualPass;
import cc.creativecomputing.graphics.scene.state.CCWireFrameState;

public class CCSimpleEffect extends CCVisualEffect{
	
	private CCVisualPass _myPass;

	public CCSimpleEffect(){
		super();
		
		_myPass = new CCVisualPass();
		insertPass(_myPass);
	}
	
	public GLShaderProgram shader() {
		return _myPass.shader();
	}
	
	public void shader(GLShaderProgram theShader){
		_myPass.shader(theShader);
	}

	public CCAlphaState alphaState() {
		return _myPass.alphaState();
	}
	
	public void alphaState(CCAlphaState theState){
		_myPass.alphaState(theState);
	}

	public CCCullState cullState() {
		return _myPass.cullState();
	}
	
	public void cullState(CCCullState theState){
		_myPass.cullState(theState);
	}

	public CCDepthState depthState() {
		return _myPass.depthState();
	}
	
	public void depthState(CCDepthState theState){
		_myPass.depthState(theState);
	}

	public CCOffsetState offsetState() {
		return _myPass.offsetState();
	}
	
	public void offsetState(CCOffsetState theState){
		_myPass.offsetState(theState);
	}

	public CCStencilState stencilState() {
		return _myPass.stencilState();
	}
	
	public void stencilState(CCStencilState theState){
		_myPass.stencilState(theState);
	}

	public CCWireFrameState wireState() {
		return _myPass.wireState();
	} 
	
	public void wireState(CCWireFrameState theState){
		_myPass.wireState(theState);
	}
}

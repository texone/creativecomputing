package cc.creativecomputing.graphics.scene.state;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLShaderProgram;

public class CCVisualPass {
	
	protected GLShaderProgram _myShader;
	
	protected CCAlphaState _myAlphaState = new CCAlphaState();
	protected CCCullState _myCullState = new CCCullState();
	protected CCDepthState _myDepthState = new CCDepthState();
	protected CCOffsetState _myOffsetState = new CCOffsetState();
    protected CCStencilState _myStencilState = new CCStencilState();
    protected CCWireFrameState _myWireState = new CCWireFrameState();
    
	public CCVisualPass (){}

	public GLShaderProgram shader() {
		return _myShader;
	}

	public void shader(GLShaderProgram theShader) {
		_myShader = theShader;
	}

	public CCAlphaState alphaState() {
		return _myAlphaState;
	}

	public void alphaState(CCAlphaState theAlphaState) {
		_myAlphaState = theAlphaState;
	}

	public CCCullState cullState() {
		return _myCullState;
	}

	public void cullState(CCCullState theCullState) {
		_myCullState = theCullState;
	}

	public CCDepthState depthState() {
		return _myDepthState;
	}

	public void depthState(CCDepthState theDepthBufferState) {
		_myDepthState = theDepthBufferState;
	}

	public CCOffsetState offsetState() {
		return _myOffsetState;
	}

	public void offsetState(CCOffsetState theOffsetState) {
		_myOffsetState = theOffsetState;
	}

	public CCStencilState stencilState() {
		return _myStencilState;
	}

	public void stencilState(CCStencilState theStencilState) {
		_myStencilState = theStencilState;
	}

	public CCWireFrameState wireState() {
		return _myWireState;
	}

	public void wireState(CCWireFrameState theWireState) {
		_myWireState = theWireState;
	}

	public void draw(GLGraphics g){
		
	}
	
}

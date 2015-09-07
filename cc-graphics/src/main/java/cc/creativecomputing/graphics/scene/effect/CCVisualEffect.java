package cc.creativecomputing.graphics.scene.effect;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLUniformParameters;
import cc.creativecomputing.graphics.scene.CCGeometry;
import cc.creativecomputing.graphics.scene.CCRenderer;
import cc.creativecomputing.graphics.scene.state.CCAlphaState;
import cc.creativecomputing.graphics.scene.state.CCCullState;
import cc.creativecomputing.graphics.scene.state.CCDepthState;
import cc.creativecomputing.graphics.scene.state.CCOffsetState;
import cc.creativecomputing.graphics.scene.state.CCStencilState;
import cc.creativecomputing.graphics.scene.state.CCVisualPass;
import cc.creativecomputing.graphics.scene.state.CCWireFrameState;

public class CCVisualEffect {

	protected List<CCVisualPass> _myPasses = new ArrayList<>();

	public CCVisualEffect (){
		
	}
	
	// Access to components of the effect.
	public int numberOfPasses (int theTechniqueIndex){
		return _myPasses.size();
	}
	
	public CCVisualPass pass (int thePassIndex){
		return _myPasses.get(thePassIndex);
	}
	
	/**
	 * Support for deferred creation. The function appends the new pass to the end of the list.
	 * @param thePass
	 */
	public void insertPass(CCVisualPass thePass) {
		_myPasses.add(thePass);
	}

	public int numberOfPasses() {
		return _myPasses.size();
	}

	public GLShaderProgram shader(int thePassIndex) {
		return _myPasses.get(thePassIndex).shader();
	}

	public CCAlphaState alphaState(int thePassIndex) {
		return _myPasses.get(thePassIndex).alphaState();
	}

	public CCCullState cullState(int thePassIndex) {
		return _myPasses.get(thePassIndex).cullState();
	}

	public CCDepthState depthState(int thePassIndex) {
		return _myPasses.get(thePassIndex).depthState();
	}

	public CCOffsetState offsetState(int thePassIndex) {
		return _myPasses.get(thePassIndex).offsetState();
	}

	public CCStencilState stencilState(int thePassIndex) {
		return _myPasses.get(thePassIndex).stencilState();
	}

	public CCWireFrameState wireState(int thePassIndex) {
		return _myPasses.get(thePassIndex).wireState();
	}
	
	protected GLUniformParameters parameters(GLShaderProgram theShader){
		return null;
	}
	
	public void draw(CCRenderer theRenderer, CCGeometry theGeometry){
		GLGraphics g = theRenderer.graphics();
		for(CCVisualPass myPass:_myPasses){
			myPass.alphaState().draw(g);
			myPass.cullState().draw(g);
			myPass.depthState().draw(g);
			myPass.offsetState().draw(g);
			myPass.stencilState().draw(g);
	        myPass.wireState().draw(g);
        
			myPass.shader().use();
			myPass.shader().uniforms(parameters(myPass.shader()));
			myPass.shader().uniforms(theGeometry.parameters(myPass.shader()));
			theGeometry.drawImplementation(theRenderer);
			myPass.shader();
		}
	}
}

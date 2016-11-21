package cc.creativecomputing.graphics.texture;

import java.nio.FloatBuffer;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCColor;

public class CCGLSwapBuffer {

	protected CCShaderBuffer _myCurrentDataTexture;
	protected CCShaderBuffer _myDestinationDataTexture;
	
	public CCGLSwapBuffer(
		final CCPixelType theType,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfAttachments, 
		final int theWidth, 
		final int theHeight, final CCTextureTarget theTarget
	){
		_myCurrentDataTexture = new CCShaderBuffer(theType, theNumberOfBits, theNumberOfChannels, theNumberOfAttachments, theWidth, theHeight, theTarget);
		_myCurrentDataTexture.clear();
		_myDestinationDataTexture = new CCShaderBuffer(theType, theNumberOfBits, theNumberOfChannels, theNumberOfAttachments, theWidth, theHeight, theTarget);
		_myDestinationDataTexture.clear();
	}
	
	public CCGLSwapBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfAttachments, 
		final int theWidth, 
		final int theHeight, 
		final CCTextureTarget theTarget
	){
		this(
			CCPixelType.FLOAT,
			theNumberOfBits,
			theNumberOfChannels,
			theNumberOfAttachments,
			theWidth,
			theHeight,
			theTarget
		);
	}
			
	public CCGLSwapBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfAttachments, 
		final int theWidth,
		final int theHeight
	){
		this(
			theNumberOfBits,
			theNumberOfChannels,
			theNumberOfAttachments,
			theWidth,
			theHeight,
			CCTextureTarget.TEXTURE_RECT
		);
	}
		
	public CCGLSwapBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theWidth, 
		final int theHeight, 
		final CCTextureTarget theTarget
	){
		this(
			theNumberOfBits,
			theNumberOfChannels,
			1,
			theWidth,
			theHeight,
			theTarget
		);
	}
		
	public CCGLSwapBuffer(
		final int theWidth, 
		final int theHeight, 
		final CCTextureTarget theTarget
	){
		this(32,3,theWidth,theHeight,theTarget);
	}
		
	public CCGLSwapBuffer(
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theWidth, final int theHeight
	){
		this(theNumberOfBits,theNumberOfChannels,1,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
		
	public CCGLSwapBuffer(final int theWidth, final int theHeight){
		this(32,3,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public void clear(CCGraphics g, CCColor theClearColor, int...theAttachments){
		g.pushAttribute();
		g.clearColor(theClearColor);
		for(int myAttachment:theAttachments){
			_myCurrentDataTexture.beginDraw(myAttachment);
			g.clear();
			_myCurrentDataTexture.endDraw();
		}
		g.popAttribute();
	}
	
	public void textureFilter(int theAttachment, CCTextureFilter theFilter){
		_myCurrentDataTexture.attachment(0).textureFilter(theFilter);
		_myDestinationDataTexture.attachment(0).textureFilter(theFilter);
	}
	
	public CCTexture2D attachment(int theID){
		return _myCurrentDataTexture.attachment(theID);
	}
	
	public void clear(){
		_myCurrentDataTexture.clear();
	}
	
	public void swap(){
		CCShaderBuffer myTemp = _myDestinationDataTexture;
		_myDestinationDataTexture = _myCurrentDataTexture;
		_myCurrentDataTexture = myTemp;
	}
	
	public void beginDrawCurrent(){
		_myCurrentDataTexture.beginDraw();
	}
	
	public void beginDrawCurrent(int theAttachment){
		_myCurrentDataTexture.beginDraw(theAttachment);
	}
	
	public void beginDrawDestination(){
		_myDestinationDataTexture.beginDraw();
	}
	
	public void beginDrawDestination(int theAttachment){
		_myDestinationDataTexture.beginDraw(theAttachment);
	}

	public void draw() {
		_myDestinationDataTexture.draw();
	}
	
	public void draw(double theX0, double theY0, double theX1, double theY1) {
		_myDestinationDataTexture.draw(theX0, theY0, theX1, theY1);
	}
	
	public void draw(CCAABoundingRectangle theRect) {
		_myDestinationDataTexture.draw(theRect);
	}
	
	public void endDrawCurrent(){
		_myCurrentDataTexture.endDraw();
	}
	
	public void endDrawDestination(){
		_myDestinationDataTexture.endDraw();
	}
	
	public CCShaderBuffer currentBuffer(){
		return _myCurrentDataTexture;
	}
	
	public CCShaderBuffer destinationBuffer(){
		return _myDestinationDataTexture;
	}
	
	public FloatBuffer getData(int theX, int theY, int theWidth, int theHeight){
		return _myCurrentDataTexture.getData(theX, theY, theWidth, theHeight);
	}
	
	public int width(){
		return _myCurrentDataTexture.width();
	}
	
	public int height(){
		return _myCurrentDataTexture.height();
	}
}

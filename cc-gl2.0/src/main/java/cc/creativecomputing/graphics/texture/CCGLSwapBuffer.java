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
		final CCGraphics g,
		final CCPixelType theType,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfAttachments, 
		final int theWidth, 
		final int theHeight, final CCTextureTarget theTarget
	){
		_myCurrentDataTexture = new CCShaderBuffer(theType, theNumberOfBits, theNumberOfChannels, theNumberOfAttachments, theWidth, theHeight, theTarget);
		_myCurrentDataTexture.clear(g);
		_myDestinationDataTexture = new CCShaderBuffer(theType, theNumberOfBits, theNumberOfChannels, theNumberOfAttachments, theWidth, theHeight, theTarget);
		_myDestinationDataTexture.clear(g);
	}
	
	public CCGLSwapBuffer(
		final CCGraphics g,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfAttachments, 
		final int theWidth, 
		final int theHeight, 
		final CCTextureTarget theTarget
	){
		this(
			g,
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
		final CCGraphics g,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theNumberOfAttachments, 
		final int theWidth,
		final int theHeight
	){
		this(
			g,
			theNumberOfBits,
			theNumberOfChannels,
			theNumberOfAttachments,
			theWidth,
			theHeight,
			CCTextureTarget.TEXTURE_RECT
		);
	}
		
	public CCGLSwapBuffer(
		final CCGraphics g,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theWidth, 
		final int theHeight, 
		final CCTextureTarget theTarget
	){
		this(
			g,
			theNumberOfBits,
			theNumberOfChannels,
			1,
			theWidth,
			theHeight,
			theTarget
		);
	}
		
	public CCGLSwapBuffer(
		final CCGraphics g,
		final int theWidth, 
		final int theHeight, 
		final CCTextureTarget theTarget
	){
		this(g,32,3,theWidth,theHeight,theTarget);
	}
		
	public CCGLSwapBuffer(
		final CCGraphics g,
		final int theNumberOfBits, 
		final int theNumberOfChannels, 
		final int theWidth, final int theHeight
	){
		this(g, theNumberOfBits,theNumberOfChannels,1,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
		
	public CCGLSwapBuffer(final CCGraphics g,final int theWidth, final int theHeight){
		this(g, 32,3,theWidth,theHeight,CCTextureTarget.TEXTURE_RECT);
	}
	
	public void clear(CCGraphics g, CCColor theClearColor, int...theAttachments){
		g.pushAttribute();
		g.clearColor(theClearColor);
		for(int myAttachment:theAttachments){
			_myCurrentDataTexture.beginDraw(g,myAttachment);
			g.clear();
			_myCurrentDataTexture.endDraw(g);
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
	
	public void clear(CCGraphics g){
		_myCurrentDataTexture.clear(g);
	}
	
	public void swap(){
		CCShaderBuffer myTemp = _myDestinationDataTexture;
		_myDestinationDataTexture = _myCurrentDataTexture;
		_myCurrentDataTexture = myTemp;
	}
	
	public void beginDrawCurrent(CCGraphics g){
		_myCurrentDataTexture.beginDraw(g);
	}
	
	public void beginDrawCurrent(CCGraphics g, int theAttachment){
		_myCurrentDataTexture.beginDraw(g, theAttachment);
	}
	
	public void beginDrawDestination(CCGraphics g){
		_myDestinationDataTexture.beginDraw(g);
	}
	
	public void beginDrawDestination(CCGraphics g, int theAttachment){
		_myDestinationDataTexture.beginDraw(g, theAttachment);
	}

	public void draw(CCGraphics g) {
		_myDestinationDataTexture.draw(g);
	}
	
	public void draw(CCGraphics g, double theX0, double theY0, double theX1, double theY1) {
		_myDestinationDataTexture.draw(g, theX0, theY0, theX1, theY1);
	}
	
	public void draw(CCGraphics g, CCAABoundingRectangle theRect) {
		_myDestinationDataTexture.draw(g, theRect);
	}
	
	public void endDrawCurrent(CCGraphics g){
		_myCurrentDataTexture.endDraw(g);
	}
	
	public void endDrawDestination(CCGraphics g){
		_myDestinationDataTexture.endDraw(g);
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

package cc.creativecomputing.graphics.shader;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;
import cc.creativecomputing.math.interpolate.CCInterpolatable;

/**
 * Use this class to easily put envelopes, gradients and splines into a texture 
 * to use it in a shader by interpolating between 0 and 1
 * @author christianr
 *
 */
public class CCDataBuffer extends CCShaderBuffer{

	private CCGLWriteDataShader _myWriteDataShader;
	
	private CCInterpolatable<?>[] _myInterpolatables;
	
	private double _myResolution;
	private int _myNumberOfChannels;

	public CCDataBuffer(int theResolution, int theNumberOfChannels, CCTextureTarget theTarget){
		super(theResolution, theNumberOfChannels, theTarget);
		attachment(0).textureFilter(CCTextureFilter.LINEAR);
		attachment(0).wrap(CCTextureWrap.CLAMP_TO_EDGE);

		_myResolution = theResolution;
		_myNumberOfChannels = theNumberOfChannels;
		_myWriteDataShader = new CCGLWriteDataShader();
		_myInterpolatables = new CCInterpolatable[theNumberOfChannels];
	}
	
	public CCDataBuffer(int theResolution, int theNumberOfChannels){
		this(theResolution, theNumberOfChannels, CCTextureTarget.TEXTURE_2D);
	}
	
	public CCDataBuffer(int theResolution, CCInterpolatable<?> ...theInterpolatables){
		this(theResolution, theInterpolatables.length);
		_myInterpolatables = theInterpolatables;
	}
	
	public void interpolable(int theChannel, CCInterpolatable<?> theInterpolatable){
		if(theChannel < 0 || theChannel >= _myInterpolatables.length )return;
		_myInterpolatables[theChannel] = theInterpolatable;
	}
	
	public void updateData(CCGraphics g){
		beginDraw(g);
		g.clear();
		g.pushAttribute();
		g.noBlend();
		g.pointSize(1);
		_myWriteDataShader.start();
		g.beginShape(CCDrawMode.POINTS);
		for(int y = 0; y < _myNumberOfChannels; y++){
			CCInterpolatable<?> myInterpolatable = _myInterpolatables[y];
			if(myInterpolatable == null)continue;
			Object myCheck = myInterpolatable.interpolate(0);
			if(myCheck instanceof Double){
				CCInterpolatable<Double> myTypedInterpolatable = (CCInterpolatable<Double>)myInterpolatable;
				
				for(int x = 0; x < _myResolution; x++){
					double blend = x / _myResolution;
					Double myVal = myTypedInterpolatable.interpolate(blend);
					g.textureCoords4D(0, myVal, myVal, myVal, 1d);
					g.vertex(x + 0.5, y + 1);
				}
				continue;
			}
			if(myCheck instanceof CCVector1){
				CCInterpolatable<CCVector1> myTypedInterpolatable = (CCInterpolatable<CCVector1>)myInterpolatable;
				
				for(int x = 0; x < _myResolution; x++){
					double blend = x / _myResolution;
					CCVector1 myVal = myTypedInterpolatable.interpolate(blend);
					g.textureCoords4D(0, myVal.x, myVal.x, myVal.x, 1d);
					g.vertex(x + 0.5, y + 1);
				}
				continue;
			}
			if(myCheck instanceof CCVector2){
				CCInterpolatable<CCVector2> myTypedInterpolatable = (CCInterpolatable<CCVector2>)myInterpolatable;
				
				for(int x = 0; x < _myResolution; x++){
					double blend = x / _myResolution;
					CCVector2 myVal = myTypedInterpolatable.interpolate(blend);
					g.textureCoords4D(0, myVal.x, myVal.y, 0d, 1d);
					g.vertex(x + 0.5, y + 1);
				}
				continue;
			}
			if(myCheck instanceof CCVector3){
				CCInterpolatable<CCVector3> myTypedInterpolatable = (CCInterpolatable<CCVector3>)myInterpolatable;
				
				for(int x = 0; x < _myResolution; x++){
					double blend = x / _myResolution;
					CCVector3 myVal = myTypedInterpolatable.interpolate(blend);
					g.textureCoords4D(0, myVal.x, myVal.y, myVal.z, 1d);
					g.vertex(x + 0.5, y + 1);
				}
				continue;
			}
			if(myCheck instanceof CCVector4){
				CCInterpolatable<CCVector4> myTypedInterpolatable = (CCInterpolatable<CCVector4>)myInterpolatable;
				
				for(int x = 0; x < _myResolution; x++){
					double blend = x / _myResolution;
					CCVector4 myVal = myTypedInterpolatable.interpolate(blend);
					g.textureCoords4D(0, myVal.x, myVal.y, myVal.z, myVal.w);
					g.vertex(x + 0.5, y + 1);
				}
				continue;
			}
			if(myCheck instanceof CCColor){
				CCInterpolatable<CCColor> myTypedInterpolatable = (CCInterpolatable<CCColor>)myInterpolatable;
				for(int x = 0; x < _myResolution; x++){
					double blend = x / _myResolution;
					CCColor myVal = myTypedInterpolatable.interpolate(blend);
					g.textureCoords4D(0, myVal.r, myVal.g, myVal.b, myVal.a);
					g.vertex(x + 0.5, y + 1);
				}
				continue;
			}
		}
		g.endShape();
		_myWriteDataShader.end();
		g.popAttribute();
		endDraw(g);
	}
}

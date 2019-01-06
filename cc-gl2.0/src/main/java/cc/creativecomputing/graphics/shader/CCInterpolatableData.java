package cc.creativecomputing.graphics.shader;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.interpolate.CCInterpolatable;

public class CCInterpolatableData {

	protected CCShaderBuffer _myEnvelopeData;

	protected CCGLWriteDataShader _mySetDataShader;
	
	protected List<CCInterpolatable<?>> _cInterpolatables = new ArrayList<>();
	
	public CCInterpolatableData(int theWidth, int theHeight, CCTextureTarget theTextureTarget) {
		_myEnvelopeData = new CCShaderBuffer(theWidth, theHeight, theTextureTarget);
		
		_mySetDataShader = new CCGLWriteDataShader();
	}
	
	public CCTexture2D texture() {
		return _myEnvelopeData.attachment(0);
	}
	
	public int add(CCInterpolatable<?> theInterpolatable) {
		_cInterpolatables.add(theInterpolatable);
		return _cInterpolatables.size() - 1;
	}
	
	public void preDisplay(CCGraphics g) {
		_myEnvelopeData.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		_myEnvelopeData.attachment(0).wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myEnvelopeData.beginDraw(g);
		g.clear();
		_mySetDataShader.start();
		g.beginShape(CCDrawMode.POINTS);
		int y = 0;
		for(CCInterpolatable<?> myInterpolatable :_cInterpolatables){
			if(myInterpolatable instanceof CCEnvelope) {
				CCEnvelope myEnvelope = (CCEnvelope)myInterpolatable;
				for(double x = 0; x < _myEnvelopeData.width(); x++){
					double myVal = myEnvelope.interpolate(x / _myEnvelopeData.width());
					g.textureCoords4D(0, myVal, myVal, myVal, 1d);
					g.vertex(x + 0.5, y + 0.5);
				}
			}
			if(myInterpolatable instanceof CCGradient) {
				CCGradient myGradient = (CCGradient)myInterpolatable;
				for(double x = 0; x < _myEnvelopeData.width(); x++){
					CCColor myVal = myGradient.interpolate(x / _myEnvelopeData.width());
					g.textureCoords4D(0, myVal.r, myVal.g, myVal.b, myVal.a);
					g.vertex(x + 0.5, y + 0.5);
				}
			}
			y++;
		}
		g.endShape();
		_mySetDataShader.end();
		_myEnvelopeData.endDraw(g);
	}
}

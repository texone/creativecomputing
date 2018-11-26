package cc.creativecomputing.simulation.particles;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.interpolate.CCInterpolatable;

public class CCParticleEnvelopeData {

	protected CCShaderBuffer _myEnvelopeData;

	protected CCGLWriteDataShader _mySetDataShader;
	
	protected List<CCInterpolatable<?>> _cInterpolatables = new ArrayList<>();
	
	public CCParticleEnvelopeData(int theWidth, int theHeight) {
		_myEnvelopeData = new CCShaderBuffer(theWidth, theHeight + 1);
		
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
		_myEnvelopeData.beginDraw(g);
		g.clear();
		_mySetDataShader.start();
		g.beginShape(CCDrawMode.POINTS);
		int y = 0;
		for(CCInterpolatable<?> myInterpolatable :_cInterpolatables){
			if(myInterpolatable instanceof CCEnvelope) {
				CCEnvelope myEnvelope = (CCEnvelope)myInterpolatable;
				for(int i = 0; i < 100; i++){
					double myVal = myEnvelope.interpolate(i / 100d);
					g.textureCoords4D(0, myVal, myVal, myVal, 1d);
					g.vertex(i + 0.5, y + 0.5);
				}
			}
			
			y++;
		}
		g.endShape();
		_mySetDataShader.end();
		_myEnvelopeData.endDraw(g);
	}
}

package cc.creativecomputing.graphics.scene.state;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLDestinationBlendFunction;
import cc.creativecomputing.gl4.GLGraphics.GLSourceBlendFunction;
import cc.creativecomputing.math.CCColor;

public class CCAlphaState extends CCGlobalState {
	
	public static final CCAlphaState DEFAULT = new CCAlphaState();

	protected CCAlphaState() {
		super(CCGlobalStateType.ALPHA);
	}
	
	/**
	 * default: false
	 */
	public boolean blendEnabled = false;
	
	/**
	 * default : {@linkplain GLSourceBlendFunction#SRC_ALPHA}
	 */
	public GLSourceBlendFunction sourceBlend = GLSourceBlendFunction.SRC_ALPHA;
	
	/**
	 * default : {@linkplain GLDestinationBlendFunction#ONE_MINUS_SRC_ALPHA}
	 */
	public GLDestinationBlendFunction destinationBlend = GLDestinationBlendFunction.ONE_MINUS_SRC_ALPHA;
	
	/**
	 * default: (0,0,0,0)
	 */
    public CCColor constantColor = new CCColor(0,0,0,0);  
	
	@Override
	public void draw(GLGraphics g) {
		if (blendEnabled){
			g.blend();
			g.blendFunc(sourceBlend, destinationBlend);
			g.blendColor(constantColor);
	    }else{
	        g.noBlend();
	    }
	}

}

package cc.creativecomputing.simulation.fluid;

import java.nio.file.Path;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCVector3;

public class CCFluidDisplay extends CCGLProgram{

	public CCVector3 bias = new CCVector3(0,0,0);
	public CCVector3 scale = new CCVector3(0,0,0);
	
	public CCFluidDisplay(Path theFragment){
		super(
			null,//CCNIOUtil.classPath(CCFluidDisplay.class, "basic.vs"),
			theFragment
		);
	}
	
	// set bias and scale for including range of negative values
    public void scaleNegative() {
        double v = 0.5;
        this.bias.set(v, v, v);
        this.scale.set(v, v, v);
    }

    public void display(CCGraphics g, CCTexture2D read, int theX, int theY, int theWidth, int theHeight) {
    	start();
    	uniform1i("read", 0);
    	uniform3f("bias", bias);
    	uniform3f("scale", scale);
    	g.image(read,theX, theY, theWidth, theHeight);
    	end();
    }
}

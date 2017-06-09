package cc.creativecomputing.simulation.fluid;

import java.nio.file.Path;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCGLSwapBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;

public class CCFluidJacobi extends CCFluidOperation{
	
	private final CCFluidGrid _myGrid;
	
	private int iterations = 50;
	
	public double alpha = -1;
	public double beta = 4;
	
	public CCFluidJacobi(CCFluidGrid theGrid, Path thePath){
		super(thePath);
		_myGrid = theGrid;
	}

	public void step(CCGraphics g, CCTexture2D x, CCTexture2D b, CCGLSwapBuffer theOutput){
		g.texture(0,x);
        g.texture(1,b);
        start();
        uniform1i("x", 0);
        uniform1i("b", 1);
        uniform2f("gridSize", _myGrid.size);
        uniform1f("alpha", alpha);
        uniform1f("beta", beta);
        theOutput.draw(g);
        end();
        g.noTexture();
        theOutput.swap();
	}

	public void compute(CCGraphics g, CCTexture2D x, CCTexture2D b, CCGLSwapBuffer theOutput){
		for (int i = 0; i < this.iterations; i++) {
            step(g, x, b, theOutput);
        }
	}
}

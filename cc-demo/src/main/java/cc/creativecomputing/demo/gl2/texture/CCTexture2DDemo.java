package cc.creativecomputing.demo.gl2.texture;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCTexture2DDemo extends CCGLApp{
	
	private CCTexture2D _myTexture;
	
	@Override
	public void setup() {
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/waltz.dds")));
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho2D();
		g.image(_myTexture, 0, 0);
	}

	public static void main(String[] args) {
		CCTexture2DDemo myDemo = new CCTexture2DDemo();
		
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}

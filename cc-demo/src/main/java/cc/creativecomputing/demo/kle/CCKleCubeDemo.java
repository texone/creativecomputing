package cc.creativecomputing.demo.kle;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCEffectables;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCMatrixMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCKleCubeDemo extends CCGL2Adapter{
	
	private double texheight = 16 * 12;
	private double texwidth = 9 * 28 * 12;
	
	
	private class CCCubeEffectable extends CCEffectable{
		
		private CCVector2 _myCenter;
		

		private double _myAlpha;
		private double _myScale;
		private double _myRotation;
		
		

		public CCCubeEffectable(int theId, CCVector2 theCenter) {
			super(theId);
			_myCenter = theCenter;
		}
		
		@Override
		public void apply(double...theValues) {
			
			_myAlpha = theValues[0];
			_myScale = theValues[1] * 2;
			_myRotation = (theValues[2] - 0.5) * 360;
		}
		
		public void box(CCGraphics g, double theXSize, double theYSize, double theZSize){
			final double x1 = -theXSize / 2f;
			final double x2 = theXSize / 2f;
			final double y1 = -theYSize / 2f;
			final double y2 = theYSize / 2f;
			final double z1 = -theZSize / 2f;
			final double z2 = theZSize / 2f;

			g.beginShape(CCDrawMode.QUADS);

			g.gl.glMultiTexCoord2d(0,(y2) / texwidth,(z1) / texheight);
			g.vertex(0, y2, z2);
			g.gl.glMultiTexCoord2d(0,(y1) / texwidth,(z1) / texheight);
			g.vertex(0, y2, z1);
			g.gl.glMultiTexCoord2d(0,(y1) / texwidth,(z2) / texheight);
			g.vertex(0, y1, z1);
			g.gl.glMultiTexCoord2d(0,(y2) / texwidth,(z2) / texheight);
			g.vertex(0, y1, z2);


			g.endShape();
		}
		
		
		
		public void draw(CCGraphics g){
			
			g.color(CCMath.pow( _myAlpha,_cColorPow),CCMath.pow( _myAlpha,_cAlphaPow));
			
			g.pushMatrix();
			g.translate(_myCenter);
//			g.scale(_myScale);
//			CCLog.info(_myRotateX+ ":" + _myRotateY + ":" + _myRotateZ);
			g.rotateX(90);
			g.rotateY(90);
			g.rotateZ(90);
			
			g.matrixMode(CCMatrixMode.TEXTURE);
			g.pushMatrix();
			g.translate(_myCenter.x / texwidth, _myCenter.y / texheight);
			g.scale(_myScale);
			g.rotate(_myRotation);
			box(g,12,12,12);
			g.popMatrix();
			
			g.matrixMode(CCMatrixMode.MODELVIEW);
			g.popMatrix();
		}
	}
	
	
	
	
	
	
	private CCEffectables<CCCubeEffectable> _myCubes = new CCEffectables<>();
	
	@CCProperty(name = "alpha pow", min = 0, max = 1)
	private double _cAlphaPow = 1;
	@CCProperty(name = "color pow", min = 0, max = 1)
	private double _cColorPow = 1;
	
	@CCProperty(name = "effects")
	private CCEffectManager<CCCubeEffectable> _myEffectManager;
	
	@CCProperty(name = "screener")
	private CCScreenCaptureController _myScreenCaptureController;

	private CCTexture2D[] _myTextures = new CCTexture2D[6];
	@Override
	public void init( CCGraphics g, CCAnimator theAnimator) {
		int i = 0;
		for(int c = 0; c < 9 * 28; c++){
			for(int r = 0; r < 16; r++){
				CCCubeEffectable myCube = new CCCubeEffectable(i, new CCVector2(c * 12 + 6 , r * 12 + 6));
				myCube.column(c);
				myCube.row(r);
				_myCubes.add(myCube);
				i++;
			}
		}
		_myCubes.addIdSources(CCEffectable.COLUMN_SOURCE, CCEffectable.ROW_SOURCE);
		
		_myEffectManager = new CCEffectManager<CCCubeEffectable>(_myCubes, "x", "y", "z");
		
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("offset2", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());

//		_myTextures[0] = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("yo.png")));
//		_myTextures[1] = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("insta01.png")));
//		_myTextures[2] = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("3d/03.png")));
//		_myTextures[3] = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("3d/04.png")));
//		_myTextures[4] = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("3d/05.png")));
//		_myTextures[5] = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("3d/06.png")));
		
		_myScreenCaptureController = new CCScreenCaptureController(this, theAnimator);
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.ortho();
		
//		for(int i = 0; i < _myTextures.length;i++){
//			g.texture(i,_myTextures[i]);
//		}
		g.color(1f);
//		g.image(_myTextures[1], 0,0);
//		g.noDepthTest();
//		_myTextures[1].textureFilter(CCTextureFilter.LINEAR);
//		g.texture(0,_myTextures[0]);
		for(CCCubeEffectable myCube:_myCubes){
			myCube.draw(g);
		}
//		g.noTexture();
		
	}
	
	public static void main(String[] args) {
		
		
		CCKleCubeDemo demo = new CCKleCubeDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1920, 1080);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

package cc.creativecomputing.demo.kle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;
import cc.creativecomputing.math.easing.CCEasing.CCEaseMode;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;

public class CCEffectQuadDemo extends CCGL2Adapter {
	
	private class CCScreenEffectable extends CCEffectable implements Comparable<CCScreenEffectable>{
		
		private int _myColumn;
		private int _myRow;
		
		private double _myYAnimation;
		
		private double _myY;
		private double _myConstrainedY;
		
		private CCColor _myColor;
		
		private double _myo0;
		private double _myo1;

		public CCScreenEffectable(int theId, int theColumn, int theRow, CCColor theColor) {
			super(theId);
			_myColumn = theColumn;
			_myRow = theRow;
			_myColor = _merckCols[(int)CCMath.random(_merckCols.length)];
		}
		
	
		
		@Override
		public void apply(double... theValues) {
			_myYAnimation = theValues[0] * _cYMax;

			_myY = rowBlend() * (_cScreenHeight + _cScreenSpace) * (rows - 1) * 0.5 + _myYAnimation;
			
			_myo0 = theValues[1];
			_myo1 = theValues[2];
		}
		
		public void drawContent(CCGraphics g){
			double _myX0 = columnBlend() * (_cScreenWidth + _cScreenSpace) * (columns - 1) * 0.5;
			double _myY0 = _myY;
			double _myX1 = _myX0 + _cScreenWidth;
			double _myY1 = _myY0 + _cScreenHeight;
			
			
			g.color(_myColor);
			g.vertex(_myX0, _myY0);
			g.vertex(_myX1, _myY0);
			g.vertex(_myX1, _myY1);
			g.vertex(_myX0, _myY1);
		}
		
		public void drawLine(CCGraphics g, int i){
			double _myX0 = columnBlend() * (_cScreenWidth + _cScreenSpace) * (columns - 1) * 0.5;
			double _myX1 = _myX0 + _cScreenWidth;
			
			CCColor myColor = CCColor.createFromHSB(
				_cColors[_myRow].hue() + _myo0, 
				_cColors[_myRow].saturation(), 
				_cColors[_myRow].brightness(), 
				_cColors[_myRow].alpha()
			);
			g.color(myColor);
			g.vertex((_myX0 + _myX1) / 2, _myY + _cScreenHeight / 2 + i * 3 * _myo1);
		}
		
		public CCVector3 getLineVertex(){
			double _myX0 = columnBlend() * (_cScreenWidth + _cScreenSpace) * (columns - 1) * 0.5;
			double _myX1 = _myX0 + _cScreenWidth;
			return new CCVector3((_myX0 + _myX1) / 2, _myY + _cScreenHeight / 2);
		}
		
		public void drawScreen(CCGraphics g){
			double _myX0 = columnBlend() * (_cScreenWidth + _cScreenSpace) * (columns - 1) * 0.5;
			double _myY0 = _myConstrainedY;
			double _myX1 = _myX0 + _cScreenWidth;
			double _myY1 = _myY0 + _cScreenHeight;
			
			g.vertex(_myX0, _myY0);
			g.vertex(_myX1, _myY0);
			g.vertex(_myX1, _myY1);
			g.vertex(_myX0, _myY1);
		}
		
		public void drawTextured(CCGraphics g){
			double _myX0 = columnBlend() * (_cScreenWidth + _cScreenSpace) * (columns - 1) * 0.5;
			double _myY0 = _myConstrainedY;
			double _myX1 = _myX0 + _cScreenWidth;
			double _myY1 = _myY0 + _cScreenHeight;
			
			double _myTX0 = CCMath.norm(_myX0, _cMinTexX, _cMaxTexX);
			double _myTX1 = CCMath.norm(_myX1, _cMinTexX, _cMaxTexX);
			double _myTY0 = CCMath.norm(_myY0, _cMinTexY, _cMaxTexY);
			double _myTY1 = CCMath.norm(_myY1, _cMinTexY, _cMaxTexY);
			
			g.textureCoords2D(_myTX0 + _myo0, _myTY0 + _myo1);
			g.vertex(_myX0, _myY0);
			g.textureCoords2D(_myTX1 + _myo0, _myTY0 + _myo1);
			g.vertex(_myX1, _myY0);
			g.textureCoords2D(_myTX1 + _myo0, _myTY1 + _myo1);
			g.vertex(_myX1, _myY1);
			g.textureCoords2D(_myTX0 + _myo0, _myTY1 + _myo1);
			g.vertex(_myX0, _myY1);
		}
		
		public void drawFace(CCGraphics g){
			double _myX0 = columnBlend() * (_cScreenWidth + _cScreenSpace) * (columns - 1) * 0.5;
			double _myY0 = _myConstrainedY;
			double _myX1 = _myX0 + _cScreenWidth;
			double _myY1 = _myY0 + _cScreenHeight;
			
			double myZoom = _cTexZoom * _myo1;
			g.textureCoords3D(myZoom, myZoom, _myo0);
			g.vertex(_myX0, _myY0);
			g.textureCoords3D(1 - myZoom, myZoom, _myo0);
			g.vertex(_myX1, _myY0);
			g.textureCoords3D(1 - myZoom, 1 - myZoom, _myo0);
			g.vertex(_myX1, _myY1);
			g.textureCoords3D(myZoom, 1 - myZoom, _myo0);
			g.vertex(_myX0, _myY1);
		}

		@Override
		public int compareTo(CCScreenEffectable o) {
			// TODO Auto-generated method stub
			return Double.compare(_myY, o._myY);
		}
	}
	@CCProperty(name = "_cTexZoom", min = 0, max = 1)
	private double _cTexZoom = 0;
	@CCProperty(name = "ease formular")
	private CCEaseFormular _cEaseFormular = CCEaseFormular.SINE;
	@CCProperty(name = "ease mode")
	private CCEaseMode _cEaseMode = CCEaseMode.IN_OUT;
	
	@CCProperty(name = "min tex X", min = -500, max = 500)
	private double _cMinTexX = 0;
	@CCProperty(name = "min tex Y", min = -500, max = 500)
	private double _cMinTexY = 0;
	@CCProperty(name = "max tex X", min = -500, max = 1500)
	private double _cMaxTexX = 0;
	@CCProperty(name = "max tex Y", min = -500, max = 500)
	private double _cMaxTexY = 0;
	
	@CCProperty(name = "color 0")
	private CCColor _cColor0 = new CCColor();
	@CCProperty(name = "color 1")
	private CCColor _cColor1 = new CCColor();
	@CCProperty(name = "color 2")
	private CCColor _cColor2 = new CCColor();
	
	private CCColor[] _cColors = new CCColor[]{_cColor0, _cColor1, _cColor2};
	
	private CCTexture3D _myFaceTexture;
	private Path _myFolder = CCNIOUtil.dataPath("videos/facecheck3");
	
	private class CCScreenColumn{
		private List<CCScreenEffectable> _myScreens;
		
		public CCScreenColumn(){
			_myScreens = new ArrayList<>();
		}
		
		public void constrain(){
			Collections.sort(_myScreens);
			
			CCScreenEffectable myScreen0 = _myScreens.get(0);
			CCScreenEffectable myScreen1 = _myScreens.get(1);
			CCScreenEffectable myScreen2 = _myScreens.get(2);
			
			double myDist = _cScreenHeight + _cScreenSpace;
			double myEaseDist = myDist + _cEaseDistance;
			
			myScreen0._myConstrainedY = myScreen0._myY;
			myScreen1._myConstrainedY = myScreen1._myY;
			myScreen2._myConstrainedY = myScreen2._myY;
					
			if(_cEaseDistance == 0){
				if(myScreen0._myY > myScreen1._myY - myDist){
					double myConstraint = myScreen1._myY - myDist;
					myScreen0._myConstrainedY = myConstraint;
				}
				if(myScreen2._myY < myScreen1._myY + myDist){
					double myConstraint = myScreen1._myY + myDist;
					myScreen2._myConstrainedY = myConstraint;
				}
			}else{
				if(myScreen0._myY > myScreen1._myY - myEaseDist){
					double myConstraint = myScreen1._myY - myDist;
					double myBlend = CCMath.norm(myScreen0._myY, myScreen1._myY - myEaseDist, myScreen1._myY - myDist);
					myBlend = CCMath.saturate(myBlend);
					myScreen0._myConstrainedY = _cEaseFormular.easing().ease(_cEaseMode, myScreen0._myY, myConstraint, myBlend);
				
				}
				if(myScreen2._myY < myScreen1._myY + myEaseDist){
					double myConstraint = myScreen1._myY + myDist;
					double myBlend = CCMath.norm(myScreen2._myY, myScreen1._myY + myEaseDist, myScreen1._myY + myDist);
					myBlend = CCMath.saturate(myBlend);
					myScreen2._myConstrainedY = _cEaseFormular.easing().ease(_cEaseMode, myScreen2._myY, myConstraint, myBlend);
				}
			}
			
			
		}
		
		
	}
	
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "y max", min = 0, max = 200)
	private double _cYMax = 20;
	
	
	@CCProperty(name = "blend mode")
	private CCBlendMode _cBlendMode = CCBlendMode.BLEND;
	@CCProperty(name = "depth Test")
	private boolean _cDepthTest = true;
	@CCProperty(name = "easeDistance", min = 0, max = 100)
	private double _cEaseDistance = 0;
	
	private List<CCScreenEffectable> _myScreens = new ArrayList<>();
	
	private List<CCScreenColumn> _myScreenColumns = new ArrayList<>();

	@CCProperty(name = "effects")
	private CCEffectManager<CCScreenEffectable> _myEffectManager;
	
	@CCProperty(name = "screen space", min = 0, max = 100)
	private double _cScreenSpace = 10;

	@CCProperty(name = "screen width", min = 10, max = 500)
	private double _cScreenWidth = 10;
	@CCProperty(name = "screen height", min = 10, max = 500)
	private double _cScreenHeight = 10;
	
	int columns = 25;
	int rows = 3;
	
	private CCTexture2D _myTexture;
	
	private CCTexture2D _myBandTexture;
	
	@CCProperty(name = "screencapture controller")
	private CCScreenCaptureController _myScreenCaptureController;

	private CCColor[] _merckCols = new CCColor[]{
		CCColor.parseFromInteger(0x52328f),
		CCColor.parseFromInteger(0x0068b0),
		CCColor.parseFromInteger(0x00995c),
		CCColor.parseFromInteger(0xe61e50),
		CCColor.parseFromInteger(0xeb4296),
		CCColor.parseFromInteger(0x28becd),
		CCColor.parseFromInteger(0xa6cd4d),
		CCColor.parseFromInteger(0xffca30),
		CCColor.parseFromInteger(0xe0c2cb),
		CCColor.parseFromInteger(0x98d5d1),
		CCColor.parseFromInteger(0xc4dfa7),
		CCColor.parseFromInteger(0xfedbb6),
	};
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCameraController = new CCCameraController(this, g, 100);
		
		int id = 0;
		for(int x = 0; x < columns;x++){
			CCScreenColumn myScreenColumn = new CCScreenColumn();
			for(int y = 0; y < rows;y++){
				CCScreenEffectable myScreen0 = new CCScreenEffectable(id++, x, y, _cColors[y]);
				myScreen0.column(x);
				myScreen0.row(y);
				_myScreens.add(myScreen0);
				myScreenColumn._myScreens.add(myScreen0);
			}
			_myScreenColumns.add(myScreenColumn);
		}
		
		
		_myEffectManager = new CCEffectManager<CCScreenEffectable>(_myScreens, "y", "o0", "o1");
		_myEffectManager.addIdSources(CCEffectable.COLUMN_SOURCE, CCEffectable.ROW_SOURCE);
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
		
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/Clouds.jpg")));
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		
		List<Path> myFiles = CCNIOUtil.list(_myFolder, "png");
		_myFaceTexture = new CCTexture3D(CCImageIO.newImage(myFiles.get(0)), myFiles.size());
		_myFaceTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myFaceTexture.textureFilter(CCTextureFilter.LINEAR);
		
		int i = 0;
		for(Path myPath:myFiles) {
			_myFaceTexture.updateData(CCImageIO.newImage(myPath), i++);
		}
		
		_myBandTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/band01.png")));
		_myBandTexture.wrap(CCTextureWrap.REPEAT);
		_myBandTexture.textureFilter(CCTextureFilter.LINEAR);
		
		_myScreenCaptureController = new CCScreenCaptureController(this);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_cColors = new CCColor[]{_cColor0, _cColor1, _cColor2};
		_myEffectManager.update(theAnimator);
		for(CCScreenColumn myColumn:_myScreenColumns){
			myColumn.constrain();
		}
	}
	
	public static enum CCScreenDraw{
		LINE, QUAD, TEXTURE, FACE
	}
	
	@CCProperty(name = "screen draw")
	private CCScreenDraw _cScreenDraw = CCScreenDraw.TEXTURE;
	
	@CCProperty(name = "line attributes")
	private CCDrawAttributes _cLineAttributes = new CCDrawAttributes();
	@CCProperty(name = "quad attributes")
	private CCDrawAttributes _cQuadAttributes = new CCDrawAttributes();
	@CCProperty(name = "texture attributes")
	private CCDrawAttributes _cTextureAttributes = new CCDrawAttributes();

	@CCProperty(name = "band width", min = 0, max = 30)
	private double _cBandWitdth = 10;
	@CCProperty(name = "band scale", min = 0, max = 30)
	private double _cBandScale = 10;
	
	@CCProperty(name = "screen color")
	private CCColor _cScreenColor = new CCColor();
	@CCProperty(name = "clear color")
	private CCColor _cClearColor = new CCColor();
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(_cClearColor);
		g.clear();
		
		_myCameraController.camera().draw(g);
		
		g.polygonMode(CCPolygonMode.FILL);
		g.color(_cScreenColor);
		g.beginShape(CCDrawMode.QUADS);
		for(CCScreenEffectable myScreen:_myScreens){
			myScreen.drawScreen(g);
		}
		g.endShape();
		
		g.blend(_cBlendMode);
		if(_cDepthTest){
			g.depthTest();
		}else{
			g.noDepthTest();
		}
		
		switch(_cScreenDraw){
		case LINE:
			_cLineAttributes.start(g);
			g.beginMask();
			g.polygonMode(CCPolygonMode.FILL);
			g.color(1d);
			g.beginShape(CCDrawMode.QUADS);
			for(CCScreenEffectable myScreen:_myScreens){
				myScreen.drawScreen(g);
			}
			g.endShape();
			g.endMask();
			g.polygonMode(CCPolygonMode.FILL);
			for(int y = 0; y < rows;y++){
				CCCatmulRomSpline mySpline = new CCCatmulRomSpline(0.5, false);
				
					
					mySpline.beginEditSpline();
					for(int x = 0; x < columns;x++){
						int id = x * 3 + y;
						mySpline.addPoint(_myScreens.get(id).getLineVertex());
					}
					mySpline.endEditSpline();
					g.color(1d);
					g.texture(_myBandTexture);
					g.beginShape(CCDrawMode.TRIANGLE_STRIP);
					for(int j = 0; j < 400;j++){
						CCVector3 my1 = mySpline.interpolate(j / 399d);
						CCVector3 my2 = mySpline.interpolate((j + 1d) / 399d);
						
						CCVector3 myNormal = new CCVector3(my2.y - my1.y,-(my2.x - my1.x)).normalizeLocal();
					
						g.textureCoords2D(j / 399d * _cBandScale, 0d);
						g.vertex(my1.add(myNormal.multiply(_cBandWitdth)));
						g.textureCoords2D(j / 399d * _cBandScale, 1d);
						g.vertex(my1.add(myNormal.multiply(-_cBandScale)));
					}
					g.endShape();
					g.noTexture();
				
			}
			g.noMask();
			_cLineAttributes.end(g);
			break;
		case QUAD:
			_cQuadAttributes.start(g);
			g.beginMask();
			g.polygonMode(CCPolygonMode.FILL);
			g.color(1d);
			g.beginShape(CCDrawMode.QUADS);
			for(CCScreenEffectable myScreen:_myScreens){
				myScreen.drawScreen(g);
			}
			g.endShape();
			g.endMask();
			g.polygonMode(CCPolygonMode.FILL);
			g.color(1d, 0.25d);
			g.beginShape(CCDrawMode.QUADS);
			for(CCScreenEffectable myScreen:_myScreens){
				myScreen.drawContent(g);
			}
			g.endShape();
			_cQuadAttributes.end(g);
			break;
		case TEXTURE:
			_cTextureAttributes.start(g);
			g.texture(_myTexture);
			g.polygonMode(CCPolygonMode.FILL);
			g.color(1d);
			g.beginShape(CCDrawMode.QUADS);
			for(CCScreenEffectable myScreen:_myScreens){
				myScreen.drawTextured(g);
			}
			g.endShape();
			g.noTexture();
			_cTextureAttributes.end(g);
			break;
		case FACE:
			_cTextureAttributes.start(g);
			g.texture(_myFaceTexture);
			g.polygonMode(CCPolygonMode.FILL);
			g.color(1d);
			g.beginShape(CCDrawMode.QUADS);
			for(CCScreenEffectable myScreen:_myScreens){
				myScreen.drawFace(g);
			}
			g.endShape();
			g.noTexture();
			_cTextureAttributes.end(g);
			break;
		}
		
		g.polygonMode(CCPolygonMode.LINE);
		g.color(1d);
		g.beginShape(CCDrawMode.QUADS);
		for(CCScreenEffectable myScreen:_myScreens){
			myScreen.drawScreen(g);
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCEffectQuadDemo demo = new CCEffectQuadDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

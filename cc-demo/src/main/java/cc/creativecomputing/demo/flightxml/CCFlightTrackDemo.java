package cc.creativecomputing.demo.flightxml;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.util.CCStopWatch;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.flightXML.CCArrivalFlightStruct;
import cc.creativecomputing.flightXML.CCFlightXML;
import cc.creativecomputing.flightXML.CCTrackStruct;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCFlightTrackDemo extends CCGL2Adapter{
	
	
	

	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	private List<CCArrivalFlightStruct> _myFlights = new ArrayList<>();
	private List<List<CCTrackStruct>> _myTracks = new ArrayList<>();
	
	@CCProperty(name = "xy scale", min = 1, max = 100)
	private double _cXYScale = 1;
	
	@CCProperty(name = "z scale", min = 0, max = 1)
	private double _cZScale = 0;
	
	@CCProperty(name = "color")
	private CCColor _cColor = new CCColor();
	
	@CCProperty(name = "polygon mode")
	private CCDrawMode _cDrawMode = CCDrawMode.LINES;
	
	@CCProperty(name = "draw indexed")
	private boolean _cDrawIndexd = false;
	
	@CCProperty(name = "draw index")
	private int _cIndex = 0;
	
	@CCProperty(name = "trace head color")
	private CCColor _myTraceHeadColor = new CCColor();
	@CCProperty(name = "trace slow color")
	private CCColor _myTraceSlowColor = new CCColor();

	@CCProperty(name = "trace duration", min = 0, max = 1)
	private double _cTraceDuration = 1;
	@CCProperty(name = "trace fade", min = 0, max = 100)
	private double _cTraceFade = 1;
	@CCProperty(name = "clouds alpha", min = 0, max = 1)
	private double _cCloudsAlpha = 1;
	@CCProperty(name = "clouds z", min = 0, max = 1000)
	private double _cCloudsZ = 1;
	@CCProperty(name = "map alpha", min = 0, max = 1)
	private double _cMapAlpha = 1;
	@CCProperty(name = "stroke weight", min = 0.1, max = 10)
	private double _cStrokeWeight = 1;


	@CCProperty(name = "speed", min = 0, max = 1)
	public double _cSpeed = 0;
	
	private CCVBOMesh _myMesh;
	private CCGLProgram _myShader;
	

	private CCTexture2D _myDiffuseMap;
	private CCTexture2D _myClouds;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		
		
		_myCameraController = new CCCameraController(this, g, 100);
		
		String myUser = "ChristianRiekoff";
		String myKey = "663d8349f2144f6dc63788b5a7445cc6a5769eef";
		
		CCFlightXML myFlightXML = new CCFlightXML(myUser, myKey);
		myFlightXML.setMaximumResultSize(5000);
		
		
		
//		myFlightXML.historicalTrack("DLH1198-1461735014-airline-0136:2");
		
		int myVertices = 0;
		List<CCArrivalFlightStruct> myArrivedFlights = myFlightXML.arrived("LSZH");//LSZH//EDDF
		for(CCArrivalFlightStruct myFlight:myArrivedFlights){
			List<CCTrackStruct> myTracks = myFlightXML.lastTrack(myFlight.ident);
			int mySize = myTracks.size();
			if(mySize > 0){
				myVertices += (mySize - 1) * 2;
			}
		}
		_myMesh = new CCVBOMesh(CCDrawMode.LINES, myVertices);
		
		for(CCArrivalFlightStruct myFlight:myArrivedFlights){
			List<CCTrackStruct> myTracks = myFlightXML.lastTrack(myFlight.ident);
			fixTrack(myTracks);
			int mySize = myTracks.size();
			if(mySize > 0){
				for(int i = 0; i < myTracks.size() - 1; i++){
					CCTrackStruct myPoint0 = myTracks.get(i);
					CCTrackStruct myPoint1 = myTracks.get(i+1);

					double difLon = CCMath.abs(myPoint1.longitude - myPoint0.longitude);
					double difLat = CCMath.abs(myPoint1.latitude - myPoint0.latitude);
					if(difLat > 0.3 || difLon > 0.3)CCLog.info(i + ":" + myFlight.ident + ":" + myFlight.originName + ":" + difLat + ":" + difLon);
					if(myPoint0.updateType.equals("TP") && myPoint1.updateType.equals("TP")){
//						myPoint.altitude = myLastAltitude;
					}
					
					_myMesh.addVertex(myPoint0.longitude, myPoint0.latitude, myPoint0.altitude,myPoint0.timestamp.dayProgress());
					_myMesh.addVertex(myPoint1.longitude, myPoint1.latitude, myPoint1.altitude,myPoint1.timestamp.dayProgress());
					System.out.println(myPoint0.timestamp.dayProgress());
//					_myMesh.addColor(CCColor.createFromHSB(myPoint0.timestamp.dayProgress(), 1d, 1d));
//					_myMesh.addColor(CCColor.createFromHSB(myPoint1.timestamp.dayProgress(), 1d, 1d));
				}
				_myTracks.add(myTracks);
				_myFlights.add(myFlight);
				CCLog.info(myFlight.ident + ":" + mySize + ":" + myFlight.originCity);
			}
		}
		
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(this,"stream_map.glsl"), 
			CCNIOUtil.classPath(this,"stream_frag.glsl")
		);

		_myDiffuseMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("earth/world_16384.jpg")));
		_myDiffuseMap.anisotropicFiltering(1);
		_myDiffuseMap.textureFilter(CCTextureFilter.LINEAR);
		_myDiffuseMap.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		_myDiffuseMap.generateMipmaps(true);
		_myClouds = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("earth/cloud8192.jpg")));
		_myClouds.anisotropicFiltering(1);
		_myClouds.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		_myClouds.textureFilter(CCTextureFilter.LINEAR);
		_myClouds.generateMipmaps(true);
	}
	
	private void fixTrack(List<CCTrackStruct> theTrack){
		int myLastAltitude = 0;
		for(CCTrackStruct myPoint:theTrack){
			if(myPoint.updateType.equals("TP")){
				myPoint.altitude = myLastAltitude;
			}else{
				myLastAltitude = myPoint.altitude;
			}
		}
	}

	@CCProperty(name = "offset", min = 0, max = 10)
	private float _myOffset = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
//		_myGlobe.update(theAnimator.deltaTime());
	}
	
//	private void printCurrentTrack(){
//		if(_cIndex >= 0 && _cIndex < _myTracks.size()){
//			g.beginShape(_cDrawMode);
//			for(CCTrackStruct myPosition:_myTracks.get(_cIndex)){
////				CCLog.info(myPosition.altitude);
//				g.vertex(myPosition.longitude, myPosition.latitude, myPosition.altitude );
//			}
//			g.endShape();
//		}
//	}
	
	
	@Override
	public void display(CCGraphics g) {
		
		g.clear();
		g.color(_cColor);
		
		_myCameraController.camera().draw(g);
		g.noDepthTest();
		g.pushMatrix();
		g.scale(_cXYScale, _cXYScale, _cZScale);
		g.translate(-8.549167, -47.464722);
		
		if(_cDrawIndexd){
			if(_cIndex >= 0 && _cIndex < _myTracks.size()){
				g.beginShape(_cDrawMode);
				for(CCTrackStruct myPosition:_myTracks.get(_cIndex)){
//					CCLog.info(myPosition.altitude);
					g.vertex(myPosition.longitude, myPosition.latitude, myPosition.altitude );
				}
				g.endShape();
				
				g.color(1);
				g.text(_myFlights.get(_cIndex).ident,0,0);
				System.out.println(_myFlights.get(_cIndex).ident);
			}
		}else{
			g.color(1,_cMapAlpha);
			g.image(_myDiffuseMap, -180, -90, 360, 180);

			g.strokeWeight(_cStrokeWeight);
			g.blend(CCBlendMode.ADD);
			_myShader.start();

			_myShader.uniform1f("currentTime", _cSpeed);
			_myShader.uniform1f("positionPow", _cTraceFade);
			_myShader.uniform1f("positionRange", _cTraceDuration);

			_myShader.uniform4f(
				"positionMaxColor", 
				_myTraceHeadColor.r, 
				_myTraceHeadColor.g, 
				_myTraceHeadColor.b, 
				_myTraceHeadColor.a * 1
			);
			_myShader.uniform4f(
				"positionMinColor", 
				_myTraceSlowColor.r, 
				_myTraceSlowColor.g, 
				_myTraceSlowColor.b, 
				_myTraceSlowColor.a * 1
			);
			_myMesh.drawMode(_cDrawMode);
			_myMesh.draw(g);
			_myShader.end();
			

			g.blend(CCBlendMode.ADD);
			g.color(_cCloudsAlpha);
			g.pushMatrix();
			g.translate(0,0,_cCloudsZ);
			g.image(_myClouds, -180, -90, 360, 180);
			g.popMatrix();
		}
//		for(List<CCTrackStruct> myTrack:_myTracks){
//			
//		}
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		CCFlightTrackDemo demo = new CCFlightTrackDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

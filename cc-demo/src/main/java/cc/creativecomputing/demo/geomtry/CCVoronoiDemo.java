package cc.creativecomputing.demo.geomtry;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.util.CCStopWatch;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.geometry.hemesh.CCHEEdge;
import cc.creativecomputing.geometry.hemesh.CCHEFace;
import cc.creativecomputing.geometry.hemesh.CCHEMesh;
import cc.creativecomputing.geometry.hemesh.CCVoronoi;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCLine3;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.random.CCRandom;

public class CCVoronoiDemo extends CCGL2Adapter {

	@CCProperty(name = "seed", min = 0, max = 100)
	private int _cSeed = 0;

	@CCProperty(name = "points", min = 10, max = 100)
	private int _cPoints = 0;

	List<CCVector2> _myPoints = new ArrayList<>();
	CCVoronoi _myVoronoi = null;
	CCRandom _myRandom;

	// int points = 30276;
	int points = 40;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {

		_myRandom = new CCRandom(0);
		_myPoints.clear();
		for (int i = 0; i < points; i++) {
			_myPoints.add(new CCVector2(_myRandom.random(-g.width() / 2 + 20, g.width() / 2 - 20),
					_myRandom.random(-g.height() / 2 + 20, g.height() / 2 - 20)));
		}

		CCStopWatch.instance().print(true);
		CCStopWatch.instance().startWatch("voronoi");
		_myVoronoi = new CCVoronoi(_myPoints, -g.width() / 2 + 20, g.width() / 2 - 20, -g.height() / 2 + 20,
				g.height() / 2 - 20);
		CCStopWatch.instance().endLast();

		keyPressed().add(theEvent -> {
			switch (theEvent.keyCode()) {
			case VK_V:
				_myPoints.clear();
				for (int i = 0; i < points; i++) {
					_myPoints.add(new CCVector2(_myRandom.random(-g.width() / 2 + 20, g.width() / 2 - 20),
							_myRandom.random(-g.height() / 2 + 20, g.height() / 2 - 20)));
				}
				_myVoronoi = new CCVoronoi(_myPoints, -g.width() / 2 + 20, g.width() / 2 - 20, -g.height() / 2 + 20,
						g.height() / 2 - 20);
				break;

			default:
				break;
			}
		});
	}

	private void updateVoronoi(CCGraphics g) {
		_myRandom = new CCRandom(_cSeed);
		_myPoints.clear();
		for (int i = 0; i < _cPoints; i++) {
			_myPoints.add(new CCVector2(_myRandom.random(-g.width() / 2 + 20, g.width() / 2 - 20),
					_myRandom.random(-g.height() / 2 + 20, g.height() / 2 - 20)));
		}

		CCStopWatch.instance().startWatch("voronoi");
		_myVoronoi = new CCVoronoi(_myPoints, -g.width() / 2 + 20, g.width() / 2 - 20, -g.height() / 2 + 20,
				g.height() / 2 - 20);
		CCStopWatch.instance().endLast();

		CCStopWatch.instance().startWatch("mesh");
		_myVoronoi.mesh();
		CCStopWatch.instance().endLast();
	}

	public void drawEdges(CCGraphics g, CCHEMesh theMesh) {
		g.beginShape(CCDrawMode.LINES);
		for (CCHEEdge myEdge : theMesh.edges()) {
			if (myEdge.next() == null)
				continue;
			// if(!myEdge.mark0())continue;
			// if(myEdge.pair()!= null)continue;
			g.color(255, 0, 0);
			g.vertex(myEdge.start().vector());
			g.color(0, 255, 0);
			g.vertex(myEdge.next().start().vector());
		}
		g.endShape();
	}
	
	public void drawFaces(CCGraphics g, CCHEMesh theMesh){
	for(CCHEFace myFace:theMesh.faces()){
//		if(myFace.edge() == null)continue;
//		CCHEEdge myEdge = myFace.edge();
//		if(myEdge.next() == null)continue;

		g.beginShape(CCDrawMode.POLYGON);
		for(CCHEEdge myEdge:myFace.edges()){
			g.vertex(myEdge.start().vector());
		}
//		do{
////			g.color(255,0,0);
//			
////			g.color(0,255,0);
////			g.vertex(myEdge.next().start().vector());
//			myEdge = myEdge.next();
//			
//		}while(myEdge.next() != null && myEdge != myFace.edge());
		g.endShape();
	}
}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.blend(CCBlendMode.ADD);
		g.strokeWeight(2);
		g.color(0, 0, 255);
		g.beginShape(CCDrawMode.LINES);
		for (CCLine3 myLine : _myVoronoi.edges()) {
			g.vertex(myLine.start());
			g.vertex(myLine.end());
		}
		g.endShape();

		g.strokeWeight(1);
		CCHEMesh myMesh = _myVoronoi.mesh();
		if (_myPoints.size() > 10)
			drawEdges(g, myMesh);
		// if(_myPoints.size() > 10)myMesh.drawFaces(g);
		// g.beginShape(CCDrawMode.LINES);
		// for(CCHEFace myFace:myMesh.faces()){
		// g.vertex(myFace.edge().start().vector());
		// g.vertex(myFace.centroid());
		// }
		// g.endShape();
	}

	public static void main(String[] args) {

		CCVoronoiDemo demo = new CCVoronoiDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(900, 900);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

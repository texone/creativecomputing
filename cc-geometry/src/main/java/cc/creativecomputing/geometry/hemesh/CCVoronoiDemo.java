package cc.creativecomputing.geometry.hemesh;


public class CCVoronoiDemo  {
	
//	@CCControl(name = "seed", min = 0, max = 100)
//	private int _cSeed = 0;
//	
//	@CCControl(name = "points", min = 10, max = 100)
//	private int _cPoints = 0;
//	
//	List<CCVector2f> _myPoints = new ArrayList<>();
//	CCVoronoi _myVoronoi = null;
//	CCRandom _myRandom;
//	
//
////	int points = 30276;
//	int points = 40;
//
//	@Override
//	public void setup() {
//		addControls("app", "app", this);
//		
//		_myRandom = new CCRandom(0);
//		_myPoints.clear();
//		for(int i = 0; i < points;i++){
//			_myPoints.add(new CCVector2f(_myRandom.random(-width/2+20, width/2-20),_myRandom.random(-height/2+20, height/2-20)));
//		}
//		
//		CCStopWatch.instance().print(true);
//		CCStopWatch.instance().startWatch("voronoi");
//		_myVoronoi = new CCVoronoi(_myPoints, -width/2 + 20, width/2 - 20, -height/2 + 20, height/2 - 20);
//		CCStopWatch.instance().endLast();
//	}
//
//	@Override
//	public void update(final float theDeltaTime) {
//		_myRandom = new CCRandom(_cSeed);
//		_myPoints.clear();
//		for(int i = 0; i < _cPoints;i++){
//			_myPoints.add(new CCVector2f(_myRandom.random(-width/2+20, width/2-20),_myRandom.random(-height/2+20, height/2-20)));
//		}
//		
//		CCStopWatch.instance().startWatch("voronoi");
//		_myVoronoi = new CCVoronoi(_myPoints, -width/2 + 20, width/2 - 20, -height/2 + 20, height/2 - 20);
//		CCStopWatch.instance().endLast();
//		
//		CCStopWatch.instance().startWatch("mesh");
//		_myVoronoi.mesh();
//		CCStopWatch.instance().endLast();
//	}
//
//	@Override
//	public void draw() {
//		g.clear();
//		g.blend(CCBlendMode.ADD);
//		g.strokeWeight(2);
//		g.color(0,0,255);
//		g.beginShape(CCDrawMode.LINES);
//		for(CCLine3f myLine:_myVoronoi.edges()){
//			g.vertex(myLine.start());
//			g.vertex(myLine.end());
//		}
//		g.endShape();
//
//		g.strokeWeight(1);
//		CCHEMesh myMesh = _myVoronoi.mesh();
//		if(_myPoints.size() > 10)myMesh.drawEdges(g);
////		if(_myPoints.size() > 10)myMesh.drawFaces(g);
////		g.beginShape(CCDrawMode.LINES);
////		for(CCHEFace myFace:myMesh.faces()){
////			g.vertex(myFace.edge().start().vector());
////			g.vertex(myFace.centroid());
////		}
////		g.endShape();
//	}
//	
//	
//	@Override
//	public void keyPressed(CCKeyEvent theKeyEvent) {
//		switch (theKeyEvent.keyCode()) {
//		case VK_V:
//			_myPoints.clear();
//			for(int i = 0; i < points;i++){
//				_myPoints.add(new CCVector2f(_myRandom.random(-width/2+20, width/2-20),_myRandom.random(-height/2+20, height/2-20)));
//			}
//			_myVoronoi = new CCVoronoi(_myPoints, -width/2 + 20, width/2 - 20, -height/2 + 20, height/2 - 20);
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	public static void main(String[] args) {
//		CCApplicationManager myManager = new CCApplicationManager(CCVoronoiDemo.class);
//		myManager.settings().size(900, 900);
//		myManager.settings().antialiasing(8);
////		myManager.settings().frameRate(1);
//		myManager.start();
//	}
}

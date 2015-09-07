//package cc.creativecomputing.kle.model;
//
//import java.nio.FloatBuffer;
//import java.util.ArrayList;
//import java.util.List;
//
//import cc.creativecomputing.app.modules.CCAnimator;
//import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
//import cc.creativecomputing.gl.app.CCGLAdapter;
//import cc.creativecomputing.graphics.CCDrawMode;
//import cc.creativecomputing.graphics.CCGraphics;
//import cc.creativecomputing.graphics.CCMesh;
//import cc.creativecomputing.graphics.CCVBOMesh;
//import cc.creativecomputing.graphics.app.CCGL2Application;
//import cc.creativecomputing.io.CCNIOUtil;
//import cc.creativecomputing.math.CCVector3;
//import cc.creativecomputing.model.obj.CCContent3dIO;
//import cc.creativecomputing.model.obj.CCModel;
//import cc.creativecomputing.model.obj.CCSegment;
//
//public class CCModelDemo extends CCGLAdapter<CCGraphics>{
//	
//	private List<CCMesh> _myParts = new ArrayList<CCMesh>();
//	
//	@Override
//	public void start(CCAnimator theAnimator) {
//		
//	}
//	
//	@Override
//	public void init(CCGraphics g) {
//		
//		CCModel myModel = CCContent3dIO.createModel(CCNIOUtil.dataPath("150202_ct4_galleria.obj"), CCContent3dIO.OBJ);
//		myModel.convert();
//		
//		for(String myGroup:myModel.groupNames()){
//			System.out.println(myGroup);
//			CCSegment mySegment = myModel.segment(myGroup);
//			List<CCVector3> myVertices = mySegment.vertices();
//			CCVBOMesh mySegmentMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, myVertices.size());
////			mySegmentMesh.prepareVertexData(myVertices.size(), 3);
//			FloatBuffer myVertexBuffer = FloatBuffer.allocate(myVertices.size() * 3);
//			for(CCVector3 myVertex:myVertices){
//				myVertexBuffer.put(myVertex.x);
//				myVertexBuffer.put(myVertex.y);
//				myVertexBuffer.put(myVertex.z);
//			}
//			myVertexBuffer.rewind();
//			mySegmentMesh.vertices(myVertexBuffer);
//			_myParts.add(mySegmentMesh);
//		}
//	}
//	
//	private float _myAngle = 0;
//	
//	@Override
//	public void update(CCAnimator theAnimator) {
//		// TODO Auto-generated method stub
//		super.update(theAnimator);
//		_myAngle += theAnimator.deltaTime() * 20;
//	}
//	
//	@Override
//	public void display(CCGraphics g) {
//		g.clearColor(255);
//		g.clear();
//		g.pushMatrix();
//		g.rotateY(_myAngle);
//		g.color(0);
//		for(CCMesh myMesh:_myParts){
//			myMesh.draw(g);
//		}
//		g.popMatrix();
//	}
//	
////	@Override
////	public void keyPressed(CCKeyEvent theKeyEvent) {
////		switch(theKeyEvent.keyCode()){
////		case VK_L:
////			String myFile = CCIOUtil.selectInput("choose_bin_file", CCIOUtil.appPath(""));
////			if(myFile == null)return;
////			_myViewer.load(myFile);
////			break;
////		}
////	}
//
//	public static void main(String[] args) {
//		CCGL2Application myAppManager = new CCGL2Application(new CCModelDemo());
//		myAppManager.glcontext().size(1800, 900);
//		myAppManager.animator().framerate = 30;
//		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
//		myAppManager.start();
//	}
//	
//}

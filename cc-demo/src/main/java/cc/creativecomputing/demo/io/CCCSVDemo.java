package cc.creativecomputing.demo.io;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCCSVIO;
import cc.creativecomputing.io.xml.CCDataElement;

public class CCCSVDemo extends CCGL2Adapter {

	double[][] myData;
	double[][] myVels;
	double[][] myAccs;
	double[][] myJerks;
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	int colums = 20;
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCDataElement myElements = CCCSVIO.createDataElement(CCNIOUtil.dataPath("csv/wippe10.csv"), "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19", ",");
		myData = new double[colums][myElements.children().size()];
		int i = 0;
		
		for(CCDataElement myElement:myElements) {
			for(int j = 0; j < colums; j++) {
//				CCLog.info(j, myElement.attribute(j+""));
				myData[j][i] = myElement.doubleAttribute(j+"");
			}
			i++;
			CCLog.info(i);
		}
		myVels = new double[colums][myElements.children().size()];
		for(i = 0; i < myElements.children().size() - 1;i++) {
			for(int j = 0; j < colums; j++) {
				myVels[j][i] = (myData[j][i + 1] - myData[j][i]) * 5;
			}
		}
		myAccs = new double[colums][myElements.children().size()];
		for(i = 0; i < myElements.children().size() - 2;i++) {
			for(int j = 0; j < colums; j++) {
				myAccs[j][i] = (myVels[j][i + 1] - myVels[j][i]) * 5;
			}
		}
		myJerks = new double[colums][myElements.children().size()];
		for(i = 0; i < myElements.children().size() - 3;i++) {
			for(int j = 0; j < colums; j++) {
				myJerks[j][i] = (myAccs[j][i + 1] - myAccs[j][i]) * 5;
			}
		}
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@CCProperty(name = "value scale", min = 0, max = 10)
	private double _cValueScale = 1;
	@CCProperty(name = "vel scale", min = 0, max = 10)
	private double _cVelScale = 1;
	@CCProperty(name = "acc scale", min = 0, max = 10)
	private double _cAccScale = 1;
	@CCProperty(name = "jerk scale", min = 0, max = 10)
	private double _cJerkScale = 1;

	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();
		_cCameraController.camera().draw(g);
		
		g.color(255);
		for(int i = 0; i < colums;i++) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j < myData[i].length;j++) {
				g.vertex(j,myData[i][j] * _cValueScale + g.height()/2);
			}
			
			g.endShape();
		}
		
		g.color(255,0,0);
		for(int i = 0; i < colums;i++) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j < myVels[i].length;j++) {
				g.vertex(j,myVels[i][j]  * _cVelScale + g.height()/2);
			}
			
			g.endShape();
		}
		
		g.color(0,255,0);
		for(int i = 0; i < colums;i++) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j < myAccs[i].length;j++) {
				g.vertex(j,myAccs[i][j] *_cAccScale + g.height()/2);
			}
			
			g.endShape();
		}
		
		g.color(0,0,255);
		for(int i = 0; i < colums;i++) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j < myJerks[i].length;j++) {
				g.vertex(j,myJerks[i][j] * _cJerkScale  + g.height()/2);
			}
			
			g.endShape();
		}
	}

	public static void main(String[] args) {

		CCCSVDemo demo = new CCCSVDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(3800, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
		
		
	}
}

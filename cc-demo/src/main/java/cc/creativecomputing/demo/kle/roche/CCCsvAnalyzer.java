package cc.creativecomputing.demo.kle.roche;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCCSVIO;
import cc.creativecomputing.io.xml.CCDataElement;

public class CCCsvAnalyzer extends CCGL2Adapter {
	
	private double[][] _mySequence;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCDataElement myData = CCCSVIO.createDataElement(CCNIOUtil.dataPath("event7.csv"), "0,1,2,3,4,5,6,7,8,9,10,11,12", ",");
		_mySequence = new double [myData.countChildren()][13];
		
		int i = 0;
		for(CCDataElement myElement:myData) {
			for(int j = 0; j < 13; j++) {
				try {
				_mySequence[i][j] = myElement.doubleAttribute(j +"");
				}catch(Exception e) {
					
				}
			}
			i++;
		}
		
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();
		
		double timeScale = 5;
		int curves = 13;
		
		g.pushMatrix();
		g.ortho2D();
		double[][] _myVels = new double[_mySequence.length][_mySequence[0].length];
		double[][] _myAccs = new double[_mySequence.length][_mySequence[0].length];
		double[][] _myJerks = new double[_mySequence.length][_mySequence[0].length];
		g.color(255);
		for(int i = 0; i < curves;i++) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j < _mySequence.length;j++) {
				g.vertex(j * timeScale, _mySequence[j][i] + 100);
				
				if(j > 0)_myVels[j][i] = (_mySequence[j- 1][i] - _mySequence[j][i]) / 0.2;
				if(j > 1)_myAccs[j][i] = (_myVels[j- 1][i] - _myVels[j][i]) / 0.2;
				if(j > 2)_myJerks[j][i] = (_myAccs[j- 1][i] - _myAccs[j][i]) / 0.2;
			}
			g.endShape();
		}
		g.color(255,0,0);
		for(int i = 0; i < curves;i++) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j < _myVels.length;j++) {
				g.vertex(j * timeScale, _myVels[j][i] * 2 + 300);
			}
			g.endShape();
		}
		
		g.color(0,255,0);
		for(int i = 0; i < curves;i++) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j < _myAccs.length;j++) {
				g.vertex(j * timeScale, _myAccs[j][i] * 4 + 300);
			}
			g.endShape();
		}
		
		g.color(0,0,255);
		for(int i = 0; i < curves;i++) {
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j < _myJerks.length;j++) {
				g.vertex(j * timeScale, _myJerks[j][i] * 10 + 300);
			}
			g.endShape();
		}
		
		g.popMatrix();
	}

	public static void main(String[] args) {

		CCCsvAnalyzer demo = new CCCsvAnalyzer();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
		

		
//		CCLog.info(myData);
	}
}

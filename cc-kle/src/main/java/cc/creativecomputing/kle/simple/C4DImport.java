package cc.creativecomputing.kle.simple;

import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector2;

public class C4DImport {

	public CCVector2[][] dataEF;
	public CCVector2[][] dataFE;
	
	int nElements;
	int nFrames = Integer.MAX_VALUE;
	
	public void load (String root, int theNElements) {
		
		nElements = theNElements;
		dataEF = new CCVector2[nElements][];
		
		for (int i=0; i<nElements; i++) {
			String frames[] = CCIOUtil.loadStrings(root+"/e"+CCFormatUtil.nf(i, 3)+".txt");
			
			nFrames = frames.length;
			dataEF[i] = new CCVector2 [nFrames];
			for (int j=0; j<nFrames; j++) {
				String[] frame = frames[j].split(" ");
				dataEF[i][j] = new CCVector2 (Float.parseFloat(frame[0]), Float.parseFloat(frame[1])*-1);
			}
		}
		dataFE = new CCVector2 [nFrames][nElements];
		
		for (int e=0; e<nElements; e++) {
			for (int f=0; f<nFrames; f++) {
				dataFE[f][e] = dataEF[e][f];
			}
		}
	}
	
	public CCVector2 getPosition (int theElement, int theFrame) {
		return dataEF[theElement][theFrame];
	}
	
	public CCVector2[] getPositions (int theFrame) {
		return dataFE[theFrame];
	}
}

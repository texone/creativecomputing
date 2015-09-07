package cc.creativecomputing.kle;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.formats.CCSequenceBinFormat;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;

public class CCSequenceStatistics {

	public static void main(String[] args) {
		CCSequenceElements myElements = null;//new CCSequenceElements(CCNIOUtil.dataPath("FINALMASTER/out01complete"), 0, 0, 0);
		
		CCSequence mySequence = new CCSequenceBinFormat().load(CCNIOUtil.dataPath("FINALMASTER/out01complete/raw.bin"), myElements.mappings().get("motors"));
		
		CCLog.info(mySequence.columns() + ":" +mySequence.rows()+":" + mySequence.depth() + ":" +mySequence.size() / 5 / 60);
		int ID = 0;
		
		double myLength = mySequence.size() / 5;
		double[][][] myMovements = new double[mySequence.columns()][mySequence.rows()][mySequence.depth()];
		double[][][] mySpeedChanges = new double[mySequence.columns()][mySequence.rows()][mySequence.depth()];
		double[][][] myMaxMovements = new double[mySequence.columns()][mySequence.rows()][mySequence.depth()];
		double[][][] myMaxSpeedChanges = new double[mySequence.columns()][mySequence.rows()][mySequence.depth()];
		int[][][] myDirectionChanges = new int[mySequence.columns()][mySequence.rows()][mySequence.depth()];
		for(int i = 0; i < mySequence.size() - 1; i++){
			CCMatrix2 myFrame = mySequence.get(i);
			CCMatrix2 myNextFrame = mySequence.get(i + 1);
			CCMatrix2 myNextNextFrame = null;
			if(i < mySequence.size() - 2)myNextNextFrame = mySequence.get(i + 2);
			for(int c = 0; c < mySequence.columns(); c++){
				for(int r = 0; r < mySequence.rows(); r++){
					for(int d = 0; d < mySequence.depth(); d++){
						double myMovement = CCMath.abs(myNextFrame.data()[c][r][d] - myFrame.data()[c][r][d]);
						myMaxMovements[c][r][d] = CCMath.max(myMovement, myMaxMovements[c][r][d]);
						myMovements[c][r][d] += myMovement;
						
						if(myNextNextFrame == null)continue;
						
						myMovement = myNextFrame.data()[c][r][d] - myFrame.data()[c][r][d];
						double myNextMovement = myNextNextFrame.data()[c][r][d] - myNextFrame.data()[c][r][d];
						double mySpeedChange = CCMath.abs(myNextMovement - myMovement);
						if(CCMath.sign(myNextMovement) != CCMath.sign(myMovement) && mySpeedChange * 25 / 100 > 0.47){
							CCLog.info(mySpeedChange * 25);
							myDirectionChanges[c][r][d]++;
						}
						mySpeedChanges[c][r][d] += mySpeedChange;
						myMaxSpeedChanges[c][r][d] = CCMath.max(mySpeedChange, myMaxSpeedChanges[c][r][d]);
					}
				}
			}
			
		}
		double myMovement = 0;
		double myMaxSpeed = 0;
		double myAvgAcc = 0;
		double myMaxAcc = 0;
		double myAVGDirectionChanges = 0;
		System.out.println("column,row,depth,movement over 35 min,average speed,max speed,average acceleration,max acceleration,seconds between direction change");
		for(int c = 0; c < mySequence.columns(); c++){
			for(int r = 0; r < mySequence.rows(); r++){
				for(int d = 0; d < mySequence.depth(); d++){
					double myElementMovement = myMovements[c][r][d] / 100;
					double myElementMaxSpeed = myMaxMovements[c][r][d] * 5 / 100;
					double myElementAvgAcc = mySpeedChanges[c][r][d] / (myLength - 0.2f) * 25 / 100;
					double myElementMaxAcc = myMaxSpeedChanges[c][r][d] * 25 / 100;
					
					System.out.println(c+"," +r+"," +d+"," + myElementMovement + "," + myElementMovement / myLength+"," + myElementMaxSpeed+"," + myElementAvgAcc+"," + myElementMaxAcc+"," + 1 / (myDirectionChanges[c][r][d] / myLength));
					myMovement += myElementMovement;
					myMaxSpeed += myElementMaxSpeed;
					myAvgAcc += myElementAvgAcc;
					myMaxAcc += myElementMaxAcc;
					myAVGDirectionChanges += myDirectionChanges[c][r][d];
				}
			}
		}
		myMovement /= mySequence.columns() * mySequence.rows() * mySequence.depth();
		myMaxSpeed /= mySequence.columns() * mySequence.rows() * mySequence.depth();
		myAvgAcc /= mySequence.columns() * mySequence.rows() * mySequence.depth();
		myMaxAcc /= mySequence.columns() * mySequence.rows() * mySequence.depth();
		myAVGDirectionChanges /= mySequence.columns() * mySequence.rows() * mySequence.depth();
		System.out.println("avg,avg,avg," + myMovement + "," + myMovement / myLength + "," +myMaxSpeed + "," +myAvgAcc + "," +myMaxAcc + "," + 1f / (myAVGDirectionChanges / myLength));
	}
}

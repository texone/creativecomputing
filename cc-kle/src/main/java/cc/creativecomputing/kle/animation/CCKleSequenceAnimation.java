package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.CCSequenceAsset;
import cc.creativecomputing.kle.elements.CCSequenceChannel;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMatrix2;

public class CCKleSequenceAnimation extends CCKleAnimation {
	
	@CCProperty(name = "sequence")
	private CCSequenceAsset _mySequenceAsset;
	@CCProperty(name = "rope length scale")
	private double _myRopeLengthScale = 0.1f;
	
	private int _myResultLength = 0;
	
	private final int[] _myChannels;
	
	public CCKleSequenceAnimation(CCSequenceMapping<?> theMapping, final int...theChannels){
		_myChannels = theChannels;
		_mySequenceAsset = new CCSequenceAsset(theMapping);
	}
	
	public CCKleSequenceAnimation(CCSequenceMapping<?> theMapping){
		this(theMapping, 0, 1);
	}
	
	private CCMatrix2 _myFrame;

	public void update(final double theDeltaTime) {
		_myFrame = _mySequenceAsset.frame();
	}
	
	public CCSequenceAsset sequence(){
		return _mySequenceAsset;
	}

	public double[] animate(CCSequenceElement theElement) {
		double[] myResult = new double[_myResultLength];
		if(_myFrame == null)return myResult;
		
		for(int i = 0; i < _myChannels.length;i++){
			int myChannelID = _myChannels[i];
			CCSequenceChannel myChannel = theElement.channels().get(myChannelID);
		
			double myValue = _myFrame.data()[myChannel.column()][myChannel.row()][myChannel.depth()] * _myRopeLengthScale;
		}
		
//		CC2Motor2ConnectionSetup mySetup = (CC2Motor2ConnectionSetup)theElement.motorSetup();
//		CCPositionRopeLengthAngle myData = mySetup.twoPointMatrices().dataByRopeLength(myValue0, myValue1);
//		if(myData == null)return new CCVector2(0,0);
//
//		double myBlend = elementBlend(theElement);
//		return new CCVector2(
//			(myData.relativePosition().x * 2 - 1) * myBlend, 
//			(myData.relativePosition().y * 2 - 1) * myBlend
//		);
//		return new CCVector2(0,0);
		
		return myResult;
	}
	
	@Override
	public void valueNames(String... theValueNames) {
		_myResultLength = theValueNames.length;
	}
}

package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCSequenceAsset;
import cc.creativecomputing.kle.elements.CCSequenceChannel;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionSetup;
import cc.creativecomputing.kle.elements.motors.CCPositionRopeLengthAngle;
import cc.creativecomputing.math.CCMatrix2;
import cc.creativecomputing.math.CCVector2;

public class CCKleVector2SequenceAnimation extends CCKleAnimation<CCVector2> {
	
	@CCProperty(name = "sequence")
	private CCSequenceAsset _mySequenceAsset;
	@CCProperty(name = "rope length scale")
	private double _myRopeLengthScale = 0.1f;
	
	private final int _myChannel0;
	private final int _myChannel1;
	
	public CCKleVector2SequenceAnimation(CCSequenceMapping<?> theMapping, final int theChannel0, final int theChannel1){
		_myChannel0 = theChannel0;
		_myChannel1 = theChannel1;
		_mySequenceAsset = new CCSequenceAsset(theMapping);
	}
	
	public CCKleVector2SequenceAnimation(CCSequenceMapping<?> theMapping){
		this(theMapping, 0, 1);
	}
	
	private CCMatrix2 _myFrame;

	public void update(final double theDeltaTime) {
		_myFrame = _mySequenceAsset.frame();
	}
	
	public CCSequenceAsset sequence(){
		return _mySequenceAsset;
	}

	public CCVector2 animate(CCSequenceElement theElement) {

		if(_myFrame == null)return new CCVector2();
		CCSequenceChannel myChannel0 = theElement.channels().get(_myChannel0);
		CCSequenceChannel myChannel1 = theElement.channels().get(_myChannel1);
		
		double myValue0 = _myFrame.data()[myChannel0.column()][myChannel0.row()][myChannel0.depth()] * _myRopeLengthScale;
		double myValue1 = _myFrame.data()[myChannel1.column()][myChannel1.row()][myChannel1.depth()] * _myRopeLengthScale;
		
		CC2Motor2ConnectionSetup mySetup = (CC2Motor2ConnectionSetup)theElement.motorSetup();
		CCPositionRopeLengthAngle myData = mySetup.twoPointMatrices().dataByRopeLength(myValue0, myValue1);
		if(myData == null)return new CCVector2(0,0);

		double myBlend = elementBlend(theElement);
		return new CCVector2(
			(myData.relativePosition().x * 2 - 1) * myBlend, 
			(myData.relativePosition().y * 2 - 1) * myBlend
		);
//		return new CCVector2(0,0);
	}
}

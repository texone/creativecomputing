package cc.creativecomputing.kle.elements.motors;

import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.util.CCMatrixObject;

public class CCPositionRopeLengthAngle implements CCMatrixObject<CCPositionRopeLengthAngle> {

	private final CCVector2 _myPosition;
	
	private final CCVector2 _myRelativePosition;

	private final double _myLeftRopeLength;
	private final double _myRightRopeLength;

	private double _myAngle;

	CCPositionRopeLengthAngle(
		CCVector2 thePosition, 
		double theAngle, 
		double theLeftLength, 
		double theRightLength,
		CCVector2 theRelativePosition
	) {
		_myPosition = thePosition;
		_myAngle = theAngle;
		_myLeftRopeLength = theLeftLength;
		_myRightRopeLength = theRightLength;
		_myRelativePosition = theRelativePosition;
	}
	
	CCPositionRopeLengthAngle(
		CCVector3 thePosition, 
		double theLeftLength, 
		double theRightLength, 
		CCVector2 theRelativePosition
	) {
		_myPosition = new CCVector2( thePosition.x, thePosition.y);
		_myAngle = thePosition.z;
		_myLeftRopeLength = theLeftLength;
		_myRightRopeLength = theRightLength;
		_myRelativePosition = theRelativePosition;
	}
	
	public CCPositionRopeLengthAngle(
		double thePositionX, 
		double thePositionY, 
		double theAngle, 
		double theLeftRopeLength, 
		double theRightRopeLength, 
		double theRelativeX, 
		double theRelativeY
	){
		_myPosition = new CCVector2(thePositionX, thePositionY);
		_myAngle = theAngle;
		_myLeftRopeLength = theLeftRopeLength;
		_myRightRopeLength = theRightRopeLength;
		_myRelativePosition = new CCVector2(theRelativeX, theRelativeY);
	}
	
	public CCPositionRopeLengthAngle(){
		this(0,0,0,0,0,0,0);
	}
	
	@Override
	public int dataSize() {
		return 7;
	}
	
	@Override
	public double[] data() {
		return new double[]{_myPosition.x, _myPosition.y, angle(), leftRopeLength(), rightRopeLength(), _myRelativePosition.x, _myRelativePosition.y};
	}

	public String toString() {
		return "[ " + _myPosition.x + ", " + _myPosition.y + ", " + angle() + ", " + leftRopeLength() + ", " + rightRopeLength() + ", " + _myRelativePosition.x + ", " + _myRelativePosition.y + " ]";
	}

	@Override
	public CCPositionRopeLengthAngle create(double[] theData) {
		return new CCPositionRopeLengthAngle(theData[0], theData[1], theData[2], theData[3], theData[4], theData[5], theData[6]);
	}

	public CCVector2 position() {
		return _myPosition;
	}

	public double leftRopeLength() {
		return _myLeftRopeLength;
	}

	public double rightRopeLength() {
		return _myRightRopeLength;
	}

	public double angle() {
		return _myAngle;
	}
	
	public CCVector2 relativePosition(){
		return _myRelativePosition;
	}

}
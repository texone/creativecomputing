package cc.creativecomputing.kle.elements.motors;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.motors.formulars.CC2Motor2ConnectionPositionFormular;
import cc.creativecomputing.kle.elements.motors.formulars.CC2Motor2ConnectionPositionHermiteInterpolationFormular;
import cc.creativecomputing.kle.elements.motors.formulars.CC2Motor2ConnectionPositionNumericSolver1Formular;
import cc.creativecomputing.kle.elements.motors.formulars.CC2Motor2ConnectionPositionSimpleInterpolationFormular;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.filter.CCMotionLimiter;

public class CC2Motor2ConnectionCalculations extends CCMotorCalculations<CC2Motor2ConnectionSetup>{
	
	private double _myRopeAngle = 6;
	
	@CCProperty(name = "x limiter")
	public CCMotionLimiter _myXMotionLimiter;
	@CCProperty(name = "y limiter")
	public CCMotionLimiter _myYMotionLimiter;
	
	@CCProperty(name = "formulars")
	private Map<String, CC2Motor2ConnectionPositionFormular> _myFormulars = new LinkedHashMap<>();

	@CCProperty(name = "calc back")
	protected boolean _cCalcBack = false;
	
	@CCProperty(name = "top width", readBack = true)
	protected double _cTopWidth = 0;
	@CCProperty(name = "bottom width", readBack = true)
	protected double _cBottomWidth = 0;
	
	private static enum CC2MotorFormular{
		HERMITE,
		POW,
		NUMERIC_1
	}
	
	@CCProperty(name = "formular")
	private CC2MotorFormular _myFormular = CC2MotorFormular.NUMERIC_1;
	
	private CC2Motor2ConnectionPositionNumericSolver1Formular _myNumeric1Formular;
	private CC2Motor2ConnectionPositionSimpleInterpolationFormular _myPowFormular;
	private CC2Motor2ConnectionPositionHermiteInterpolationFormular _myHermiteFormular;
	
	@Override
	public void setElements(CCSequenceElements theElements){
		super.setElements(theElements);
		_myXMotionLimiter = new CCMotionLimiter(_myElements.size());
		_myYMotionLimiter = new CCMotionLimiter(_myElements.size());
		
		_myFormulars.put("hermite", _myHermiteFormular = new CC2Motor2ConnectionPositionHermiteInterpolationFormular());
		_myFormulars.put("pow", _myPowFormular = new CC2Motor2ConnectionPositionSimpleInterpolationFormular());
		_myFormulars.put("numeric 1", _myNumeric1Formular = new CC2Motor2ConnectionPositionNumericSolver1Formular());
	}
	
	
	private double _myTime;
	
	public void update(CCAnimator theAnimator){
		_myTime = theAnimator.time();
	}
	
	public CCVector2 filter(int theChannel, CCVector2 thePosition){
		return new CCVector2(
			_myXMotionLimiter.process(theChannel, thePosition.x, _myTime),
			_myYMotionLimiter.process(theChannel, thePosition.y, _myTime)
		);
	}
	
	public double rotation(CC2Motor2ConnectionSetup theSetup){
		switch(_myFormular){
		case NUMERIC_1:
			return _myNumeric1Formular.rotation(theSetup);
		case POW:
			return _myPowFormular.rotation(theSetup);
		case HERMITE:
			return _myHermiteFormular.rotation(theSetup);
		}
		return _myNumeric1Formular.rotation(theSetup);
	}
	
	public CCVector2 position(CC2Motor2ConnectionSetup theSetup){
		return _myNumeric1Formular.position(theSetup);
	}
	
	public CCVector2 position(CC2Motor2ConnectionSetup theSetup, double l0, double l1){
		return _myNumeric1Formular.position(theSetup, l0, l1);
	}
	
	@Override
	public void updateBounds(CC2Motor2ConnectionSetup mySetup){
		mySetup.bounds().clear();
		mySetup.bounds().add(boundPoint(mySetup, 0, 1, _myTopDistance));
		mySetup.bounds().add(boundPoint(mySetup, 1, 0, _myTopDistance));
		mySetup.bounds().add(boundPoint(mySetup, 1, 0, _myBottomDistance));
		mySetup.bounds().add(boundPoint(mySetup, 0, 1, _myBottomDistance));
		
		_cTopWidth = mySetup.bounds().get(0).distance(mySetup.bounds().get(1));
		_cBottomWidth = mySetup.bounds().get(2).distance(mySetup.bounds().get(3));
		
		CCVector3 animBound0  = mySetup.bounds().get(0).clone();
		animBound0.x += mySetup.elementRadius() * _myElementRadiusScale;
		animBound0.y -= mySetup.elementRadius() * _myElementRadiusScale;
		CCVector3 animBound1  = mySetup.bounds().get(1).clone();
		animBound1.x -= mySetup.elementRadius() * _myElementRadiusScale;
		animBound1.y -= mySetup.elementRadius() * _myElementRadiusScale;
		CCVector3 animBound2 = mySetup.bounds().get(2).clone();
		animBound2.x -= mySetup.elementRadius() * _myElementRadiusScale;
		animBound2.y += mySetup.elementRadius() * _myElementRadiusScale;
		CCVector3 animBound3 = mySetup.bounds().get(3).clone();
		animBound3.x += mySetup.elementRadius() * _myElementRadiusScale;
		animBound3.y += mySetup.elementRadius() * _myElementRadiusScale;
		
		mySetup.animationBounds().clear();
		mySetup.animationBounds().add(animBound0);
		mySetup.animationBounds().add(animBound1);
		mySetup.animationBounds().add(animBound2);
		mySetup.animationBounds().add(animBound3);
	}

	private CCVector3 boundPoint(CCMotorSetup theSetup, int theID0, int theID1, double theTopDistance){
		CCVector3 myMotorPos0 = theSetup.channels().get(theID0)._myPosition;
		CCVector3 myMotorPos1 = theSetup.channels().get(theID1)._myPosition;
		CCVector3 myCenter = myMotorPos0.add(myMotorPos1).multiply(0.5f);
		CCVector3 myDirection = myMotorPos0.subtract(myCenter).normalizeLocal();
		CCVector3 myResult = myMotorPos0.subtract(myDirection.multiply(CCMath.tan(CCMath.radians(_myRopeAngle)) * theTopDistance));
		myResult.y -= theTopDistance;
		return myResult;
	}
	
	@SuppressWarnings("unused")
	private CCVector2 boundPoint2D(CCMotorSetup theSetup, int theID0, int theID1, double theTopDistance){
		CCVector3 myMotorPos0 = theSetup.channels().get(theID0)._myPosition;
		CCVector3 myMotorPos1 = theSetup.channels().get(theID1)._myPosition;
		double myLeft = myMotorPos0.distance(myMotorPos1);
		if(theID0 > theID1)myLeft = -myLeft;
		return new CCVector2(
			myLeft - CCMath.sign(myLeft) * CCMath.tan(CCMath.radians(6)) * theTopDistance,
			-theTopDistance
		);
	}
	
	@CCProperty(name = "radius scale", min = 0, max = 2, defaultValue = 1)
	public void elementRadiusScale(double theElementRadiusScale){
		_myElementRadiusScale = theElementRadiusScale;
		updateBounds();
	}
	
	public double minRopeAngle(){
		return _myRopeAngle;
	}
	
	@CCProperty(name = "min rope angle", min = 0, max = 30, defaultValue = 6)
	public void minRopeAngle(double theAngle){
		_myRopeAngle = theAngle;
		updateBounds();
	}
	
}

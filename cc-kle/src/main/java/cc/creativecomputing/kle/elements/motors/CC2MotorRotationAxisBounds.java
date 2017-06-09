package cc.creativecomputing.kle.elements.motors;

import cc.creativecomputing.core.CCProperty;

public class CC2MotorRotationAxisBounds extends CCMotorCalculations<CC2MotorRotationAxisSetup>{
	
	@CCProperty(name = "radius")
	private double _cRadius = 100;
	
	@CCProperty(name = "center angle", min = -180, max = 180)
	private double _cCenter = 0;

	@CCProperty(name = "amplitude angle", min = 0, max = 180)
	private double _cAmplitude = 0;

	
	public double radius(){
		return _cRadius;
	}
	
	public void radius(double theRadius){
		_cRadius = theRadius;
	}
	
	public double center(){
		return _cCenter;
	}
	
	public void center(double theCenter){
		_cCenter = theCenter;
	}
	
	public double amplitude(){
		return _cAmplitude;
	}
	
	public void amplitude(double theAmplitude){
		_cAmplitude = theAmplitude;
	}
	
	
	@Override
	public void updateBounds(CC2MotorRotationAxisSetup mySetup){
		
	}

	@Override
	public CCMotorSetupTypes type() {
		return CCMotorSetupTypes.SETUP_2_MOTOR_ROTATION_AXIS;
	}

	
}

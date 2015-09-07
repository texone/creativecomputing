package cc.creativecomputing.graphics.scene;

import cc.creativecomputing.graphics.scene.effect.CCVisualEffect;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * Attenuation is typically specified as a modulator
 * 
 * <pre>
 * m = 1 / (C + L * d + Q * d * d)
 * </pre>
 * 
 * where C is the constant coefficient, L is the linear coefficient, Q is the
 * quadratic coefficient, and d is the distance from the light position to the
 * vertex position. To allow for a linear adjustment of intensity, the choice is
 * to use instead
 * 
 * <pre>
 * m = I / (C + L * d + Q * d * d)
 * </pre>
 * 
 * where I is an intensity factor.
 * 
 * @author christianr
 * 
 */
public class CCLight extends CCSpatial {

	public static enum CCLightType {
		AMBIENT, DIRECTIONAL, POINT, SPOT
	};

	protected CCLightType _myType;

	// Construction and destruction.
	public CCLight(CCLightType theType) {
		_myType = theType;
	}

	public CCLight() {
		this(CCLightType.AMBIENT);
	}

	// The light type, currently only one of the classic types. The default
	// value is LT_AMBIENT.
	public CCLightType type() {
		return _myType;
	}

	// The colors of the light.
	public CCColor Ambient = new CCColor(0f,0f,0f,1f);
	public CCColor Diffuse= new CCColor(0f,0f,0f,1f);
	public CCColor Specular= new CCColor(0f,0f,0f,1f);

	public double constant = 1; 
	public double linear = 0; 
	public double quadratic = 0; 
	public double intensity = 1;

	// Parameters for spot lights. 
	public double angle = CCMath.PI; // default: pi
	public double cosAngle = -1; // default: -1
	public double sinAngle = 0; // default: 0
	public double exponent = 1; // default: 1
	
	/**
	 * A helper function that lets you set angle and have cosAngle and
	 * sinAngle computed for you.
	 * The cone angle must be in radians and must satisfy 0 < Angle <= pi.
	 * @param theAngle
	 */
	public void angle(double theAngle) {
		if(0.0f < angle && angle <= CCMath.PI){
			throw new RuntimeException("Angle out of range in angle");
		}

		angle = theAngle;
		cosAngle = CCMath.cos(angle);
		sinAngle = CCMath.sin(angle);
	}

	// Although the standard directional and spot lights need only a direction
	// vector, to allow for new types of derived-class lights that would use
	// a full coordinate frame, Light provides storage for such a frame. The
	// light frame is always in world coordinates.
	// The set {D,U,R} must be a right-handed orthonormal set. That is, each
	// vector is unit length, the vectors are mutually perpendicular, and
	// R = Cross(D,U).
	public CCVector3 position = CCVector3.ZERO.clone();
	public CCVector3 direction = CCVector3.NEG_UNIT_Z.clone();
	public CCVector3 up = CCVector3.UNIT_Y.clone();
	public CCVector3 right = CCVector3.UNIT_X.clone();

	/**
	 * A helper function that lets you set the direction vector and computes the
	 * up and right vectors automatically.
	 * 
	 * @param theDirection
	 */
	public void direction(CCVector3 theDirection) {
		direction = theDirection;
		CCVector3.generateOrthonormalBasis(up, right, direction);
	}

	@Override
	public void draw(CCRenderer theRenderer, CCVisualEffect theEffect) {
		// TODO Auto-generated method stub
		
	}
}

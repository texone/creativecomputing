package cc.creativecomputing.kle.elements.motors;

import java.nio.file.Path;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.interpolate.CCInterpolators;
import cc.creativecomputing.math.util.CCObjectMatrix2;

public class CCTwoPointMatrices implements UnivariateFunction {
	private double ll;
	private double lr;
	// motor distance
	private double r;
	// element point distances
	private double e;
	// element l center distance
	private double f;
	// element r center distance
	private double g;

	private final CCVector2 _myMotor0;
	private final CCVector2 _myMotor1;

	private double _myMinAngle = 0;
	private double _myMaxAngle = 90;

	private double eta;

	private CCVector2 bound0;
	private CCVector2 bound1;
	private CCVector2 bound2;
	private CCVector2 bound3;

	private final int _myID;

	private CCVector2 boundPoint(double theLeft, double theTopDistance) {
		return new CCVector2(
			theLeft - CCMath.sign(theLeft) * CCMath.tan(CCMath.radians(6)) * theTopDistance,
			-theTopDistance
		);
	}

	private CCObjectMatrix2<CCPositionRopeLengthAngle> _myPositionAngleMatrix;
	private CCObjectMatrix2<CCPositionRopeLengthAngle> _myRopeLengthMatrix;

	private double _myMinLength;
	private double _myMaxLength;

	private final double _myBoundsFloor;
	private final double _myBoundsTop;
	private final double _myAngle;

	private int _myRopeResolution;
	private int _myPointXResolution;
	private int _myPointYResolution;

	private double _myCenterAngle = 0;

	private UnivariateOptimizer _myOptimizer = new BrentOptimizer(1e-5, 1e-7);

	public CCTwoPointMatrices(int theID, CCVector3 theMotor0, CCVector3 theElement0, CCVector3 theMotor1,
			CCVector3 theElement1, double theBoundsFloor, double theBoundsTop, double theAngle) {
		_myID = theID;

		_myBoundsFloor = theBoundsFloor;
		_myBoundsTop = theBoundsTop;
		_myAngle = theAngle;

		ll = theMotor0.distance(theElement0);
		lr = theMotor1.distance(theElement1);
		r = theMotor0.distance(theMotor1);
		e = theElement0.distance(theElement1);
		CCVector3 myCenter = theElement0.add(theElement1).multiplyLocal(0.5f);
		myCenter.y = 0;
		f = theElement0.distance(myCenter);
		g = theElement1.distance(myCenter);

		bound0 = boundPoint(-r / 2, _myBoundsTop);
		bound1 = boundPoint(r / 2, _myBoundsTop);
		bound2 = boundPoint(r / 2, _myBoundsFloor);
		bound3 = boundPoint(-r / 2, _myBoundsFloor);

		_myMotor0 = new CCVector2(-r / 2, 0);
		_myMotor1 = new CCVector2(r / 2, 0);

		_myMinLength = _myMotor0.distance(bound0);
		_myMaxLength = _myMotor0.distance(bound2);

		eta = CCMath.acos((CCMath.sq(g) - CCMath.sq(f) - CCMath.sq(e)) / (-2 * e * f));
	}

	public CCVector2 motor0() {
		return _myMotor0;
	}

	public CCVector2 motor1() {
		return _myMotor1;
	}

	public int ropeResolution() {
		return _myRopeResolution;
	}

	public void ropeResolution(int theRopeResolution) {
		_myRopeResolution = theRopeResolution;
	}

	public int pointXResolution() {
		return _myPointXResolution;
	}

	public void pointXResolution(int thePointXResolution) {
		_myPointXResolution = thePointXResolution;
	}

	public int pointYResolution() {
		return _myPointYResolution;
	}

	public void pointYResolution(int thePointYResolution) {
		_myPointYResolution = thePointYResolution;
	}

	public CCVector3 formular(double alpha) {
		return formular(alpha, ll, lr);
	}

	public CCVector3 formular(double alpha, double ll, double lr) {
		double c = CCMath.sqrt(CCMath.sq(r) + CCMath.sq(ll) - 2 * r * ll * CCMath.cos(alpha));
		double lam = CCMath.acos((CCMath.sq(ll) - CCMath.sq(r) - CCMath.sq(c)) / (-2 * r * c));
		double eps = CCMath.acos((CCMath.sq(e) - CCMath.sq(lr) - CCMath.sq(c)) / (-2 * lr * c));
		double gam = CCMath.acos((CCMath.sq(c) - CCMath.sq(lr) - CCMath.sq(e)) / (-2 * lr * e));
		double rho = CCMath.PI - (lam + eps + gam) + eta;
		double delta = CCMath.TWO_PI - (alpha + lam + eps + gam);

		double xs = -r / 2 + ll * CCMath.cos(alpha) + f * CCMath.cos(rho);
		double ys = -ll * CCMath.sin(alpha) - f * CCMath.sin(rho);

		return new CCVector3(xs, ys, rho);
	}

	@Override
	public double value(double theArg0) {
		return formular((double) theArg0).y;
	}

	public CCVector3 minimum() {
		UnivariatePointValuePair myPair = _myOptimizer.optimize(new MaxEval(200),
				new UnivariateObjectiveFunction(this), GoalType.MINIMIZE,
				new SearchInterval(CCMath.radians(_myMinAngle), CCMath.radians(_myMaxAngle)));

		if (Double.isNaN(myPair.getValue())) {
			return null;
		}
		return formular((double) myPair.getPoint());
	}

	public void updateMatrices() {
		_myPositionAngleMatrix = new CCObjectMatrix2<>(new CCPositionRopeLengthAngle(), _myRopeResolution,
				_myRopeResolution);

		double lltmp = ll;
		double lrtmp = lr;

		for (double angle = 90; angle <= 180; angle += 10) {
			_myMinAngle = angle - 120;
			_myMaxAngle = angle;

			for (int x = 0; x < _myRopeResolution; x++) {
				ll = CCMath.map(x, 0, _myRopeResolution - 1, _myMinLength, _myMaxLength);
				for (int y = 0; y < _myRopeResolution; y++) {
					lr = CCMath.map(y, 0, _myRopeResolution - 1, _myMinLength, _myMaxLength);

					if (_myPositionAngleMatrix.data()[x][y][0] != 0)
						continue;
					CCVector3 myPoint = minimum();
					if (myPoint == null)
						continue;

					double myYBlend = CCMath.norm(-myPoint.y, _myBoundsTop, _myBoundsFloor);

					CCVector2 myVec0 = bound0.lerp(bound3, myYBlend);
					CCVector2 myVec1 = bound1.lerp(bound2, myYBlend);

					double myXBlend = CCMath.norm(myPoint.x, myVec0.x, myVec1.x);

					// CCLog.info(myPoint.x+":" + myVec0.x+":" + myVec1.x+":" +
					// myXBlend+":" + myYBlend);

					_myPositionAngleMatrix.setObject(new CCPositionRopeLengthAngle(myPoint, ll, lr, new CCVector2(
							myXBlend, myYBlend)), x, y);
				}
			}
		}

		ll = lltmp;
		lr = lrtmp;

		_myRopeLengthMatrix = new CCObjectMatrix2<>(new CCPositionRopeLengthAngle(), _myPointXResolution,
				_myPointYResolution);

		for (int x = 0; x < _myPointXResolution; x++) {
			for (int y = 0; y < _myPointYResolution; y++) {
				double px = CCMath.map(x, 0, _myPointXResolution - 1, _myMotor0.x, _myMotor1.x);
				double py = CCMath.map(y, 0, _myPointYResolution - 1, _myBoundsTop, _myBoundsFloor);

				CCPositionRopeLengthAngle myBase = new CCPositionRopeLengthAngle(px, -py, 0, 0, 0, 0, 0);
				CCPositionRopeLengthAngle myInterpolation = _myPositionAngleMatrix.interpolate(myBase, 0, 1);
				if (myInterpolation == null)
					continue;

				_myRopeLengthMatrix.data()[x][y] = myInterpolation.data();

			}
		}

		_myCenterAngle = dataByRopeLength(_myMaxLength, _myMaxLength).angle();
	}

	public void saveMatrices(Path theMatrixPath) {
		CCNIOUtil.saveBytes(theMatrixPath.resolve("position.mtx"), _myPositionAngleMatrix.toByteBuffer());
		CCNIOUtil.saveBytes(theMatrixPath.resolve("ropelength.mtx"), _myRopeLengthMatrix.toByteBuffer());
	}

	public void loadMatrices(Path theMatrixPath) {
		_myPositionAngleMatrix = new CCObjectMatrix2<>(new CCPositionRopeLengthAngle(), _myRopeResolution,
				_myRopeResolution);
		_myRopeLengthMatrix = new CCObjectMatrix2<>(new CCPositionRopeLengthAngle(), _myPointXResolution,
				_myPointYResolution);

		_myPositionAngleMatrix.data(CCNIOUtil.loadBytes(theMatrixPath.resolve("position.mtx")));
		_myRopeResolution = _myPositionAngleMatrix.columns();
		_myRopeLengthMatrix.data(CCNIOUtil.loadBytes(theMatrixPath.resolve("ropelength.mtx")));
		_myPointXResolution = _myRopeLengthMatrix.columns();
		_myPointYResolution = _myRopeLengthMatrix.rows();

		_myCenterAngle = dataByRopeLength(_myMaxLength, _myMaxLength).angle();
	}

	public double centerAngle() {
		return _myCenterAngle;
	}

	public CCVector2 leftConnection(CCVector2 thePosition, double theAngle) {
		return new CCVector2(thePosition.x - f * CCMath.cos(theAngle), thePosition.y + f * CCMath.sin(theAngle));
	}

	public CCVector2 rightConnection(CCVector2 thePosition, double theAngle) {
		double xS = thePosition.x;
		double yS = thePosition.y;
		double xL = xS - f * CCMath.cos(theAngle);
		double yL = yS + f * CCMath.sin(theAngle);

		return new CCVector2(xL + e * CCMath.cos(theAngle - eta), yL - e * CCMath.sin(theAngle - eta));
	}

	public CCVector3 leftConnection(CCVector3 thePosition, CCVector3 thePlaneDirection, double theAngle) {

		return new CCVector3(thePosition.x + f * CCMath.cos(theAngle) * thePlaneDirection.x, thePosition.y + f
				* CCMath.sin(theAngle), thePosition.z + f * CCMath.cos(theAngle) * thePlaneDirection.z);
	}

	public CCVector3 rightConnection(CCVector3 thePosition, CCVector3 thePlaneDirection, double theAngle) {
		double xL = thePosition.x + f * CCMath.cos(theAngle) * thePlaneDirection.x;
		double yL = thePosition.y + f * CCMath.sin(theAngle);
		double zL = thePosition.z + f * CCMath.cos(theAngle) * thePlaneDirection.z;

		return new CCVector3(xL - e * CCMath.cos(theAngle - eta) * thePlaneDirection.x, yL - e
				* CCMath.sin(theAngle - eta), zL - e * CCMath.cos(theAngle - eta) * thePlaneDirection.z);
	}

	// public CCPositionRopeLengthAngle dataByRopeLength(double
	// theLeftRopeLength, double theRightRopeLength){
	// if(_myPositionAngleMatrix == null)return new CCPositionRopeLengthAngle();
	// return _myPositionAngleMatrix.interpolate(new
	// CCPositionRopeLengthAngle(0,0,0, theLeftRopeLength,
	// theRightRopeLength,0,0), 3, 4);
	// }
	//
	// public CCPositionRopeLengthAngle dataByPosition(double theX, double
	// theY){
	// if(_myRopeLengthMatrix == null)return new CCPositionRopeLengthAngle();
	// return _myRopeLengthMatrix.interpolate(new
	// CCPositionRopeLengthAngle(theX,theY,0, 0, 0,0,0), 0, 1);
	// }

	public CCPositionRopeLengthAngle dataByRopeLength(double theLeftRopeLength, double theRightRopeLength) {
		if (_myPositionAngleMatrix == null)
			return new CCPositionRopeLengthAngle();

		double ll = CCMath.map(theLeftRopeLength, _myMinLength, _myMaxLength, 0, _myRopeResolution - 1);
		double lr = CCMath.map(theRightRopeLength, _myMinLength, _myMaxLength, 0, _myRopeResolution - 1);

		return _myPositionAngleMatrix.getObject(CCInterpolators.HERMITE, ll, lr);
	}

	public CCPositionRopeLengthAngle dataByPosition(double theX, double theY) {
		if (_myRopeLengthMatrix == null)
			return new CCPositionRopeLengthAngle();

		double px = CCMath.map(theX, _myMotor0.x, _myMotor1.x, 0, _myPointXResolution - 1);
		double py = CCMath.map(theY, _myBoundsTop, _myBoundsFloor, 0, _myPointYResolution - 1);

		return _myRopeLengthMatrix.getObject(CCInterpolators.HERMITE, px, py);
	}

	public void draw(CCGraphics g) {

		if (_myPositionAngleMatrix == null)
			return;

		g.color(255, 0, 0);
		g.beginShape(CCDrawMode.POINTS);
		for (int x = 0; x < _myPositionAngleMatrix.columns(); x++) {
			for (int y = 0; y < _myPositionAngleMatrix.rows(); y++) {

				CCPositionRopeLengthAngle myPoint = _myPositionAngleMatrix.getObject(x, y);
				if (myPoint == null)
					continue;
				g.vertex(myPoint.position().x, myPoint.position().y);
			}
		}
		g.endShape();

		g.color(0, 255, 0);
		g.beginShape(CCDrawMode.POINTS);
		for (int x = 0; x < _myRopeLengthMatrix.columns(); x++) {
			for (int y = 0; y < _myRopeLengthMatrix.rows(); y++) {

				CCPositionRopeLengthAngle myPoint = _myRopeLengthMatrix.getObject(x, y);
				if (myPoint == null)
					continue;
				// g.color(CCColor.createFromHSB(CCMath.map(myPoint.angle(),
				// -CCMath.PI / 2, CCMath.PI / 2, 0, 1), 1f, 1f));
				g.color(myPoint.data()[5], myPoint.data()[6]);
				g.vertex(myPoint.position().x, myPoint.position().y);
			}
		}
		g.endShape();

		g.line(_myMotor0.x, -_myBoundsTop, _myMotor1.x, -_myBoundsTop);
		g.line(_myMotor0.x, -_myBoundsFloor, _myMotor1.x, -_myBoundsFloor);

		g.beginShape(CCDrawMode.LINE_LOOP);
		g.vertex(bound0);
		g.vertex(bound1);
		g.vertex(bound2);
		g.vertex(bound3);
		g.endShape();

	}
}
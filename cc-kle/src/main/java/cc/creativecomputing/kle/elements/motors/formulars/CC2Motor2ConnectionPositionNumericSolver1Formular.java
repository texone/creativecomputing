package cc.creativecomputing.kle.elements.motors.formulars;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionSetup;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CC2Motor2ConnectionPositionNumericSolver1Formular extends CC2Motor2ConnectionPositionFormular{
	
	@CCProperty(name = "depth rotation", min = 0, max = 40)
	private int _cDepthRotation = 5;
	@CCProperty(name = "depth position", min = 0, max = 40)
	private int _cDepthPosition = 5;

	double c;

//	double epsilon; // Winkel zwischen den beiden Verbindungen Schwerpunkt -
					// Anlenkpunkt - //=2*ARCSIN((e_/2)/c_)

	public CC2Motor2ConnectionPositionNumericSolver1Formular() {
	}



	private class Values {
		CCVector2 L; // Position L (Aufhängepunkt Links)
		CCVector2 R; // Position R (Aufhängepunkt Rechts)
		double tanAlpha; // Winkel zwischen Senkrechter und linkem Seil
		double tanBeta; // Winkel zwischen Senkrechter und rechtem Seil
		double tanAlphaBeta;
		double res;
		
		final CCVector2 pos;
		
		final double Fby_n; // Y - Komp.der Kraft in B, normiert mit G auf[0…1] /=(xs+t_/2)/t_
		final double Fay_n; // DITO PUNKT A //= (t_ / 2 - xs) / t_
		final double v; // this is the target we want to be close to

		final CC2Motor2ConnectionSetup _mySetup;
		
		final double c;
		final double e;
		final double epsilon;
		
		Values(CC2Motor2ConnectionSetup theSetup, CCVector2 thePosition) {
			_mySetup = theSetup;
			pos = thePosition;
			L = new CCVector2();
			R = new CCVector2();
			tanAlpha = 0;
			tanBeta = 0;
			tanAlphaBeta = 0;
			
			Fby_n = (pos.x + _mySetup.pulleyDistance() / 2) / _mySetup.pulleyDistance();
			Fay_n = (_mySetup.pulleyDistance() / 2 - pos.x) / _mySetup.pulleyDistance(); // DITO PUNKT A //= (t_ / 2 - xs) / t_
			v = Fby_n / Fay_n;
			
			res = CCMath.abs(v - tanAlphaBeta);
			
			c = _mySetup.centroidConnection0OriginDistance();// _myCenterConnectionDistance; //Abstand Schwerpunkt vom Aufhängepunkt
			e = _mySetup.connectionDistance();
			epsilon = 2 * CCMath.asin((e / 2) / c);
		}

		public Values clone() {
			Values myResult = new Values(_mySetup, pos.clone());
			myResult.L = L.clone();
			myResult.R = R.clone();
			myResult.tanAlpha = tanAlpha;
			myResult.tanBeta = tanBeta;
			myResult.tanAlphaBeta = tanAlphaBeta;
			myResult.res = res;
			return myResult;
		}

		public void set(Values theValues) {
			L = theValues.L;
			R = theValues.R;
			tanAlpha = theValues.tanAlpha;
			tanBeta = theValues.tanBeta;
			tanAlphaBeta = theValues.tanAlphaBeta;
			res = theValues.res;
		}

		public void update(double sigma) {
			// =xs-c_*COS(sigma), =ys+c_*SIN(sigma)
			L = new CCVector2(pos.x - c * CCMath.cos(sigma), pos.y + c * CCMath.sin(sigma)); 
			 // =xs-c_*COS(sigma+epsilon), =ys+c_*SIN(sigma+epsilon)
			R = new CCVector2(pos.x - c * CCMath.cos(sigma + epsilon), pos.y + c * CCMath.sin(sigma + epsilon));
			
			tanAlpha = CCMath.abs((L.x + _mySetup.pulleyDistance() / 2) / L.y);
			tanBeta = CCMath.abs((R.x - _mySetup.pulleyDistance() / 2) / R.y);
			// this is the calculated value (must be near v)
			tanAlphaBeta = tanAlpha / tanBeta;
			res = CCMath.abs(v - tanAlphaBeta);
		}

		public double ropeLengthLeft() {
			return CCMath.sqrt(L.x * L.x + L.y * L.y);// =WURZEL((B39-B38)^2+(C39-C38)^2)
		}

		public double ropeLengthRight() {
			return CCMath.sqrt(R.x * R.x + R.y * R.y);// =WURZEL((B41-B44)^2+(C41-C44)^2)
		}

		public double ropeForceLeft() {
			return Fay_n / CCMath.cos(tanAlpha) / 2 * 100; // =B18/COS(B20)/2*100
		}

		public double ropeForceRight() {
			return Fby_n / CCMath.cos(tanBeta) / 2 * 100;
		}
	}

	private double iterateRotation(Values theValues, double theStart, double theEnd, double theStep, int theDepth) {
		Values values = theValues.clone();
		Values prevValues = theValues.clone();

		for (double sigmaDegrees = theStart; sigmaDegrees <= theEnd + theStep; sigmaDegrees += theStep) {

			double sigma = CCMath.radians(sigmaDegrees);
			// set previous values before calculating
			prevValues = values.clone();

			values.update(sigma);

			if (values.tanAlphaBeta > values.v) {
				// we are over the target
				// lets see if the previous result is closer
				if (prevValues.res < values.res) {
					values = prevValues;
					theValues.set(prevValues);
				} else {
					// could add going into more precise search here

					theValues.set(values);
				}
				if (theDepth == 0)
					return sigmaDegrees;

				return iterateRotation(theValues, sigmaDegrees - theStep, sigmaDegrees, theStep / 10, theDepth - 1);
			}
		}
		return theEnd;
	}
	
	@CCProperty(name = "use negatino")
	private boolean _cUseNegation = true;

	@Override
	public double rotation(CC2Motor2ConnectionSetup theSetup) {
		double myX = theSetup.elementOffset().x;
		double myY = theSetup.elementOffset().y;
		boolean negateResult = false;
		if (myX < 0 && _cUseNegation) {
			myX *= -1;
			negateResult = true;
		}

		Values values = new Values(theSetup, new CCVector2(myX, myY));

		// ITERATE (ZEILWERTSUCHE)
		double rotation = iterateRotation(values, -60, 60, 1, _cDepthRotation);
		// = ARCTAN(B20) * 180 / PI()
		double tanAlphaDegrees = CCMath.atan(values.tanAlpha) * 180 / CCMath.PI; 
		double tanBetaDegrees = CCMath.atan(values.tanBeta) * 180 / CCMath.PI; 
		// Verdrehwinkel des pedal elements
		double phi = 90 - (rotation + CCMath.degrees(values.epsilon) / 2); 
		
		// double ropeLengthLeft = CCMath.sqrt((L.x - A.x) * (L.x - A.x) + (L.y
		// - A.y) * (L.y - A.y));//=WURZEL((B39-B38)^2+(C39-C38)^2)
		// double ropeLengthRight = CCMath.sqrt((R.x - B.x) * (R.x - B.x) + (R.y
		// - B.y) * (R.y - B.y));//=WURZEL((B41-B44)^2+(C41-C44)^2)
		// double ropeForceLeft = Fay_n / CCMath.cos(tanAlpha) / 2 * 100;
		// //=B18/COS(B20)/2*100
		// double ropeForceRight = Fby_n / CCMath.cos(tanBeta) / 2 * 100;
		//
		//
		// //set results into this object
		// targetElement.setCenterPosition (pos);
		// targetElement.setConnectionPositionL ( L);
		// targetElement.setConnectionPositionR ( R);
		// targetElement.setElementRotation ( phi);
		// targetElement.setRopeForces ( new CCVector2(ropeForceLeft,
		// ropeForceRight));
		// targetElement.setRopeLengths ( new CCVector2(ropeLengthLeft,
		// ropeLengthRight));

		return negateResult ? -phi : phi;
		//
		// return targetElement.getRopeLengths();
	}
	
	private double ll;
	private double lr;
	private double r;
	private double e;
	private double f;
	private double rho;
	
	private double calculateY(double alpha){
        //double alpha = i * Math.PI / 180.0; //degree to rad                                            
        double c = CCMath.sqrt((r * r) + (ll * ll) - 2 * r * ll * CCMath.cos(alpha));
        double lambda = CCMath.acos(((ll * ll) - (r * r) - (c * c)) / (-2 * r * c)); 
        double epsilon = CCMath.acos(((e * e) - (lr * lr) - (c * c)) / (-2 * c * lr)); 
        double eta = CCMath.acos(((f * f) - (f * f) - (e * e)) / (-2 * e * f)); 
        double gamma = CCMath.acos(((c * c) - (lr * lr) - (e * e)) / (-2 * lr * e));
        double delta = 2 * CCMath.PI - alpha - lambda - epsilon - gamma; 
        rho = alpha - (CCMath.PI - delta - eta); 
        
        return (-ll * CCMath.sin(alpha)) - f * CCMath.sin(rho);
    }
	
	private double iterateMinimum(double theFrom, double theTo, int theSteps, int theDepth){
        double myMinY = 0;
        double myMinAlpha = Double.NaN;
        double myStepsize = (theTo - theFrom) / theSteps;

        for (double myAlpha = theFrom; myAlpha < theTo; myAlpha += myStepsize){
            double myY = calculateY(myAlpha);
            if (Double.isNaN(myY))continue;
            if (myY > 0)continue;
            if (myY > myMinY){
            	if(theDepth == 0) return myMinAlpha;
            	return iterateMinimum(myMinAlpha - myStepsize, myMinAlpha + myStepsize, 10, theDepth-1);
            }
            
            myMinY = myY;
            myMinAlpha = myAlpha; 
        }
        
        return myMinAlpha;
    }
	
	private double searchMinimum(){
        //First we check roughly the range with a few values
        //this is the initial guess (180 degrees)
        double min = this.iterateMinimum(0, CCMath.PI, 60, _cDepthPosition);
        //check if we have any result
        if (Double.isNaN(min))
            return Double.NaN; //no solution


        return min;
    }
	
	public CCVector2 centerPosition(double theAlpha) {
		double myResult = calculateY(theAlpha);
		if (Double.isNaN(myResult))
			return new CCVector2(0, 0);
		else
			return new CCVector2(
				((-r / 2) + ll * CCMath.cos(theAlpha)) + f * CCMath.cos(rho),
				(-ll * CCMath.sin(theAlpha)) - f * CCMath.sin(rho)
			);
	}

	@Override
	public CCVector2 position(CC2Motor2ConnectionSetup theSetup) {
		ll = theSetup.motor0().value();
		lr = theSetup.motor1().value();
		r = theSetup.pulleyDistance();
		e = theSetup.connectionDistance();
		f = theSetup.centroidConnection0Distance();
		double alpha = searchMinimum();
//		CCRopeValues myResult = iteration(theSetup, 0, Math.PI, Math.PI / 50, theLength0, theLength1, _cDepth);
		return centerPosition(alpha);
	}
	
	public CCVector2 position(CC2Motor2ConnectionSetup theSetup, double l1, double l2) {
		ll = l1;
		lr = l2;
		r = theSetup.pulleyDistance();
		e = theSetup.connectionDistance();
		f = theSetup.centroidConnection0Distance();
		double alpha = searchMinimum();
//		CCRopeValues myResult = iteration(theSetup, 0, Math.PI, Math.PI / 50, theLength0, theLength1, _cDepth);
		return centerPosition(alpha);
	}
}
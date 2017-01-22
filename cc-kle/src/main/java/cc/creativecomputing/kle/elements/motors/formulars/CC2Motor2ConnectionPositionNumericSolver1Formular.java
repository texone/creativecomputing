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
			
			c = _mySetup.centroidConnection0Distance();// _myCenterConnectionDistance; //Abstand Schwerpunkt vom Aufhängepunkt
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

	private double iteration(Values theValues, double theStart, double theEnd, double theStep, int theDepth) {
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

				return iteration(theValues, sigmaDegrees - theStep, sigmaDegrees, theStep / 10, theDepth - 1);
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
		double rotation = iteration(values, -60, 60, 1, _cDepthRotation);
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
	
//	public static CCVector2 ComputePosition(
//		CC2Motor2ConnectionSetup theSetup, 
//		double theLeftRope, 
//		double theRightRope,
//		double arcAtABetweenLeftRopeAndXAxis
//	) {
//			
//		double f = theSetup.centroidConnection0Distance();
//		double g = theSetup.centroidConnection1Distance();
//
//		double al_sq = theLeftRope * theLeftRope;
//		double ab_sq = theSetup.pulleyDistance() * theSetup.pulleyDistance();
//		double c = Math.sqrt(ab_sq + al_sq - (2 * theSetup.pulleyDistance() * theLeftRope * Math.cos(arcAtABetweenLeftRopeAndXAxis)));
//		double c_sq = (c * c);
//		double cos_arc_c_ab = (al_sq - ab_sq - c_sq) / (-2 * theSetup.pulleyDistance() * c);
//			
//		if (cos_arc_c_ab < -1 || cos_arc_c_ab > 1) {
//			return null;
//		}
//		double e_sq = theSetup.connectionDistance() * theSetup.connectionDistance();
//		double br_sq = theRightRope * theRightRope;
//
//		double cos_arc_c_br = (e_sq - br_sq - c_sq) / (-2 * c * theRightRope);
//		if (cos_arc_c_br < -1 || cos_arc_c_br > 1) {
//			return null;
//		}
//		double arc_c_ab = Math.acos(cos_arc_c_ab);
//		double arc_c_br = Math.acos(cos_arc_c_br);
//		double arc_e_f = Math.acos((g * g - f * f - e_sq) / (-2 * theSetup.connectionDistance() * f));
//		double arc_e_br = Math.acos((c_sq - br_sq - e_sq) / (-2 * theSetup.connectionDistance() * theRightRope));
//		double arc_f_x = Math.PI - arc_c_ab - arc_c_br - arc_e_br + arc_e_f;
//
//		return new CCVector2(
//			-theSetup.pulleyDistance() / 2 + theLeftRope * Math.cos(arcAtABetweenLeftRopeAndXAxis) + f * Math.cos(arc_f_x),
//			0 - theLeftRope * Math.sin(arcAtABetweenLeftRopeAndXAxis) - f * Math.sin(arc_f_x)
//		);
//	}
//
//	private CCVector2 minimizePosition(
//		CC2Motor2ConnectionSetup theSetup, 
//		double theLeftRope, 
//		double theRightRope, 
//		double precision
//	) {
//		double divider = 50;
//		double arcA = Math.PI;
//		double arcPrevA = 0;
//		double arcPrevPrevA = 0;
//			
//		CCVector2 currentPos = new CCVector2(0, 0);
//		CCVector2 prevPos2d = new CCVector2(0, 0);
//		CCVector2 prevPrevPos2d = new CCVector2(0, 0);
//		CCVector2 minPosition2d = new CCVector2(0, 1);
//			
//		while (CCMath.abs(currentPos.y - minPosition2d.y) > precision) {
//
//			// divider = divider*10;
//			double currentMaxArcA = arcA;
//			arcA = arcPrevPrevA;
//			arcPrevA = arcPrevPrevA;
//			double delta = (currentMaxArcA - arcA) / divider;
//			currentPos = prevPrevPos2d;
//			prevPos2d = prevPrevPos2d;
//			while (arcA <= currentMaxArcA && currentPos.y <= prevPos2d.y) {
//				prevPrevPos2d = prevPos2d;
//				prevPos2d = currentPos;
//				arcPrevPrevA = arcPrevA;
//				arcPrevA = arcA;
//				arcA = arcA + delta;
//					
//				currentPos = ComputePosition(
//					theSetup,
//					theLeftRope, 
//					theRightRope, 
//					arcA
//				);
//				if (currentPos == null || currentPos.y > 0) {
//					currentPos = prevPos2d;
//				}
//			}
//			divider = 5;
//			if (currentPos.y > prevPos2d.y) {
//				minPosition2d = prevPos2d;
//			} else {
//				minPosition2d = currentPos;
//			}
//		}
//		if (minPosition2d.y == 1)
//			return null;
//		
//		return minPosition2d;
//	}

//	@Override
//	public CCVector2 position(CC2Motor2ConnectionSetup theSetup, double theLeftRope, double theRightRope) {
//		return minimizePosition(theSetup, theLeftRope, theRightRope, 0.00000001);
//	}
	
//	private static class CCRopeValues{
//		
//		private final CC2Motor2ConnectionSetup _mySetup;
//		private final double ll;
//		private final double lr;
//		
//		private final double r;
//		private final double e;
//		private final double f;
//		
//		private final double alpha;
//		private final double lambda;
//		private final double epsilon;
//		private final double rho;
//		private final double y;
//		
//		private CCRopeValues(CC2Motor2ConnectionSetup theSetup, double theLeftRope, double theRightRope, double theAlpha){
//			_mySetup = theSetup;
//			ll = theLeftRope;
//			lr = theRightRope;
//			alpha = theAlpha;
//			r = _mySetup.pulleyDistance();
//			e = _mySetup.connectionDistance();
//			f = _mySetup.centroidConnection0Distance();
//			
//			double r = _mySetup.pulleyDistance();
//			double c = Math.sqrt((r * r) + (ll * ll) - 2 * r * ll * Math.cos(alpha));
//	        lambda = Math.acos(((ll * ll) - (r * r) - (c * c)) / (-2 * r * c)); 
//	        epsilon = Math.acos(((e * e) - (lr * lr) - (c * c)) / (-2 * c * lr)); 
//	        double eta = Math.acos(((f * f) - (f * f) - (e * e)) / (-2 * e * f)); 
//	        double gamma = Math.acos(((c * c) - (lr * lr) - (e * e)) / (-2 * lr * e));
//	        double delta = 2 * Math.PI - alpha - lambda - epsilon - gamma; 
//	        rho = theAlpha - (Math.PI - delta - eta); 
//	        
//	        y = (-ll * Math.sin(alpha)) - f * Math.sin(rho);
//		}
//		
//		public CCVector2 connection0(){
//			return new CCVector2(
//				(-r / 2) + ll * Math.cos(alpha), 
//				-ll * Math.sin(alpha)
//			);
//        }
//		
//		public CCVector2 connection1(){
//			return new CCVector2(
//				r / 2 - Math.cos(lambda + epsilon) * lr, 
//				-Math.sin(lambda + epsilon) * lr
//			);
//        }
//
//        public CCVector2 centroid(){
//            return new CCVector2(
//            	((-r / 2) + ll * CCMath.cos(alpha)) + f * CCMath.cos(rho),
//            	(-ll * CCMath.sin(alpha)) - f * CCMath.sin(rho)
//            );
//        }
//
//        public double rotation(){
//        	return rho;
//        }
//	}
//	
//	private CCRopeValues iteration(CC2Motor2ConnectionSetup theSetup, double theStart, double theEnd, double theStep, double theLength0, double theLength1, int theDepth){
//        
//        double minY = 0;
//        
//        CCRopeValues myResult = null;
//
//        for(double alpha = theStart; alpha < theEnd; alpha += theStep){
//        	CCRopeValues myValues = new CCRopeValues(theSetup, theLength0, theLength1, alpha);
//        	if(Double.isNaN(myValues.y)){
//        		continue;
//        	}
//        	if(myValues.y > 0){
//        		continue;
//        	}
//        	
//            if (myValues.y <= minY){
//            	minY = myValues.y;
//            	if(theSetup.id() == 0)CCLog.info(minY + ":" + theDepth);
//                //following values could all be nan so we remember the lowest alpha
//            	myResult = myValues; 
//            }
//        }
//        if (theDepth == 0)
//			return myResult;
//        
//        if(myResult != null){
//			return iteration(theSetup, myResult.alpha - theStep, myResult.alpha + theStep, theStep / 10, theLength0, theLength1, theDepth - 1);
//        }
//        return myResult;
//    }
//
//	@Override
//	public CCVector2 position(CC2Motor2ConnectionSetup theSetup, double theLength0, double theLength1) {
//		CCRopeValues myResult = iteration(theSetup, 0, Math.PI, Math.PI / 50, theLength0, theLength1, _cDepth);
//		if(myResult == null)return new CCVector2(); 
//		return myResult.centroid();
//	}
	
	
	
	
	
	public double Iterate(double from, double to, double stepsize, int depth){
        double myMinY = 0;
        double myMinAlpha = Double.NaN;

        for (double alpha = from; alpha < to; alpha += stepsize){
            double y = Calculate(alpha);
            if (Double.isNaN(y))continue;
            if (y > 0)continue;
            if (y > myMinY){
            	if(depth == 0)return myMinAlpha;
            	return Iterate(myMinAlpha - stepsize, myMinAlpha + stepsize, stepsize / 10, depth-1);
            }
            
            myMinY = y;
            myMinAlpha = alpha; 
        }
        
        return myMinAlpha;
    }
	
	public double SearchMinimum(){
        //First we check roughly the range with a few values
        //this is the initial guess (180 degrees)
        double from = 0;
        double to = Math.PI;
        double stepsize = (to - from) / 60;
        double min = this.Iterate(from, to, stepsize, _cDepthPosition);
        //check if we have any result
        if (Double.isNaN(min))
            return Double.NaN; //no solution

//        //now we iterate on the found minimum with offset (+-~11 degrees)
//        from = min - stepsize;
//        to = min + stepsize;
//        stepsize = (to - from) / 120;
//        min = this.Iterate(from, to, stepsize);
//
//        if (Double.isNaN(min)) //this should not happen at this point since we already checked before
//            return Double.NaN; //no solution
//
//        //lets move in close
//        from = min - stepsize;
//        to = min + stepsize;
//        stepsize = (to - from) / 150;
//        min = this.Iterate(from, to, stepsize);
//
//        if (Double.isNaN(min)) //this should not happen at this point since we already checked before
//            return Double.NaN; //no solution
//
//        //one last time
//        from = min - stepsize;
//        to = min + stepsize;
//        stepsize = (to - from) / 120;
//        min = this.Iterate(from, to, stepsize);
//
//        if (Double.isNaN(min)) //this should not happen at this point since we already checked before
//            return Double.NaN; //no solution


        return min;
    }
	
	public CCVector2 GetCenterPosition(double alpha)
    {
        double notnan = this.Calculate(alpha);
        if (Double.isNaN(notnan))
            return new CCVector2(0,0);
        else
            return new CCVector2(((-r / 2) + ll * Math.cos(alpha)) + f * Math.cos(rho),
                (-ll * Math.sin(alpha)) - f * Math.sin(rho));
    }
	
	private double Calculate(double alpha)
    {
        //double alpha = i * Math.PI / 180.0; //degree to rad                                            
        double c = Math.sqrt((r * r) + (ll * ll) - 2 * r * ll * Math.cos(alpha));
        double lambda = Math.acos(((ll * ll) - (r * r) - (c * c)) / (-2 * r * c)); 
        double epsilon = Math.acos(((e * e) - (lr * lr) - (c * c)) / (-2 * c * lr)); 
        double eta = Math.acos(((f * f) - (f * f) - (e * e)) / (-2 * e * f)); 
        double gamma = Math.acos(((c * c) - (lr * lr) - (e * e)) / (-2 * lr * e));
        double delta = 2 * Math.PI - alpha - lambda - epsilon - gamma; 
        rho = alpha - (Math.PI - delta - eta); 
        
        return (-ll * Math.sin(alpha)) - f * Math.sin(rho);
    }
	
	double ll;
	double lr;
	double r;
	double e;
	double f;
	double rho;

	@Override
	public CCVector2 position(CC2Motor2ConnectionSetup theSetup) {
		ll = theSetup.motor0().value();
		lr = theSetup.motor1().value();
		r = theSetup.pulleyDistance();
		e = theSetup.connectionDistance();
		f = theSetup.centroidConnection0Distance();
		double alpha = SearchMinimum();
//		CCRopeValues myResult = iteration(theSetup, 0, Math.PI, Math.PI / 50, theLength0, theLength1, _cDepth);
		return GetCenterPosition(alpha);
	}
	
	public CCVector2 position(CC2Motor2ConnectionSetup theSetup, double l1, double l2) {
		ll = l1;
		lr = l2;
		r = theSetup.pulleyDistance();
		e = theSetup.connectionDistance();
		f = theSetup.centroidConnection0Distance();
		double alpha = SearchMinimum();
//		CCRopeValues myResult = iteration(theSetup, 0, Math.PI, Math.PI / 50, theLength0, theLength1, _cDepth);
		return GetCenterPosition(alpha);
	}
	
	public static void main(String[] args) {
		
	}
}
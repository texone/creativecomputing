package cc.creativecomputing.kle.elements.motors;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CC2Motor2ConnectionCalculations{
		
		private static double[] connectionDistances = new double[] { 277.642, 264.554, 243.867, 224.153, 211.715, 201.893, 195.422, 192.312 };
		private static double[] centerDistances = new double[] { 160.296, 152.74, 140.796, 129.415, 122.234, 116.563, 112.827, 111.031 };
		public static int getMirrorId(int id){
			switch (id % 16){
			case 0:
			case 15:
				return 0;
			case 1:
			case 14:
				return 1;
			case 2:
			case 13:
				return 2;
			case 3:
			case 12:
				return 3;
			case 4:
			case 11:
				return 4;
			case 5:
			case 10:
				return 5;
			case 6:
			case 9:
				return 6;
			case 7:
			case 8:
				return 7;
			default:
				return 0;
			}
		}

		public static double connectionDistance(int id) {
			return connectionDistances[getMirrorId(id)];
		}

		public static double centerDistance(int id) {
			return centerDistances[getMirrorId(id)];
		}
	    
	    double c;
	    double e;
	    double t = 800; //distance Aufhängung Links rechts 
	    
	    double epsilon; //Winkel zwischen den beiden Verbindungen Schwerpunkt - Anlenkpunkt - //=2*ARCSIN((e_/2)/c_)
	    
	    public CC2Motor2ConnectionCalculations(int theID, double theMotorDistance){
	    	c = centerDistance(theID);//_myCenterConnectionDistance; //Abstand Schwerpunkt vom Aufhängepunkt
	        e = connectionDistance(theID);
	        t = theMotorDistance;
	        
	        epsilon = 2 * CCMath.asin((e / 2) / c);
	    }
	    
	    boolean negateResult;
	    
	    double Fby_n; //Y - Komp.der Kraft in B, normiert mit G auf[0…1] //=(xs+t_/2)/t_
	    double Fay_n; //DITO PUNKT A //= (t_ / 2 - xs) / t_
	    double v;  //this is the target we want to be close to
	    
	    private class Values{
	    	CCVector2 L; //Position L (Aufhängepunkt Links)
	    	CCVector2 R; //Position R (Aufhängepunkt Rechts)
	    	double tanAlpha; //Winkel zwischen Senkrechter und linkem Seil
	        double tanBeta; //Winkel zwischen Senkrechter und rechtem Seil
	        double tanAlphaBeta;
	        double res;
	        
	        Values(){
	        	L = new CCVector2(); 
	        	R = new CCVector2(); 
	        	tanAlpha = 0;
	        	tanBeta = 0;
	        	tanAlphaBeta = 0;
	        	res = CCMath.abs(v - tanAlphaBeta);
	        }
	    	
	        public Values clone(){
	        	Values myResult = new Values();
	        	myResult.L = L.clone();
	        	myResult.R = R.clone();
	        	myResult.tanAlpha = tanAlpha;
	        	myResult.tanBeta = tanBeta;
	        	myResult.tanAlphaBeta = tanAlphaBeta;
	        	myResult.res = res;
	        	return myResult;
	        }
	        
	        public void set(Values theValues){
	        	L = theValues.L;
	        	R = theValues.R;
	        	tanAlpha = theValues.tanAlpha;
	        	tanBeta = theValues.tanBeta;
	        	tanAlphaBeta = theValues.tanAlphaBeta;
	        	res = theValues.res;
	        }
	        
	        public void update(CCVector2 pos, double sigma){
	        	L = new CCVector2(pos.x - c * CCMath.cos(sigma), pos.y + c * CCMath.sin(sigma)); //=xs-c_*COS(sigma), =ys+c_*SIN(sigma)
	            R = new CCVector2(pos.x - c * CCMath.cos(sigma + epsilon), pos.y + c * CCMath.sin(sigma + epsilon));  //=xs-c_*COS(sigma+epsilon), =ys+c_*SIN(sigma+epsilon)
	            tanAlpha = CCMath.abs((L.x + t / 2) / L.y);   
	            tanBeta = CCMath.abs((R.x - t / 2) / R.y);
	            //this is the calculated value (must be near v)
	            tanAlphaBeta = tanAlpha / tanBeta;
	        	res = CCMath.abs(v - tanAlphaBeta);
	        }
	        
	        public double ropeLengthLeft(){
	        	return CCMath.sqrt(L.x * L.x + L.y * L.y);//=WURZEL((B39-B38)^2+(C39-C38)^2)
	        }
	        
	        public double ropeLengthRight(){
	        	return CCMath.sqrt(R.x * R.x + R.y * R.y);//=WURZEL((B41-B44)^2+(C41-C44)^2)
	        }
	        
	        public double ropeForceLeft(){
	        	return Fay_n / CCMath.cos(tanAlpha) / 2 * 100; //=B18/COS(B20)/2*100
	        }
	        
	        public double ropeForceRight(){
	        	return Fby_n / CCMath.cos(tanBeta) / 2 * 100;
	        }
	    }
	    
	    private double iteration(Values theValues, CCVector2 pos, double theStart, double theEnd, double theStep, int theDepth){
	    	Values values = new Values();
	        Values prevValues = values.clone();         
	        
	        for(double sigmaDegrees = theStart; sigmaDegrees <= theEnd;sigmaDegrees+=theStep){

	            double sigma = CCMath.radians(sigmaDegrees);
	            //set previous values before calculating
	        	prevValues = values.clone();

	            values.update(pos, sigma);
	            
	            if(values.tanAlphaBeta > v) {
	                //we are over the target
	                //lets see if the previous result is closer
	                if (prevValues.res < values.res){
	                	values = prevValues;
	                	theValues.set(prevValues);
	                }else{
	                    //could add going into more precise search here

	                	theValues.set(values);
	                }
	                if(theDepth == 0)return sigmaDegrees;
	                
	                return iteration(theValues, pos, sigmaDegrees - theStep, sigmaDegrees +  theStep, theStep / 10, theDepth - 1);
	            }
	        }
	        return theEnd;
	    }
		
		public double rotation(CCVector2 pos, int theDepth){
	        
	        negateResult = false;
	        if(pos.x < 0){
	        	pos.x *= -1;
	        	negateResult = true;
	        }  
	        
	        Fby_n = (pos.x + t / 2) / t; 
	        Fay_n = (t / 2 - pos.x) / t; //DITO PUNKT A //= (t_ / 2 - xs) / t_
	        v = Fby_n / Fay_n;
	        
	        Values values = new Values();
	        
	        //ITERATE (ZEILWERTSUCHE)
	        double rotation = iteration(values, pos, -60, 60, 1, theDepth);
	     
	        double tanAlphaDegrees = CCMath.atan(values.tanAlpha) * 180 / CCMath.PI; //= ARCTAN(B20) * 180 / PI()
	        double tanBetaDegrees = CCMath.atan(values.tanBeta) * 180 / CCMath.PI; //= ARCTAN(B20) * 180 / PI()                    
	        double phi = 90 - (rotation + CCMath.degrees(epsilon) / 2); //Verdrehwinkel des pedal elements
//	        double ropeLengthLeft = CCMath.sqrt((L.x - A.x) * (L.x - A.x) + (L.y - A.y) * (L.y - A.y));//=WURZEL((B39-B38)^2+(C39-C38)^2)
//	        double ropeLengthRight = CCMath.sqrt((R.x - B.x) * (R.x - B.x) + (R.y - B.y) * (R.y - B.y));//=WURZEL((B41-B44)^2+(C41-C44)^2)
//	        double ropeForceLeft = Fay_n / CCMath.cos(tanAlpha) / 2 * 100; //=B18/COS(B20)/2*100
//	        double ropeForceRight = Fby_n / CCMath.cos(tanBeta) / 2 * 100;
//	        
//
//	        //set results into this object
//	        targetElement.setCenterPosition (pos);
//	        targetElement.setConnectionPositionL ( L);
//	        targetElement.setConnectionPositionR ( R);
//	        targetElement.setElementRotation ( phi);
//	        targetElement.setRopeForces ( new CCVector2(ropeForceLeft, ropeForceRight));
//	        targetElement.setRopeLengths ( new CCVector2(ropeLengthLeft, ropeLengthRight));
	     
	        return negateResult ? -phi : phi;
	//
//	        return targetElement.getRopeLengths();
	    }
	}
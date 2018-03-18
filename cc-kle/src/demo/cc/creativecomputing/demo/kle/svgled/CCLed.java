package cc.creativecomputing.demo.kle.svgled;

import cc.creativecomputing.math.CCVector2;

public class CCLed implements Comparable<CCLed>{
	CCLedType type;
	CCVector2 center;
	double diameter;
	int channel;
	
	public CCLed(CCVector2 theCenter, CCLedType theType, double theDiameter, int theChannel){
		center = theCenter;
		type = theType;
		diameter = theDiameter;
		channel = theChannel;
	}

	@Override
	public int compareTo(CCLed o) {
		return Integer.compare(channel, o.channel);
	}
	
	
}
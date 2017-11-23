package cc.creativecomputing.effects;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCSimpleGradientEffect extends CCEffect{
	
	
	@CCProperty(name = "offset 1 amp", min = 0, max = 1)
	private double _cOffset1Amp = 1;
	@CCProperty(name = "offset 2 amp", min = 0, max = 1)
	private double _cOffset2Amp = 1;
	@CCProperty(name = "offset add", min = -1, max = 1)
	private double _cOffset1Add = 0;
	

	@CCProperty(name = "gradient")
	private CCGradient _myGradient = new CCGradient();
	private int _myResultLength = 0;
	
	@CCProperty(name = "amp", min = 0, max = 10)
	private double _cAmp = 1;
	
	@CCProperty(name = "h shift", min = -1, max = 1)
	private double _cHShift = 0;
	@CCProperty(name = "s shift", min = -1, max = 1)
	private double _cSShift = 0;
	@CCProperty(name = "b shift", min = -1, max = 1)
	private double _cBShift = 0;
	
	@Override
	public String[] modulationSources(String[]theValueNames) {
		return new String[] {"offset1 modulation", "offset2 modulation"};
	}
	
	public double[] applyTo(CCEffectable theEffectable){
		double[] myResult = new double[_myResultLength];
		
		double myGradientBlend = modulation("offset1 modulation").modulation(theEffectable);
//		myGradientBlend = CCMath.blend(myGradientBlend, 1 - myGradientBlend, modulation("offset2").modulation(theEffectable));
		myGradientBlend += 1;
		myGradientBlend += modulation("offset2 modulation").modulation(theEffectable);
		myGradientBlend %= 1;
//		CCLog.info(myGradientBlend);
		CCColor myColor = _myGradient.color(myGradientBlend);
		
		double[] hsb = myColor.hsb();
		myColor.setHSB(
			(hsb[0] + _cHShift) % 1, 
			CCMath.saturate(hsb[1] + _cSShift),
			CCMath.saturate(hsb[2] + _cBShift),
			myColor.a
		);
		
		double myBlend = elementBlend(theEffectable);
		for(int i = 0; i < _myResultLength;i++){
			switch(i % 4){
			case 0:
				myResult[i] = CCMath.saturate(myColor.r * myBlend * _cAmp);
				break;
			case 1:
				myResult[i] = CCMath.saturate(myColor.g * myBlend * _cAmp);
				break;
			case 2:
				myResult[i] = CCMath.saturate(myColor.b * myBlend * _cAmp);
				break;
			case 3:
				myResult[i] = CCMath.saturate(myColor.a * myBlend * _cAmp);
				break;
			}
		}
		
		return myResult;
	}
	
	@Override
	public void valueNames(CCEffectManager<?> theEffectManager, String... theValueNames) {
		_myResultLength = theValueNames.length;
	}
}

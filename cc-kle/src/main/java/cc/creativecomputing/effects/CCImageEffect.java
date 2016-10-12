package cc.creativecomputing.effects;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.image.CCImageAsset;
import cc.creativecomputing.math.CCColor;

public class CCImageEffect extends CCEffect{
	
//	@CCProperty(name = "x motion modulation")
//	private CCKleMotionModulation _cXMotionModulation = new CCKleMotionModulation();
	
	@CCProperty(name = "amp", min = 1, max = 2)
	private double _cAmp = 1;
	@CCProperty(name = "h shift", min = -1, max = 1)
	private double _cHShift = 0;
	@CCProperty(name = "s shift", min = -1, max = 1)
	private double _cSShift = 0;
	@CCProperty(name = "b shift", min = -1, max = 1)
	private double _cBShift = 0;
	
	@CCProperty(name = "image")
	private CCImageAsset _myImage = new CCImageAsset();
	
	private int _myResultLength = 0;

	@Override
	public double[] applyTo(CCEffectable theEffectable) {
		if(_myImage.value() == null)return new double[0];
		CCColor myResult = _myImage.value().getPixel(
			modulation("x").modulation(theEffectable) * _myImage.value().width(), //+ _cXMotionModulation.modulation(theEffectable)
			modulation("y").modulation(theEffectable) * _myImage.value().height()
		);

//		double myBlend = elementBlend(theElement);
//		myResult.r = CCMath.saturate(myResult.r * myBlend * _cAmp);
//		myResult.g = CCMath.saturate(myResult.g * myBlend * _cAmp);
//		myResult.b = CCMath.saturate(myResult.b * myBlend * _cAmp);
//		
//		double[] hsb = myResult.hsb();
//		myResult.setHSB(
//			(hsb[0] + _cHShift) % 1, 
//			CCMath.saturate(hsb[1] + _cSShift),
//			CCMath.saturate(hsb[2] + _cBShift)
//		);
//		CCLog.info(theMessage);
		double[] myResultA = new double[_myResultLength];
		for(int i = 0; i < myResultA.length;i++){
			if(i % 4 == 0){
				myResultA[i] = myResult.r;
			}else if(i % 4 == 1){
				myResultA[i] = myResult.g;
			}else if(i % 4 == 2){
				myResultA[i] = myResult.b;
			}else{
				myResultA[i] = myResult.a;
			}
		}
		return myResultA;
	}

	@Override
	public void valueNames(CCEffectables<?> theEffectables, String... theValueNames) {
		_myResultLength = theValueNames.length;
		super.valueNames(theEffectables, "x", "y");
	}
}

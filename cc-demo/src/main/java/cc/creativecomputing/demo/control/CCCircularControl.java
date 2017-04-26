package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCCircularControl  {
	private CCVector2 _myStart = null;
	private CCVector2 _myLast = null;
	private CCVector2 _myEnd = null;

	private double _myValue = 0;
	
	private int _myDigit = 1;

	public void init(CCGL2Adapter theApp, CCGraphics g, CCAnimator theAnimator) {
		theApp.mousePressed().add(e -> {
			_myStart = new CCVector2(e.x(), e.y());
			_myLast = new CCVector2(e.x(), e.y());
			_myEnd = new CCVector2(e.x(), e.y());
			_myValue = 0;
		});
		
		theApp.mouseReleased().add(e -> {
			_myStart = null;
			_myEnd = null;
		});
		
		theApp.mouseDragged().add(e ->{
			_myEnd = new CCVector2(e.x(), e.y()); 
			if(CCMath.abs(_myEnd.y - _myLast.y) < CCMath.abs(_myEnd.x - _myLast.x)){
				_myDigit = (int)((_myEnd.x - _myStart.x) / 10 * -1);
				_myDigit = CCMath.constrain(_myDigit, -4, 4);
				CCLog.info(_myDigit);
			}else{
				double myScale = CCMath.pow(10d, _myDigit);
				_myValue += (_myEnd.y - _myLast.y) * myScale;
				_myValue = CCMath.constrain(_myValue, -9999.9999, 9999.9999);
			}
			_myLast = _myEnd;
		});
	}
	
	public void update(CCAnimator theAnimator) {
		if(_myStart != null){
			
		}
	}
	
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
		
		if(_myStart == null)return;
		
		String myValueString = CCFormatUtil.nd(_myValue, 4, 4);
		int mySplit = _myDigit * -1 + 4;
		if(mySplit > 4) mySplit+=1;
		if(_myValue < 0) mySplit+=1;
		
		myValueString = myValueString.substring(0, + mySplit) + "|" + myValueString.substring( mySplit);
		g.text(myValueString, 200,200);
	}
	
	public static void main(String[] args) {
		CCLog.info(CCMath.pow(10d, -2));
	}
}

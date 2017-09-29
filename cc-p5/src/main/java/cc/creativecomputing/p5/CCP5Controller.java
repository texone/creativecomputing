package cc.creativecomputing.p5;

import java.lang.reflect.Method;
import java.nio.file.Paths;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.controlui.CCColorMap;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.util.CCReflectionUtil;
import cc.creativecomputing.core.util.CCReflectionUtil.CCField;
import processing.core.PApplet;

public class CCP5Controller extends CCAnimator{
	
	private CCField<?> _myFrameRateField;
	
	private Object _myParent;
	
	private CCControlApp _myApp;
	
	private String presetPath;

	public CCP5Controller(PApplet theApplet){
		super();
		CCColorMap.HSB_DEPTH = 2;
		CCObjectPropertyHandle.dataPath = Paths.get(theApplet.sketchPath());
		if(presetPath == null){
			presetPath = "settings/" + theApplet.getClass().getName() + "/";
		}
		try{
			Method myMethod = theApplet.getClass().getMethod("registerMethod", String.class, Object.class);
			myMethod.invoke(theApplet, "pre", this);
			
			_myFrameRateField = CCReflectionUtil.getField(theApplet, "frameRate");
			
			_myParent = theApplet;
			
			_myApp = new CCControlApp(theApplet, this, theApplet.getClass());
			_myApp.setData(theApplet, presetPath);
			_myApp.update(0);
//			_myApp.afterInit();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	private double _myFrameRate = 1;
	
	private double _myTime = 0;
	
	private int _myFrames = 0;
	
	public void pre(){
		try {
			_myFrameRate = _myFrameRateField.floatValue();
			_myTime += 1 / _myFrameRate;
			
			listener().proxy().update(this);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isAnimating() {
		return true;
	}

	@Override
	public double frameRate() {
		return _myFrameRate;
	}

	@Override
	public double deltaTime() {
		return 1 / _myFrameRate;
	}

	@Override
	public double deltaTimeVariation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double time() {
		return _myTime;
	}

	@Override
	public int frames() {
		return _myFrames;
	}


	@Override
	public void start() {
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}

package cc.creativecomputing.kle.analyze;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.kle.CCKleChannel;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.lights.CCLightChannel;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.time.CCTimedMotionData;
import cc.creativecomputing.math.util.CCHistogram;

public class CCKleAnalyzer extends CCMotionHistoryRenderer{
	
	public class CCMotionDataAnalyzer{
		protected final List<CCTimedMotionData> data;
		protected final CCKleEffectable _myElement;
		
		protected CCMotionDataAnalyzer(CCKleEffectable theElement){
			data = new ArrayList<CCTimedMotionData>();
			_myElement = theElement;
		}
		
		public void reset(){
			data.clear();
		}
		
		public void update(double theDeltaTime){
			
		}
		
		public void addData(CCVector3 thePosition, double theLength, double theDeltaTime){
			if(data.size() == 0){
				data.add(new CCTimedMotionData(thePosition, theLength, 0, 0, 0, theDeltaTime));
				return;
			}
			
			CCTimedMotionData myLastData = data.get(data.size() - 1);
			double myVelocity = (theLength - myLastData.length) / theDeltaTime;
			double myAcceleration = (myVelocity - myLastData.velocity) / theDeltaTime;
			double myJerk = (myAcceleration - myLastData.acceleration) / theDeltaTime;
			CCTimedMotionData myNewData = new CCTimedMotionData(thePosition, theLength, myVelocity, myAcceleration, myJerk, theDeltaTime);
			
			while(_myUseHistorySize && data.size() >= _cHistorySize){
				data.remove(0);
			}
			data.add(myNewData);
		}
	}
	
	private abstract class CCChannelAnalyzer<ChannelType extends CCKleChannel> extends CCMotionDataAnalyzer{
		protected final ChannelType _myChannel;
		
		public CCChannelAnalyzer(CCKleEffectable theElement, ChannelType theChannel){
			super(theElement);
			_myChannel = theChannel;
		}
		
		@Override
		public void update(double theDeltaTime){
		}
		
		public void drawLength(CCGraphics g, double theHeight){
			if(!_cShowLength)return;
			
			int i = 0;
			g.color(0f, _cAlpha);
			double mySize = CCMath.max(_cHistorySize, data.size());
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(CCTimedMotionData myData:data){
				g.vertex(CCMath.map(i, 0, mySize, -g.width() / 2, g.width() / 2),CCMath.map(myData.length,_myChannel.min(),_myChannel.max(),0,theHeight));
				i++;
			}
			g.endShape();
		}
	}
	
	private class CCMotorChannelAnalyzer extends CCChannelAnalyzer<CCMotorChannel>{

		public CCMotorChannelAnalyzer(CCKleEffectable theElement, CCMotorChannel theChannel) {
			super(theElement, theChannel);
		}
		@Override
		public void update(double theDeltaTime){
			addData(_myChannel.connectionPosition(), _myChannel.value(), theDeltaTime);
		}
	}
	
	private class CCLightChannelAnalyzer extends CCChannelAnalyzer<CCLightChannel>{

		public CCLightChannelAnalyzer(CCKleEffectable theElement, CCLightChannel theChannel) {
			super(theElement,theChannel);
		}
		@Override
		public void update(double theDeltaTime){
			addData(new CCVector3(), _myChannel.value(), theDeltaTime);
		}
	}
	
	protected class CCElementAnalyzer extends CCMotionDataAnalyzer{
		private List<CCChannelAnalyzer<?>> _myChannelAnalyzers = new ArrayList<>();
		
		
		protected CCElementAnalyzer(CCKleEffectable theElement, CCKleChannelType theChannelType){
			super(theElement);
			
			switch(_myChannelType){
			case MOTORS:
				for(CCMotorChannel myChannel:theElement.motorSetup().channels()){
					_myChannelAnalyzers.add(new CCMotorChannelAnalyzer(theElement, myChannel));
				}
				break;
			case LIGHTS:
				for(CCLightChannel myChannel:theElement.lightSetup().channels()){
					_myChannelAnalyzers.add(new CCLightChannelAnalyzer(theElement, myChannel));
				}
				break;
			}
		}
		
		@Override
		public void reset() {
			super.reset();
			for(CCChannelAnalyzer<?> myChannelAnalyzer:_myChannelAnalyzers){
				myChannelAnalyzer.reset();
			}
		}
		
		@Override
		public void update(double theDeltaTime){
			if(data.size() == 0){
				switch(_myChannelType){
				case MOTORS:
					data.add(new CCTimedMotionData(_myElement.motorSetup().position(), 0, 0, 0, 0, theDeltaTime));
					break;
				case LIGHTS:
					data.add(new CCTimedMotionData(new CCVector3(), 0, 0, 0, 0, theDeltaTime));
					break;
				}
				return;
			}
			CCTimedMotionData myLastData = data.get(data.size() - 1);
			
			double myVelocity = (_myElement.motorSetup().position().distance(myLastData.position)) / theDeltaTime;
			double myAcceleration = (myVelocity - myLastData.velocity) / theDeltaTime;
			double myJerk = (myAcceleration - myLastData.acceleration) / theDeltaTime;
			CCTimedMotionData myNewData = new CCTimedMotionData(_myElement.motorSetup().position(), 0, myVelocity, myAcceleration, myJerk, theDeltaTime);
			while(_myUseHistorySize && data.size() >= _cHistorySize){
				data.remove(0);
			}
			data.add(myNewData);
			
			for(CCChannelAnalyzer<?> myChannelAnalyzer:_myChannelAnalyzers){
				myChannelAnalyzer.update(theDeltaTime);
			}
		}
		
		public void addData(){
			
		}
	}
	
	/**
	 * Current Mode for kle data analyzation
	 * @author christianr
	 *
	 */
	public enum CCAnalyzeMode{
		/**
		 * Shows the data integrated into the 3D view
		 */
		ANALYZE_3D, 
		ANALYZE_CURVES, 
		ANALYZE_CURVES_ON_TOP, 
		ANALYZE_MIN_MAX, 
		ANALYZE_SPECTROGRAM, 
		ANALYZE_HISTOGRAM, 
		OFF
    }

	protected List<CCKleEffectable> _myElements;
	
	@CCProperty(name = "mode")
	protected CCAnalyzeMode _cAnalyzeMode = CCAnalyzeMode.ANALYZE_3D;
	@CCProperty(name = "analyze channels")
	private boolean _cAnalyzeChannels = false;
	@CCProperty(name = "update")
	protected boolean _cUpdate = true;
	
	protected boolean _myUseHistorySize;
	
	@CCProperty(name = "number of elements", min = 1, max = 1000)
	private int _cNumberOfElements = 1000;
	@CCProperty(name = "element", min = 0, max = 1000)
	private int _cElement = 1000;
	
	@CCProperty(name = "curve scale", min = 0.1f, max = 10)
	private double _cCurveScale = 1;
	
	@CCProperty(name = "background alpha", min = 0, max = 1)
	private double _cBackgroundAlpha = 1f;
	
	@CCProperty(name = "show length")
	private boolean _cShowLength = false;
	
	protected final List<CCElementAnalyzer> _myElementAnalyzers = new ArrayList<CCKleAnalyzer.CCElementAnalyzer>();
	
	private CCKleChannelType _myChannelType;
	
	public CCKleAnalyzer(List<CCKleEffectable> theElements, CCKleChannelType theType){
		_myElements = theElements;
		_myChannelType = theType;
		
		for(CCKleEffectable myElement:_myElements){
			CCElementAnalyzer myAnalyzer = new CCElementAnalyzer(myElement, _myChannelType);
			_myElementAnalyzers.add(myAnalyzer);
		}
		
		addGraph("value", theData -> {return theData.length;});
		addGraph("velocity", theData -> {return theData.velocity;});
		addGraph("acceleration", theData -> {return theData.acceleration;});
		addGraph("jerk", theData -> {return theData.jerk;});
	}
	
	public void reset(){
		for(CCElementAnalyzer myAnalyzer:_myElementAnalyzers){
			myAnalyzer.reset();
		}
	}
	
	public CCAnalyzeMode mode(){
		return _cAnalyzeMode;
	}
	@CCProperty(name = "histogram", hide = true)
	private CCHistogram _cHistogram = new CCHistogram();
	@CCProperty(name = "histogram frame")
	private boolean _cHistogramFrame = false;
	
	public void draw3D(CCGraphics g){
		if(_cAnalyzeMode != CCAnalyzeMode.ANALYZE_3D)return;
		g.pushAttribute();
		g.pointSize(5);
		for(int i = _cElement; i < _myElementAnalyzers.size() && i < _cElement + _cNumberOfElements;i++){
			CCElementAnalyzer myAnalyzer = _myElementAnalyzers.get(i);
			g.pushMatrix();
			g.applyMatrix(myAnalyzer._myElement.matrix());
			if(_cAnalyzeChannels){
				for(CCChannelAnalyzer<?> myChannelAnalyzer:myAnalyzer._myChannelAnalyzers){
					draw3D(g, myChannelAnalyzer.data);
				}
			}else{
				draw3D(g, myAnalyzer.data);
			}
			g.popMatrix();
		}
		g.popAttribute();
	}
	
	public void draw2D(CCGraphics g){
		switch(_cAnalyzeMode){
		case ANALYZE_3D:return;
		case OFF:return;
		case ANALYZE_CURVES:
			g.pushAttribute();
			g.pointSize(5);
			if(_cBackgroundAlpha > 0){
				g.color(1f, _cBackgroundAlpha);
				g.rect(-g.width()/2,  -g.height()/2, g.width(), g.height());
			}
			
			g.color(0f, _cAlpha);
			
			int myNumberOfElements = CCMath.min(_cNumberOfElements, CCMath.max(_myElementAnalyzers.size() - _cElement,0));
			int textIndex = -1;
			for(CCHistoryValueSettings<CCTimedMotionData> mySettings:_cValueSettings.values()){
				int i = 0;
				for(int e = 0; e < myNumberOfElements;e++){
					CCElementAnalyzer myAnalyzer = _myElementAnalyzers.get(e);
					if(_cAnalyzeChannels){
				
						for(CCChannelAnalyzer<?> myChannelAnalyzer:myAnalyzer._myChannelAnalyzers){
							int myNumberOfChannels = myNumberOfElements * myAnalyzer._myChannelAnalyzers.size();
							double myHeight = g.height() / (double)myNumberOfChannels;
							g.pushMatrix();
							g.translate(0, -g.height()/2 + i * myHeight);
							i++;
							drawCurves(g, myChannelAnalyzer.data, mySettings, myHeight * _cCurveScale, textIndex * 80, _cValueSettings.size() * 80);
							g.popMatrix();
						}
					}else{
						double myHeight = g.height() / (double)myNumberOfElements;
						g.pushMatrix();
	
						g.translate(0, -g.height()/2 + i * myHeight);
						i++;
						drawCurves(g,myAnalyzer.data, mySettings, myHeight* _cCurveScale, textIndex * 80, _cValueSettings.size() * 80);
						g.popMatrix();
					}
				}
				textIndex++;
			}
			if(_cTimeBased){
				for(int j = 0; j < 10;j++){
					float myX = CCMath.map(j, 0, 9, -g.width()/2, g.width()/2);
					double mySecs = CCMath.map(j, 0, 10, 0, _cTimeScale) + _cTimeOffset;
					g.line(myX,  -g.height()/2, myX, g.height());
//					g.text(mySecs, myX, 0);
				}
			}
			g.popAttribute();
			break;
		case ANALYZE_CURVES_ON_TOP:
			g.pushAttribute();
			g.pointSize(5);
			if(_cBackgroundAlpha > 0){
				g.color(1f, _cBackgroundAlpha);
				g.rect(-g.width()/2,  -g.height()/2, g.width(), g.height());
			}
			
			g.color(0f, _cAlpha);
			
			myNumberOfElements = CCMath.min(_cNumberOfElements, CCMath.max(_myElementAnalyzers.size() - _cElement,0));
			textIndex = -1;
			g.pushMatrix();
			g.translate(0, 0);
			for(CCHistoryValueSettings<CCTimedMotionData> mySettings:_cValueSettings.values()){
				for(int e = 0; e < myNumberOfElements;e++){
					CCElementAnalyzer myAnalyzer = _myElementAnalyzers.get(e);
					if(_cAnalyzeChannels){
				
						for(CCChannelAnalyzer<?> myChannelAnalyzer:myAnalyzer._myChannelAnalyzers){
							drawCurves(g, myChannelAnalyzer.data, mySettings, g.height() / 2 * _cCurveScale, textIndex * 80, _cValueSettings.size() * 80);
						}
					}else{
						drawCurves(g,myAnalyzer.data, mySettings, g.height() / 2 * _cCurveScale, textIndex * 80, _cValueSettings.size() * 80);
					}
				}
				textIndex++;
			}
			g.popMatrix();
			if(_cTimeBased){
				for(int j = 0; j < 10;j++){
					float myX = CCMath.map(j, 0, 9, -g.width()/2, g.width()/2);
					double mySecs = CCMath.map(j, 0, 10, 0, _cTimeScale) + _cTimeOffset;
					g.line(myX,  -g.height()/2, myX, g.height());
//					g.text(mySecs, myX, 0);
				}
			}
			g.popAttribute();
			break;
		case ANALYZE_MIN_MAX:
			g.pushAttribute();
			g.pointSize(5);
			if(_cBackgroundAlpha > 0){
				g.color(1f, _cBackgroundAlpha);
				g.rect(-g.width()/2,  -g.height()/2, g.width(), g.height());
			}
			
			g.color(0f, _cAlpha);
			
			myNumberOfElements = CCMath.min(_cNumberOfElements, CCMath.max(_myElementAnalyzers.size() - _cElement,0));
			textIndex = -1;
			g.pushMatrix();
			g.translate(0, -g.height()/2);
			for(CCHistoryValueSettings<CCTimedMotionData> mySettings:_cValueSettings.values()){
				for(int e = 0; e < myNumberOfElements;e++){
					CCElementAnalyzer myAnalyzer = _myElementAnalyzers.get(e);
					if(_cAnalyzeChannels){
				
						for(CCChannelAnalyzer<?> myChannelAnalyzer:myAnalyzer._myChannelAnalyzers){
							drawCurves(g, myChannelAnalyzer.data, mySettings, g.height() * _cCurveScale, textIndex * 80, _cValueSettings.size() * 80);
						}
					}else{
						drawCurves(g,myAnalyzer.data, mySettings, g.height() * _cCurveScale, textIndex * 80, _cValueSettings.size() * 80);
					}
				}
				textIndex++;
			}
			g.popMatrix();
			if(_cTimeBased){
				for(int j = 0; j < 10;j++){
					float myX = CCMath.map(j, 0, 9, -g.width()/2, g.width()/2);
					double mySecs = CCMath.map(j, 0, 10, 0, _cTimeScale) + _cTimeOffset;
					g.line(myX,  -g.height()/2, myX, g.height());
//					g.text(mySecs, myX, 0);
				}
			}
			g.popAttribute();
			break;
		case ANALYZE_SPECTROGRAM:
			g.pushAttribute();
			g.pointSize(5);
			if(_cBackgroundAlpha > 0){
				g.color(1f, _cBackgroundAlpha);
				g.rect(-g.width()/2,  -g.height()/2, g.width(), g.height());
			}
			
			
			g.color(0f, _cAlpha);

			myNumberOfElements = CCMath.min(_cNumberOfElements, CCMath.max(_myElementAnalyzers.size() - _cElement,0));
			
			for(CCHistoryValueSettings<CCTimedMotionData> mySettings:_cValueSettings.values()){
				int i = 0;
				for(int e = 0; e < myNumberOfElements;e++){
					CCElementAnalyzer myAnalyzer = _myElementAnalyzers.get(e);
					if(_cAnalyzeChannels){
						for(CCChannelAnalyzer<?> myChannelAnalyzer:myAnalyzer._myChannelAnalyzers){
							int myNumberOfChannels = myNumberOfElements * myAnalyzer._myChannelAnalyzers.size();
							double myHeight = g.height() / (double)myNumberOfChannels;
							g.pushMatrix();
							g.translate(0, -g.height()/2 + i * myHeight);
//							g.translate(0, CCMath.map(i, 0, myNumberOfChannels, g.height/2  - g.height/ (myNumberOfChannels), -g.height/2));
							i++;
							drawSpectogram(g,myChannelAnalyzer.data, mySettings, 0, myHeight);
							g.popMatrix();
						}
					}else{
						double myHeight = g.height() / (double)myNumberOfElements;
						g.pushMatrix();
						g.translate(0, -g.height()/2 + i * myHeight);
						i++;
						
						drawSpectogram(g, myAnalyzer.data, mySettings, 0,myHeight);
						g.popMatrix();
					}
				}
			}
			g.popAttribute();
			break;
		case ANALYZE_HISTOGRAM:
			g.pushAttribute();
			g.pointSize(5);
			if(_cBackgroundAlpha > 0){
				g.color(1f, _cBackgroundAlpha);
				g.rect(-g.width()/2,  -g.height()/2, g.width(), g.height());
			}
			
			g.color(0f, _cAlpha);

			myNumberOfElements = CCMath.min(_cNumberOfElements, CCMath.max(_myElementAnalyzers.size() - _cElement,0));

			double myHeight = g.height() / _cValueSettings.size();
			double myWidth = g.width() / _cHistogram.bands();
			int i = 0;
			for(CCHistoryValueSettings<CCTimedMotionData> mySettings:_cValueSettings.values()){
				_cHistogram.reset();
				
				
				for(CCElementAnalyzer myAnalyzer:_myElementAnalyzers.subList(0, myNumberOfElements)){
					if(_cAnalyzeChannels){
						for(CCChannelAnalyzer<?> myChannelAnalyzer:myAnalyzer._myChannelAnalyzers){
							if(_cHistogramFrame){
								_cHistogram.add(CCMath.abs(mySettings.normedValue(myChannelAnalyzer.data.get(myChannelAnalyzer.data.size() - 1))));
							}else{
								for(CCTimedMotionData myData:myChannelAnalyzer.data){
									_cHistogram.add(CCMath.abs(mySettings.normedValue(myData)));
								}
							}
						}
					}else{
						if(_cHistogramFrame){
							_cHistogram.add(CCMath.abs(mySettings.normedValue(myAnalyzer.data.get(myAnalyzer.data.size() - 1))));
						}else{
							for(CCTimedMotionData myData:myAnalyzer.data){
								_cHistogram.add(CCMath.abs(mySettings.normedValue(myData)));
							}
						}
					}
				}
				g.color(mySettings._cColor.r,mySettings._cColor.g,mySettings._cColor.b, _cAlpha);
				g.pushMatrix();
				g.translate(0, -g.height()/2 + i * myHeight);
				for(int j = 0; j < _cHistogram.bands();j++){
					g.rect(CCMath.map(j, 0, _cHistogram.bands(), -g.width() / 2, g.width() / 2), 0, myWidth - 1, CCMath.map(_cHistogram.count(j), 0, _cHistogram.max(), 0, myHeight));
				}
				g.popMatrix();
				i++;
			}
			g.popAttribute();
			break;
		}
		
		
	}
}

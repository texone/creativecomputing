package cc.creativecomputing.kle.analyze;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceChannel;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.lights.CCLightChannel;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCSequenceAnalyzer extends CCAnalyzeSettings{
	
	public class CCMotionDataAnalyzer{
		protected final List<CCMotionData> data;
		protected final CCSequenceElement _myElement;
		
		protected CCMotionDataAnalyzer(CCSequenceElement theElement){
			data = new ArrayList<CCMotionData>();
			_myElement = theElement;
		}
		
		public void reset(){
			data.clear();
		}
		
		public void update(double theDeltaTime){
			
		}
		
		public void addData(CCVector3 thePosition, double theLength, double theDeltaTime){
			if(data.size() == 0){
				data.add(new CCMotionData(thePosition, theLength, 0, 0, 0, theDeltaTime));
				return;
			}
			
			CCMotionData myLastData = data.get(data.size() - 1);
			double myVelocity = (theLength - myLastData.length) / theDeltaTime;
			double myAcceleration = (myVelocity - myLastData.velocity) / theDeltaTime;
			double myJerk = (myAcceleration - myLastData.acceleration) / theDeltaTime;
			CCMotionData myNewData = new CCMotionData(thePosition, theLength, myVelocity, myAcceleration, myJerk, theDeltaTime);
			
			while(_myUseHistorySize && data.size() >= _cHistorySize){
				data.remove(0);
			}
			data.add(myNewData);
		}
		
		public void draw3D(CCGraphics g){
			_cVelocity.draw3D(g, data);
			_cAcceleration.draw3D(g, data);
			_cJerk.draw3D(g, data);
		}
		
		public void drawCurves(CCGraphics g, double theHeight){
			_cValue.drawCurves(g, data, theHeight);
			_cVelocity.drawCurves(g, data, theHeight);
			_cAcceleration.drawCurves(g, data, theHeight);
			_cJerk.drawCurves(g, data, theHeight);
		}
		
		public void drawSpectogram(CCGraphics g, double theOffset, double theHeight){
			_cValue.drawSpectogram(g, data, theOffset, theHeight);
			_cVelocity.drawSpectogram(g, data,theOffset, theHeight);
			_cAcceleration.drawSpectogram(g, data,theOffset, theHeight);
			_cJerk.drawSpectogram(g, data, theOffset, theHeight);
		}
	}
	
	private abstract class CCChannelAnalyzer<ChannelType extends CCSequenceChannel> extends CCMotionDataAnalyzer{
		protected final ChannelType _myChannel;
		
		public CCChannelAnalyzer(CCSequenceElement theElement, ChannelType theChannel){
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
			for(CCMotionData myData:data){
				g.vertex(CCMath.map(i, 0, mySize, -g.width() / 2, g.width() / 2),CCMath.map(myData.length,_myChannel.min(),_myChannel.max(),0,theHeight));
				i++;
			}
			g.endShape();
		}
		
		@Override
		public void drawCurves(CCGraphics g , double theHeight){
			super.drawCurves(g, theHeight);
			drawLength(g, theHeight);
		}
	}
	
	private class CCMotorChannelAnalyzer extends CCChannelAnalyzer<CCMotorChannel>{

		public CCMotorChannelAnalyzer(CCSequenceElement theElement, CCMotorChannel theChannel) {
			super(theElement, theChannel);
		}
		@Override
		public void update(double theDeltaTime){
			addData(_myChannel.connectionPosition(), _myChannel.value(), theDeltaTime);
		}
	}
	
	private class CCLightChannelAnalyzer extends CCChannelAnalyzer<CCLightChannel>{

		public CCLightChannelAnalyzer(CCSequenceElement theElement, CCLightChannel theChannel) {
			super(theElement,theChannel);
		}
		@Override
		public void update(double theDeltaTime){
			addData(new CCVector3(), _myChannel.value(), theDeltaTime);
		}
	}
	
	protected class CCElementAnalyzer extends CCMotionDataAnalyzer{
		private List<CCChannelAnalyzer<?>> _myChannelAnalyzers = new ArrayList<>();
		
		
		protected CCElementAnalyzer(CCSequenceElement theElement, CCKleChannelType theChannelType){
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
					data.add(new CCMotionData(_myElement.motorSetup().position(), 0, 0, 0, 0, theDeltaTime));
					break;
				case LIGHTS:
					data.add(new CCMotionData(new CCVector3(), 0, 0, 0, 0, theDeltaTime));
					break;
				}
				return;
			}
			CCMotionData myLastData = data.get(data.size() - 1);
			
			double myVelocity = (_myElement.motorSetup().position().distance(myLastData.position)) / theDeltaTime;
			double myAcceleration = (myVelocity - myLastData.velocity) / theDeltaTime;
			double myJerk = (myAcceleration - myLastData.acceleration) / theDeltaTime;
			CCMotionData myNewData = new CCMotionData(_myElement.motorSetup().position(), 0, myVelocity, myAcceleration, myJerk, theDeltaTime);
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
	
	public static enum CCAnalyzeMode{
		ANALYZE_3D, ANALYZE_CURVES, ANALYZE_SPECTROGRAM, OFF;
	}

	protected CCSequenceElements _myElements;
	
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
	@CCProperty(name = "value")
	private CCMotionHistoryGraph _cValue = new CCMotionHistoryGraph(this) {
		@Override
		public double value(CCMotionData theData) {return (double)theData.length;}
	};
	@CCProperty(name = "velocity")
	private CCMotionHistoryGraph _cVelocity = new CCMotionHistoryGraph(this) {
		@Override
		public double value(CCMotionData theData) {return (double)theData.velocity;}
	};
	@CCProperty(name = "acceleration")
	private CCMotionHistoryGraph _cAcceleration = new CCMotionHistoryGraph(this) {
		@Override
		public double value(CCMotionData theData) {return (double)theData.acceleration;}
	};
	@CCProperty(name = "jerk")
	private CCMotionHistoryGraph _cJerk = new CCMotionHistoryGraph(this) {
		@Override
		public double value(CCMotionData theData) {return (double)theData.jerk;}
	};
	
	protected final List<CCElementAnalyzer> _myElementAnalyzers = new ArrayList<CCSequenceAnalyzer.CCElementAnalyzer>();
	
	private CCKleChannelType _myChannelType;
	
	public CCSequenceAnalyzer(CCSequenceElements theElements, CCKleChannelType theType){
		_myElements = theElements;
		_myChannelType = theType;
		
		for(CCSequenceElement myElement:_myElements){
			CCElementAnalyzer myAnalyzer = new CCElementAnalyzer(myElement, _myChannelType);
			_myElementAnalyzers.add(myAnalyzer);
		}
	}
	
	public void reset(){
		for(CCElementAnalyzer myAnalyzer:_myElementAnalyzers){
			myAnalyzer.reset();
		}
	}
	
	public CCAnalyzeMode mode(){
		return _cAnalyzeMode;
	}
	
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
					myChannelAnalyzer.draw3D(g);
				}
			}else{
				myAnalyzer.draw3D(g);
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
			
			double i = 0;
			g.color(0f, _cAlpha);
			
			List<CCMotionHistoryGraph> _myTypes = new ArrayList<CCMotionHistoryGraph>();
			if(_cValue._cShow)_myTypes.add(_cValue);
			if(_cVelocity._cShow)_myTypes.add(_cVelocity);
			if(_cAcceleration._cShow)_myTypes.add(_cAcceleration);
			if(_cJerk._cShow)_myTypes.add(_cJerk);

			int myNumberOfElements = CCMath.min(_cNumberOfElements, CCMath.max(_myElementAnalyzers.size() - _cElement,0));
			for(int m = 0; m < _myTypes.size();m++){
				i = 0;
				CCMotionHistoryGraph myType = _myTypes.get(m);
				for(int e = 0; e < myNumberOfElements;e++){
					CCElementAnalyzer myAnalyzer = _myElementAnalyzers.get(e);
					if(_cAnalyzeChannels){
						for(CCChannelAnalyzer<?> myChannelAnalyzer:myAnalyzer._myChannelAnalyzers){
							int myNumberOfChannels = myNumberOfElements * myAnalyzer._myChannelAnalyzers.size();
							double myHeight = g.height() / (double)myNumberOfChannels / _myTypes.size();;
							g.pushMatrix();
							g.translate(0, -g.height()/2 + m * myHeight + _myTypes.size() * myHeight * i);
							i++;
							myType.drawCurves(g, myChannelAnalyzer.data, myHeight * _cCurveScale);
							g.popMatrix();
						}
					}else{
						double myHeight = g.height() / (double)myNumberOfElements / _myTypes.size();;
						g.pushMatrix();
	
						g.translate(0, -g.height()/2 + m * myHeight + _myTypes.size() * myHeight * i);
						i++;
						myType.drawCurves(g,myAnalyzer.data, myHeight* _cCurveScale);
						g.popMatrix();
					}
				}
			}
			if(_cTimeBased){
				for(int j = 0; j < 10;j++){
					float myX = CCMath.map(j, 0, 9, -g.width()/2, g.width()/2);
					double mySecs = CCMath.map(j, 0, 10, 0, _cTimeScale) + _cTimeOffset;
					g.line(myX,  -g.height()/2, myX, g.height());
					g.text(mySecs, myX, 0);
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
			
			_myTypes = new ArrayList<CCMotionHistoryGraph>();
			if(_cVelocity._cShow)_myTypes.add(_cVelocity);
			if(_cAcceleration._cShow)_myTypes.add(_cAcceleration);
			if(_cJerk._cShow)_myTypes.add(_cJerk);
			
			for(int m = 0; m < _myTypes.size();m++){
				i = 0;
				CCMotionHistoryGraph myType = _myTypes.get(m);
				for(int e = 0; e < myNumberOfElements;e++){
					CCElementAnalyzer myAnalyzer = _myElementAnalyzers.get(e);
					if(_cAnalyzeChannels){
						for(CCChannelAnalyzer<?> myChannelAnalyzer:myAnalyzer._myChannelAnalyzers){
							int myNumberOfChannels = myNumberOfElements * myAnalyzer._myChannelAnalyzers.size();
							double myHeight = g.height() / (double)myNumberOfChannels / _myTypes.size();
							g.pushMatrix();
							g.translate(0, -g.height()/2 + m * myHeight + _myTypes.size() * myHeight * i);
//							g.translate(0, CCMath.map(i, 0, myNumberOfChannels, g.height/2  - g.height/ (myNumberOfChannels), -g.height/2));
							i++;
							myType.drawSpectogram(g,myChannelAnalyzer.data,0, myHeight);
							g.popMatrix();
						}
					}else{
						double myHeight = g.height() / (double)myNumberOfElements  / _myTypes.size();
						g.pushMatrix();
						g.translate(0, -g.height()/2 + m * myHeight + _myTypes.size() * myHeight * i);
						i++;
						
						myType.drawSpectogram(g, myAnalyzer.data, 0,myHeight);
						g.popMatrix();
					}
				}
			}
			g.popAttribute();
			break;
		}
		
	}
}

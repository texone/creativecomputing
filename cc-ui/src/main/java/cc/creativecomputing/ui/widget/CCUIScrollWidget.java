package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIPane;

public class CCUIScrollWidget extends CCUIGridPane{
	
	
	private class CUIMaskWidget extends CCUIPane{

		private CCUIWidget _myWidget;
		
		public CUIMaskWidget(CCUIWidget theWidget, int theWidth, int theHeight){
			super(new CCUIWidgetStyle(), theWidth, theHeight);
			addChild(_myWidget = theWidget);
		}
	
		@Override
		public CCUIWidget childAtPosition(CCVector2 thePosition) {
			thePosition = _myLocalInverseMatrix.transform(thePosition);
			
			if(
				thePosition.x >= 0 && 
				thePosition.x <= width() &&
				thePosition.y <= 0 && 
				thePosition.y >= -height()
			){
				if(_myWidget instanceof CCUIPane){
					return ((CCUIPane)_myWidget).childAtPosition(thePosition);
				}
				return _myWidget;	
			}
			
			return null;
		}
		
//		public void draw(CCGraphics g){
//			updateMatrices();
//			g.pushAttribute();
//			g.color(1d);
//			//CCLog.info(_myWorldMatrix.transform(new CCVector2()));
//			g.scissor(400,10,1000, 1000);
//			g.popAttribute();
//		}
		
		@Override
		public void drawContent(CCGraphics g) {
			g.beginMask();
			g.rect(0,-_myHeight, _myWidth, _myHeight);
			g.endMask();
			super.drawContent(g);
			g.noMask();
		}
		
		@Override
		public void updateLayout() {
			_myWidget.width(_myWidth);
		}
	}
	
	public static final int DEFAULT_SLIDER_WIDTH = 14;
	
	private CUIMaskWidget _myMaskWidget;
	private CCUIWidget _myWidget;
	private CCUISlider _myHSlider;
	private CCUISlider _myVSlider;
	
	private CCVector2 sliderTranslation(){
		return new CCVector2(
			_myHSlider == null ? 0 : -_myHSlider.value() * (_myWidget.width() - _myWidth + _mySliderWidth),
			_myVSlider == null ? 0 : _myVSlider.value() * (_myWidget.height() - _myHeight + _mySliderWidth)
		);
	}
	
	private int _mySliderWidth;
	
	private int _myXSliderWidth;
	private int _myYSliderWidth;

	public CCUIScrollWidget(CCUIWidget theWidget, int theWidth, int theHeight, int theSliderWidth, boolean theUseHorizontalSlider, boolean theUseVerticalSlider){
		super(theWidth, theHeight);
		_myWidget = theWidget;
		
		_mySliderWidth = theSliderWidth;
		_myXSliderWidth = theUseVerticalSlider ? _mySliderWidth : 0;
		_myYSliderWidth = theUseHorizontalSlider ? _mySliderWidth : 0;
		
		columnWidths(_myWidth - _myXSliderWidth, _myXSliderWidth);
		
		_myMaskWidget = new CUIMaskWidget(
			theWidget, 
			theWidth - _myXSliderWidth, 
			theHeight - _myYSliderWidth
		);
		addChild(_myMaskWidget, 0,0 );

		if(theUseHorizontalSlider){
			_myHSlider = new CCUISlider(theWidth - _myXSliderWidth, _mySliderWidth, 0, 1, 0, true);
			_myHSlider.changeEvents.add(e ->{
				theWidget.translation().set(sliderTranslation());
				theWidget.updateMatrices();
			});
			addChild(_myHSlider, 0,1 );
		}
		
		if(theUseVerticalSlider){
			_myVSlider = new CCUISlider(_mySliderWidth, theHeight - _myYSliderWidth, 0, 1, 0, false);
			_myVSlider.changeEvents.add(e ->{
				theWidget.translation().set(sliderTranslation());
				theWidget.updateMatrices();
			});
			addChild(_myVSlider, 1,0 );
		}
		
		theWidget.scrollEvents.add(e ->{
			if(theUseHorizontalSlider)_myHSlider.value(_myHSlider.value() + e.x);
			if(theUseVerticalSlider)_myVSlider.value(_myVSlider.value() + e.y);
		});
	}
	
	public CCUIScrollWidget(CCUIWidget theWidget, int theWidth, int theHeight){
		this(theWidget, theWidth, theHeight, DEFAULT_SLIDER_WIDTH, true, true);
	}
	
	@Override
	public void height(double theHeight){
		_myMaskWidget.height(theHeight - _myYSliderWidth);
		if(_myVSlider != null)_myVSlider.height(theHeight - _myYSliderWidth);
		super.height(theHeight);
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		super.drawContent(g);
	}
}

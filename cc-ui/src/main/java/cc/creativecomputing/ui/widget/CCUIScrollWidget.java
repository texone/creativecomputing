package cc.creativecomputing.ui.widget;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.yoga.CCYogaNode;

public class CCUIScrollWidget extends CCUIWidget{
	
	private class CUIMaskWidget extends CCUIWidget{

		private CCUIWidget _myWidget;
		
		private CCVector2 _myTranslation;
		
		public CUIMaskWidget(CCUIWidget theWidget){
			super(new CCUIWidgetStyle());
			style().background(new CCUIFillDrawable(CCColor.YELLOW));
			addChild(_myWidget = theWidget);
			
			_myWidget.positionType(CCYogaPositionType.ABSOLUTE);
			_myTranslation = new CCVector2(0,0);
		}
		
		@Override
		public double left() {
			return _myTranslation.x;
		}
		
		@Override
		public double top() {
			return _myTranslation.y;
		}
		
		public void position(double theX, double theY) {
			_myTranslation.x = -theX * (_myWidget.width() - width() + _mySliderWidth);
			_myTranslation.y = -theY * (_myWidget.height() - height() + _mySliderWidth);
			
			updateMatrices();
		}
		
		@Override
		public void updateMatrices() {
			if(_myWidget.width() < width()) {
				_myTranslation.x = 0;
			}
			
			if(_myWidget.height() < height()) {
				_myTranslation.y = 0;
			}
			super.updateMatrices();
		}
		
		public void position(CCVector2 thePosition) {
			position(thePosition.x, thePosition.y);
		}
		
		@Override
		public void display(CCGraphics g) {
			g.pushMatrix();
			g.applyMatrix(_myLocalMatrix);
			if(_myIsOverlay)g.translate(0,0,1);
			g.beginMask();
			g.pushMatrix();
			g.translate(_myTranslation.negate());
			g.rect(0,0, width(), height());
			g.popMatrix();
			g.endMask();
			for(CCYogaNode myChild:this) {
				myChild.display(g);
			}
			g.noMask();
			g.popMatrix();
		}
		
		@Override
		public void displayContent(CCGraphics g) {
		
//			g.beginMask();
//			g.rect(0,0, width(), height());
//			g.endMask();
//			super.displayContent(g);
//			g.noMask();
		}
		
//		@Override
//		public void updateLayout() {
//			_myWidget.width(_myWidth);
//		}
	}
	
	public static final int DEFAULT_SLIDER_WIDTH = 14;
	
	private CUIMaskWidget _myMaskWidget;
	private CCUISlider _myHSlider;
	private CCUISlider _myVSlider;
	
	private CCVector2 sliderTranslation(){
		return new CCVector2(
			_myHSlider == null ? 0 : _myHSlider.value(),
			_myVSlider == null ? 0 : _myVSlider.value()
		);
	}
	
	private int _mySliderWidth;
	

	public CCUIScrollWidget(CCUIWidget theWidget, int theSliderWidth, boolean theUseHorizontalSlider, boolean theUseVerticalSlider){
		_myMaskWidget = new CUIMaskWidget(theWidget);
		_myMaskWidget.flexDirection(CCYogaFlexDirection.COLUMN);
		_myMaskWidget.flex(1);
		
		if(theUseHorizontalSlider && theUseVerticalSlider) {
			flexDirection(CCYogaFlexDirection.COLUMN);
			
			_myVSlider = createSlider(false);
			
			CCUIWidget myTopWidget = new CCUIWidget();
			myTopWidget.flexDirection(CCYogaFlexDirection.ROW);
			myTopWidget.flex(1);
			
			myTopWidget.addChild(_myMaskWidget);
			myTopWidget.addChild(_myVSlider);
			addChild(myTopWidget);
			
			_myHSlider = createSlider(true);
			_myHSlider.flex(1);
			
			CCUIWidget myCornerWidget = new CCUIWidget();
			myCornerWidget.minHeight(theSliderWidth);
			myCornerWidget.minWidth(theSliderWidth);

			CCUIWidget myBottomWidget = new CCUIWidget();
			myBottomWidget.flexDirection(CCYogaFlexDirection.ROW);
			myBottomWidget.addChild(_myHSlider);
			myBottomWidget.addChild(myCornerWidget);
			addChild(myBottomWidget);
			
			
		}else if(theUseHorizontalSlider) {
			flexDirection(CCYogaFlexDirection.COLUMN);
			
			addChild(_myMaskWidget);
			
			_myHSlider = createSlider(true);
			_myHSlider.flex(1);
			addChild(_myHSlider);
		}else {
			flexDirection(CCYogaFlexDirection.ROW);
			
			addChild(_myMaskWidget);
			_myVSlider = createSlider(false);
			addChild(_myVSlider);
		}
		
		style().background(new CCUIFillDrawable(CCColor.CYAN));;
		
		theWidget.scrollEvents.add(e ->{
			if(theUseHorizontalSlider)_myHSlider.value(_myHSlider.value() + e.x);
			if(theUseVerticalSlider)_myVSlider.value(_myVSlider.value() + e.y);
		});
	}
	
	private CCUISlider createSlider(boolean theIsHorizontal) {
		CCUISlider myHorizontalSlider = new CCUISlider(14,0,1,0, theIsHorizontal);
		myHorizontalSlider.changeEvents.add(e -> _myMaskWidget.position(sliderTranslation()));
		return myHorizontalSlider;
	}
	
	public CCUIScrollWidget(CCUIWidget theWidget){
		this(theWidget, DEFAULT_SLIDER_WIDTH, true, true);
	}
	
	@Override
	public void displayContent(CCGraphics g) {
		super.displayContent(g);
	}
}

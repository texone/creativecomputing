package cc.creativecomputing.ui.widget;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.font.CCEntypoIcon;

public class CCUITreeWidget extends CCUIWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = CCUILabelWidget.createDefaultStyle();
		return myResult;
	}
	
	public class CCUITreeNode extends CCUIWidget{
		
		private List<CCUITreeNode> children = new ArrayList<>();
		
		private final CCUIIconWidget _myIcon;
		private final CCUILabelWidget _myLabel;
		
		private CCUITreeNode(CCUIIconWidget theIcon, CCUILabelWidget theLabel){
			CCUITreeWidget.this.padding(CCYogaEdge.ALL, 5);
			
			_myIcon = theIcon;
			_myIcon.mouseReleased.add(e -> {
				isActive(!isActive());
				
				//layout();
			});
			_myIcon.style().background(null);
			
			_myLabel = theLabel;
			_myLabel.mouseReleased.add(e -> {
				CCLog.info("YO2", _myLabel.textField());
			});
			addChild(_myIcon);
			addChild(theLabel);
		}
		
		public CCUITreeNode(CCEntypoIcon theIcon, CCUIWidgetStyle theStyle, String theText){
			this(
				new CCUIIconWidget(theIcon),
				new CCUILabelWidget(theStyle, theText)
			);
		}
		
		public CCUITreeNode(CCUIWidgetStyle theStyle, String theText){
			this(CCEntypoIcon.OFF, theStyle, theText);
		}
		
		public String name(){
			return text();
		}
		
		public String text(){
			return ((CCUILabelWidget)childAt(1)).textField().text();
		}
		
		private CCUITreeNode addNode(CCUITreeNode theItem, CCEvent<?> theEvent) {
			children.add(theItem);

			//layout();
			updateMatrices();
			if(theEvent == null)return theItem;
			theItem.mouseReleased.add(event -> {
				theEvent.event(null);
			});
			return theItem;
		}
		
		public CCUITreeNode addNode(String theLabel){
			return addNode(new CCUITreeNode(_myStyle, theLabel), null);
		}
		
		public CCUITreeNode addNode(String theLabel, CCEvent theEvent) {
			return addNode(new CCUITreeNode(CCUITreeWidget.this._myStyle, theLabel), theEvent);
		}
		
		public CCUITreeNode addNode(CCEntypoIcon theIcon, String theLabel, CCEvent theEvent) {
			return addNode(new CCUITreeNode(theIcon, CCUITreeWidget.this._myStyle, theLabel), theEvent);
		}
	}
	
	private CCUITreeNode _myRootNode;
	
	public CCUITreeWidget(CCUIWidgetStyle theStyle, String theRootLabel){
		super(theStyle);
		_myRootNode = new CCUITreeNode(_myStyle, theRootLabel);
		//layout();
	}
	
	public CCUITreeWidget(String theRootLabel){
		this(createDefaultStyle(), theRootLabel);
	}
	
	public CCUITreeNode root(){
		return _myRootNode;
	}
	/*
	private void layout(){
		removeAll();
		_myMinHeight = layout(_myRootNode, _myStyle.leftInset(), -_myStyle.topInset());
		updateMatrices();
	}
	
	private double layout(CCUITreeNode theNode, double theX, double theY){
		theNode.translation().set(theX, theY);
		addChild(theNode);
		if(theNode.children.size() == 0) {
			theNode._myIcon.icon(CCEntypoIcon.ICON_DOT_SINGLE);
		}else {
			if(theNode.isActive()){
				theNode._myIcon.icon(CCEntypoIcon.ICON_TRIANGLE_DOWN);
			}else{
				theNode._myIcon.icon(CCEntypoIcon.ICON_TRIANGLE_RIGHT);
			}
		}
		double myMaxWidth = 0;
		double myX = theX + 20;
		double myY = theY - theNode.height();
		if(theNode.isActive()){
			for(CCUITreeNode myNode:theNode.children) {
				myY = layout(myNode, myX, myY);
				myY -= _cVerticalSpace;
			}
		}
		return myY;
	}
	*/
//	@Override
//	public void updateMatrices() {
//
//		_myMinSize = new CCVector2(2 * _myInset + width(), -myY - _cVerticalSpace + _myInset);
//		super.updateMatrices();
//	}
}

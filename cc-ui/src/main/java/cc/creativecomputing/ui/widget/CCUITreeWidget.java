package cc.creativecomputing.ui.widget;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.events.CCEvent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.demo.font.CCEnttypoDemo;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.layout.CCUIPane;

public class CCUITreeWidget extends CCUIPane{
	
	public class CCUITreeNode extends CCUIHorizontalFlowPane{
		
		private List<CCUITreeNode> children = new ArrayList<>();
		
		private final CCUIIconWidget _myIcon;
		private final CCUILabelWidget _myLabel;
		
		private CCUITreeNode(CCUIIconWidget theIcon, CCUILabelWidget theLabel){
			inset(5);
			space(5);
			
			_myIcon = theIcon;
			_myIcon.mouseReleased.add(e -> {
				CCLog.info("YO", _myIcon.icon());
				isActive(!isActive());
				
				layout();
			});
			
			_myLabel = theLabel;
			_myLabel.mouseReleased.add(e -> {
				CCLog.info("YO2", _myLabel.text());
			});
			addChild(_myIcon);
			addChild(theLabel);
		}
		
		public CCUITreeNode(CCEntypoIcon theIcon, CCFont<?> theFont, String theText){
			this(
				new CCUIIconWidget(theIcon),
				new CCUILabelWidget(theFont, theText)
			);
		}
		
		public CCUITreeNode(CCFont<?> theFont, String theText){
			this(CCEntypoIcon.OFF,theFont, theText);
		}
		
		public String name(){
			return text();
		}
		
		public String text(){
			return ((CCUILabelWidget)_myChildren.get(1)).text().text();
		}
		
		private CCUITreeNode addNode(CCUITreeNode theItem, CCEvent theEvent) {
			children.add(theItem);

			layout();
			updateMatrices();
			if(theEvent == null)return theItem;
			theItem.mouseReleased.add(event -> {
				theEvent.event();
			});
			return theItem;
		}
		
		public CCUITreeNode addNode(String theLabel){
			return addNode(new CCUITreeNode(_myFont, theLabel), null);
		}
		
		public CCUITreeNode addNode(String theLabel, CCEvent theEvent) {
			return addNode(new CCUITreeNode(_myFont, theLabel), theEvent);
		}
		
		public CCUITreeNode addNode(CCEntypoIcon theIcon, String theLabel, CCEvent theEvent) {
			return addNode(new CCUITreeNode(theIcon, _myFont, theLabel), theEvent);
		}
	}
	
	private CCFont<?> _myFont;
	
	private CCUITreeNode _myRootNode;
	
	public CCUITreeWidget(CCFont<?> theFont, String theRootLabel){
		_myFont = theFont;
		_myRootNode = new CCUITreeNode(theFont, theRootLabel);
		layout();
	}
	
	public CCUITreeNode root(){
		return _myRootNode;
	}
	
	private void layout(){
		removeAll();
		_myMinSize.y = layout(_myRootNode, _myInset, -_myInset);
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
	
//	@Override
//	public void updateMatrices() {
//
//		_myMinSize = new CCVector2(2 * _myInset + width(), -myY - _cVerticalSpace + _myInset);
//		super.updateMatrices();
//	}
}

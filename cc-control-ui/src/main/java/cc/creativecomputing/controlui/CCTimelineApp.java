package cc.creativecomputing.controlui;

import cc.creativecomputing.controlui.timeline.controller.CCTimelineFileManager;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.view.transport.CCTransportView;
import cc.creativecomputing.controlui.view.menu.CCFileMenu;
import cc.creativecomputing.controlui.view.menu.CCTimelineMenu;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.widget.CCUIMenu;
import cc.creativecomputing.ui.widget.CCUIMenuBar;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaFlexDirection;

public class CCTimelineApp extends CCGLApp{


	protected CCTimelineFileManager _myFileManager;
	private CCTimelineContainer _myTimelineContainer;
	private CCUIMenuBar _myMenuBar;
	
	private CCTransportView _myTransport;
	
	private CCUIContext _myContext;
	
	private void init(Object theObject){
        _myContext = new CCUIContext(this, CCYogaFlexDirection.COLUMN);
		_myTimelineContainer = new CCTimelineContainer(this, _myRootHandle);
		
		 CCUIWidgetStyle myMenuStyle = CCUIMenu.createDefaultStyle();
		 myMenuStyle.font(CCUIContext.FONT_30);
		 myMenuStyle.itemSelectBackground(new CCColor(0.5d));
		 myMenuStyle.backgroundFill(new CCColor(0.2d));
			
		 _myMenuBar = new CCUIMenuBar(myMenuStyle);
		 _myMenuBar.padding(CCYogaEdge.ALL, 10);
		 _myMenuBar.add("file", new CCFileMenu(myMenuStyle, _myFileManager, _myTimelineContainer));
		 _myMenuBar.add("timeline", new CCTimelineMenu(myMenuStyle, _myFileManager, _myTimelineContainer));
		 _myMenuBar.margin(CCYogaEdge.BOTTOM, 4);
		 _myContext.addChild(_myMenuBar);
 
        _myFileManager = new CCTimelineFileManager();
        
        _myTransport = new CCTransportView(_myTimelineContainer);
		_myTransport.margin(CCYogaEdge.BOTTOM, 4);
		_myContext.addChild(_myTransport);
	}
}

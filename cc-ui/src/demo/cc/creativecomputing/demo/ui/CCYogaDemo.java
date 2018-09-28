package cc.creativecomputing.demo.ui;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.yoga.CCYogaNode;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaDirection;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaFlexDirection;

public class CCYogaDemo extends CCGLApp {
	
	private CCYogaNode root;

    private CCYogaNode header;
    private CCYogaNode footer;

    private CCYogaNode container;

    private CCYogaNode navbar;
    private CCYogaNode article;
    private CCYogaNode sidebar;
    
	@Override
	public void setup() {
		
		 root = new CCYogaNode();
		 root.flexDirection(CCYogaFlexDirection.COLUMN);
		 root.debugInfo("Root", CCColor.CYAN);

		 header = new CCYogaNode();
		 header.height(100.0);
		 header.debugInfo("Header", CCColor.WHITE);
		 
		 container = new CCYogaNode();
		 container.flex( 1.0f);
		 
		 footer = new CCYogaNode();
		 footer.height(40.0f);
		 footer.debugInfo("Footer", CCColor.WHITE);

		 root.addChild(header);
		 root.addChild(container);
		 root.addChild(footer);

		 navbar = new CCYogaNode();
		 navbar.flex(1.0f);
		 navbar.debugInfo("Navbar contents\n( Box 2 )", CCColor.parseFromIntegerRGBA(0xBCD39BFF));
		 
		 article = new CCYogaNode();
		 article.flex(3.0f);
		 article.debugInfo("Article contents\n( Box 1 )", CCColor.parseFromIntegerRGBA(0xCE9B64FF));
		 
		 sidebar = new CCYogaNode();
		 sidebar.flex(1.0f);
		 sidebar.debugInfo("Sidebar contents\n( Box 3 )", CCColor.parseFromIntegerRGBA(0x62626DFF));

		 container.addChild(navbar);
		 container.addChild(article);
		 container.addChild(sidebar);

		 root.calculateLayout(width, height, CCYogaFlexDirection.COLUMN);
		 
		 keyReleaseEvents.add(e -> {
			 switch(e.key) {
			 case KEY_D:
				 root.direction(root.direction() == CCYogaDirection.RTL ? CCYogaDirection.LTR : CCYogaDirection.RTL);
				 root.calculateLayout(width, height, CCYogaFlexDirection.COLUMN);
				 break;
			 }
		 });
		 
		 windowSizeEvents.add(size -> {
			// Toggle mobile/desktop layout when the threshold is passed
			 if (width <= 480) {
				 if (container.flexDirection() == CCYogaFlexDirection.ROW) {
					 toggleLayout(article, navbar, CCYogaFlexDirection.COLUMN);
				 }
			 } else if (container.flexDirection() == CCYogaFlexDirection.COLUMN) {
				 toggleLayout(navbar, article, CCYogaFlexDirection.ROW);
			 }

			 root.calculateLayout(width, height, CCYogaFlexDirection.COLUMN);
		 });
	}
	
	private void toggleLayout(CCYogaNode first, CCYogaNode second, CCYogaFlexDirection direction) {
    	container.removeChild(first);
    	container.removeChild(second);
    	container.insertChild(first, 0);
    	container.insertChild(second, 1);
    	container.flexDirection(direction);
    }

	@Override
	public void update(final CCGLTimer theTimer) {
		
	}

	@Override
	public void display(CCGraphics g) {
		g.ortho();
		g.clear();
		root.displayDebug(g);
	}
	
	

	public static void main(String[] args) {
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(new CCYogaDemo());
		myApplicationManager.run();
	}
}

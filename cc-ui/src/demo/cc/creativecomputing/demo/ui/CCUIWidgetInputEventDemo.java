/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.demo.ui;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIInputEventType;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.decorator.border.CCUILineBorderDecorator;
import cc.creativecomputing.ui.event.CCUIWidgetEvent;
import cc.creativecomputing.ui.event.CCUIWidgetEventListener;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.util.logging.CCLog;

public class CCUIWidgetInputEventDemo extends CCApp {
	
	@CCControl(name = "scale x", min = 0.1f, max = 2)
	private float _cScaleX = 0;
	
	@CCControl(name = "scale y", min = 0.1f, max = 2)
	private float _cScaleY = 0;
	
	@CCControl(name = "translate x", min = -200f, max = 200)
	private float _cTranslateX = 0;
	
	@CCControl(name = "translate y", min = -200f, max = 200)
	private float _cTranslateY = 0;
	
	@CCControl(name = "rotate", min = 0f, max = 360)
	private float _cRotate = 0;
	
	@CCControl(name = "horizontal alignment")
	private CCUIHorizontalAlignment _cHorizontalAlignment = CCUIHorizontalAlignment.LEFT;
	
	@CCControl(name = "vertical alignment")
	private CCUIVerticalAlignment _cVerticalAlignment = CCUIVerticalAlignment.BOTTOM;
	
	private CCUIWidget _myWidget;

	@Override
	public void setup() {
		_myWidget = new CCUIWidget(100, 200);
		_myWidget.border(new CCUILineBorderDecorator());
		
		_myWidget.addListener(new CCUIWidgetEventListener() {
			
			@Override
			public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
				CCLog.info(theEvent.type());
			}
		});
		
		addControls("ui", "ui", this);
	}

	@Override
	public void update(final float theDeltaTime) {
		_myWidget.property2f("scale").set(_cScaleX, _cScaleY);
		_myWidget.property2f("translation").set(_cTranslateX, _cTranslateY);
		_myWidget.property1f("rotation").set(_cRotate);
		
		_myWidget.horizontalAlignment(_cHorizontalAlignment);
		_myWidget.verticalAlignment(_cVerticalAlignment);
		
		_myWidget.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		g.line(-width/2,0,width/2,0);
		g.line(0,-height/2,0,height/2);
		_myWidget.draw(g);
	}
	
	private CCVector2f mouseToScreen(CCMouseEvent theEvent) {
		return new CCVector2f(theEvent.x() - width/2, height/2 - theEvent.y());
	}
	
	
	@Override
	public void mousePressed(CCMouseEvent theMouseEvent) {
		_myWidget.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.PRESS);
	}
	
	@Override
	public void mouseReleased(CCMouseEvent theMouseEvent) {
		_myWidget.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.RELEASE);
	}

	@Override
	public void mouseMoved(CCMouseEvent theMouseEvent) {
		_myWidget.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.MOVE);
	}

	@Override
	public void mouseDragged(CCMouseEvent theMouseEvent) {
		_myWidget.checkEvent(mouseToScreen(theMouseEvent), CCUIInputEventType.DRAGG);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCUIWidgetInputEventDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

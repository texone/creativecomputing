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
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.ui.CCUIInputEventType;
import cc.creativecomputing.ui.decorator.border.CCUILineBorderDecorator;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIWidgetAnimationDemo extends CCApp {
	
	private CCUIWidget _myWidget;

	@Override
	public void setup() {
		_myWidget = new CCUIWidget(100, 200);
		_myWidget.border(new CCUILineBorderDecorator());
	}

	@Override
	public void update(final float theDeltaTime) {
		_myWidget.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
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
		CCApplicationManager myManager = new CCApplicationManager(CCUIWidgetAnimationDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

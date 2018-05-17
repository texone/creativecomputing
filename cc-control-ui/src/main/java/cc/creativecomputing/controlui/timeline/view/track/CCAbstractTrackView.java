/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui.timeline.view.track;

import cc.creativecomputing.gl.app.CCGLCursorShape;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.layout.CCUIPane;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;

public abstract class CCAbstractTrackView extends CCUIPane {
	
	protected CCAbstractTrackDataView<?> _myDataView;
	
	protected CCGLWindow _myMainFrame;
	
	public CCAbstractTrackView(
		CCGLWindow theMainFrame,
		CCAbstractTrackDataView<?> theDataView
	){
		super(new CCUIWidgetStyle());
		_myMainFrame = theMainFrame;
		_myDataView = theDataView;
	}
	
	public CCAbstractTrackView(CCGLWindow theMainFrame){
		super(new CCUIWidgetStyle());
		_myMainFrame = theMainFrame;
		_myDataView = null;
	}
	
	public CCAbstractTrackView(){
		super(new CCUIWidgetStyle());
	}

	public abstract void mute(final boolean theMute);

	public abstract void min(final double theMin);

	public abstract void max(final double theMax);

	public abstract void color(CCColor theColor);

	public void value(String theValue) {}
	

	public CCAbstractTrackDataView<?> dataView() {
		return _myDataView;
	}
	
	public void update() {
		_myDataView.update();
	}
	
	public void render(CCGraphics g) {
		_myDataView.renderData(g);
	}

	public void drawTimelineInfos(CCGraphics g) {
		_myDataView.drawTimelineInfos(g);
	}
	
	@Override
	public double height() {
    	if(_myDataView == null)return 0;
        return _myDataView.height();
    }
    
	@Override
    public double width() {
    	if(_myDataView == null)return 0;
    	return _myDataView.width();
    }
    
    public void selectCursor() {
    		_myMainFrame.cursor(CCGLCursorShape.HAND);
    }
    
    public void resizeCursor(){
    		_myMainFrame.cursor(CCGLCursorShape.HRESIZE);
    }
    
    public void moveCursor(){
    		_myMainFrame.cursor(CCGLCursorShape.CROSSHAIR);
    }
    
    public void defaultCursor() {
    		_myMainFrame.cursor(CCGLCursorShape.ARROW);
    }
	
	public void moveRangeCursor() {
		resizeCursor();
	}

}

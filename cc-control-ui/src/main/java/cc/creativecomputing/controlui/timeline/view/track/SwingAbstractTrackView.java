package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.timeline.view.SwingToolChooserPopup;

public abstract class SwingAbstractTrackView extends JPanel implements CCTrackView{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7700094752340157349L;
	
	protected SwingTrackDataView _myDataView;
	
	protected JFrame _myMainFrame;
	
	public SwingAbstractTrackView(
		JFrame theMainFrame,
		SwingToolChooserPopup theToolChooserPopUp, 
		SwingTrackDataRenderer theDataRenderer,
		TimelineController theTimelineController,
		TrackController theController
	){
		_myMainFrame = theMainFrame;
		_myDataView = new SwingTrackDataView(theToolChooserPopUp, theDataRenderer, theTimelineController, theController);
	}
	
	public SwingAbstractTrackView(JFrame theMainFrame){
		_myMainFrame = theMainFrame;
		_myDataView = null;
	}
	
	public SwingAbstractTrackView(){
		
	}

	public abstract void mute(final boolean theMute);

	public abstract void min(final double theMin);

	public abstract void max(final double theMax);

	public abstract void color(Color theColor);

	public void value(String theValue) {}
	

	public SwingTrackDataView dataView() {
		return _myDataView;
	}
	
	public void update() {
		_myDataView.update();
	}
	
	public void render() {
		_myDataView.render();
	}

	public void renderInfo() {
		_myDataView.renderInfo();
	}
	
	public int height() {
        return _myDataView.height();
    }
    
    public int width() {
    	return _myDataView.width();
    }
    
    public void selectCursor() {
    	_myMainFrame.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public void resizeCursor(){
    	_myMainFrame.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
    }
    
    public void moveCursor(){
    	_myMainFrame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
    }
    
    public void defaultCursor() {
    	_myMainFrame.setCursor(Cursor.getDefaultCursor());
    }
	
	public void moveRangeCursor() {
		resizeCursor();
	}

}

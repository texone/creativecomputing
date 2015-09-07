package cc.creativecomputing.controlui.timeline.view;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import cc.creativecomputing.controlui.timeline.view.SwingTableLayout.Separator;
import cc.creativecomputing.controlui.timeline.view.SwingTableLayout.SeparatorAlignment;
import cc.creativecomputing.controlui.timeline.view.SwingTableLayout.TableLayoutConstraints;


@SuppressWarnings("serial")
public class SwingMultiTrackPanel extends JPanel implements Iterable<Component> {
	
	// Small MouseAdapter to trigger the drag operations
    public static class MultiTrackMouseAdapter implements MouseListener, MouseMotionListener {
    	
        private Separator _myDraggedSeparator = null;
        
        private Component _myParent;
        
        private SwingMultiTrackPanel _myPanel;
        private SwingMultiTrackPanel _myRulerPanel;
        
        public MultiTrackMouseAdapter(Component theParent, SwingMultiTrackPanel theTrackPanel, SwingMultiTrackPanel theRulerPanel){
        	_myParent = theParent;
        	_myPanel = theTrackPanel;
        	_myRulerPanel = theRulerPanel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }
    
        @Override
        public void mouseEntered(MouseEvent e) {
        }
    
        @Override
        public void mouseExited(MouseEvent e) {
        	_myParent.setCursor(Cursor.getDefaultCursor());
        }
    
        @Override
        public void mousePressed(MouseEvent e) {
            _myDraggedSeparator = _myPanel.separator(e.getX(), e.getY());
        }
    
        @Override
        public void mouseReleased(MouseEvent e) {
            _myDraggedSeparator = null;
        }
    
        @Override
        public void mouseDragged(MouseEvent e) {
            if (_myDraggedSeparator == null) return;
            if(_myDraggedSeparator.alignment == SeparatorAlignment.VERTICAL){
        		int myWidth = _myPanel.width();
        		_myPanel.tableLayout().adjustWeight(_myPanel, _myDraggedSeparator.index, (float)e.getX() / (float)myWidth);
        		_myRulerPanel.tableLayout().adjustWeight(_myRulerPanel, _myDraggedSeparator.index, (float)e.getX() / (float)myWidth);
        		return;
            }
            _myPanel.tableLayout().setSeparatorPosition(_myPanel, _myDraggedSeparator, e.getX(), e.getY(), false);
        }
    
        @Override
        public void mouseMoved(MouseEvent e) {
        	 Separator mySeparator = _myPanel.separator(e.getX(), e.getY());
        	 
             if (mySeparator != null && !mySeparator.isFixed) {
            	 _myParent.setCursor(new Cursor(mySeparator.alignment == SeparatorAlignment.HORIZONTAL ? Cursor.N_RESIZE_CURSOR : Cursor.E_RESIZE_CURSOR));
                 return;
             }
             _myParent.setCursor(Cursor.getDefaultCursor());
        }
    }

    
    private SwingTableLayout _myLayout = null;
    
    private ArrayList<Component> _myTrackViews = new ArrayList<Component>();
    private ArrayList<Component> _myTrackDataViews = new ArrayList<Component>();
    
    /**
     * keeps all tracks by there name for easier access
     */
    private Map<Path, Component> _myTrackViewsMap = new HashMap<>();
    private Map<Path, Component> _myTrackDataViewsMap = new HashMap<>();
    
    private Component _myParent;
    
    public SwingMultiTrackPanel(Component theParent) {
    	_myParent = theParent;
        _myLayout = new SwingTableLayout();
        setLayout(_myLayout);
    }
    
    public SwingTableLayout tableLayout(){
    	return _myLayout;
    }
    
    public Separator separator(int theX, int theY){
    	return _myLayout.getSeparator(this, theX, theY);
    }
    
    public int width(){
    	Insets myInsets = getInsets();
		Dimension mySize = getSize();
		
		return mySize.width + (myInsets.left + myInsets.right);
    }
    
    public void clear() {
        _myTrackViews.clear();
        _myTrackViewsMap.clear();
        
        _myTrackDataViews.clear();
        _myTrackDataViewsMap.clear();

        _myLayout = new SwingTableLayout();
        setLayout(_myLayout);
        
        removeAll();
    }
    
    public int columnWidth(int theColumn){
    	return _myLayout.getWidth(this, theColumn);
    }
    
    public int index(Component theComponent) {
    	return _myTrackViews.indexOf(theComponent);
    }
    
    public void insertTrackView(Component theTrackView, Path thePath, int theIndex, int theHeight, boolean theIsFixed) {
        _myTrackViews.add(theIndex, theTrackView);
        _myTrackViewsMap.put(thePath, theTrackView);
        _myLayout.insertRow(theIndex, theHeight,theIsFixed);
        
        add(theTrackView,new TableLayoutConstraints(theIndex, 0));
        
        updateUI();
    }
    
    public void insertTrackDataView(Component theTrackDataView, Path thePath, int theIndex) {
    	_myTrackDataViews.add(theIndex, theTrackDataView);
        _myTrackDataViewsMap.put(thePath, theTrackDataView);
        
        add(theTrackDataView, new TableLayoutConstraints(theIndex, 1));
        
        updateUI();
    }
    
    public void removeTrackView(final Path thePath) {
    	Component myTrackView = _myTrackViewsMap.remove(thePath);
        int myIndex = _myTrackViews.indexOf(myTrackView);
        
        // if track is part of a group and the group is closed this is totally okay
        if(myIndex < 0) {
        	return;
        }
        _myTrackViews.remove(myIndex);
        remove(myTrackView);
        
        Component myTrackDataView = _myTrackDataViewsMap.remove(thePath);
        _myTrackDataViews.remove(myTrackDataView);
        remove(myTrackDataView);
        
        _myLayout.removeRow(myIndex);
        
        updateUI();
    }
    
    
    @Override
    public Iterator<Component> iterator() {
        return _myTrackViews.iterator();
    }

    
    @Override
    public Dimension getSize() {
    	int myHeight = 0;
    	for(Component myComponent:new ArrayList<>(_myTrackViews)){
    		myHeight += myComponent.getHeight() + 2;
    	}
    	return new Dimension(_myParent.getWidth() - 18,myHeight);
    }
 
}

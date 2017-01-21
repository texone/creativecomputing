package cc.creativecomputing.controlui.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import cc.creativecomputing.controlui.timeline.controller.FileManager;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.io.CCFileFilter;

public class SwingTimelineMenu extends JMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7702761824018449400L;
	private final FileManager _myFileManager;
	private CCFileChooser _myFileChooser;
	private TimelineContainer _myTimelineContainer;

	public SwingTimelineMenu(TimelineContainer theTimelineContainer){
		super("Timeline");
		_myFileManager = theTimelineContainer.fileManager();
		_myTimelineContainer = theTimelineContainer;
		
		_myFileChooser = new CCFileChooser();
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("xml", "xml"));
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("json", "json"));
		
		addTimelineFileItems();
		addSeparator();
		
		addEditItems();
		addSeparator();
		addViewItems();
		addSeparator();
		SwingQuantizeMenu myQuantizeMenue = new SwingQuantizeMenu(theTimelineContainer);
		add(myQuantizeMenue);
	}
	
	private void addTimelineFileItems(){
		
		setMnemonic(KeyEvent.VK_T);
		
		JMenuItem myLoadItem = new JMenuItem("Replace");
		myLoadItem.addActionListener(theE -> {
			Path myPath = _myFileChooser.chosePath("open");
			if (myPath != null) _myFileManager.replaceCurrentTimeline(myPath);
		});
		myLoadItem.setToolTipText("Replaces the current active timeline.");
		myLoadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.META_MASK));
		add(myLoadItem);
		
		JMenuItem myLoadAddItem = new JMenuItem("Add");
		myLoadAddItem.addActionListener(theE -> {
			Path myPath = _myFileChooser.chosePath("open");
			if (myPath != null) _myFileManager.addToCurrentTimeline(myPath);
		});
		myLoadAddItem.setToolTipText("Loads the tracks from the file and adds them to the current timeline.");
		myLoadAddItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
		add(myLoadItem);
		
		JMenuItem myNewItem = new JMenuItem("Clear");
		myNewItem.addActionListener(theE -> {
			_myFileChooser.resetPath();
			_myFileManager.resetTimeline();
		});
		myNewItem.setToolTipText("Clears the current active timeline");
		myNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
		add(myNewItem);
		
		JMenuItem myInsertItem = new JMenuItem("Insert at Time");
		myInsertItem.addActionListener(theE -> {
			Path myPath = _myFileChooser.chosePath("open");
			if (myPath != null) _myFileManager.insertAtTimeToCurrentTimeline(myPath);
		});
		myInsertItem.setToolTipText("Inserts the data for in the file and the timeline.");
		myInsertItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
		add(myInsertItem);
		
		JMenuItem mySaveItem = new JMenuItem("Export");
		mySaveItem.addActionListener(theE -> {
			Path myPath = _myFileChooser.chosePath("Export Timeline");
			if(myPath != null)_myFileManager.exportCurrentTimeline(myPath);
		});
		mySaveItem.setToolTipText("Saves the content of all tracks.");
		mySaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));
		add(mySaveItem);
		add(mySaveItem);
		
		JMenuItem mySaveSelectionItem = new JMenuItem("Export Selection");
		mySaveSelectionItem.addActionListener(theE -> {
			Path myPath = _myFileChooser.chosePath("save selection");
			if (myPath != null) _myFileManager.exportCurrentTimelineSelection(myPath);
		});
		mySaveSelectionItem.setToolTipText("Saves the content of all tracks.");
		add(mySaveSelectionItem);
	}
	
	private void addEditItems(){
		if(SwingGuiConstants.CREATE_UNDO_ENTRIES) {
			JMenuItem myUndoMenu = new JMenuItem("Undo");
			myUndoMenu.addActionListener(theE -> {
				UndoHistory.instance().undo();
			});
			myUndoMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK));
			myUndoMenu.setMnemonic(KeyEvent.VK_Z);
			add(myUndoMenu);
			
			JMenuItem myRedoMenu = new JMenuItem("Redo");
			myRedoMenu.addActionListener(theE -> {
				UndoHistory.instance().redo();
			});
			myRedoMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
			myRedoMenu.setMnemonic(KeyEvent.VK_R);
			add(myRedoMenu);
		}

		JMenuItem myCutMenu = new JMenuItem("Cut");
		myCutMenu.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().toolController().selectionController().cut();
		});
		myCutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.META_MASK));
		myCutMenu.setMnemonic(KeyEvent.VK_T);
		add(myCutMenu);

		JMenuItem myCopyMenu = new JMenuItem("Copy");
		myCopyMenu.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().toolController().selectionController().copy();
		});
		myCopyMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK));
		myCopyMenu.setMnemonic(KeyEvent.VK_P);
		add(myCopyMenu);

		JMenuItem myPasteMenu = new JMenuItem("Paste");
		myPasteMenu.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().toolController().selectionController().insert();
		});
		myPasteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.META_MASK));
		myPasteMenu.setMnemonic(KeyEvent.VK_A);
		add(myPasteMenu);

		JMenuItem myReplaceMenu = new JMenuItem("Replace");
		myReplaceMenu.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().toolController().selectionController().replace();
		});
		myReplaceMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.SHIFT_MASK | ActionEvent.META_MASK));
		myReplaceMenu.setMnemonic(KeyEvent.VK_R);
		add(myReplaceMenu);

		// TODO fix write values
//		JMenuItem myWriteValuesMenue = new JMenuItem("Write Values");
//		myWriteValuesMenue.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent theE) {
//				_myTimelineController.writeValues();
//			}
//		});
//		myWriteValuesMenue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.META_MASK));
//		myWriteValuesMenue.setMnemonic(KeyEvent.VK_W);
//		add(myWriteValuesMenue);
		
		JMenuItem myInsertTimeMenue = new JMenuItem("Insert Time");
		myInsertTimeMenue.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().insertTime();
		});
		myInsertTimeMenue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.META_MASK));
		myInsertTimeMenue.setMnemonic(KeyEvent.VK_I);
		add(myInsertTimeMenue);
		
		JMenuItem myRemoveTimeMenue = new JMenuItem("Remove Time");
		myRemoveTimeMenue.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().removeTime();
		});
		myRemoveTimeMenue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.META_MASK));
		myRemoveTimeMenue.setMnemonic(KeyEvent.VK_R);
		add(myRemoveTimeMenue);
		
		JMenuItem myReverseTracksMenue = new JMenuItem("Reverse");
		myReverseTracksMenue.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().reverseTracks();
		});
		add(myReverseTracksMenue);
		
		JMenuItem myCreateClipTrackMenue = new JMenuItem("Create Clip Track");
		myCreateClipTrackMenue.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().createClipTrack();
		});
		add(myCreateClipTrackMenue);
	}
	
	private void addViewItems(){
		JMenuItem myResetZoomMenu = new JMenuItem("Reset Zoom");
		myResetZoomMenu.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().zoomController().reset();
		});
		myResetZoomMenu.setMnemonic(KeyEvent.VK_R);
		add(myResetZoomMenu);

		JMenuItem myZoomToMax = new JMenuItem("Zoom to Max");
		myZoomToMax.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().zoomToMaximum();
		});
		myZoomToMax.setMnemonic(KeyEvent.VK_M);
		add(myZoomToMax);

		JMenuItem myZoomSelection = new JMenuItem("Zoom to Selection");
		myZoomSelection.addActionListener(theE -> {
			_myTimelineContainer.activeTimeline().zoomToSelection();
		});
		myZoomSelection.setMnemonic(KeyEvent.VK_S);
		add(myZoomSelection);
		
		ActionListener myShowUnusedTracksListener = new ActionListener() {
			public void actionPerformed(ActionEvent theE) {
				JCheckBoxMenuItem myButton = (JCheckBoxMenuItem)theE.getSource();
				boolean selected = myButton.isSelected();
				_myTimelineContainer.activeTimeline().hideUnusedTracks(selected);
			}
		};
		
		JCheckBoxMenuItem myHideUnusedTracksItem = new JCheckBoxMenuItem("Hide Unused Tracks");
		myHideUnusedTracksItem.setSelected(false);
		myHideUnusedTracksItem.addActionListener(myShowUnusedTracksListener);
		add(myHideUnusedTracksItem);
	}
}

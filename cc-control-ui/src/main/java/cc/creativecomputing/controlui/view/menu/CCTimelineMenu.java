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
package cc.creativecomputing.controlui.view.menu;

import java.nio.file.Path;

import cc.creativecomputing.controlui.timeline.controller.FileManager;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.ui.widget.CCUICheckBox;
import cc.creativecomputing.ui.widget.CCUIMenu;

public class CCTimelineMenu extends CCUIMenu{
	
	private FileManager _myFileManager;
	private CCFileChooser _myFileChooser;
	private CCTimelineContainer _myTimelineContainer;

	public CCTimelineMenu(CCFont<?> theFont, CCTimelineContainer theTimelineContainer){
		super(theFont);
		_myFileManager = theTimelineContainer.fileManager();
		_myTimelineContainer = theTimelineContainer;
		
		_myFileChooser = new CCFileChooser("xml", "json");
		
		addTimelineFileItems();
		addSeparator();
		
		addEditItems();
		addSeparator();
//		addViewItems();
//		addSeparator();
//		CCQuantizeMenu myQuantizeMenue = new CCQuantizeMenu(theFont, theTimelineContainer);
//		addItem("Quantize", myQuantizeMenue);
	}
	
	

	private void addTimelineFileItems(){
		
		addItem(
			"Replace",
			() -> {
				Path myPath = _myFileChooser.openFile("open");
				if (myPath != null) _myFileManager.replaceCurrentTimeline(myPath);
			}
		).toolTipText("Replaces the current active timeline.");
		
		addItem(
			"Add",
			() -> {
				Path myPath = _myFileChooser.openFile("open");
				if (myPath != null) _myFileManager.addToCurrentTimeline(myPath);
			}
		).toolTipText("Loads the tracks from the file and adds them to the current timeline.");
		
		addItem(
			"Clear",() -> {
				_myFileChooser.resetPath();
				_myFileManager.resetTimeline();
			}
		).toolTipText("Clears the current active timeline");;
		
		
		addItem(
			"Insert at Time",
			() -> {
				Path myPath = _myFileChooser.openFile("open");
				if (myPath != null) _myFileManager.insertAtTimeToCurrentTimeline(myPath);
			}
		).toolTipText("Inserts the data for in the file and the timeline.");
		
		
		addItem(
			"Export",
			() -> {
				Path myPath = _myFileChooser.saveFile("Export Timeline");
				if(myPath != null)_myFileManager.exportCurrentTimeline(myPath);
			}
		).toolTipText("Saves the content of all tracks.");
		
		
		addItem(
			"Export Selection",
			() -> {
				Path myPath = _myFileChooser.saveFile("save selection");
				if (myPath != null) _myFileManager.exportCurrentTimelineSelection(myPath);
			}
		).toolTipText("Saves the content of all tracks.");
	}
	
	private void addEditItems(){
		if(CCUIConstants.CREATE_UNDO_ENTRIES) {
			addItem(
				"Undo",
				() -> {
					UndoHistory.instance().undo();
				}
			);
			
			addItem(
				"Redo",
				() -> {
					UndoHistory.instance().redo();
				}
			);
		}

//		CCUIMenuItem myCutMenu = new CCUIMenuItem("Cut");
//		myCutMenu.addActionListener(() -> {
//			_myTimelineContainer.activeTimeline().toolController().selectionController().cut();
//		});
//		myCutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.META_MASK));
//		myCutMenu.setMnemonic(KeyEvent.VK_T);
//		addItem(myCutMenu);
//
//		CCUIMenuItem myCopyMenu = new CCUIMenuItem("Copy");
//		myCopyMenu.addActionListener(() -> {
//			_myTimelineContainer.activeTimeline().toolController().selectionController().copy();
//		});
//		myCopyMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.META_MASK));
//		myCopyMenu.setMnemonic(KeyEvent.VK_P);
//		addItem(myCopyMenu);
//
//		CCUIMenuItem myPasteMenu = new CCUIMenuItem("Paste");
//		myPasteMenu.addActionListener(() -> {
//			_myTimelineContainer.activeTimeline().toolController().selectionController().insert();
//		});
//		myPasteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.META_MASK));
//		myPasteMenu.setMnemonic(KeyEvent.VK_A);
//		addItem(myPasteMenu);
//
//		CCUIMenuItem myReplaceMenu = new CCUIMenuItem("Replace");
//		myReplaceMenu.addActionListener(() -> {
//			_myTimelineContainer.activeTimeline().toolController().selectionController().replace();
//		});
//		myReplaceMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.SHIFT_MASK | ActionEvent.META_MASK));
//		myReplaceMenu.setMnemonic(KeyEvent.VK_R);
//		addItem(myReplaceMenu);

		// TODO fix write values
//		CCUIMenuItem myWriteValuesMenue = new CCUIMenuItem("Write Values");
//		myWriteValuesMenue.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent ()) {
//				_myTimelineController.writeValues();
//			}
//		});
//		myWriteValuesMenue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.META_MASK));
//		myWriteValuesMenue.setMnemonic(KeyEvent.VK_W);
//		add(myWriteValuesMenue);
		
		
		addItem(
			"Reverse",
			() -> {
				_myTimelineContainer.activeTimeline().reverseTracks();
			}
		);
		
		addItem(
			"Create Clip Track",
			() -> {
				_myTimelineContainer.activeTimeline().createClipTrack();
			}
		);
	}
	
	private void addViewItems(){
		CCUICheckBox myCheckBox = new CCUICheckBox();
		addItem(
			myCheckBox,
			"Hide Unused Tracks", 
			()->{
				boolean selected = myCheckBox.isSelected();
				_myTimelineContainer.activeTimeline().hideUnusedTracks(selected);
			}
		);
	}
}

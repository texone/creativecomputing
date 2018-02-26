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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.timeline.controller.FileManager;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.ui.widget.CCUIMenu;

public class CCFileMenu extends CCUIMenu{
	
	private final FileManager _myFileManager;
	private final CCFileChooser _myFileChooser;
	
	private static final int MAX_RECENT_FILES = 10;
	private static final String PREF_NAME = "recent_items";
	
	private class CCRecentFileHandler{
		private List<Path> _myRecentFiles = new ArrayList<>();
		
		public CCRecentFileHandler(){
			loaditemsFromPreferences();
		}
		
		private void loaditemsFromPreferences() {
			for (int i = 0; i < MAX_RECENT_FILES; i++) {
				String value = CCControlApp.preferences.get(PREF_NAME + i, "");
				if(value == null)continue;
				if(value.equals(""))continue;
				
				Path myPath = Paths.get(value);
				if(!CCNIOUtil.exists(myPath)){
					CCControlApp.preferences.remove(PREF_NAME + i);
					continue;
				}
				
				_myRecentFiles.add(myPath);
				addRecentItem(myPath);
			}
		}
		
		private void addRecentItem(Path thePath){
			myRecentItem.addItem(
				thePath.toString(),
				() ->{
					_myFileChooser.setDirectory(thePath.getParent());
					_myFileManager.loadProject(thePath);
				}
			);
		}
		
		public void addFile(Path thePath){
			if(_myRecentFiles.contains(thePath))return;
			_myRecentFiles.add(thePath);
			while(_myRecentFiles.size() > MAX_RECENT_FILES){
				_myRecentFiles.remove(0);
			}
			for (int i = 0; i < MAX_RECENT_FILES; i++) {
				CCControlApp.preferences.remove(PREF_NAME + i);
			}
			myRecentItem.removeAll();
			int i = 0;
			for(Path myPath:_myRecentFiles){
				addRecentItem(myPath);
				CCControlApp.preferences.put(PREF_NAME + i, myPath + "");
				i++;
			}
			
			for (i = 0; i < MAX_RECENT_FILES; i++) {
				String value = CCControlApp.preferences.get(PREF_NAME + i, "");
			}
		}
	}
	
	private CCRecentFileHandler _myRecentFileHandler;

	public CCFileMenu(CCFont<?> theFont, CCTimelineContainer theTimelineContainer){
		super(theFont);
		_myFileManager = theTimelineContainer.fileManager();
		
		_myFileChooser = new CCFileChooser("xml", "json");
		addTimelineFileItems();

		_myRecentFileHandler = new CCRecentFileHandler();
	}
	
	private CCUIMenu myRecentItem;
	
	private void addTimelineFileItems(){
		addItem(
			"Load", 
			() -> {
				Path myPath = _myFileChooser.openFile("Load Project");
				if (myPath == null) return;
				_myFileManager.loadProject(myPath);
				_myRecentFileHandler.addFile(myPath);
			}
		).toolTipText("Loads an existing project.");
		
		addItem(
			"NEW",
			() -> {
				_myFileChooser.resetPath();
				_myFileManager.newProject();
			}
		).toolTipText("Creates a new Project.");
		
		addItem(
			"Save",
			() -> {
				Path myPath = _myFileChooser.saveFile("Save Project");
				if(myPath == null)return;
				_myFileManager.saveProject(myPath);
				_myRecentFileHandler.addFile(myPath);
			}
		).toolTipText("Saves the content of all tracks.");
		
		myRecentItem = new CCUIMenu(_myFont);
		addItem("Recent").toolTipText("Open a recent file.");
		
//		JMenuItem mySaveAsItem = new JMenuItem("Save As ...");
//		mySaveAsItem.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent theE) {
//				Path myPath = _myFileChooser.chosePath("save");
//				if (myPath != null) {
//					_myFileManager.exportCurrentTimeline(myPath);
//				}
//			}
//		});
//		mySaveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
//		add(mySaveAsItem);
//		
		addItem(
			"Export Selection",
			() -> {
				Path myPath = _myFileChooser.saveFile("save selection");
				if (myPath != null) _myFileManager.exportCurrentTimelineSelection(myPath);
			}
		).toolTipText("Saves the content of all tracks.");
	}

}

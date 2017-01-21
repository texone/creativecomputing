package cc.creativecomputing.controlui.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.timeline.controller.FileManager;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.io.CCFileFilter;
import cc.creativecomputing.io.CCNIOUtil;

public class SwingFileMenu extends JMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6837663569844298314L;
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
				if (value != null && !value.equals("")) {
					Path myPath = Paths.get(value);
					if(!CCNIOUtil.exists(myPath)){
						CCControlApp.preferences.remove(PREF_NAME + i);
						continue;
					}
					_myRecentFiles.add(myPath);
					addRecentItem(myPath);
					
				}
			}
		}
		
		private void addRecentItem(Path thePath){
			JMenuItem myFileItem = new JMenuItem(thePath.toString());
			myFileItem.addActionListener(event ->{
				_myFileManager.loadProject(thePath);
			});
			myRecentItem.add(myFileItem);
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
		}
	}
	
	private CCRecentFileHandler _myRecentFileHandler;

	public SwingFileMenu(TimelineContainer theTimelineContainer){
		super("File");
		_myFileManager = theTimelineContainer.fileManager();
		
		_myFileChooser = new CCFileChooser();
		FileFilter myXMLFilter = new CCFileFilter("xml", "xml");
		FileFilter myJSONFilter = new CCFileFilter("json", "json");
		_myFileChooser.addChoosableFileFilter(myXMLFilter);
		_myFileChooser.addChoosableFileFilter(myJSONFilter);
		_myFileChooser.setFileFilter(myJSONFilter);
		addTimelineFileItems();

		_myRecentFileHandler = new CCRecentFileHandler();
	}
	
	private JMenu myRecentItem;
	
	private void addTimelineFileItems(){
		
		setMnemonic(KeyEvent.VK_F);
		
		JMenuItem myLoadItem = new JMenuItem("Load");
		myLoadItem.addActionListener(e -> {
			Path myPath = _myFileChooser.chosePath("Load Project");
			if (myPath == null) return;
			_myFileManager.loadProject(myPath);
			_myRecentFileHandler.addFile(myPath);
			
		});
		myLoadItem.setToolTipText("Loads an existing project.");
		myLoadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.META_MASK));
		add(myLoadItem);
		
		JMenuItem myNewItem = new JMenuItem("New");
		myNewItem.addActionListener(e -> {
			_myFileChooser.resetPath();
			_myFileManager.newProject();
			
		});
		myNewItem.setToolTipText("Creates a new Project.");
		myNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
		add(myNewItem);
		
		JMenuItem mySaveItem = new JMenuItem("Save");
		mySaveItem.addActionListener(e -> {
			Path myPath = _myFileChooser.chosePath("Save Project");
			if(myPath != null){
				_myFileManager.saveProject(myPath);
				_myRecentFileHandler.addFile(myPath);
			}
			
		});
		mySaveItem.setToolTipText("Saves the content of all tracks.");
		mySaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));
		add(mySaveItem);
		
		myRecentItem = new JMenu("Recent");
		
		myRecentItem.addActionListener(e -> {
			Path myPath = _myFileChooser.chosePath("Save Project");
			if(myPath != null)_myFileManager.saveProject(myPath);
			
		});
		myRecentItem.setToolTipText("Open a recent file.");
		add(myRecentItem);
		
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
		JMenuItem mySaveSelectionItem = new JMenuItem("Export Selection");
		mySaveSelectionItem.addActionListener(e -> {
			Path myPath = _myFileChooser.chosePath("save selection");
			if (myPath != null) _myFileManager.exportCurrentTimelineSelection(myPath);
		});
		mySaveSelectionItem.setToolTipText("Saves the content of all tracks.");
		add(mySaveSelectionItem);
	}

}

package cc.creativecomputing.controlui.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import cc.creativecomputing.controlui.timeline.controller.FileManager;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.io.CCFileFilter;

public class SwingFileMenu extends JMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6837663569844298314L;
	private final FileManager _myFileManager;
	private final CCFileChooser _myFileChooser;

	public SwingFileMenu(TimelineContainer theTimelineContainer){
		super("File");
		_myFileManager = theTimelineContainer.fileManager();
		
		_myFileChooser = new CCFileChooser();
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("xml", "xml"));
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("json", "json"));
		
		addTimelineFileItems();
	}
	
	private void addTimelineFileItems(){
		
		setMnemonic(KeyEvent.VK_F);
		
		JMenuItem myLoadItem = new JMenuItem("Load");
		myLoadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent theE) {
				Path myPath = _myFileChooser.chosePath("Load Project");
				if (myPath == null) return;
				
				_myFileManager.loadProject(myPath);
			}
		});
		myLoadItem.setToolTipText("Loads an existing project.");
		myLoadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.META_MASK));
		add(myLoadItem);
		
		JMenuItem myNewItem = new JMenuItem("New");
		myNewItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent theE) {
				_myFileChooser.resetPath();
				_myFileManager.newProject();
			}
		});
		myNewItem.setToolTipText("Creates a new Project.");
		myNewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
		add(myNewItem);
		
		JMenuItem mySaveItem = new JMenuItem("Save");
		mySaveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				Path myPath = _myFileChooser.chosePath("Save Project");
				if(myPath != null)_myFileManager.saveProject(myPath);
			}
		});
		mySaveItem.setToolTipText("Saves the content of all tracks.");
		mySaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));
		add(mySaveItem);
		add(mySaveItem);
		
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
		mySaveSelectionItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				Path myPath = _myFileChooser.chosePath("save selection");
				if (myPath != null) _myFileManager.exportCurrentTimelineSelection(myPath);
			}
		});
		mySaveSelectionItem.setToolTipText("Saves the content of all tracks.");
		add(mySaveSelectionItem);
	}

}

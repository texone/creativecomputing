package cc.creativecomputing.controlui.timeline.view;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

import cc.creativecomputing.controlui.timeline.controller.FileManager;
import cc.creativecomputing.controlui.util.UndoHistory;


public class ExitHandler implements WindowListener {
	
	private FileManager _myFileManager;
	public ExitHandler(FileManager theFileManager) {
		_myFileManager = theFileManager;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
//		List<Track> myModels = _myTimelineController.tracks();
//		boolean myDirtyFlag = false;
//		for (Track myModel : myModels) {
//			myDirtyFlag |= myModel.isDirty();
//		}
		if (UndoHistory.instance().size() > 0) {
			int myResult = JOptionPane.showConfirmDialog(null, "Do you wan't to save your work?");
			if (myResult == JOptionPane.OK_OPTION) {
				_myFileManager.save();
				System.exit(0);
			} else if (myResult == JOptionPane.NO_OPTION) {
				System.exit(0);
			} else {
				return;
			}
		}else {
			System.exit(0);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}

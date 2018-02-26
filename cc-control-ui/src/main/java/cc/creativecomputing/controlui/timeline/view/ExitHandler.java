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

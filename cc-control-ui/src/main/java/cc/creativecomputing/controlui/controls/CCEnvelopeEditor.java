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
/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.controlui.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.timeline.view.SwingCurvePanel;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLWindow;

/**
 * @author christianriekoff
 *
 */
public class CCEnvelopeEditor extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SwingCurvePanel _myCurvePanel;
	
	private void saveWindowPosition(String thePath){
		CCControlApp.preferences.put(thePath + "/x" , getX() + "");
		CCControlApp.preferences.put(thePath + "/y" , getY() + "");
		CCControlApp.preferences.put(thePath + "/width" , getWidth() + "");
		CCControlApp.preferences.put(thePath + "/height" , getHeight() + "");
	}

	public CCEnvelopeEditor(String theTitle, CCGLWindow theWindow) {
		super(theTitle);
		_myCurvePanel = new SwingCurvePanel(this);
	
		JPanel containerPanel = new JPanel();
		containerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		containerPanel.setLayout(new BorderLayout());
		// panel to test
		JPanel testPanel = new JPanel();
		testPanel.setBackground(Color.blue);
		containerPanel.add(_myCurvePanel.dataView(), BorderLayout.CENTER);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(containerPanel, BorderLayout.CENTER);
		
		theWindow.closeEvents.add(e -> {
			CCControlApp.preferences.put(theTitle + "/open" , false + "");
		});
		theWindow.windowSizeEvents.add(e -> {
			
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				CCControlApp.preferences.put(theTitle + "/open" , true + "");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
		});
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				saveWindowPosition(theTitle);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				saveWindowPosition(theTitle);
			}
			
		});
		
		if(CCControlApp.preferences.getInt(theTitle + "/x", -1) != -1){
			CCLog.info(CCControlApp.preferences.getInt(theTitle + "/x", -1), CCControlApp.preferences.getInt(theTitle + "/y", -1));
			setLocation(
				CCControlApp.preferences.getInt(theTitle + "/x", -1), 
				CCControlApp.preferences.getInt(theTitle + "/y", -1)
			);
			setSize(
				CCControlApp.preferences.getInt(theTitle + "/width", -1), 
				CCControlApp.preferences.getInt(theTitle + "/height", -1)
			);
		}

		pack();
	}
	
	public void render(){
		_myCurvePanel.render();
	}
	
	public void update(){
		_myCurvePanel.update();
	}

	public CCTrack track() {
		return _myCurvePanel.track();
	}

	public double value(double theIn) {
		return _myCurvePanel.value(theIn);
	}

	public SwingCurvePanel panel() {
		return _myCurvePanel;
	}

	public static void main(String[] args) {
		CCEnvelopeEditor myFrame = new CCEnvelopeEditor("check it");
		myFrame.setVisible(true);
	}
}

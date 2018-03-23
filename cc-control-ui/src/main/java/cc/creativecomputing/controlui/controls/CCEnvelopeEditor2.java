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

import org.fife.ui.rtextarea.RTextAreaEditorKit.SetWritableAction;

import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.timeline.view.CCCurvePane;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;

/**
 * @author christianriekoff
 *
 */
public class CCEnvelopeEditor2 extends CCGLApp {

	private CCCurvePane _myCurvePanel;
	
	private void saveWindowPosition(String thePath){
//		CCControlApp.preferences.put(thePath + "/x" , getX() + "");
//		CCControlApp.preferences.put(thePath + "/y" , getY() + "");
		CCControlApp.preferences.put(thePath + "/width" , width + "");
		CCControlApp.preferences.put(thePath + "/height" , height + "");
	}

	public CCEnvelopeEditor2(String theTitle) {
		title = theTitle;
		_myCurvePanel = new CCCurvePane(this);

		closeEvents.add(e -> {
			CCControlApp.preferences.put(theTitle + "/open" , false + "");
		});
		windowSizeEvents.add(e -> {
			saveWindowPosition(theTitle);
		});
		windowPosEvents.add(e -> {
			saveWindowPosition(theTitle);
		});

//			public void windowOpened(WindowEvent e) {
//				CCControlApp.preferences.put(theTitle + "/open" , true + "");
//			}
		
		if(CCControlApp.preferences.getInt(theTitle + "/x", -1) != -1){
			CCLog.info(CCControlApp.preferences.getInt(theTitle + "/x", -1), CCControlApp.preferences.getInt(theTitle + "/y", -1));
			position(
				CCControlApp.preferences.getInt(theTitle + "/x", -1), 
				CCControlApp.preferences.getInt(theTitle + "/y", -1)
			);
			windowSize(
				CCControlApp.preferences.getInt(theTitle + "/width", -1), 
				CCControlApp.preferences.getInt(theTitle + "/height", -1)
			);
		}
	}
	
	@Override
	public void display(CCGraphics g){
		_myCurvePanel.display(g);
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

	public CCCurvePane panel() {
		return _myCurvePanel;
	}

	public static void main(String[] args) {
//		CCEnvelopeEditor myFrame = new CCEnvelopeEditor("check it");
//		myFrame.setVisible(true);
	}
}

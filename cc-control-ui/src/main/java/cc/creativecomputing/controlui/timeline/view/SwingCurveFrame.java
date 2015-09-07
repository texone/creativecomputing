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
package cc.creativecomputing.controlui.timeline.view;

import javax.swing.JFrame;

import cc.creativecomputing.control.timeline.Track;

/**
 * @author christianriekoff
 *
 */
public class SwingCurveFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SwingCurvePanel _myCurvePanel;

	public SwingCurveFrame() {
		_myCurvePanel = new SwingCurvePanel();
		
		getContentPane().add(_myCurvePanel.view());
		pack();
		setVisible(true);
	}
	
	public Track track(){
		return _myCurvePanel.track();
	}
	
	public double value(double theIn) {
		return _myCurvePanel.value(theIn);
	}
	
	public SwingCurvePanel panel(){
		return _myCurvePanel;
	}
	
	public static void main(String[] args) {
		new SwingCurveFrame();
	}
}

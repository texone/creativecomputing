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

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.controlui.timeline.view.SwingCurvePanel;

/**
 * @author christianriekoff
 *
 */
public class CCEnvelopeEditor extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SwingCurvePanel _myCurvePanel;

	public CCEnvelopeEditor(String theTitle) {
		super(theTitle);
		_myCurvePanel = new SwingCurvePanel(this);
//		
//		
//		getContentPane().add(_myCurvePanel.view());
		
		JPanel containerPanel = new JPanel();
		 containerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		 containerPanel.setLayout(new BorderLayout());
		 //panel to test
		 JPanel testPanel = new JPanel();
		 testPanel.setBackground(Color.blue);        
		 containerPanel.add(_myCurvePanel.view(),BorderLayout.CENTER);

		 //assuming you are extending JFrame
		 getContentPane().setLayout(new BorderLayout());
		 getContentPane().add(containerPanel, BorderLayout.CENTER);
		 
		pack();
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
		CCEnvelopeEditor myFrame = new CCEnvelopeEditor("check it");
		myFrame.setVisible(true);
	}
}

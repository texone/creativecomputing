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
package cc.creativecomputing.controlui;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.controls.CCObjectControl;

public class CCControlTreeComponent extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4865190067039581920L;

	private JTree _myTree;

	private DefaultMutableTreeNode _myRootNode;
	private CCObjectPropertyHandle _myRootHandle;

	private CCControlComponent _myControlCompoent;
	
	private CCObjectControl _myLastControl;

	public CCControlTreeComponent(String theTopNode, CCControlComponent theControlComponent) {
		super(new GridLayout(1, 0));

		_myControlCompoent = theControlComponent;
		setBorder(new EmptyBorder(0, 0, 0, 0));

		// Create the nodes.
		_myRootNode = new DefaultMutableTreeNode("app");

		// Create a tree that allows one selection at a time.
		_myTree = new JTree(_myRootNode);
		_myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listen for when the selection changes.
		_myTree.addTreeSelectionListener(e -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _myTree.getLastSelectedPathComponent();
			if (node == null)
				return;
			if (!(node.getUserObject() instanceof CCObjectPropertyHandle))
				return;

			if(_myLastControl != null)_myLastControl.dispose();
				
			CCObjectControl nodeInfo = _myLastControl = new CCObjectControl((CCObjectPropertyHandle)node.getUserObject(),_myControlCompoent,0);
			nodeInfo.open();
			
			_myControlCompoent.showContent(nodeInfo);
			_myControlCompoent.setPresets(nodeInfo.propertyHandle());
		});

		// Listen for when the selection changes.
		// _myTree.addTreeSelectionListener(this);
		JScrollPane treeView = new JScrollPane(_myTree);
		treeView.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(treeView);

		// add MouseListener to tree
		MouseAdapter ma = new MouseAdapter() {
			private void myPopupEvent(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				JTree tree = (JTree) e.getSource();
				TreePath path = tree.getPathForLocation(x, y);
				if (path == null)
					return;

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

				CCObjectControl nodeInfo = (CCObjectControl) node.getUserObject();
				nodeInfo.popup().show(tree, x, y);

			}

			public void mousePressed(MouseEvent e) {
//				if (e.isPopupTrigger())
//					myPopupEvent(e);
			}
		};

		_myTree.addMouseListener(ma);
	}

	public CCObjectPropertyHandle rootProperty() {
		return _myRootHandle;
	}

	public void setData(Object theObject, String thePresetPath) {
		_myPropertyMap.setData(theObject, thePresetPath);

		_myRootHandle = _myPropertyMap.rootHandle();
		CCObjectControl myObjectControl = new CCObjectControl(_myRootHandle, _myControlCompoent, 0);
		_myRootNode.setUserObject(_myRootHandle);
		createTree(_myRootHandle, _myRootNode);
		_myControlCompoent.showContent(myObjectControl);
	}
	
	private void createTree(CCObjectPropertyHandle theHandle, DefaultMutableTreeNode theParent){
		for(CCPropertyHandle<?> myPropertyHandle:theHandle.children().values()){
			if(myPropertyHandle instanceof CCObjectPropertyHandle){
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				
				DefaultMutableTreeNode myObjectNode = new DefaultMutableTreeNode(myObjectHandle);
				theParent.add(myObjectNode);
				createTree(myObjectHandle, myObjectNode);
			}
		}
	}


}

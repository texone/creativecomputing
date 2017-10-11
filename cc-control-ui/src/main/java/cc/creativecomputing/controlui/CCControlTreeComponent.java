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
import cc.creativecomputing.controlui.controls.CCObjectControl;

public class CCControlTreeComponent extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4865190067039581920L;

	private JTree _myTree;
	
	private DefaultMutableTreeNode _myRootNode;
	private CCObjectPropertyHandle _myRootHandle;
	
	private CCControlComponent _myControlCompoent;
	
	private CCPropertyMap _myPropertyMap;

	public CCControlTreeComponent(String theTopNode, CCControlComponent theControlComponent){
		super(new GridLayout(1,0));
		
		_myControlCompoent = theControlComponent;
		setBorder(new EmptyBorder(0, 0, 0, 0));
		
		//Create the nodes.
		_myRootNode = new DefaultMutableTreeNode("app");
        
        //Create a tree that allows one selection at a time.
        _myTree = new JTree(_myRootNode);
        _myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //Listen for when the selection changes.
        _myTree.addTreeSelectionListener(e -> {
        		DefaultMutableTreeNode node = (DefaultMutableTreeNode)_myTree.getLastSelectedPathComponent();
        	 
            if (node == null) return;
            if(!(node.getUserObject() instanceof CCObjectControl))return;
     
            CCObjectControl nodeInfo = (CCObjectControl)node.getUserObject();
            nodeInfo.open();
            
            _myControlCompoent.showContent(nodeInfo);
            _myControlCompoent.setPresets(nodeInfo.propertyHandle());
        });
        
 
        //Listen for when the selection changes.
//        _myTree.addTreeSelectionListener(this);
        JScrollPane treeView = new JScrollPane(_myTree);
        treeView.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(treeView);
        
        _myPropertyMap = new CCPropertyMap();
        
     // add MouseListener to tree
        MouseAdapter ma = new MouseAdapter() {
            private void myPopupEvent(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                JTree tree = (JTree)e.getSource();
                TreePath path = tree.getPathForLocation(x, y);
                if (path == null)
                    return;

                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                
                CCObjectControl nodeInfo = (CCObjectControl)node.getUserObject();
                nodeInfo.popup().show(tree, x, y);
      
            }
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) myPopupEvent(e);
            }
        };

        _myTree.addMouseListener(ma);
	}
	
	public CCObjectPropertyHandle rootProperty() {
		return _myRootHandle;
	}
	
	public void setData(Object theObject, String thePresetPath){
		_myPropertyMap.setData(theObject, thePresetPath);

		_myRootHandle = _myPropertyMap.rootHandle();
		CCObjectControl myObjectControl = new CCObjectControl(_myRootHandle, _myControlCompoent, 0);
		_myRootNode.setUserObject(myObjectControl);
		myObjectControl.createUI(_myRootNode);
        _myControlCompoent.showContent(myObjectControl);
	}
	
	public CCObjectPropertyHandle rootHandle(){
		return _myPropertyMap.rootHandle();
	}
	
	public CCPropertyMap propertyMap(){
		return _myPropertyMap;
	}
	
}

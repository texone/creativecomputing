package cc.creativecomputing.controlui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.controlui.controls.CCObjectControl;

public class CCControlTreeComponent extends JPanel implements TreeSelectionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4865190067039581920L;

	private JTree _myTree;
	
	private DefaultMutableTreeNode _myRootNode;
	
	private CCControlComponent _myControlCompoent;
	
	private CCPropertyMap _myPropertyMap;

	public CCControlTreeComponent(String theTopNode, CCControlComponent theControlComponent){
		super(new GridLayout(1,0));
		
		_myControlCompoent = theControlComponent;
		
		//Create the nodes.
		_myRootNode = new DefaultMutableTreeNode("app");
        
        //Create a tree that allows one selection at a time.
        _myTree = new JTree(_myRootNode);
        _myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //Listen for when the selection changes.
        _myTree.addTreeSelectionListener(this);
 
        //Listen for when the selection changes.
//        _myTree.addTreeSelectionListener(this);
        JScrollPane treeView = new JScrollPane(_myTree);
        add(treeView);
        
        _myPropertyMap = new CCPropertyMap();
	}
	
	public void setData(Object theObject, String thePresetPath){
		_myPropertyMap.setData(theObject, thePresetPath);

		CCObjectPropertyHandle myRootHandle = _myPropertyMap.rootHandle();
		CCObjectControl myObjectControl = new CCObjectControl(myRootHandle, _myControlCompoent, 0);
		_myRootNode.setUserObject(myObjectControl);
		myObjectControl.createUI(_myRootNode);
	}
	
	public CCObjectPropertyHandle rootHandle(){
		return _myPropertyMap.rootHandle();
	}
	
	public CCPropertyMap propertyMap(){
		return _myPropertyMap;
	}
	
	

    /** Required by TreeSelectionListener interface. */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)_myTree.getLastSelectedPathComponent();
 
        if (node == null) return;
        if(!(node.getUserObject() instanceof CCObjectControl))return;
 
        CCObjectControl nodeInfo = (CCObjectControl)node.getUserObject();
        _myControlCompoent.showContent(nodeInfo.controlComponent());
        _myControlCompoent.setPresets(nodeInfo.propertyHandle());
        _myControlCompoent.infoPanel();
    }
	
	
}

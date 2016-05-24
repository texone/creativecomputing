package cc.creativecomputing.controlui;

import java.awt.GridLayout;
import java.nio.file.Path;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.control.code.CCShaderObject;
import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCRealtimeCompileHandle;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.control.handles.CCShaderCompileHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.controlui.controls.CCBooleanControl;
import cc.creativecomputing.controlui.controls.CCColorControl;
import cc.creativecomputing.controlui.controls.CCControl;
import cc.creativecomputing.controlui.controls.CCEnumControl;
import cc.creativecomputing.controlui.controls.CCEnvelopeControl;
import cc.creativecomputing.controlui.controls.CCEventTriggerControl;
import cc.creativecomputing.controlui.controls.CCGradientControl;
import cc.creativecomputing.controlui.controls.CCNumberControl;
import cc.creativecomputing.controlui.controls.CCObjectControl;
import cc.creativecomputing.controlui.controls.CCPathControl;
import cc.creativecomputing.controlui.controls.CCSelectionControl;
import cc.creativecomputing.controlui.controls.CCStringControl;
import cc.creativecomputing.controlui.controls.code.CCRealtimeCompileControl;
import cc.creativecomputing.controlui.controls.code.CCShaderCompileControl;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCColor;

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
		_myRootNode.setUserObject(new CCTreeNodeUserObject(myRootHandle));
		CCObjectControl myObjectControl = new CCObjectControl(myRootHandle, _myControlCompoent, 0);
		setData(_myRootNode, _myPropertyMap.rootHandle(), myObjectControl,1);
	}
	
	public CCObjectPropertyHandle rootHandle(){
		return _myPropertyMap.rootHandle();
	}
	
	public CCPropertyMap propertyMap(){
		return _myPropertyMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setData(DefaultMutableTreeNode theParentNode, CCObjectPropertyHandle theObjectHandle, CCObjectControl theObjectControl, int theDepth){
		CCTreeNodeUserObject myUserObject = (CCTreeNodeUserObject)theParentNode.getUserObject();
		for(CCPropertyHandle<?> myPropertyHandle:theObjectHandle.children().values()){
			Class<?> myClass = myPropertyHandle.type();
			
			CCControl myControlPanel;
			if(myClass == null){
				myControlPanel = new CCEventTriggerControl((CCEventTriggerHandle)myPropertyHandle, _myControlCompoent);
			}else if(myClass == CCTriggerProgress.class){
				myControlPanel = new CCEventTriggerControl((CCEventTriggerHandle)myPropertyHandle, _myControlCompoent);
			}else if(myClass == Float.class || myClass == Float.TYPE){
				myControlPanel = new CCNumberControl((CCNumberPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else if(myClass == Double.class || myClass == Double.TYPE){
				myControlPanel = new CCNumberControl((CCNumberPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == Integer.class || myClass == Integer.TYPE){
				myControlPanel = new CCNumberControl((CCNumberPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == Boolean.class || myClass == Boolean.TYPE){
				myControlPanel = new CCBooleanControl((CCBooleanPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass.isEnum()){
				myControlPanel = new CCEnumControl((CCEnumPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == CCSelection.class){
				myControlPanel = new CCSelectionControl((CCSelectionPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == CCColor.class){
				myControlPanel = new CCColorControl((CCColorPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == CCGradient.class){
				myControlPanel = new CCGradientControl((CCGradientPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == String.class){
				myControlPanel = new CCStringControl((CCStringPropertyHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == CCEnvelope.class){
				myControlPanel = new CCEnvelopeControl((CCEnvelopeHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == Path.class){
				myControlPanel = new CCPathControl((CCPathHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == CCRealtimeCompile.class){
				myControlPanel = new CCRealtimeCompileControl((CCRealtimeCompileHandle)myPropertyHandle, _myControlCompoent);
			}else  if(myClass == CCShaderObject.class){
				CCLog.info(myClass.getTypeName());
				myControlPanel = new CCShaderCompileControl((CCShaderCompileHandle)myPropertyHandle, _myControlCompoent);
			}else{
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				CCObjectControl myObjectControl = new CCObjectControl(myObjectHandle, _myControlCompoent, theDepth);
				myControlPanel = myObjectControl;
				
				DefaultMutableTreeNode myObjectNode = new DefaultMutableTreeNode(new CCTreeNodeUserObject(myObjectHandle));
				theParentNode.add(myObjectNode);
				setData(myObjectNode, myObjectHandle, myObjectControl, theDepth+1);
			}

			myUserObject.add(myControlPanel, theDepth);
		}
		if(myUserObject.controlComponent() != null)theObjectControl.addStuff(myUserObject.controlComponent());
	}

    /** Required by TreeSelectionListener interface. */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)_myTree.getLastSelectedPathComponent();
 
        if (node == null) return;
 
        CCTreeNodeUserObject nodeInfo = (CCTreeNodeUserObject)node.getUserObject();
        _myControlCompoent.showContent(nodeInfo.controlComponent());
        _myControlCompoent.setPresets(nodeInfo.propertyHandle());
        _myControlCompoent.infoPanel();
    }
	
	
}

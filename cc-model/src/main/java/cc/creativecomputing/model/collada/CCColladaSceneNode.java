/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.model.collada;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;

/**
 * Declares a point of interest in a scene.
 * <p>
 * The <node> element embodies the hierarchical relationship of elements in a scene.by 
 * declaring a point of interest in a scene. A node denotes one point on a branch of the 
 * scene graph. The <node> element is essentially the root of a subgraph of the entire scene graph.
 * <p>
 * Within the scene graph abstraction, there are arcs and nodes. Nodes are points of information 
 * within the graph. Arcs connect nodes to other nodes. Nodes are further distinguished as interior 
 * (branch) nodes and exterior (leaf) nodes. COLLADA uses the term node to denote interior nodes. 
 * Arcs are also called paths.
 * @author christianriekoff
 *
 */
public class CCColladaSceneNode extends CCColladaElement{
	
	public enum CCColladaSceneNodeType{
		JOINT, NODE
	}
	
	public static enum CCColladaSceneNodeInstanceType{
		CAMERA,
		CONTROLLER,
		GEOMETRY,
		LIGHT,
		NODE
	}
	
	private Map<String, CCColladaSceneNode> _myNodeMap = new HashMap<String, CCColladaSceneNode>();
	private List<CCColladaSceneNode> _myNodes = new ArrayList<CCColladaSceneNode>();
	
	private CCMatrix4x4 _myMatrix;
	private CCColladaSceneNodeType _myType;
	
	private CCColladaSceneNodeInstanceType _myInstanceType;
	
	private List<CCMesh> _myGeometries = new ArrayList<>();
	private CCCamera _myCamera;

	public CCColladaSceneNode(CCColladaLoader theLoader, CCXMLElement theNodeXML) {
		super(theNodeXML);
		
		_myType = CCColladaSceneNodeType.valueOf(theNodeXML.attribute("type", "NODE"));
		
		_myMatrix = new CCMatrix4x4();
		
		for(CCXMLElement myChild:theNodeXML){
			switch(myChild.name()){
			case "matrix":
				String[] myFloatStrings = myChild.content().split("\\s");
				double[] myFloats = new double[16];
				for(int i = 0; i < myFloats.length;i++) {
					myFloats[i] = Float.parseFloat(myFloatStrings[i]);
				}
				_myMatrix.fromArray(myFloats, true);
				break;
			case "translate":
				String[] myTranslattion = myChild.content().split("\\s");
				_myMatrix.applyTranslationPre(
					Float.parseFloat(myTranslattion[0]), 
					Float.parseFloat(myTranslattion[1]), 
					Float.parseFloat(myTranslattion[2])
				);
				break;
			case "rotate":
				String[] myRotation = myChild.content().split("\\s");
				_myMatrix.applyRotation(
					CCMath.radians(Float.parseFloat(myRotation[3])),
					Float.parseFloat(myRotation[0]), 
					Float.parseFloat(myRotation[1]), 
					Float.parseFloat(myRotation[2])
				);
				break;
			case "scale":
				String[] myScale = myChild.content().split("\\s");
				_myMatrix.scale(
					Float.parseFloat(myScale[0]), 
					Float.parseFloat(myScale[1]), 
					Float.parseFloat(myScale[2])
				);
				break;
			case "instance_camera":
				String myCameraURL = myChild.attribute("url").replaceAll("#", "");
				CCColladaCamera myCamera = theLoader.cameras().element(myCameraURL);
				_myCamera = myCamera.camera();
				CCVector3 myCameraPosition = _myCamera.position();
				CCVector3 myCameraTarget = _myCamera.target();
				_myMatrix.applyPostPoint(myCameraPosition, myCameraPosition);
				_myMatrix.applyPostPoint(myCameraTarget, myCameraTarget);
				
				_myCamera.position(myCameraPosition);
				_myCamera.target(myCameraTarget);
				_myCamera.up(0, 1, 0);
				_myInstanceType = CCColladaSceneNodeInstanceType.CAMERA;
				break;
			case "instance_geometry":
				String myGeometryURL = myChild.attribute("url").replaceAll("#", "");
				CCColladaGeometry myGeometry = theLoader.geometries().element(myGeometryURL);
				if(myGeometry.data().size() == 0)return;
				CCColladaGeometryData myGeometryData = myGeometry.data().get(0);
				
				CCDrawMode _myDrawMode = CCDrawMode.TRIANGLES;
				switch(myGeometryData.primitiveSize()){
				case 3:
					_myDrawMode = CCDrawMode.TRIANGLES;
					break;
				}
				CCMesh myGeometryMesh = new CCMesh(_myDrawMode);
				
				myGeometryMesh.vertices(myGeometryData.positions());
				if(myGeometryData.hasNormals()){
					myGeometryData.normals().rewind();
					myGeometryMesh.normals(myGeometryData.normals());
				}
				_myGeometries.add(myGeometryMesh);
				
				_myInstanceType = CCColladaSceneNodeInstanceType.GEOMETRY;
				break;
			}
		}
		
		
		for(CCXMLElement myNodeXML:theNodeXML.children("node")) {
			CCColladaSceneNode myNode = new CCColladaSceneNode(theLoader, myNodeXML);
			_myNodeMap.put(myNode.id(), myNode);
			_myNodes.add(myNode);
		}
	}
	
	public List<CCColladaSceneNode> children(){
		return _myNodes;
	}
	
	/**
	 * Use node() to get a certain child node.
	 * @param theIndex number of the child
	 * @return CCXMLElement, the child
	 */
	public CCColladaSceneNode node(final int theIndex){
		return _myNodes.get(theIndex);
	}
	
	/**
	 * Returns the first child node with the given node name. If there
	 * is no such child node the method returns null.
	 * @param theNodeName String
	 * @return CCXMLElement
	 */
	public CCColladaSceneNode node(final String theNodeName){
		if (theNodeName.indexOf('/') != -1) {
	      return nodeRecursive(CCStringUtil.split(theNodeName, '/'), 0);
	    }
		return _myNodeMap.get(theNodeName);
	}

	/**
	 * Internal helper function for {@linkplain #child(String)}
	 * 
	 * @param theItems result of splitting the query on slashes
	 * @param theOffset where in the items[] array we're currently looking
	 * @return matching element or null if no match
	 */
	protected CCColladaSceneNode nodeRecursive(String[] theItems, int theOffset) {
		// if it's a number, do an index instead
		if (Character.isDigit(theItems[theOffset].charAt(0))) {
			CCColladaSceneNode myResult = node(Integer.parseInt(theItems[theOffset]));
			if (theOffset == theItems.length - 1) {
				return myResult;
			} else {
				return myResult.nodeRecursive(theItems, theOffset + 1);
			}
		}

		CCColladaSceneNode myResult = node(theItems[theOffset]);

		if (theOffset == theItems.length - 1) {
			return myResult;
		} else {
			return myResult.nodeRecursive(theItems, theOffset + 1);
		}
	}
	
	public CCMatrix4x4 matrix() {
		return _myMatrix;
	}

	public CCColladaSceneNodeType type() {
		return _myType;
	}
	
	public CCColladaSceneNodeInstanceType instanceType(){
		return _myInstanceType;
	}

	public void type(CCColladaSceneNodeType theType) {
		_myType = theType;
	}
	
	public CCCamera camera(){
		return _myCamera;
	}
	
	public void draw(CCGraphics g){
		switch(_myInstanceType){
		case CAMERA:
			if(_myCamera == null)return;
		
			
			_myCamera.viewport(g.camera().viewport());
			_myCamera.draw(g);
//			g.applyMatrix(_myMatrix);
			break;
		case GEOMETRY:
			
			g.pushMatrix();
			g.applyMatrix(_myMatrix);
			for(CCMesh myGeometry:_myGeometries){
				myGeometry.draw(g);
			}
			g.popMatrix();
			break;
		}
	}
}

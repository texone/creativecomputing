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

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.util.CCFrustum;
import cc.creativecomputing.graphics.util.CCFrustum.CCFrustumRelation;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCTransform;
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
public class CCColladaSceneNode extends CCColladaElement implements Iterable<CCColladaSceneNode>{
	
	public enum CCColladaSceneNodeType{
		JOINT, NODE
	}
	
	public enum CCColladaSceneNodeInstanceType{
		CAMERA,
		CONTROLLER,
		GEOMETRY,
		LIGHT,
		NODE
	}
	
	@CCProperty(name = "nodes")
	private Map<String, CCColladaSceneNode> _myNodeMap = new LinkedHashMap<>();
	private List<CCColladaSceneNode> _myNodes = new ArrayList<CCColladaSceneNode>();
	
	private CCMatrix4x4 _myMatrix;
	@CCProperty(name = "transform")
	private CCTransform _myTransform;
	private CCColladaSceneNodeType _myType;
	
	private CCColladaSceneNodeInstanceType _myInstanceType = null;
	@CCProperty(name = "meshes")
	private List<CCMesh> _myGeometries = new ArrayList<>();
	private CCCamera _myCamera;
	
	private Map<String, Object> _myAttributes = new HashMap<>();
	
	private CCAABB _myBoundingBox = null;
	
	private CCColladaSceneNodeDisplayVisitor _myDrawVisitor;
	
	private CCColladaSceneNodeMaterial _myMaterial;
	
	private CCColladaGeometry _myGeometry;
	
	
	// fassade
	
	
	private CCColladaSceneNode _myParent;

	public CCColladaSceneNode(CCColladaLoader theLoader, CCColladaSceneNode theParent, CCDataElement theNodeXML, boolean theUseNameKey) {
		super(theNodeXML);
		
		_myParent = theParent;
		_myType = CCColladaSceneNodeType.valueOf(theNodeXML.attribute("type", "NODE"));
		
		_myMatrix = new CCMatrix4x4();
		_myTransform = new CCTransform();
		
		for(CCDataElement myChild:theNodeXML){
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
				
//				_myMatrix.applyTranslationPre(
//					Float.parseFloat(myTranslattion[0]), 
//					Float.parseFloat(myTranslattion[1]), 
//					Float.parseFloat(myTranslattion[2])
//				);
				
				_myTransform.translation(
					Float.parseFloat(myTranslattion[0]), 
					Float.parseFloat(myTranslattion[1]), 
					Float.parseFloat(myTranslattion[2])
				);
				
				break;
			case "rotate":
				String[] myRotation = myChild.content().split("\\s");
	
//				_myMatrix.applyRotation(
//					CCMath.radians(Float.parseFloat(myRotation[3])),
//					Float.parseFloat(myRotation[0]), 
//					Float.parseFloat(myRotation[1]), 
//					Float.parseFloat(myRotation[2])
//				);
				
				_myTransform.rotate(
					CCMath.radians(Float.parseFloat(myRotation[3])),
					Float.parseFloat(myRotation[0]), 
					Float.parseFloat(myRotation[1]), 
					Float.parseFloat(myRotation[2])
				);
				break;
			case "scale":
				String[] myScale = myChild.content().split("\\s");
				
//				_myMatrix.scale(
//					Float.parseFloat(myScale[0]), 
//					Float.parseFloat(myScale[1]), 
//					Float.parseFloat(myScale[2])
//				);
				
				_myTransform.scale(
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
				
				_myTransform.applyForward(myCameraPosition, myCameraPosition);
				_myTransform.applyForward(myCameraTarget, myCameraTarget);
				
				_myCamera.position(myCameraPosition);
				_myCamera.target(myCameraTarget);
				_myCamera.up(0, 1, 0);
				_myInstanceType = CCColladaSceneNodeInstanceType.CAMERA;
				break;
			case "instance_geometry":
				String myGeometryURL = myChild.attribute("url").replace("#", "");
				_myGeometry = theLoader.geometries().element(myGeometryURL);
				if(_myGeometry.data().size() == 0)return;
				CCColladaGeometryData myGeometryData = _myGeometry.data().get(0);
				
				CCMesh myGeometryMesh = new CCVBOMesh(myGeometryData.drawMode());
				myGeometryMesh.vertices(myGeometryData.positions());
				if(myGeometryData.hasNormals()){
					myGeometryData.normals().rewind();
					myGeometryMesh.normals(myGeometryData.normals());
				}
				if(myGeometryData.hasTexCoords()) {
					myGeometryData.texCoords().rewind();
					myGeometryMesh.textureCoords(myGeometryData.texCoords());
				}
				_myGeometries.add(myGeometryMesh);
				
				_myInstanceType = CCColladaSceneNodeInstanceType.GEOMETRY;
				break;
			case "instance_node":
				_myInstanceType = CCColladaSceneNodeInstanceType.NODE;
				String myNodeURL = theUseNameKey && !name().equals("") ? name() : myChild.attribute("url").replace("#", "");
				if(theLoader.nodes() == null) {
					_myNodeMap.put(myNodeURL, null);
				}else {
					CCColladaSceneNode myInstanceNode = theLoader.nodes().element(myNodeURL);
					_myNodeMap.put(myNodeURL, myInstanceNode);
					if(myInstanceNode != null) {
						_myNodes.add(myInstanceNode);
					}
				}
				break;
			case "node":
				if(_myInstanceType == null)_myInstanceType = CCColladaSceneNodeInstanceType.NODE;
				CCColladaSceneNode myNode = new CCColladaSceneNode(theLoader, this, myChild, theUseNameKey);
				myNodeURL = theUseNameKey && !myNode.name().equals("") ? myNode.name() : myNode.id();
				_myNodeMap.put(myNodeURL, myNode);
				_myNodes.add(myNode);
				break;
			}
		}
		
		if(instanceType() == CCColladaSceneNodeInstanceType.GEOMETRY){
			CCMatrix4x4 myStack = new CCMatrix4x4();
			matrixStack(myStack);
			
			_myBoundingBox = null;
			for(CCMesh myMesh:geometries()){
				FloatBuffer myVertices = myMesh.vertices();
				myVertices.rewind();
				CCVector3 myVertex = new CCVector3(myVertices.get(),myVertices.get(),myVertices.get());
				myStack.applyPostPoint(myVertex, myVertex);
				if(_myBoundingBox == null){
					_myBoundingBox = new CCAABB(myVertex);
				}
				while(myVertices.hasRemaining()){
					myVertex = new CCVector3(myVertices.get(),myVertices.get(),myVertices.get());
					myStack.applyPostPoint(myVertex, myVertex);
					_myBoundingBox.checkSize(myVertex);
				}
				myVertices.rewind();
			}
		}
	}
	
	public CCColladaGeometry geometry() {
		return _myGeometry;
	}
	
	public CCAABB bounds() {
		if(_myBoundingBox == null) {
			for(CCColladaSceneNode myNode:children()) {
				if(myNode.bounds() == null)continue;
				if(_myBoundingBox == null) {
					_myBoundingBox = new CCAABB(myNode.bounds().min());
				}
				_myBoundingBox.checkSize(myNode.bounds());
			}
		}
		return _myBoundingBox;
	}
	
	public void matrixStack(CCMatrix4x4 theMatrix) {
		theMatrix.multiply(matrix(), theMatrix);
		theMatrix.multiply(transform().toMatrix(), theMatrix);
		
		if(_myParent != null)_myParent.matrixStack(theMatrix);
		
	}
	
	public CCMatrix4x4 toWorld() {
		CCMatrix4x4 myResult = new CCMatrix4x4();
		matrixStack(myResult);
		return myResult;
	}
	
	public void resolveMissingNodes(CCColladaLoader theLoader) {
		for(String myKey:_myNodeMap.keySet()) {
			if(_myNodeMap.get(myKey) == null) {
				CCColladaSceneNode myInstanceNode = theLoader.nodes().element(myKey);
				_myNodeMap.put(myKey, myInstanceNode);
				_myNodes.add(myInstanceNode);
			}
		}
		for(CCColladaSceneNode myChild:_myNodes) {
			myChild.resolveMissingNodes(theLoader);
		}
	}
	
	public static interface CCColladaSceneNodeVisitor{
		public void apply(CCColladaSceneNode theNode);
	}
	
	public static interface CCColladaSceneNodeDisplayVisitor{
		public void apply(CCColladaSceneNode theNode, CCGraphics g);
	}
	
	public static interface CCColladaSceneNodeMaterial{
		public void start(CCColladaSceneNode theNode, CCGraphics g);
		public void end(CCColladaSceneNode theNode, CCGraphics g);
	}
	
	public void visit(CCColladaSceneNodeVisitor theVisitor){
		theVisitor.apply(this);
		for(CCColladaSceneNode myChild:_myNodes){
			myChild.visit(theVisitor);
		}
	}
	
	public void drawVisitor(CCColladaSceneNodeDisplayVisitor theVisitor){
		_myDrawVisitor = theVisitor;
		for(CCColladaSceneNode myChild:_myNodes){
			myChild.drawVisitor(theVisitor);
		}
	}
	
	public void material(CCColladaSceneNodeMaterial theMaterial) {
		_myMaterial = theMaterial;
	}
	
	public boolean hasChildren(){
		return _myNodes != null && _myNodes.size() > 1;
	}
	
	public List<CCColladaSceneNode> children(){
		return _myNodes;
	}
	
	public Map<String,Object> attributes(){
		return _myAttributes;
	}
	
	public List<CCMesh> geometries(){
		return _myGeometries;
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
	
	/**
	 * Returns the first child node with the given node id. If there
	 * is no such child node the method returns null.
	 * @param theNodeID String
	 * @return the scene node with the given id
	 */
	public CCColladaSceneNode node(final String theNodeID){
		if (theNodeID.indexOf('/') != -1) {
	      return nodeRecursive(CCStringUtil.split(theNodeID, '/'), 0);
	    }
		return _myNodeMap.get(theNodeID);
	}
	
	/**
	 * Internal helper function for {@linkplain #child(String)}
	 * 
	 * @param theItems result of splitting the query on slashes
	 * @param theOffset where in the items[] array we're currently looking
	 * @return matching element or null if no match
	 */
	protected Optional<CCColladaSceneNode> nodeNameRecursive(String[] theItems, int theOffset) {
		Optional<CCColladaSceneNode> myResult = nodeByName(theItems[theOffset]);

		if (theOffset == theItems.length - 1) {
			return myResult;
		} else {
			return myResult.get().nodeNameRecursive(theItems, theOffset + 1);
		}
	}
	
	/**
	 * Returns the first child node with the given node name. If there
	 * is no such child node the method returns null.
	 * @param theNodeName the name of the node to look for
	 * @return the scene node with the given name
	 */
	public Optional<CCColladaSceneNode> nodeByName(final String theNodeName){
		if (theNodeName.indexOf('/') != -1) {
	      return nodeNameRecursive(CCStringUtil.split(theNodeName, '/'), 0);
	    }
		for(CCColladaSceneNode myNode:children()) {
			if(myNode.name().equals(theNodeName))return Optional.of(myNode);
		}
		return Optional.empty();
	}

	
	
	public CCMatrix4x4 matrix() {
		return _myMatrix;
	}
	
	public CCTransform transform(){
		return _myTransform;
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
	
	public void drawBounds(CCGraphics g) {
		if(!_cDrawBounds)return;
		if(_myInstanceType == null)return;
		
		switch(_myInstanceType){
		case GEOMETRY:
			g.pushMatrix();
			
			
			g.beginShape(CCDrawMode.LINE_LOOP);
			g.vertex(_myBoundingBox.min().x, _myBoundingBox.min().y, _myBoundingBox.min().z);
			g.vertex(_myBoundingBox.max().x, _myBoundingBox.min().y, _myBoundingBox.min().z);
			g.vertex(_myBoundingBox.max().x, _myBoundingBox.min().y, _myBoundingBox.max().z);
			g.vertex(_myBoundingBox.min().x, _myBoundingBox.min().y, _myBoundingBox.max().z);
			g.endShape();

			g.beginShape(CCDrawMode.LINE_LOOP);
			g.vertex(_myBoundingBox.min().x, _myBoundingBox.max().y, _myBoundingBox.min().z);
			g.vertex(_myBoundingBox.max().x, _myBoundingBox.max().y, _myBoundingBox.min().z);
			g.vertex(_myBoundingBox.max().x, _myBoundingBox.max().y, _myBoundingBox.max().z);
			g.vertex(_myBoundingBox.min().x, _myBoundingBox.max().y, _myBoundingBox.max().z);
			g.endShape();
			
			g.beginShape(CCDrawMode.LINES);
			g.vertex(_myBoundingBox.min().x, _myBoundingBox.min().y, _myBoundingBox.min().z);
			g.vertex(_myBoundingBox.min().x, _myBoundingBox.max().y, _myBoundingBox.min().z);
			
			g.vertex(_myBoundingBox.max().x, _myBoundingBox.min().y, _myBoundingBox.min().z);
			g.vertex(_myBoundingBox.max().x, _myBoundingBox.max().y, _myBoundingBox.min().z);
			
			g.vertex(_myBoundingBox.max().x, _myBoundingBox.min().y, _myBoundingBox.max().z);
			g.vertex(_myBoundingBox.max().x, _myBoundingBox.max().y, _myBoundingBox.max().z);
			
			g.vertex(_myBoundingBox.min().x, _myBoundingBox.min().y, _myBoundingBox.max().z);
			g.vertex(_myBoundingBox.min().x, _myBoundingBox.max().y, _myBoundingBox.max().z);
			g.endShape();
			g.popMatrix();
			break;
		case NODE:
			for(CCColladaSceneNode myNode:_myNodes){
				myNode.drawBounds(g);
			}
			break;
		default:
			break;
		}
	}
	
	public void draw(CCGraphics g){
		draw(g, null);
	}
	
	public static int skip = 0;
	public static int count = 0;
	
	public void draw(CCGraphics g, CCFrustum theFrustum){
		if(!_cDraw)return;
		if(_myInstanceType == null)return;
		count++;
		if(theFrustum != null && _myBoundingBox != null && _cCheckBounds) {
			CCVector3 myCenter = _myBoundingBox.center();
			double myRadius = CCMath.max(_myBoundingBox.extent().x, _myBoundingBox.extent().y, _myBoundingBox.extent().z);
			if(theFrustum.isInFrustum(myCenter, myRadius) == CCFrustumRelation.OUTSIDE) {
//				CCLog.info("skip", name());
				skip++;
				return;
			}
		}
		
		if(_myDrawVisitor!= null){
			_myDrawVisitor.apply(this, g);
		}
		
		if(_myMaterial != null)_myMaterial.start(this, g);
		
		switch(_myInstanceType){
		case CAMERA:
			if(_myCamera == null)return;
		
			
//			_myCamera.viewport(g.camera().viewport());
//			_myCamera.draw(g);
//			g.applyMatrix(_myMatrix);
			break;
		case GEOMETRY:
			g.pushMatrix();
			g.applyMatrix(_myMatrix);
			g.applyMatrix(_myTransform.toMatrix());
			for(CCMesh myGeometry:_myGeometries){
				myGeometry.draw(g);
			}
			g.popMatrix();
			break;
		case NODE:
			g.pushMatrix();
			g.applyMatrix(_myMatrix);
			g.applyMatrix(_myTransform.toMatrix());
			for(CCColladaSceneNode myNode:_myNodes){
				myNode.draw(g, theFrustum);
			}
			g.popMatrix();
			break;
		case CONTROLLER:
			break;
		case LIGHT:
			break;
		default:
			break;
		}

		if(_myMaterial != null)_myMaterial.end(this, g);
	}

	@Override
	public Iterator<CCColladaSceneNode> iterator() {
		return _myNodes.iterator();
	}
}

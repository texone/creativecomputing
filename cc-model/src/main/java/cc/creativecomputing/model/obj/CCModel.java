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
package cc.creativecomputing.model.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCColorMaterialMode;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;


public class CCModel {
	private final HashMap<String, CCObject> _myObjectMap = new LinkedHashMap<>();
	private final HashMap<String, CCObjectGroup> _myObjectGroupMap = new LinkedHashMap<>();
	
	private final HashMap<String, CCMaterial> _myMaterialMap = new LinkedHashMap<String, CCMaterial>();
	
	private final List<CCVector3> _myVertices = new ArrayList<CCVector3>();
	private final List<CCVector3> _myNormals = new ArrayList<CCVector3>();
	private final List<CCVector2> _myTextureCoords = new ArrayList<CCVector2>();
	private final List<CCFace> _myFaces = new ArrayList<CCFace>();
	private final CCAABB _myBoundingBox = new CCAABB();
	
	public CCModel(){
	}
	
	public CCAABB boundingBox(){
		return _myBoundingBox;
	}

	public HashMap<String, CCObject> objectMap() {
		return _myObjectMap;
	}
	
	public String[] objectNames(){
		return _myObjectMap.keySet().toArray(new String[0]);
	}
	
	public CCObject object(final String theGroup){
		return _myObjectMap.get(theGroup);
	}
	
	public HashMap<String, CCObjectGroup> groupMap() {
		return _myObjectGroupMap;
	}
	
	public String[] groupNames(){
		return _myObjectGroupMap.keySet().toArray(new String[0]);
	}
	
	public CCObjectGroup group(final String theGroup){
		return _myObjectGroupMap.get(theGroup);
	}
	
	/**
	 * Returns the first segment of the given group. Usually this
	 * will be the only segment as most files only have one segment
	 * per group although more would be possible
	 * @param theObject
	 * @return
	 */
	public CCSegment segment(final String theObject){
		return _myObjectGroupMap.get(theObject).objects().get(0).segments().get(0);
	}

	public HashMap<String, CCMaterial> materialMap() {
		return _myMaterialMap;
	}
	
	public String[] getMaterialNames(){
		return _myMaterialMap.keySet().toArray(new String[0]);
	}
	
	public CCMaterial material(final String theMaterialName){
		return _myMaterialMap.get(theMaterialName);
	}

	public List<CCVector3> vertices() {
		return _myVertices;
	}
	
	public void addVertex(final CCVector3 theVertex){
		_myBoundingBox.checkSize(theVertex);
		_myVertices.add(theVertex);
	}

	public List<CCVector3> normals() {
		return _myNormals;
	}

	public List<CCVector2> textureCoords() {
		return _myTextureCoords;
	}
	
	public List<CCFace> faces(){
		return _myFaces;
	}
	
	
	
	/**
	 * You must call this method before drawing, to put your model data into a
	 * <code>CCVBOMesh</code> for fast drawing. All active groups will be converted to VBOMesh.
	 * Use <code>deactivateGroup</code> to avoid conversion of certain groups, be aware that you
	 * can not activate these groups for later drawing.
	 * @see #deactivateGroup(String)
	 */
	public void convert(final boolean theGenerateNormal){
//		for(CCVector3 myVertex:_myVertices){
//			_myTransformationMatrix.transform(myVertex);
//		}
//		_myTransformationMatrix.transform(_myBoundingBox.min());
//		_myTransformationMatrix.transform(_myBoundingBox.max());
		for(CCObjectGroup myGroup:_myObjectGroupMap.values()){
			for(CCObject myObject:myGroup.objects()){
				if(!myObject.isActiv())continue;
				for(CCSegment mySegment:myObject.segments()){
					mySegment.convert(theGenerateNormal);
				}
			}
		}
		
	}
	
	public void convert(){
		convert(false);
	}
	
	/**
	 * Centers the model by moving the coordinates
	 */
	public void center(){
		CCVector3 myTranslation = _myBoundingBox.center().clone();
		myTranslation.multiplyLocal(-1);
		translate(myTranslation);
	}
	
	public void scale(final float theScale){
		for(CCVector3 myVertex:_myVertices){
			myVertex.multiplyLocal(theScale);
		}
		
		/* also update boundingbox */
		_myBoundingBox.max().multiplyLocal(theScale);
		_myBoundingBox.min().multiplyLocal(theScale);
	}
	
	public void translate(final CCVector3 theVector){
		for(CCVector3 myVertex:_myVertices){
			myVertex.addLocal(theVector);
		}
		
		/* also update boundingbox */
		_myBoundingBox.max().addLocal(theVector);
		_myBoundingBox.min().addLocal(theVector);
	}
    
    public CCVector3 centerOfModel(){
		return _myBoundingBox.center().clone();
	}
	
	/**
	 * Use this function to activate a group of the model for drawing.
	 * 
	 * @param theGroupName
	 * @related deativateGroup ( )
	 */
	public void activateGroup(final String theGroupName){
		CCObject myGroup = _myObjectMap.get(theGroupName);
		if(myGroup != null)myGroup.isActiv(true);
	}
	
	/**
	 * Deactivates the given group, so that it will not be drawn, when you draw the model.
	 * If you call this method before convert, the group will also not be included in the
	 * resulting <code>CCVBOMesh</code> instance, and can not be activated for later drawing.
	 * @param theGroupName
	 * @see #convert()
	 * @see #activateGroup(String)
	 */
	public void deactivateGroup(final String theGroupName){
		CCObject myGroup = _myObjectMap.get(theGroupName);
		if(myGroup != null)myGroup.isActiv(false);
	}
	
	/**
	 * This method is similar to <code>deactivateGroup</code> but deactivates all groups except
	 * the given one.
	 * @param theGroupName
	 */
	public void deactivateAllBut(final String theGroupName){
		for(CCObject myGroup:_myObjectMap.values()){
			myGroup.isActiv(false);
		}
		activateGroup(theGroupName);
	}
	
	/**
	 * 
	 * @param g the graphics object for rendering
	 * @param theGroup theGroup to draw
	 */
	public void draw(CCGraphics g,final String theGroup){
		CCColorMaterialMode myColorMaterialMode = g.colorMaterial();
		g.colorMaterial(CCColorMaterialMode.OFF);
		CCObject myGroup = _myObjectMap.get(theGroup.toLowerCase());
		if(myGroup != null){
			for(CCSegment mySegment:myGroup.segments()){
				mySegment.draw(g);
			}
		}
		g.colorMaterial(myColorMaterialMode);
	}
	
	/**
	 * Draws all activated groups of the loaded model. By default all groups are
	 * activated and will be drawn. If you pass a group name to this function only
	 * this group will be drawn no matter if it is activated or not.
	 * @param g the graphics object for rendering
	 */
	public void draw(CCGraphics g){
		CCColorMaterialMode myColorMaterialMode = g.colorMaterial();
		g.colorMaterial(CCColorMaterialMode.OFF);
		for(CCObject myGroup:_myObjectMap.values()){
			if(myGroup.isActiv()){
				for(CCSegment mySegment:myGroup.segments()){
					mySegment.draw(g);
				}
			}
		}
		g.colorMaterial(myColorMaterialMode);
	}
}

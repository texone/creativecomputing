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
import java.util.List;

import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;


public class CCModel {
	private final HashMap<String, CCGroup> _myGroupMap = new HashMap<String, CCGroup>();
	
	private final HashMap<String, CCMaterial> _myMaterialMap = new HashMap<String, CCMaterial>();
	
	private final List<CCVector3> _myVertices = new ArrayList<CCVector3>();
	private final List<CCVector3> _myNormals = new ArrayList<CCVector3>();
	private final List<CCVector2> _myTextureCoords = new ArrayList<CCVector2>();
	private final List<CCFace> _myFaces = new ArrayList<CCFace>();
	
	public CCModel(){
	}

	public HashMap<String, CCGroup> groupMap() {
		return _myGroupMap;
	}
	
	public String[] groupNames(){
		return _myGroupMap.keySet().toArray(new String[0]);
	}
	
	public CCGroup group(final String theGroup){
		return _myGroupMap.get(theGroup);
	}
	
	/**
	 * Returns the first segment of the given group. Usually this
	 * will be the only segment as most files only have one segment
	 * per group although more would be possible
	 * @param theGroup
	 * @return
	 */
	public CCSegment segment(final String theGroup){
		return _myGroupMap.get(theGroup).segments().get(0);
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
		for(CCGroup myGroup:_myGroupMap.values()){
			if(!myGroup.isActiv())continue;
			for(CCSegment mySegment:myGroup.segments()){
				mySegment.convert(theGenerateNormal);
			}
		}
	}
	
	public void convert(){
		convert(false);
	}
	
	/**
	 * Use this function to activate a group of the model for drawing.
	 * 
	 * @param theGroupName
	 * @related deativateGroup ( )
	 */
	public void activateGroup(final String theGroupName){
		CCGroup myGroup = _myGroupMap.get(theGroupName);
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
		CCGroup myGroup = _myGroupMap.get(theGroupName);
		if(myGroup != null)myGroup.isActiv(false);
	}
	
	/**
	 * This method is similar to <code>deactivateGroup</code> but deactivates all groups except
	 * the given one.
	 * @param theGroupName
	 */
	public void deactivateAllBut(final String theGroupName){
		for(CCGroup myGroup:_myGroupMap.values()){
			myGroup.isActiv(false);
		}
		activateGroup(theGroupName);
	}
}

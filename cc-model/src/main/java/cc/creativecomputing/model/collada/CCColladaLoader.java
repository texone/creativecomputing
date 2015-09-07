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

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.io.xml.CCXMLIO;


/**
 * <p>
 * Lucerne University of Applied Sciences and Arts <a href="http://www.hslu.ch">http://www.hslu.ch</a>
 * </p>
 * 
 * <p>
 * This source is free; you can redistribute it and/or modify it under the terms of the GNU General Public License and
 * by nameing of the originally author
 * </p>
 * 
 * <p>
 * this class starts the "xml to object-Mapping" and converts it to human handable Shapes.
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
public class CCColladaLoader {

	private String _myAuthoringTool = "unknown";
	private int[] _myAuthoringToolVersion = new int[] { 0 };
	
	private CCColladaScenes _myScenes;
	private CCColladaGeometries _myGeometries;
	private CCColladaControllers _myControllers;
	private CCColladaAnimations _myAnimations;
	private CCColladaCameras _myCameras;

	public CCColladaLoader(Path thePath) throws CCColladaLoaderException {
		super();

		try {
			parseXML(thePath);

			// Mapping finished. Now make them human readable
		} catch (RuntimeException e) {
			throw new CCColladaLoaderException("Couldn't parse COLLADA file. Maybe the authoring tool " + _myAuthoringTool + " isn't supported or the xml format is invalid.", e);
		}
	}

	/**
	 * @param args the command line arguments
	 */
	private void parseXML(Path thePath) {
		CCXMLElement myRoot = CCXMLIO.createXMLElement(thePath, false);
		
		if(myRoot == null){
			throw new CCColladaLoaderException("File " + thePath + " does not exist. Make sure you pass a valid file.");
		}
		_myAuthoringTool = myRoot.child("asset/contributor/authoring_tool").content();

		LinkedList<String> numbers = new LinkedList<String>();
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(_myAuthoringTool);
		while (m.find()) {
			numbers.add(m.group());
		}
		_myAuthoringToolVersion = new int[numbers.size()];
		for (int i = 0; i < numbers.size(); i++) {
			_myAuthoringToolVersion[i] = Integer.parseInt(numbers.get(i));
		}

		// build images
//		List<CCXMLElement> myImages = myRoot.children("library_images/image");
//		CCColladaImages myImageLib = new CCColladaImages(myImages);

		// build effects
//		List<CCXMLElement> myEffects = myRoot.children("library_effects/effect");
//		CCColladaEffects myEffectLib = new CCColladaEffects(myEffects, myImageLib);

		// build materials
//		List<CCXMLElement> myMaterials = myRoot.children("library_materials/material");
//		CCColladaMaterials myMaterialLib = new CCColladaMaterials(myMaterials, myEffectLib);
		
		_myCameras = new CCColladaCameras(myRoot.children("library_cameras/camera"));
		_myGeometries = new CCColladaGeometries(myRoot.children("library_geometries/geometry"));
		_myScenes = new CCColladaScenes(this, myRoot.children("library_visual_scenes/visual_scene"));
		_myControllers = new CCColladaControllers(myRoot.children("library_controllers/controller"), _myGeometries);
		_myAnimations = new CCColladaAnimations(myRoot.children("library_animations/animation"));
	}
	
	/**
	 * Returns the library of geometries.
	 * @see CCColladaGeometries
	 * @see CCColladaGeometry
	 * @return
	 */
	public CCColladaGeometries geometries() {
		return _myGeometries;
	}
	
	/**
	 * Returns the library of scenes.
	 * @see CCColladaScenes
	 * @see CCColladaScene
	 * @return
	 */
	public CCColladaScenes scenes() {
		return _myScenes;
	}
	
	public CCColladaAnimations animations() {
		return _myAnimations;
	}
	
	public CCColladaControllers controllers() {
		return _myControllers;
	}
	
	public CCColladaCameras cameras(){
		return _myCameras;
	}

	/**
	 * returns how the file was made with
	 * 
	 * @return
	 */
	public String authoringTool() {
		return _myAuthoringTool;
	}

	/**
	 * returns how the file was made with
	 * 
	 * @return
	 */
	public int[] authoringToolVersion() {
		return _myAuthoringToolVersion;
	}

	
}

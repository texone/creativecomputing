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
package cc.creativecomputing.model.svg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.svg.CCSVGElement.CCShapeKind;

public class CCSVGGroup extends CCSVGElement implements Iterable<CCSVGElement>{
	
	protected List<CCSVGElement> _myChildren;
	protected HashMap<String, CCSVGElement> _myNameTable;

	public CCSVGGroup(CCSVGGroup theParent, CCShapeKind theShapeKind) {
		super(theParent, theShapeKind, CCShapeFamily.GROUP);
	}
	
	public List<CCSVGElement> children(){
		return _myChildren;
	}

	@Override
	public Iterator<CCSVGElement> iterator() {
		return _myChildren.iterator();
	}

	/**
	 * Add a shape to the name lookup table.
	 */
	public void addName(String theName, CCSVGElement theShape) {
		if (parent != null) {
			parent.addName(theName, theShape);
		} else {
			if (_myNameTable == null) {
				_myNameTable = new HashMap<String, CCSVGElement>();
			}
			_myNameTable.put(theName, theShape);
		}
	}
	
	/**
	 * @webref pshape:method
	 * @brief Adds a new child
	 * @param theElement
	 *            any variable of type PShape
	 * @see #getChild(int)
	 */
	public void addChild(CCSVGElement theElement) {
		
		_myChildren.add(theElement);
		theElement.parent = this;

		if (theElement.name() != null) {
			addName(theElement.name(), theElement);
		}
	}

	/**
	 * Same as getChild(name), except that it first walks all the way up the
	 * hierarchy to the eldest grandparent, so that children can be found
	 * anywhere.
	 */
	public CCSVGElement findChild(String target) {
		if (parent == null) {
			return child(target);

		} else {
			return parent.findChild(target);
		}
	}
	
	/**
	 * Extracts a child shape from a parent shape. Specify the name of the shape
	 * with the <b>target</b> parameter. The shape is returned as a
	 * <b>CCSVGElement</b> object, or <b>null</b> is returned if there is an error.
	 * 
	 * @brief Returns a child element of a shape as a PShape object
	 * @param index
	 *            the layer position of the shape to get
	 * @see #addChild(CCSVGElement)
	 */
	public CCSVGElement child(int index) {
		return _myChildren.get(index);
	}
	
	/**
	 * Get a particular element based on its SVG ID. When editing SVG by hand,
	 * this is the id="" tag on any SVG element. When editing from Illustrator,
	 * these IDs can be edited by expanding the layers palette. The names used
	 * in the layers palette, both for the layers or the shapes and groups
	 * beneath them can be used here.
	 * 
	 * <pre>
	 * // This code grabs &quot;Layer 3&quot; and the shapes beneath it.
	 * CCSVGElement layer3 = svg.child(&quot;Layer 3&quot;);
	 * </pre>
	 * @param theTarget
	 *            the name of the shape to get
	 */
	public CCSVGElement child(String theTarget) {
		if (_myName != null && _myName.equals(theTarget)) {
			return this;
		}
		CCSVGElement myFoundElement = null;
		if (_myNameTable != null) {
			myFoundElement = _myNameTable.get(theTarget);
			if (myFoundElement != null){
				return myFoundElement;
			}
				
		}
		
		for (CCSVGElement child:_myChildren) {
			if (child._myName != null && child._myName.equals(theTarget)) {
				return child;
			}
			if(child instanceof CCSVGGroup){
				myFoundElement = ((CCSVGGroup)child).child(theTarget);
				if (myFoundElement != null){
					return myFoundElement;
				}
			}
		}
		return myFoundElement;
	}
	
	@Override
	public List<CCLinearSpline> contours() {
		List<CCLinearSpline> myResult = new ArrayList<>();
		for(CCSVGElement myChild:_myChildren){
			List<CCLinearSpline> myChildContours = myChild.contours();
			if(myChildContours == null)continue;
			myResult.addAll(myChildContours);
		}
		return myResult;
	}
	
	@Override
	public void drawImplementation(CCGraphics g, boolean theFill) {
		for(CCSVGElement myChild:_myChildren){
			CCLog.info(myChild.name());
			myChild.draw(g);
		}
	}
	
	@Override
	public void read(CCDataElement theSVG) {
		if(_myKind == CCShapeKind.GROUP)super.read(theSVG);
		
		_myChildren = new ArrayList<>();

		for (CCDataElement mySVG : theSVG) {
			String myName = mySVG.name();
			if(myName == null)continue;
			switch(myName){
			case "g":
				CCSVGGroup myGroup = new CCSVGGroup(this, CCShapeKind.GROUP);
				myGroup.read(mySVG);
				addChild(myGroup);
				break;
			case "defs":
				// generally this will contain gradient info, so may
				// as well just throw it into a group element for parsing
				// return new BaseObject(this, elem);
				CCSVGGroup myDefs = new CCSVGGroup(this, CCShapeKind.DEF);
				myDefs.read(mySVG);
				addChild(myDefs);
				break;
			case "line":
				CCSVGLine myLine = new CCSVGLine(this);
				myLine.read(mySVG);
				addChild(myLine);
				break;
			case "circle":
				CCSVGEllipse myCircle = new CCSVGEllipse(this, true);
				myCircle.read(mySVG);
				addChild(myCircle);
				break;
			case "ellipse":
				CCSVGEllipse myEllipse = new CCSVGEllipse(this, false);
				myEllipse.read(mySVG);
				addChild(myEllipse);
				break;
			case "rect":
				CCSVGRectangle myRect = new CCSVGRectangle(this);
				myRect.read(mySVG);
				addChild(myRect);
				break;
			case "polygon":
				CCSVGPoly myPoly = new CCSVGPoly(this, true);
				myPoly.read(mySVG);
				addChild(myPoly);
				break;
			case "polyline":
				CCSVGPoly myPolyLine = new CCSVGPoly(this, false);
				myPolyLine.read(mySVG);
				addChild(myPolyLine);
				break;
			case "path":
				CCSVGPath myPath = new CCSVGPath(this);
				myPath.read(mySVG);
				addChild(myPath);
					// return new BaseObject(this, elem, PATH);
//					shape = new PShapeSVG(this, mySVG, true);
//					shape.parsePath();
				break;

			case "radialGradient":
				CCSVGRadialGradient myRadialGradient = new CCSVGRadialGradient(this);
				myRadialGradient.read(mySVG);
				addChild(myRadialGradient);
				break;
			case "linearGradient":
				CCSVGLinearGradient myLinearGradient = new CCSVGLinearGradient(this);
				myLinearGradient.read(mySVG);
				addChild(myLinearGradient);
				break;
			case "font":
//					return new Font(this, mySVG);

					// } else if (myName.equals("font-face")) {
					// return new FontFace(this, elem);

					// } else if (myName.equals("glyph") || myName.equals("missing-glyph"))
					// {
					// return new FontGlyph(this, elem);

					break;
			case "metadata":
				break;
			case "text": // || myName.equals("font")) {
				CCLog.warn("Text and fonts in SVG files "
						+ "are not currently supported, "
						+ "convert text to outlines instead.");
				break;
			case "filter":
				CCLog.warn("Filters are not supported.");
				break;
			case "mask":
				CCLog.warn("Masks are not supported.");
				break;
			case "pattern":
				CCLog.warn("Patterns are not supported.");
				break;
			case "stop":
				// stop tag is handled by gradient parser, so don't warn about it
				break;
			case "sodipodi:namedview":
				// these are always in Inkscape files, the warnings get tedious
				break;
			default:
				if (!myName.startsWith("#")) {
					CCLog.warn("Ignoring <" + myName + "> tag.");
				}
				break;
			}
		}
	}
	
	@Override
	public String svgTag() {
		if(_myKind == CCShapeKind.GROUP)return "g";
		return "defs";
	}
}

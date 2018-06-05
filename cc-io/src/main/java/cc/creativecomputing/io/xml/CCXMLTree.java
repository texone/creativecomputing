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
package cc.creativecomputing.io.xml;

import java.util.*;

import cc.creativecomputing.core.logging.CCLog;


/**
 * Object for parsing a valid XML Document to convert its String 
 * into a HtmlTree representing its structure. After successful 
 * initialization of the tree you can use different methods and 
 * fields to get information of the page. <br>
 * The Example prints the tree structure of the processing reference 
 * page and the values of the fields of the initialized HtmlTree object. 
 * @example HtmlTree
 * @related pageTree
 */

public class CCXMLTree extends CCAbstractXMLTool{

    /**
     * PageTree is a field containing the tree structure of the document. 
     * It contains every element, excluding comments and scripts. 
     * PageTree is a HtmlElement so see its methods for further information.
     * @shortdesc This field holds the tree structure of the parsed document.
     * @example HtmlTree_pagetree
     * @related HtmlTree
     */
    private CCDataElement _myRootElement;

    /**
     * field to keep the parent element to put the children elements in
     */
    protected CCDataElement _myActualElement;
    
    private boolean _myIsfirstTag = false;
    private boolean _myIsRootNode = false;


    /**
     * Initializes a new HtmlTree
     */

    public CCXMLTree() {
    }


    /**
     * This makes sure that the given kindOfElement is to UpperCase
     */
    
    public void initBeforeParsing() {
    	_myIsfirstTag = true;
   	 	_myIsRootNode = true;
    }
    
    private void setAsRoot(final String theTagName, final Map<String, String> theAttributes){
    	_myIsRootNode = false;
		_myRootElement = new CCDataElement(theTagName, theAttributes);
		_myActualElement = _myRootElement;
		_myActualElement.line(_myParser.line);
    }
    
    /**
     * Implements the handleStartTag method of HtmlCollection. Here all
     * Elements are placed in the tree structure, according to its position in
     * the page.
     * 
     * @param theTagName
     * @param theAttributes
     */
    public void handleStartTag(final String theTagName, final Map<String, String> theAttributes, final boolean theIsStandAlone){

		if (_myIsfirstTag){
			_myIsfirstTag = false;
			if (!(theTagName.equals("doctype") || theTagName.equals("?xml"))){
				setAsRoot(theTagName, theAttributes);
			}
			return;
		}
		
		if (_myIsRootNode && !_myIsfirstTag){
			setAsRoot(theTagName, theAttributes);
			return;
		}
		
		CCDataElement keep = new CCDataElement(theTagName, theAttributes);
		keep.line(_myParser.line);
		_myActualElement.addChild(keep);
		if (!theIsStandAlone)
			_myActualElement = keep;
    }
    
    public void handleCDATASection(final String theCDATASection){
		final CCDataElement myCDATASection = new CCDataElement(theCDATASection);
		myCDATASection.cdata = true;
		myCDATASection._myIsText = true;
		_myActualElement.addChild(myCDATASection);
	}




    /**
     * In this Implementation all colorAttributes during parsing are stored
     * @param theAttributeName String
	  * @param theAttributeValue String
	  */
    public void handleAttribute(final String theAttributeName, final String theAttributeValue){}

    /**
     * After parsing an end tag. The acualElement is its parent Element
     */
    public void doAfterEndTag(final String theTag) {
		if (!_myActualElement.equals(_myRootElement))
			_myActualElement = _myActualElement.parent();
    }

    /**
		 * Places the text elements in the tree structure
		 * 
		 * @param toHandle
		 *           TextElement
		 */

    public void handleText(final String theText) {
		_myActualElement.addChild(new CCDataElement(theText, true));
    }

/**
 * Returns the root of the XML tree
 * @return the root element
 */
	public CCDataElement rootElement(){
		return _myRootElement;
	}

}

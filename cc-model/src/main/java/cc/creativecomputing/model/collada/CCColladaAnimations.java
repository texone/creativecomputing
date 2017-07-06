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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.xml.CCDataElement;

/*
 * 
 */
public class CCColladaAnimations extends CCColladaLibrary<CCColladaAnimation>{
	
	private List<CCColladaAnimation> _myAnimations = new ArrayList<CCColladaAnimation>();

	CCColladaAnimations(List<CCDataElement> theAnimationsXML) {
		for (CCDataElement myAnimationXML : theAnimationsXML) {
			CCColladaAnimation myAnimation = new CCColladaAnimation(myAnimationXML);
			_myElementMap.put(myAnimation.id(), myAnimation);
			_myElementList.add(myAnimation);
		}
		
		addAnimations(_myElementList);
	}
	
	private void addAnimations(List<CCColladaAnimation> theAnimations) {
		for(CCColladaAnimation myAnimation:theAnimations) {
			if(myAnimation.isAnimationContainer()) {
				addAnimations(myAnimation.animations());
			}else {
				_myAnimations.add(myAnimation);
			}
		}
	}
	
	public List<CCColladaAnimation> animations(){
		return _myAnimations;
	}
}

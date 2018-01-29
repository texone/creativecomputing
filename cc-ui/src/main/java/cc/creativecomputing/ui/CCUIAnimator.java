/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.animation.CCAnimatedBlend;
import cc.creativecomputing.animation.CCAnimation;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCBlendable;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.xml.property.CCPropertyException;
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.io.xml.property.CCXMLPropertyUtil;

/**
 * @author christianriekoff
 *
 */
@CCXMLPropertyObject(name="animation")
public class CCUIAnimator {
	
	@CCXMLPropertyObject(name="animation_property")
	public static class CCPropertyTarget<Type extends CCBlendable<Type>>{
		@CCXMLProperty(name="target", node=false)
		private String _myTarget;
		@CCXMLProperty(name="value")
		private CCBlendable<Type> _myValue;
		
		private CCPropertyTarget() {
		}
	}
	
	@CCXMLProperty(name="properties")
	private List<CCPropertyTarget> _myProperties = new ArrayList<>();
	
	private CCAnimationManager _myManager = new CCAnimationManager();
	
	@CCXMLProperty(name = "duration", node = false, optional = true)
	private double _myDuration;
	
//	public CCUIAnimation(CCXMLElement theAnimationXML) {
//		_myDuration = theAnimationXML.doubleAttribute("duration",0);
//		
//		CCXMLElement myPropertiesXML = theAnimationXML.child("animation_properties");
//
//		_myProperties = new CCPropertyTarget[myPropertiesXML.countChildren()];
//		
//		int i = 0;
//		for(CCXMLElement myPropertyXML:myPropertiesXML) {
//			CCPropertyTarget myPropertyTarget = myPropertyXML.toObject(CCPropertyTarget.class);
//			_myProperties[i++] = myPropertyTarget;
//		}
//	}
	
	public CCUIAnimator() {
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void animate(Object theObject) {
		CCAnimation myAnimation = new CCAnimation(_myDuration);
		for(CCPropertyTarget myPropertyTarget:_myProperties) {
			try {
				CCBlendable<?> myProperty = CCXMLPropertyUtil.property(myPropertyTarget._myTarget, theObject);
				if(myProperty == null) {
					throw new CCPropertyException("The given property:" + myPropertyTarget._myTarget + " is not accessable.");
				}
				myAnimation.events().add(new CCAnimatedBlend(myProperty, myPropertyTarget._myValue, myPropertyTarget._myValue));
			}catch(CCPropertyException e) {
				CCLog.info(e.getMessage());
			}
		}
		_myManager.play(myAnimation);
	}
	
	public void update(final double theDeltaTime) {
		_myManager.update(theDeltaTime);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer myBuffer = new StringBuffer("CCUIAnimation\n");
		myBuffer.append("duration:" + _myDuration+"\n");
		
		for(CCPropertyTarget myProperty:_myProperties) {
			myBuffer.append("Target  :" + myProperty._myTarget+"\n");
			myBuffer.append("value   :" + myProperty._myValue+"\n");
		}
		
		return myBuffer.toString();
	}
}

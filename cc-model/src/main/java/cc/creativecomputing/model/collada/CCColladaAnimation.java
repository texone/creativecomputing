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
import cc.creativecomputing.math.CCMatrix4x4;

/*
 * 
 */
public class CCColladaAnimation extends CCColladaElement{
	
	public class CCColladaAnimationKeyFrame{
		float _myTime;
		CCMatrix4x4 _myTransform;
		
		public CCColladaAnimationKeyFrame(float theTime, CCMatrix4x4 theTransform) {
			_myTime = theTime;
			_myTransform = theTransform;
		}
		
		public float time() {
			return _myTime;
		}
		
		public CCMatrix4x4 matrix() {
			return _myTransform;
		}
	}
	
	private List<CCColladaAnimation> _myAnimations = new ArrayList<CCColladaAnimation>();
	private List<CCColladaAnimationKeyFrame> _myKeyFrames = new ArrayList<CCColladaAnimation.CCColladaAnimationKeyFrame>();
	
	private String _myTarget;

	CCColladaAnimation(CCDataElement theAnimationXML){
		super(theAnimationXML);
		
		for(CCDataElement myAnimChild:theAnimationXML) {
			if(myAnimChild.name().equals("animation")) {
				_myAnimations.add(new CCColladaAnimation(myAnimChild));
				continue;
			}
		}
		
		CCDataElement mySamplerXML = theAnimationXML.child("sampler");
		if(mySamplerXML == null)return;
		
		CCColladaSource myTimesSource = source(mySamplerXML, "INPUT");
		CCColladaSource myTransformSource = source(mySamplerXML, "OUTPUT");
		
		float[][] myTimes = myTimesSource.pointMatrix();
		float[][] myTransforms = myTransformSource.pointMatrix();
		for(int i = 0; i < myTimes.length;i++) {
			double[] myArray = new double[16];
			for(int j = 0; j < myArray.length;j++){
				myArray[j] = myTransforms[i][j];
			}
			CCColladaAnimationKeyFrame myKeyFrame = new CCColladaAnimationKeyFrame(
				myTimes[i][0], 
				new CCMatrix4x4().fromArray(myArray, true)
			);
			_myKeyFrames.add(myKeyFrame);
		}
		
		CCDataElement myChannelXML = theAnimationXML.child("channel");
		_myTarget = myChannelXML.attribute("target");
	}
	
	/**
	 * Returns true if this animation serves as container for other animations
	 * @return true if this animation serves as container for other animations
	 */
	public boolean isAnimationContainer() {
		return _myAnimations.size() > 0;
	}
	
	/**
	 * Returns the child animations in case this is a container animation
	 * @return
	 */
	public List<CCColladaAnimation> animations(){
		return _myAnimations;
	}
	
	public String target() {
		return _myTarget;
	}
	
	public List<CCColladaAnimationKeyFrame> keyFrames(){
		return _myKeyFrames;
	}
}

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
package cc.creativecomputing.simulation.particles;

import java.nio.FloatBuffer;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.util.logging.CCLog;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 * 
 */
public class CCGPUParticleSort {

	private static boolean DEBUG = false;

	private CCShaderBuffer _myBuffer;
	private CCShaderBuffer _myDestinationBuffer;

	private int _myCurrentPass;
	private int _myBeginPass;
	private int _myEndPass;
	private int _mySortPassesPerFrame;
	private int _myMaxSortPasses;

	private CCCGShader _mySortRecursionShader;
	private CCCGShader _mySortEndShader;
	private CGparameter _mySortRecursionShaderSizeParameter;
	private CGparameter _mySortRecursionShaderSortStepParameter;
	private CGparameter _mySortRecursionShaderSortCountParameter;
	private CGparameter _mySortEndShaderSizeParameter;
	private CGparameter _mySortEndShaderSortStepParameter;

	private CCCGShader _myDistanceSortInitShader;
	private CCCGShader _myComputeDistanceShader;
	private CCCGShader _myLookupPositionPositionShader;

	private CCParticles _myParticles;
	private CCGraphics _myGraphics; 

	public CCGPUParticleSort(CCGraphics theGraphics, CCParticles theParticles) {
		_myGraphics = theGraphics;
		_myParticles = theParticles;

		_myBuffer = new CCShaderBuffer(32, 3, _myParticles.width(),_myParticles.height());
		_myDestinationBuffer = new CCShaderBuffer(32, 3, _myParticles.width(),_myParticles.height());

		_mySortRecursionShader = new CCCGShader(null, CCIOUtil.classPath(this,"shader/sort/mergeSortRecursion.fp"));
		_mySortRecursionShaderSizeParameter = _mySortRecursionShader.fragmentParameter("size");
		_mySortRecursionShaderSortStepParameter = _mySortRecursionShader.fragmentParameter("sortStep");
		_mySortRecursionShaderSortCountParameter = _mySortRecursionShader.fragmentParameter("sortCount");
		_mySortRecursionShader.load();

		_mySortEndShader = new CCCGShader(null, CCIOUtil.classPath(this, "shader/sort/mergeSortEnd.fp"));
		_mySortEndShaderSizeParameter = _mySortEndShader.fragmentParameter("size");
		_mySortEndShaderSortStepParameter = _mySortEndShader.fragmentParameter("sortStep");
		_mySortEndShader.load();

		_myDistanceSortInitShader = new CCCGShader(null, CCIOUtil.classPath(this, "shader/sort/initSortIndex.fp"));
		_myDistanceSortInitShader.load();

		_myComputeDistanceShader = new CCCGShader(CCIOUtil.classPath(this, "shader/sort/vertex.vp"), CCIOUtil.classPath(this, "shader/sort/computeDistance.fp"));
		_myComputeDistanceShader.load();
		
		_myLookupPositionPositionShader = new CCCGShader(null, CCIOUtil.classPath(this, "shader/sort/lookupPosition.fp"));
		_myLookupPositionPositionShader.load();
		
		_mySortPassesPerFrame = 5;
		
		reset();
	}

	public void reset() {
		_myGraphics.noBlend();
		_myBuffer.beginDraw();
		_myGraphics.clear();
		// DrawImage();
		_myBuffer.endDraw();

		_myCurrentPass = 0;
		_myMaxSortPasses = 100000;

		_myDistanceSortInitShader.start();
		_myBuffer.draw();
		_myDistanceSortInitShader.end();
		
		if(DEBUG){
			CCLog.info("RESET");
			FloatBuffer myData = _myBuffer.getData();
			while(myData.hasRemaining()){
				CCLog.info(myData.get()+":"+myData.get()+":"+myData.get());
			}
		}
	}
	
	public CCShaderBuffer indices(){
		return _myBuffer;
	}

	public void update(final float theDeltaTime) {
		_myGraphics.noBlend();
		// Update distances in sort texture.
		_myComputeDistanceShader.start();
		_myGraphics.texture(0, _myBuffer.attachment(0));
		_myGraphics.texture(1, _myParticles.dataBuffer().attachment(0));
		_myDestinationBuffer.draw();
		_myGraphics.noTexture();
		_myComputeDistanceShader.end();

		CCShaderBuffer myTmp = _myDestinationBuffer;
		_myDestinationBuffer = _myBuffer;
		_myBuffer = myTmp;
		
		
				
		mergeSort();
	}

	private void mergeSort() {
		_myCurrentPass = 0;

		int logdSize = (int) CCMath.log2(_myParticles.size());
		_myMaxSortPasses = (logdSize + 1) * logdSize / 2;

		_myBeginPass = _myEndPass;
		_myEndPass = (_myBeginPass + _mySortPassesPerFrame) % _myMaxSortPasses;

		_mySortRecursionShader.parameter(_mySortRecursionShaderSizeParameter, _myParticles.width(), _myParticles.height());
		_mySortEndShader.parameter(_mySortEndShaderSizeParameter, _myParticles.width(), _myParticles.height());

		doMergeSortPass(_myParticles.size());
	}

	private void doMergeSortPass(int theCount) {
		if (DEBUG)
			CCLog.info("mergeSort: count=" + theCount);

		if (theCount > 1) {
			doMergeSortPass(theCount / 2);
			doMergePass(theCount, 1);
		}

		if (DEBUG)
			CCLog.info("mergeSort: end");
	}
	
	private boolean doNextPass(){
		
		if (_myBeginPass < _myEndPass) {
			if (_myCurrentPass < _myBeginPass || _myCurrentPass >= _myEndPass){
				return false;
			}
		} else {
			if (_myCurrentPass < _myBeginPass && _myCurrentPass >= _myEndPass){
				return false;
			}
		}
		return true;
	}
	

	private void doMergePass(int theCount, int theStep) {
		if (theCount > 2) {
			doMergePass(theCount / 2, theStep * 2);

			_myCurrentPass++;

			if(!doNextPass())return;

			if (DEBUG)
				CCLog.info(_myCurrentPass + ": mergeRec: count=" + theCount + ", step=" + theStep);

			_mySortRecursionShader.start();
			_mySortRecursionShader.parameter(_mySortRecursionShaderSortStepParameter, (float) theStep);
			_mySortRecursionShader.parameter(_mySortRecursionShaderSortCountParameter, (float) theCount);

			_myGraphics.texture(_myBuffer.attachment(0));
			_myDestinationBuffer.draw();
			_myGraphics.noTexture();
			_mySortRecursionShader.end();

			CCShaderBuffer temp = _myBuffer;
			_myBuffer = _myDestinationBuffer;
			_myDestinationBuffer = temp;
			
			
		} else {
			_myCurrentPass++;

			if(!doNextPass())return;
			

			if (DEBUG)
				CCLog.info(_myCurrentPass + ": mergeEnd: count="+theCount+", step="+theStep);

			_mySortEndShader.start();
			_mySortEndShader.parameter(_mySortEndShaderSortStepParameter, (float) theStep);

			_myGraphics.texture(_myBuffer.attachment(0));
			_myDestinationBuffer.draw();
			_myGraphics.noTexture();

			_mySortEndShader.end();

			CCShaderBuffer temp = _myBuffer;
			_myBuffer = _myDestinationBuffer;
			_myDestinationBuffer = temp;
			
			
		}
		if(DEBUG){
//			FloatBuffer myData = _myBuffer.getData();
//			while(myData.hasRemaining()){
//				System.out.println(myData.get()+":"+myData.get()+":"+myData.get());
//			}
		}
	}
}

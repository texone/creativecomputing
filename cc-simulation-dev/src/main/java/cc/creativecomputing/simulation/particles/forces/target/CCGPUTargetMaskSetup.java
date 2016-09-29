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
package cc.creativecomputing.simulation.particles.forces.target;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;

/**
 * Form that sets the particle targets to create a texture
 * @author info
 *
 */
public class CCGPUTargetMaskSetup implements CCGPUTargetSetup{
	
	public static class CCGPUTargetMaskSetupArea{
		private int _myX;
		private int _myY;
		private int _myWidth;
		private int _myHeight;
		
		public CCGPUTargetMaskSetupArea(final int theX, final int theY, final int theWidth, final int theHeight) {
			_myX = theX;
			_myY = theY;
			_myWidth = theWidth;
			_myHeight = theHeight;
		}
		
		private int xStart() {
			return _myX;
		}
		
		private int xEnd() {
			return _myX + _myWidth;
		}
		
		private int yStart() {
			return _myY;
		}
		
		private int yEnd() {
			return _myY + _myHeight;
		}
	}
	
	public static enum CCGPUTargetMaskSetupPlane{
		XY, XZ;
	}
	
	private static class CCGPUPixelInfo{
		private float x;
		private float y;
		private float gray;
		
		private List<Integer> _myIndices = new ArrayList<Integer>();
		
		private CCGPUPixelInfo(final float theX, final float theY, final float theGray) {
			x = theX;
			y = theY;
			gray = theGray;
		}
		
		private void addIndex(final int theIndex) {
			_myIndices.add(theIndex);
		}
	}
	
	private List<CCGPUPixelInfo> _myPixels = new ArrayList<CCGPUPixelInfo>();
	private CCGPUPixelInfo[][] _myPixelGrid;
	
	private float _myScale;
	
	private boolean _myKeepTargets;
	
	private float[] _myTargets;
	
	private CCGPUTargetMaskSetupPlane _myPlane;
	
	
	public CCGPUTargetMaskSetup(final CCTextureData theTextureData, final float theScale, final CCGPUTargetMaskSetupPlane thePlane){
		_myScale = theScale;
		_myPlane = thePlane;
		
		_myPixelGrid = new CCGPUPixelInfo[theTextureData.width()][theTextureData.height()];
		
		for(int x = 0; x < theTextureData.width();x++) {
			for(int y = 0; y < theTextureData.height();y++) {
				float myGray = theTextureData.getPixel(x, y).red();
				if(myGray > 0) {
					CCGPUPixelInfo myPixelInfo = new CCGPUPixelInfo(x - theTextureData.width()/2,y - theTextureData.height()/2,myGray);
					_myPixels.add(myPixelInfo);
					_myPixelGrid[x][y] = myPixelInfo;
				}
			}
		}
	}
	
	public CCGPUTargetMaskSetup(final CCTextureData theTextureData, final float theScale) {
		this(theTextureData, theScale, CCGPUTargetMaskSetupPlane.XY);
	}
	
	public CCGPUTargetMaskSetup(final CCTextureData theTextureData, List<CCGPUTargetMaskSetupArea> theAreas, final float theScale, final CCGPUTargetMaskSetupPlane thePlane){
		_myScale = theScale;
		_myPlane = thePlane;
		
		_myPixelGrid = new CCGPUPixelInfo[theTextureData.width()][theTextureData.height()];
		
		for(CCGPUTargetMaskSetupArea myArea:theAreas) {
			for(int x = myArea.xStart(); x < myArea.xEnd();x++) {
				for(int y = myArea.yStart(); y < myArea.yEnd();y++) {
					float myGray = theTextureData.getPixel(x, y).red();
					if(myGray > 0) {
						CCGPUPixelInfo myPixelInfo = new CCGPUPixelInfo(x - theTextureData.width()/2,y - theTextureData.height()/2,myGray);
						_myPixels.add(myPixelInfo);
						_myPixelGrid[x][y] = myPixelInfo;
					}
				}
			}
		}
	}
	
	public CCGPUTargetMaskSetup(final CCTextureData theTexture, List<CCGPUTargetMaskSetupArea> theAreas, final float theScale) {
		this(theTexture, theAreas, theScale, CCGPUTargetMaskSetupPlane.XY);
	}
	
	public CCGPUTargetMaskSetup keepTargets(final boolean theIsKeepingTargets){
		_myKeepTargets = theIsKeepingTargets;
		return this;
	}
	
	public CCVector3f target(final int theParticleID) {
		return new CCVector3f(
			_myTargets[theParticleID * 3],
			_myTargets[theParticleID * 3 + 1],
			_myTargets[theParticleID * 3 + 2]
		);
	}
	
	private void setTarget(CCGraphics g, int theID, int theX, int theY) {
		float xPos, yPos, zPos;
		
		CCGPUPixelInfo myPixel = null;
		do{
			myPixel = _myPixels.get((int)CCMath.random(_myPixels.size()));
		}while(myPixel.gray < CCMath.random(1));
		
		CCVector2f myRandom2f = CCVecMath.random2f(CCMath.random(_myScale));
		
		if(_myPlane == CCGPUTargetMaskSetupPlane.XY){
			xPos = myPixel.x * _myScale + myRandom2f.x;
			yPos = myPixel.y * _myScale + myRandom2f.y;
			zPos = 0;
		}else{
			xPos = myPixel.x * _myScale + myRandom2f.x; 
			yPos =  0;
			zPos = myPixel.y * _myScale + myRandom2f.y;
		}
		
		
		if(_myKeepTargets) {
			_myTargets[theID * 3] = xPos;
			_myTargets[theID * 3 + 1] = yPos;
			_myTargets[theID * 3 + 2] = zPos;
		}
		
		myPixel.addIndex(theID);
		
		g.textureCoords(0, xPos, yPos, zPos);
		g.vertex(theX, theY);
	}

	public void setParticleTargets(final CCGraphics g, int theX, int theY, final int theWidth, final int theHeight) {
		if(_myKeepTargets){
			_myTargets = new float[theWidth * theHeight * 3];
		}
		
		if(_myPixels.size() == 0)return;
		for(CCGPUPixelInfo myPixel:_myPixels){
			myPixel._myIndices.clear();
		}
		
		for(int x = theX; x < theX + theWidth;x++) {
			for(int y = theY; y < theY + theHeight;y++) {
				int id = y * theWidth + x;
				setTarget(g,id, x,y);
			}
		}
	}
	
	public void setParticleTargets(final CCGraphics g, CCGPUIndexParticleEmitter theGroup) {
		if(_myKeepTargets){
			_myTargets = new float[theGroup.numberOfParticles() * 3];
		}
		
		if(_myPixels.size() == 0)return;
		for(CCGPUPixelInfo myPixel:_myPixels){
			myPixel._myIndices.clear();
		}
		
		for (int i = 0; i < theGroup.numberOfParticles(); i++) {
			setTarget(g,i, theGroup.xforIndex(i), theGroup.yforIndex(i));
		}
	}
	
	public List<Integer> indicesForArea(final int theXStart, final int theYStart, final int theXend, final int theYend){
		List<Integer> myResultList = new ArrayList<Integer>();
		
		for(int x = theXStart; x < theXend;x++) {
			for(int y = theYStart; y < theYend; y++) {
				if(_myPixelGrid[x][y] != null) {
					for(int value:_myPixelGrid[x][y]._myIndices ) {
						myResultList.add(value);
					}
				}
			}
		}
		
		return myResultList;
	}
}


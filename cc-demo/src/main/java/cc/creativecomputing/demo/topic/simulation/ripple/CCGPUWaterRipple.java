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
package cc.creativecomputing.demo.topic.simulation.ripple;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;


/**
 * @author info
 *
 */
public class CCGPUWaterRipple{
	
	private static class CCGPUWaterSplash{
		
		private double _myX;
		private double _myY;
		
		private double _myRadius;
		
		private double _myAmplitude;
		
		public CCGPUWaterSplash(double theX, double theY, double theRadius, double theAmplitude){
			_myX = theX;
			_myY = theY;
			_myRadius = theRadius;
			_myAmplitude = theAmplitude;
		}
	}
	
	private int _myWidth;
	private int _myHeight;
	
	@CCProperty(name = "splash shader")
	private CCGLProgram _mySineShader;
	@CCProperty(name = "sim shader")
	private CCGLProgram _mySimShader;
	@CCProperty(name = "draw shader")
	private CCGLProgram _myDrawShader;
	
	private List<CCGPUWaterSplash> _mySplashes;
	
	private CCShaderBuffer _myCurrentCellLocationsTexture;
	private CCShaderBuffer _myPreviousCellLocationsTexture;
	private CCShaderBuffer _myTargetCellLocationsTexture;
	
	// input textures
	private CCShaderBuffer _myWaveInnerEdgeTexture;
	private CCTexture2D _myBackgroundTexture;
	
	@CCProperty(name = "damping", min = 0.9f, max = 0.998f)
	public double _myDamping = 0.99;

	@CCProperty(name = "inner edges strength", min = 0, max = 1f)
	public double _myWaveInnerEdgesStrength = 1;

	@CCProperty(name = "normal height scale", min = 0, max = 500f)
	public double _myNormalHeightScale = 200;

	@CCProperty(name = "refraction", min = 0, max = 500f)
	public double _myRefraction = 200;
	
	private CCGraphics _myGraphics;
	
	public CCGPUWaterRipple(final CCGraphics g, final CCTexture2D theBackgroundTexture, final int theWidth, final int theHeight) {
		_myGraphics = g;
		_myWidth = theWidth;
		_myHeight = theHeight;

		_myBackgroundTexture = theBackgroundTexture;
		_myWaveInnerEdgeTexture = new CCShaderBuffer(theWidth, theHeight);
		_myWaveInnerEdgeTexture.clear();
		
		_mySineShader = new CCGLProgram(null, CCNIOUtil.classPath(CCGPUWaterRipple.class, "sine.glsl"));
		_mySimShader = new CCGLProgram(null, CCNIOUtil.classPath(CCGPUWaterRipple.class, "water_simulation.glsl"));
		_myDrawShader = new CCGLProgram(null,  CCNIOUtil.classPath(CCGPUWaterRipple.class, "water_draw.glsl"));
	
		_myCurrentCellLocationsTexture = new CCShaderBuffer(32, 4, 2, _myWidth, _myHeight);
		_myCurrentCellLocationsTexture.clear();
		_myPreviousCellLocationsTexture = new CCShaderBuffer(32, 4, 2, _myWidth, _myHeight);
		_myPreviousCellLocationsTexture.clear();
		_myTargetCellLocationsTexture = new CCShaderBuffer(32, 4, 2, _myWidth, _myHeight);
		_myTargetCellLocationsTexture.clear();
		
		_mySplashes = new ArrayList<CCGPUWaterSplash>();
	}
	
	public CCGPUWaterRipple(final CCGraphics g, final int theWidth, final int theHeight) {
		this(g,null, theWidth, theHeight);
	}
	
	private void applySplashes(CCGraphics g) {
		_myCurrentCellLocationsTexture.beginDraw();
//		g.colorMask(true, false, false, false);
		g.clear();

		_mySineShader.start();
		for (int i = 0; i < _mySplashes.size(); i++) {
			CCGPUWaterSplash mySplash = _mySplashes.get(i);
			double x = mySplash._myX;
			double y = mySplash._myY;
			double r = mySplash._myRadius;

			CCLog.info(x + ":" + y + r);
			_mySineShader.uniform1f("amplitude", mySplash._myAmplitude);
			g.color(255);
			g.beginShape(CCDrawMode.QUADS);
			g.vertex(x - r, y + r,0, 1);
			g.vertex(x - r, y - r,0, 0);
			g.vertex(x + r, y - r,1, 0);
			g.vertex(x + r, y + r,1, 1);
			g.endShape();
		}
		_mySineShader.end();

//		g.noColorMask();
		_myCurrentCellLocationsTexture.endDraw();

		_mySplashes.clear();
	}
	
	public void beginDrawMask() {
		_myCurrentCellLocationsTexture.beginDraw();
	}
	
	public void endDrawMask() {
		_myCurrentCellLocationsTexture.endDraw();
	}
	
	public void beginDrawActiveArea() {
		_myCurrentCellLocationsTexture.beginDraw();
		_myGraphics.colorMask(true, false, false, false);
	}
	
	public void endDrawActiveArea() {
		_myGraphics.noColorMask();
		_myCurrentCellLocationsTexture.endDraw();
	}
	
	double _myAngle = 0;
	
	private void simulate(CCGraphics g){
	    
	    g.texture(0, _myPreviousCellLocationsTexture.attachment(0));
	    g.texture(1, _myCurrentCellLocationsTexture.attachment(0));
	    g.texture(2, _myWaveInnerEdgeTexture.attachment(0));
		
	    _mySimShader.start();
	    _mySimShader.uniform1i("previous_cells", 0);
	    _mySimShader.uniform1i("current_cells", 1);
	    _mySimShader.uniform1i("wave_break_inner_edges", 2);
	    _mySimShader.uniform1f("damping", _myDamping);
	    _mySimShader.uniform1f("normalHeightScale", _myNormalHeightScale);
	    _mySimShader.uniform1f("waveInnerEdgesStrength", _myWaveInnerEdgesStrength);
	    
	    _myTargetCellLocationsTexture.clear();
	    _myTargetCellLocationsTexture.draw();
        
	    _mySimShader.end();
	    
	    g.noTexture();
	    
	    // swap textures
	    CCShaderBuffer mySwap = _myPreviousCellLocationsTexture;
	    _myPreviousCellLocationsTexture = _myCurrentCellLocationsTexture;
	    _myCurrentCellLocationsTexture = _myTargetCellLocationsTexture;
	    _myTargetCellLocationsTexture = mySwap;
	}
	
	public void update(final double theDeltaTime) {
	}
	
	public void draw(CCGraphics g) {

		applySplashes(g);
		simulate(g);
		//pass water simulation texture
		
//		g.texture(0,_myCurrentCellLocationsTexture.attachment(0));
//		g.texture(1,_myCurrentCellLocationsTexture.attachment(1));
//		if(_myBackgroundTexture != null)g.texture(2,_myBackgroundTexture);
//		_myDrawShader.start();
//		_myDrawShader.uniform1f("refraction", _myRefraction);
//		_myDrawShader.uniform1i("heightMap", 0);
//		_myDrawShader.uniform1i("normalMap", 1);
//		_myDrawShader.uniform1i("backgroundTexture", 2);
//		
//		g.beginOrtho2D();
//		g.beginShape(CCDrawMode.QUADS);
//		g.vertex(0, 0, 0, _myHeight);
//		g.vertex(_myWidth, 0, _myWidth, _myHeight);
//		g.vertex(_myWidth, _myHeight, _myWidth, 0);
//		g.vertex(0, _myHeight, 0, 0);
//		g.endShape();
//		g.endOrtho2D();
//		
//        _myDrawShader.end();
//        
//        g.noTexture();
		
		g.image(_myCurrentCellLocationsTexture.attachment(0), 0,0);
	}

	public void addSplash(final double theX, final double theY, final double theRadius, final double theAmplitude) {
		_mySplashes.add(new CCGPUWaterSplash(theX, theY, theRadius, theAmplitude));
	}
	
	public void backgroundTexture(final CCTexture2D theBackgroundTexture) {
		_myBackgroundTexture = theBackgroundTexture;
	}

	/**
	 * @return the waveInnerEdgeTexture
	 */
	public double waveInnerEdgeStrength() {
		return _myWaveInnerEdgesStrength;
	}

	/**
	 * @param theWaveInnerEdgeTexture the waveInnerEdgeTexture to set
	 */
	public void waveInnerEdgeStrength(double theWaveInnerEdgeStrength) {
		_myWaveInnerEdgesStrength = theWaveInnerEdgeStrength;
	}

	/**
	 * @return the damping
	 */
	public double damping() {
		return _myDamping;
	}

	/**
	 * @param theDamping the damping to set
	 */
	public void damping(double theDamping) {
		_myDamping = theDamping;
	}

	/**
	 * @return the normalHeightScale
	 */
	public double normalHeightScale() {
		return _myNormalHeightScale;
	}

	/**
	 * @param theNormalHeightScale the normalHeightScale to set
	 */
	public void normalHeightScale(double theNormalHeightScale) {
		_myNormalHeightScale = theNormalHeightScale;
	}

	/**
	 * @return the refraction
	 */
	public double refraction() {
		return _myRefraction;
	}

	/**
	 * @param theRefraction the refraction to set
	 */
	public void refraction(double theRefraction) {
		_myRefraction = theRefraction;
	}
	
}

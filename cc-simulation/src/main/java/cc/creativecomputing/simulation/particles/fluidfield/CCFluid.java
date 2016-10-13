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
package cc.creativecomputing.simulation.particles.fluidfield;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;

/**
 * TODO check boundary and vorticity
 * @author christianriekoff
 *
 */
public class CCFluid {
	private CCFluidBoundaryShader _myBoundaryShader;
//	private CCShaderBuffer _myBoundaryTexture;
	
//	@CCProperty(name = "advect")
	private CCFluidAdvectShader _myAdvectShader;
	
//	@CCProperty(name = "add impulse")
	private CCFluidAddColorOrImpulseShader _myAddImpulseShader;
	
//	@CCProperty(name = "add color")
	private CCFluidAddColorOrImpulseShader _myAddColorShader;
	
	private CCFluidVorticityShader _myVorticityShader;
//	private CCShaderBuffer _myVorticityTexture;
	
	private CCFluidVorticityForceShader _myVorticityForceShader;
//	private CCShaderBuffer _myVorticityForceTexture;
	
	private CCFluidDiffusionShader _myDiffusionShader;
	private double _myViscousity = 0.5f;
	private double _myDiffusionSteps = 15;
	
	private double _myColorDarking = 0;
	private double _myAdvectSpeed = 1;
	
	private CCFluidDivergenceShader _myDivergenceShader;
	private CCShaderBuffer _myDivergencyTexture;
	
	private CCFluidSubtractGradientShader _mySubtractGradientShader;
	
	private CCAABoundingRectangle _myOffBoundaryRect;
	
	private CCShaderBuffer _myVelocityBuffer;
	private CCShaderBuffer _myTmpVelocityBuffer;
	
	private CCShaderBuffer _myColorBuffer;
	private CCShaderBuffer _myTmpColorTexture;
	
	private CCShaderBuffer _myPressureBuffer;
	private CCShaderBuffer _myTmpPressureBuffer;
	
	
	private double _myGridScale = 1;
	
	private int _myWidth;
	private int _myHeight;
	
	public CCFluid(final int theWidth, final int theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myOffBoundaryRect = new CCAABoundingRectangle(1, 1, _myWidth - 1, _myHeight - 1);
		
		_myVelocityBuffer = new CCShaderBuffer(_myWidth, _myHeight);
		_myVelocityBuffer.clear();
		_myTmpVelocityBuffer = new CCShaderBuffer(_myWidth, _myHeight);
		_myTmpVelocityBuffer.clear();
		
		_myColorBuffer = new CCShaderBuffer(_myWidth, _myHeight);
		_myColorBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		_myColorBuffer.clear();
		_myTmpColorTexture = new CCShaderBuffer(_myWidth, _myHeight);
		_myTmpColorTexture.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		_myTmpColorTexture.clear();
		
		_myBoundaryShader = new CCFluidBoundaryShader();
//		_myBoundaryTexture = new CCShaderBuffer(32, 3,_myWidth,_myHeight);
		
		_myAdvectShader = new CCFluidAdvectShader();
		_myAdvectShader.dissipation(1);
		_myAdvectShader.gridScale(1);
		
		_myAddImpulseShader = new CCFluidAddColorOrImpulseShader("addimpulse.glsl");
		_myAddImpulseShader.windowDimension(_myWidth, _myHeight);
		_myAddImpulseShader.radius(0.1f);
		
		_myAddColorShader = new CCFluidAddColorOrImpulseShader("addColor.glsl");
		_myAddColorShader.windowDimension(_myWidth, _myHeight);
		_myAddColorShader.radius(0.1f);
		
		_myVorticityShader = new CCFluidVorticityShader();
		_myVorticityShader.gridScale(1f);
//		_myVorticityTexture = new CCShaderBuffer(_myWidth, _myHeight);
		
		_myVorticityForceShader = new CCFluidVorticityForceShader();
		_myVorticityForceShader.gridScale(1);
//		_myVorticityForceTexture = new CCShaderBuffer(32, 3,_myWidth,_myHeight);
		
		_myDiffusionShader = new CCFluidDiffusionShader();
		
		_myDivergenceShader = new CCFluidDivergenceShader();
		_myDivergenceShader.halfRdx(0.5f);
		_myDivergencyTexture = new CCShaderBuffer(32, 3,_myWidth,_myHeight);
		
		_mySubtractGradientShader = new CCFluidSubtractGradientShader();
		_mySubtractGradientShader.halfRdx(0.5f);
		
		_myPressureBuffer = new CCShaderBuffer(32, 3,_myWidth,_myHeight);
		_myPressureBuffer.clear();
		_myTmpPressureBuffer = new CCShaderBuffer(32, 3,_myWidth,_myHeight);
		_myTmpPressureBuffer.clear();
	}
	
	public void colorRadius(final double theColorRadius) {
		_myAddColorShader.radius(theColorRadius);
	}
	
	public void impulseRadius(final double theImpulseRadius) {
		_myAddImpulseShader.radius(theImpulseRadius);
	}
	
	@CCProperty(name = "viscousity", min = 0, max = 1)
	public void viscousity(final double theViscousity) {
		_myViscousity = theViscousity;
	}
	
	private void swapVelocities() {
		CCShaderBuffer myTmpTexture = _myVelocityBuffer;
		_myVelocityBuffer = _myTmpVelocityBuffer;
		_myTmpVelocityBuffer = myTmpTexture;
	}
	
	private void swapColors() {
		CCShaderBuffer myTmpTexture = _myColorBuffer;
		_myColorBuffer = _myTmpColorTexture;
		_myTmpColorTexture = myTmpTexture;
	}
	
	private void swapPressure() {
		CCShaderBuffer myTmpTexture = _myPressureBuffer;
		_myPressureBuffer = _myTmpPressureBuffer;
		_myTmpPressureBuffer = myTmpTexture;
	}
	
	public void adImpulse(CCGraphics g, final CCVector2 thePosition, final CCVector2 theDirection) {
		g.texture(0, _myVelocityBuffer.attachment(0));
		_myAddImpulseShader.baseTexture(0);
		_myAddImpulseShader.start();
		_myAddImpulseShader.position(thePosition);
		_myAddImpulseShader.color(theDirection.x, theDirection.y,0.0);
		_myTmpVelocityBuffer.draw();
		_myAddImpulseShader.end();
		g.noTexture();
		swapVelocities();
	}
	
	public void addColor(CCGraphics g, final CCVector2 thePosition, final CCColor theColor) {
		g.texture(0, _myColorBuffer.attachment(0));
		_myAddColorShader.baseTexture(0);
		_myAddColorShader.start();
		_myAddColorShader.position(thePosition);
		_myAddColorShader.color(theColor);
		_myTmpColorTexture.draw();
		_myAddColorShader.end();
		g.noTexture();
			
		swapColors();
	}
	
	/**
	 * This renders the four 1-pixel wide boundaries of the slab.
	 * texcoord 0 is the base texture coordinate.
	 * texcoord 1 is the offset 
	 * (done here to avoid proliferation of fragment 
	 * programs, or of conditionals inside the fragment program.)
	 */
	private void drawBoundaries(CCGraphics g) {
		g.beginShape(CCDrawMode.LINES);  
		
		// left boundary
		g.textureCoords2D(1, 1, 0); // offset amount
		g.textureCoords2D(0, 0, 0);
		g.vertex(1, 0);
		g.textureCoords2D(0, 0, _myHeight);
		g.vertex(1, _myHeight);

		// right boundary
		g.textureCoords2D(1, -1, 0); // offset amount
		g.textureCoords2D(0, _myWidth - 1, 0);
		g.vertex(_myWidth, 0);
		g.textureCoords2D(0, _myWidth - 1, _myHeight);
		g.vertex(_myWidth, _myHeight);

		// top boundary
		g.textureCoords2D(1, 0, 1); // offset amount
		g.textureCoords2D(0, 0, 0);
		g.vertex(0, 1);
		g.textureCoords2D(0, _myWidth, 0);
		g.vertex(_myWidth, 1);

		// bottom boundary
		g.textureCoords2D(1, 0, -1); // offset amount
		g.textureCoords2D(0, 0, _myHeight - 1);
		g.vertex(0, _myHeight);
		g.textureCoords2D(0, _myWidth, _myHeight-1);
		g.vertex(_myWidth, _myHeight);
		g.endShape();

	}
	
	@CCProperty(name = "advect speed", min = 0, max = 100)
	public void advectSpeed(final double theAdvectSpeed) {
		_myAdvectSpeed = theAdvectSpeed;
	}
	
	/**
	 * pulls the velocity and Ink fields forward along the velocity field.
	 * This results in a divergent field, which must be corrected using diffusion.
	 * @param theDeltaTime
	 */
	private void advectVelocities(CCGraphics g, final double theDeltaTime) {
		g.texture(0,_myVelocityBuffer.attachment(0));
		_myBoundaryShader.texture(0);
		_myBoundaryShader.start();
		_myBoundaryShader.scale(-1);
		_myTmpVelocityBuffer.beginDraw();
		drawBoundaries(g);
		_myTmpVelocityBuffer.endDraw();
		_myBoundaryShader.end();
		g.noTexture();

		g.texture(0,_myVelocityBuffer.attachment(0));
		g.texture(1, _myVelocityBuffer.attachment(0));
		_myAdvectShader.targetTexture(0);
		_myAdvectShader.velocityTexture(1);
		_myAdvectShader.start();
		_myAdvectShader.dissipation(1);
		_myAdvectShader.timeStep(theDeltaTime * _myAdvectSpeed);
		_myTmpVelocityBuffer.drawQuad(_myOffBoundaryRect);
		_myAdvectShader.end();
		g.noTexture();
		
		swapVelocities();
	}
	
	@CCProperty(name = "darking", min = 0, max = 0.001f, digits = 4)
	public void colorDarking(final double theColorDarking) {
		_myColorDarking = theColorDarking;
	}
	
	private void advectColors(CCGraphics g, final double theDeltaTime) {
		g.texture(0, _myColorBuffer.attachment(0));
		_myBoundaryShader.texture(0);
		_myBoundaryShader.start();
		_myBoundaryShader.scale(0);
		_myTmpColorTexture.beginDraw();
		drawBoundaries(g);
		_myTmpColorTexture.endDraw();
		_myBoundaryShader.end();
		g.noTexture();

		g.texture(0, _myColorBuffer.attachment(0));
		g.texture(1, _myVelocityBuffer.attachment(0));
		_myAdvectShader.targetTexture(0);
		_myAdvectShader.velocityTexture(1);
		_myAdvectShader.start();
		_myAdvectShader.dissipation(1);
		_myAdvectShader.timeStep(theDeltaTime * _myAdvectSpeed);
		_myAdvectShader.darking(_myColorDarking);
		_myTmpColorTexture.drawQuad(_myOffBoundaryRect);
		_myAdvectShader.darking(0);
		_myAdvectShader.end();
		g.noTexture();
		
		swapColors();
	}
	
	/**
	 * Applies vorticity confinement
	 * @param theDeltaTime
	 */
//	private void vorticity(final double theDeltaTime) {
//		_myVorticityShader.velocityTexture(_myVelocityBuffer.attachment(0));
//		_myVorticityShader.start();
//		_myVorticityTexture.draw();
//		_myVorticityShader.end();
//
//		_myBoundaryShader.texture(_myVelocityBuffer.attachment(0));
//		_myBoundaryShader.start();
//		_myBoundaryShader.scale(-1);
//		_myTmpVelocityBuffer.beginDraw();
//		drawBoundaries();
//		_myTmpVelocityBuffer.endDraw();
//		_myBoundaryShader.end();
//
//		_myVorticityForceShader.velocityTexture(_myVelocityBuffer.attachment(0));
//		_myVorticityForceShader.vorticityTexture(_myVorticityTexture.attachment(0));
//		_myVorticityForceShader.start();
//		_myVorticityForceShader.deltaTime(theDeltaTime);
//		_myTmpVelocityBuffer.draw();
//		_myVorticityForceShader.end();
//		
//		swapVelocities();
//	}
	
	/**
	 * If this is a viscous fluid, solve the poisson problem for the viscous diffusion
	 * @param theDeltaTime
	 */
	private void diffuseVelocity(CCGraphics g, final double theDeltaTime) {
		if(_myViscousity <= 0)return;

		double myCenterFactor = _myGridScale * _myGridScale / (_myViscousity * theDeltaTime);
		double myStencilFactor = 1.0f / (4.0f + myCenterFactor);

		for (int i = 0; i < _myDiffusionSteps; i++) {
			g.texture(0, _myVelocityBuffer.attachment(0));
			g.texture(1, _myVelocityBuffer.attachment(0));
			_myDiffusionShader.textureX(0);
			_myDiffusionShader.textureB(1);
			_myDiffusionShader.start();
			_myDiffusionShader.alpha(myCenterFactor);
			_myDiffusionShader.rBeta(myStencilFactor);
			_myTmpVelocityBuffer.drawQuad(_myOffBoundaryRect);
			_myDiffusionShader.end();
			g.noTexture();
			swapVelocities();
		}
	}
	
	/**
	 * Compute the divergence of the velocity field
	 */
	private void divergence(CCGraphics g) {
		// ---------------
//		// 4a. compute divergence
//		// ---------------
//		// Compute the divergence of the velocity field
		g.texture(0, _myVelocityBuffer.attachment(0));
		_myDivergenceShader.velocityTexture(0);
		_myDivergenceShader.start();
		_myDivergencyTexture.drawQuad(_myOffBoundaryRect);
		_myDivergenceShader.end();
		g.noTexture();
	}
	
	/**
	 * Compute pressure disturbance
	 */
	public void pressure(CCGraphics g) {
		// Clear the pressure texture, to initialize the pressure disturbance to 
		// zero before iterating.  If this is disabled, the solution converges 
		// faster, but tends to have oscillations.
//		if (_bClearPressureEachStep)
//			_myPressureTexture.clear();

		for (int i = 0; i < _myDiffusionSteps; i++) {
			// Apply pure neumann boundary conditions
			g.texture(_myPressureBuffer.attachment(0));
			_myBoundaryShader.texture(0);
			_myBoundaryShader.start();
			_myBoundaryShader.scale(1);
			_myTmpPressureBuffer.beginDraw();
			drawBoundaries(g);
			_myTmpPressureBuffer.endDraw();
			_myBoundaryShader.end();
			g.noTexture();

			// Apply pure neumann boundary conditions to arbitrary
			// interior boundaries if they are enabled.
//			if (_bArbitraryBC) {
//				_myArbitaryPressureBoundaryShader.start();
//				_myArbitaryPressureBoundaryShader.pressureTexture(_myPressureTexture);
//				_myArbitaryPressureBoundaryShader.offsetTexture(_myPressureOffsetTexture);
//				_myTmpPressureTexture.draw(_myOffBoundaryRect);
//				_myArbitaryPressureBoundaryShader.end();
//
//				mySwapTexture = _myTmpPressureTexture;
//				_myTmpPressureTexture = _myPressureTexture;
//				_myPressureTexture = mySwapTexture;
//			}

			// Solve for the pressure disturbance caused by the divergence, by solving
			// the poisson problem Laplacian(p) = div(u)
			g.texture(0, _myPressureBuffer.attachment(0));
			g.texture(1, _myDivergencyTexture.attachment(0));
			_myDiffusionShader.textureX(0);
			_myDiffusionShader.textureB(1);
			_myDiffusionShader.start();
			_myDiffusionShader.alpha(-_myGridScale * _myGridScale);
			_myDiffusionShader.rBeta(0.25f);
			_myTmpPressureBuffer.drawQuad(_myOffBoundaryRect);
			_myDiffusionShader.end();
			g.noTexture();

			swapPressure();
		}
	}
	
	public void subtractGradient(CCGraphics g) {
		g.texture(0, _myPressureBuffer.attachment(0));
		g.texture(1, _myVelocityBuffer.attachment(0));
		_mySubtractGradientShader.pressureTexture(0);
		_mySubtractGradientShader.velocityTexture(1);
		_mySubtractGradientShader.start();
		_myTmpVelocityBuffer.drawQuad(_myOffBoundaryRect);
		_mySubtractGradientShader.end();
		g.noTexture();
		
		swapVelocities();
	}

	
	private double _myDeltaTime;
	
	public void update(CCAnimator theAnimator) {
		_myDeltaTime = theAnimator.deltaTime();

	}
	
	/**
	 * 
	 * Update solves the incompressible Navier-Stokes equations for a single 
	 * time step.  It consists of four main steps:
	 *
	 * 1. Add Impulse
	 * 2. Advect
	 * 3. Apply Vorticity Confinement
	 * 4. Diffuse (if viscosity > 0)
	 * 5. Project (computes divergence-free velocity from divergent field).
	 *
	 * 
	 * 2. Add Impulse: simply add an impulse to the velocity (and optionally,Ink)
	 *    field where the user has clicked and dragged the mouse.
	 * 3. Apply Vorticity Confinement: computes the amount of vorticity in the 
	 *            flow, and applies a small impulse to account for vorticity lost 
	 *            to numerical dissipation.
	 * 4. Diffuse: viscosity causes the diffusion of velocity.  This step solves
	 *             a poisson problem: (I - nu*dt*Laplacian)u' = u.
	 * 5. Project: In this step, we correct the divergence of the velocity field
	 *             as follows.
	 *        a.  Compute the divergence of the velocity field, div(u)
	 *        b.  Solve the Poisson equation, Laplacian(p) = div(u), for p using 
	 *            Jacobi iteration.
	 *        c.  Now that we have p, compute the divergence free velocity:
	 *            u = gradient(p)
	 */ 
	public void display(CCGraphics g){
		advectVelocities(g, _myDeltaTime);
		advectColors(g,_myDeltaTime);
//		vorticity(g,_myDeltaTime);
		diffuseVelocity(g, _myDeltaTime);
		divergence(g);
		pressure(g);
//		subtractGradient();
	}
	
	public CCShaderBuffer velocityBuffer() {
		return _myVelocityBuffer;
	}
	
	public CCTexture2D velocityTexture() {
		return _myVelocityBuffer.attachment(0);
	}
	
	public CCShaderBuffer colorBuffer() {
		return _myColorBuffer;
	}
	
	public CCTexture2D colorTexture() {
		return _myColorBuffer.attachment(0);
	}
}

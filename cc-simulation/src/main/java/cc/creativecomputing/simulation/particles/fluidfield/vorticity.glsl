#version 120 

//----------------------------------------------------------------------------
// Vorticity Confinement
//----------------------------------------------------------------------------
// The motion of smoke, air and other low-viscosity fluids typically contains 
// rotational flows at a variety of scales. This rotational flow is called 
// vorticity.  As Fedkiw et al. explained (2001), numerical dissipation caused 
// by simulation on a coarse grid damps out these interesting features. 
// Therefore, they used "vorticity confinement" to restore these fine-scale 
// motions. Vorticity confinement works by first computing the vorticity,
//                          vort = curl(u). 
// The program vorticity() does this computation. From the vorticity we 
// compute a normalized vorticity vector field, 
//                          F = normalize(eta),	
// where, eta = grad(|vort|). The vectors in F point from areas of lower 
// vorticity to areas of higher vorticity. From these vectors we compute a 
// force that can be used to restore an approximation of the dissipated 
// vorticity:
//                          vortForce = eps * cross(F, vort) * dx.	
// Here eps is a user-controlled scale parameter. 
// 
// The operations above require two passes in the simulator.  This is because
// the vorticity must be computed in one pass, because computing the vector 
// field F requires sampling multiple vorticity values for each vector.  
// Because a texture can't be written and then read in a single pass, this is
// inherently a two-pass algorithm.

//----------------------------------------------------------------------------
// Function     	: vorticity
// Description	    : 
//----------------------------------------------------------------------------
/**
    The first pass of vorticity confinement computes the (scalar) vorticity 
    field.  See the description above.  In Flo, if vorticity confinement is
    disabled, but the vorticity field is being displayed, only this first
    pass is executed.
 */

// 0.5 / gridscale
uniform float halfrdx; 

// velocity
uniform sampler2DRect velocityTexture;

void main(){
	vec4 velocityLeft		= texture2DRect(velocityTexture, gl_FragCoord - vec2(1,0));
	vec4 velocityRight		= texture2DRect(velocityTexture, gl_FragCoord + vec2(1,0));
	vec4 velocityBottom		= texture2DRect(velocityTexture, gl_FragCoord - vec2(0,1));
	vec4 velocityTop		= texture2DRect(velocityTexture, gl_FragCoord + vec2(0,1));
  
	float vorticity =  (velocityRight.y - velocityLeft.y) - (velocityTop.x - velocityBottom.x);
	gl_FragColor = halfrdx + vec4(vorticity,vorticity,vorticity,1);
} 

//----------------------------------------------------------------------------
// Function     	: vortForce
// Description	    : 
//----------------------------------------------------------------------------
/**
    The second pass of vorticity confinement computes a vorticity confinement
    force field and applies it to the velocity field to arrive at a new 
    velocity field.
 */ 

// 0.5 / gridscale
uniform half halfrdx;

// vorticity confinement scale
uniform half2 dxscale;

const half EPSILON = 2.4414e-4; // 2^-12

uniform half deltaTime;

uniform samplerRECT vorticityTexture;
uniform samplerRECT velocityTexture;

void main(
	in half2 iCoords : WPOS,
	out half4 uNew   : COLOR
){

	half vorticityLeft 		= h4texRECT(vorticityTexture, iCoords - half2(1,0)).x;
	half vorticityRight		= h4texRECT(vorticityTexture, iCoords + half2(1,0)).x;
	half vorticityBottom	= h4texRECT(vorticityTexture, iCoords - half2(0,1)).x;
	half vorticityTop		= h4texRECT(vorticityTexture, iCoords + half2(0,1)).x;
	half vorticityCenter	= h4texRECT(vorticityTexture, iCoords).x;
  
	half2 force = halfrdx * half2(abs(vorticityTop) - abs(vorticityBottom), abs(vorticityRight) - abs(vorticityLeft));
  
	// safe normalize
	
	//half magSqr = max(EPSILON, dot(force, force)); 
	//force = force * rsqrt(magSqr); 

	//force *= dxscale * vorticityCenter * half2(1, -1);
	  /*
	force += 1;
	force *= 0.5;*/

	uNew = h4texRECT(velocityTexture, iCoords);

	uNew += deltaTime * float4(force.x,force.y,0,1);
	uNew = deltaTime * 100 * float4(force.x,force.y,0,1);
	uNew = float4(vorticityCenter,vorticityCenter,vorticityCenter,1);
} 
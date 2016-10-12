
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
uniform float halfrdx;

// vorticity confinement scale
uniform vec2 dxscale;

const float EPSILON = 2.4414e-4; // 2^-12

uniform float deltaTime;

uniform sampler2DRect vorticityTexture;
uniform sampler2DRect velocityTexture;

void main(){

	float vorticityLeft 	= texture2DRect(vorticityTexture, gl_FragCoord.xy - vec2(1,0)).x;
	float vorticityRight	= texture2DRect(vorticityTexture, gl_FragCoord.xy + vec2(1,0)).x;
	float vorticityBottom	= texture2DRect(vorticityTexture, gl_FragCoord.xy - vec2(0,1)).x;
	float vorticityTop		= texture2DRect(vorticityTexture, gl_FragCoord.xy + vec2(0,1)).x;
	float vorticityCenter	= texture2DRect(vorticityTexture, gl_FragCoord.xy).x;
  
	vec2 force = halfrdx * vec2(abs(vorticityTop) - abs(vorticityBottom), abs(vorticityRight) - abs(vorticityLeft));
  
	// safe normalize
	
	//float magSqr = max(EPSILON, dot(force, force)); 
	//force = force * rsqrt(magSqr); 

	//force *= dxscale * vorticityCenter * vec2(1, -1);
	  /*
	force += 1;
	force *= 0.5;*/

	vec4 uNew = texture2DRect(velocityTexture, gl_FragCoord.xy);

	uNew += deltaTime * vec4(force.x,force.y,0,1);
	uNew = deltaTime * 100.0 * vec4(force.x,force.y,0,1);
	uNew = vec4(vorticityCenter,vorticityCenter,vorticityCenter,1);
	
	gl_FragColor = uNew;
} 
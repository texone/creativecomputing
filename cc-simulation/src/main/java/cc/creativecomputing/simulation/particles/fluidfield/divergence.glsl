#version 120 

/**
 * This program computes the divergence of the specified vector field 
 * "velocity". The divergence is defined as 
 *
 *  "grad dot v" = partial(v.x)/partial(x) + partial(v.y)/partial(y),
 *
 * and it represents the quantity of "stuff" flowing in and out of a parcel of
 * fluid.  Incompressible fluids must be divergence-free.  In other words 
 * this quantity must be zero everywhere.  
 */ 

// 0.5 / gridscale
uniform float halfrdx;

// vector field       
uniform sampler2DRect w;             
        
void main(){
	vec4 vL = texture2DRect(w,gl_FragCoord.xy + vec2(-1,  0));
	vec4 vR = texture2DRect(w,gl_FragCoord.xy + vec2( 1,  0));
	vec4 vB = texture2DRect(w,gl_FragCoord.xy + vec2( 0, -1));
	vec4 vT = texture2DRect(w,gl_FragCoord.xy + vec2( 0,  1));
	float result = halfrdx * (vR.x - vL.x + vT.y - vB.y);
	gl_FragColor = vec4(result, result, result, 1.0);
} 
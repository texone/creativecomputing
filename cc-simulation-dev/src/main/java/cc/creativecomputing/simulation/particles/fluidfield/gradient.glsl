#version 120 

/**
 * This program implements the final step in the fluid simulation.  After 
 * the poisson solver has iterated to find the pressure disturbance caused by
 * the divergence of the velocity field, the gradient of that pressure needs
 * to be subtracted from this divergent velocity to get a divergence-free
 * velocity field:
 *
 * v-zero-divergence = v-divergent -  grad(p)
 *
 * The gradient(p) is defined: 
 *     grad(p) = (partial(p)/partial(x), partial(p)/partial(y))
 *
 * The discrete form of this is:
 *     grad(p) = ((p(i+1,j) - p(i-1,j)) / 2dx, (p(i,j+1)-p(i,j-1)) / 2dy)
 *
 * where dx and dy are the dimensions of a grid cell.
 *
 * This program computes the gradient of the pressure and subtracts it from
 * the velocity to get a divergence free velocity.
 */

// 0.5 / grid scale 
uniform float halfrdx;        
uniform sampler2DRect pressureTexture;
uniform sampler2DRect velocityTexture;
      
void main(){
	float pL = texture2DRect(pressureTexture, gl_FragCoord + vec2(-1, 0)).r;
	float pR = texture2DRect(pressureTexture, gl_FragCoord + vec2( 1, 0)).r;
	float pB = texture2DRect(pressureTexture, gl_FragCoord + vec2( 0,-1)).r;
	float pT = texture2DRect(pressureTexture, gl_FragCoord + vec2( 0, 1)).r;

  	vec2 grad = vec2(pR - pL, pT - pB) * halfrdx;
  	

  	gl_FragColor = texture2DRect(velocityTexture, gl_FragCoord);
  	gl_FragColor.xy -= grad;
} 
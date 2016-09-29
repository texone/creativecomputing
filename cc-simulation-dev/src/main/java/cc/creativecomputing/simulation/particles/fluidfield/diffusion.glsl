#version 120 

/**
 * This program performs a single Jacobi relaxation step for a poisson 
 * equation of the form
 *
 *                Laplacian(U) = b,
 *
 * where U = (u, v) and Laplacian(U) is defined as 
 *
 *   grad(div x) = grad(grad dot x) = 
 *            partial^2(u)/(partial(x))^2 + partial^2(v)/(partial(y))^2
 *
 * A solution of the equation can be found iteratively, by using this 
 * iteration:
 *
 *   U'(i,j) = (U(i-1,j) + U(i+1,j) + U(i,j-1) + U(i,j+1) + b) * 0.25
 *
 * That is what this routine does.  To maintain flexibility for slightly 
 * different poisson problems (such as viscous diffusion), we provide 
 * two parameters, centerFactor and stencilFactor.  These are useful for 
 * non-unit-scale grids, and when there is a coefficient on the RHS of the 
 * poisson equation.
 * 
 * This program works for both scalar and vector equations.
 */ 
uniform float alpha;

// reciprocal beta 
uniform float rBeta; 

// x vector (Ax = b)
uniform sampler2DRect x;

// b vector (Ax = b)
uniform sampler2DRect b;
    
void main(){
	vec4 xL = texture2DRect(x,gl_FragCoord + vec2(-1, 0));
	vec4 xR = texture2DRect(x,gl_FragCoord + vec2( 1, 0));
	vec4 xB = texture2DRect(x,gl_FragCoord + vec2( 0,-1));
	vec4 xT = texture2DRect(x,gl_FragCoord + vec2( 0, 1));
	
	vec4 bC = texture2DRect(b, gl_FragCoord);

	gl_FragColor = (xL + xR + xB + xT + alpha * bC) * rBeta;
} 
#version 120 

/**
 * This program is used to compute neumann boundary conditions for solving
 * poisson problems.  The neumann boundary condition for the poisson equation
 * says that partial(u)/partial(n) = 0, where n is the normal direction of the
 * inside of the boundary.  This simply means that the value of the field
 * does not change across the boundary in the normal direction.
 * 
 * In the case of our simple grid, this simply means that the value of the 
 * field at the boundary should equal the value just inside the boundary.
 *
 * We allow the user to specify the direction of "just inside the boundary" 
 * by using texture coordinate 1.
 *
 * Thus, to use this program on the left boundary, TEX1 = (1, 0):
 *
 * LEFT:   TEX1=( 1,  0)
 * RIGHT:  TEX1=(-1,  0)
 * BOTTOM: TEX1=( 0,  1)
 * TOP:    TEX1=( 0, -1)
 */
uniform float scale;
uniform sampler2DRect x;
      
void main(){
	vec4 color = texture2DRect(x, gl_TexCoord[0].xy + gl_TexCoord[1].xy);
	vec4 bv = vec4(0);
	bv.xy = scale * color.xy;
	bv.zw = color.zw;//vec4(1,0,0,1);// 
	
	gl_FragColor = bv;
}
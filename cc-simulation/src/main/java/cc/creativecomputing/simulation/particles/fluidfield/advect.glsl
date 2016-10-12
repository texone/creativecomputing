#version 120 

/**
 * This program performs a semi-lagrangian advection of a passive field by 
 * a moving velocity field.  It works by tracing backwards from each fragment
 * along the velocity field, and moving the passive value at its destination
 * forward to the starting point.  It performs bilinear interpolation at the 
 * destination to get a smooth resulting field.
 */
 
uniform float timeStep;

// mass dissipation constant.
uniform float dissipation;

// 1 / grid scale. 
uniform float rdx;

// 
uniform float darkening;

// the velocity field.    
uniform sampler2DRect velocityTexture;

// the field to be advected.       
uniform sampler2DRect targetTexture;

/**
 * These methods perform texture lookups at the four nearest neighbors of the 
 * position s and bilinearly interpolate them.
 */ 

vec4 bilerp(vec2 s){
  vec4 st;
  st.xy = floor(s - 0.5) + 0.5;
  st.zw = st.xy + 1;
  
  vec2 t = s - st.xy; //interpolating factors 
    
  vec4 tex11 = texture2DRect(targetTexture, st.xy);
  vec4 tex21 = texture2DRect(targetTexture, st.zy);
  vec4 tex12 = texture2DRect(targetTexture, st.xw);
  vec4 tex22 = texture2DRect(targetTexture, st.zw);

  // bilinear interpolation
  return mix(mix(tex11, tex21, t.x), mix(tex12, tex22, t.x), t.y);
}

void main(){
  
	// Trace backwards along trajectory (determined by current velocity)
	// distance = rate * time, but since the grid might not be unit-scale,
	// we need to also scale by the grid cell size.
	vec2 pos = gl_FragCoord.xy - timeStep * rdx * texture2DRect(velocityTexture, gl_FragCoord.xy).xy;

	// Example:
	//    the "particle" followed a trajectory and has landed like this:
	//
	//   (x1,y2)----(x2,y2)    (xN,yN)
	//      |          |    /----/  (trajectory: (xN,yN) = start, x = end)
	//      |          |---/
	//      |      /--/|    ^
	//      |  pos/    |     \_ v.xy (the velocity)
	//      |          |
	//      |          |
	//   (x1,y1)----(x2,y1)
	//
	// x1, y1, x2, and y2 are the coordinates of the 4 nearest grid points
	// around the destination.  We compute these using offsets and the floor 
	// operator.  The "-0.5" and +0.5 used below are due to the fact that
	// the centers of texels in a TEXTURE_RECTANGLE_NV are at 0.5, 1.5, 2.5, 
	// etc.

	// The function f4texRECTbilerp computes the above 4 points and interpolates 
	// a value from texture lookups at each point.Rendering this value will 
	// effectively place the interpolated value back at the starting point 
	// of the advection.
	 
	// So that we can have dissipating scalar fields (like smoke), we
	// multiply the interpolated value by a [0, 1] dissipation scalar 
	// (1 = lasts forever, 0 = instantly dissipates.  At high frame rates, 
	// useful values are in [0.99, 1].
	
	gl_FragColor = dissipation * bilerp(pos);
	//if(darkening > 0)gl_FragColor = clamp(gl_FragColor, 0.0, 1.0);
} 
/*
* 2d shape to bounce particles in the same plane like texture forces
*/
	
samplerRECT texture;
	
uniform vec2 textureSize;
uniform vec3 scale;
uniform vec3 offset;
	
uniform float resilience;
uniform float friction;
uniform float minimalVelocity;
	
vec3 function(vec3 theVelocity, vec3 thePosition, vec2 theTexID, float theDeltaTime){
			
	vec3 fPosition = thePosition + theVelocity * theDeltaTime;
		
	vec2 shapePos   = thePosition.xy / scale.xy + offset.xy;
	vec2 fshapePos  = fPosition.xy / scale.xy + offset.xy;
		
	vec3 pointNow  = texture2DRect (texture, shapePos);
	vec3 pointNext = texture2DRect (texture, fshapePos);
	vec3 newVelocity = theVelocity*texture2DRect (texture, vec2(0,0));
		
	// Calculate normal vector for the next point
	vec3 normal = vec3(0,0,0);
	float weight = 1;
		
	normal.x = texture2DRect (texture, fshapePos+vec2(-1,-1)) + 2 *texture2DRect (texture, fshapePos+vec2(-1, 0)) + texture2DRect (texture, shapePos+vec2(-1, 1))
	         - texture2DRect (texture, fshapePos+vec2( 1,-1)) - 2 *texture2DRect (texture, fshapePos+vec2( 1, 0)) - texture2DRect (texture, shapePos+vec2( 1, 1));
	normal.y = texture2DRect (texture, fshapePos+vec2(-1,-1)) + 2 *texture2DRect (texture, fshapePos+vec2( 0,-1)) + texture2DRect (texture, shapePos+vec2( 1,-1))
	         - texture2DRect (texture, fshapePos+vec2(-1, 1)) - 2 *texture2DRect (texture, fshapePos+vec2( 0, 1)) - texture2DRect (texture, shapePos+vec2( 1, 1));
		
	normal = normalize (normal);
		
	// find nearest shape point in velocity direction (shape parametrization unknown)
	float len = length (fshapePos - shapePos);
	int nSteps = log ((float)ceil(len)) / log(2.0);
		
	vec2 pos = shapePos;
	vec2 dir = fshapePos - shapePos;
	vec3 point;
	int sign = 1;
	for (int i=1; i<=nSteps; i++) {
		int div = pow (2.0, (float)i);
		pos += sign*dir/div;
		point = texture2DRect (texture, pos).xyz;
		if (length(point) > 0) {
			sign = -1;
		}
		else {
			sign = 1;
		}
	}
	float dist = length(fshapePos - pos);

	if (pointNow.z>0.2) {
		//return bounceReflection (theVelocity, normal, dist, resilience*2, friction, minimalVelocity);
		return theVelocity*friction;
	}
	
	return theVelocity;
}
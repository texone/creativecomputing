

uniform vec3 center;
uniform float radius;
	
uniform float resilience;
uniform float friction;
uniform float minimalVelocity;

uniform float inside;
	
vec3 function(vec3 theVelocity, vec3 thePosition, vec2 theTexID,float theDeltaTime){

	vec3 delta = (thePosition + theVelocity * theDeltaTime) - center;
	float dist = length(delta);

	if (dist - radius > 0) return theVelocity;
	
	
	return bounceReflection(
		theVelocity, 
		delta / dist,
		distance(thePosition, center) - radius,
		resilience, 
		friction, 
		minimalVelocity
	);
	
	//return theVelocity * -1.0;
}
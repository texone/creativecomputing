
uniform	vec3 normal;
uniform	float constant;
	
uniform	float resilience;
uniform	float friction;
uniform	float minimalVelocity;
	
vec3 function(vec3 theVelocity, vec3 thePosition, vec2 theTexID, float theDeltaTime){
	float pseudoDistance = dot(normal, thePosition + theVelocity * theDeltaTime) - constant;
	
	if (pseudoDistance > 0) return theVelocity;
		
	return bounceReflection(
		theVelocity, 
		normalize(normal), 
		pseudoDistance,
		resilience, 
		friction, 
		minimalVelocity
	);
}
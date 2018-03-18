
uniform vec3 planeNormal;
uniform float planeConstant;
	
uniform float resilience;
uniform float friction;
uniform float minimalVelocity;
	
vec3 function(vec3 theVelocity, vec3 thePosition, vec2 theTexID, float theDeltaTime){
	float pseudoDistance = dot(planeNormal, thePosition + theVelocity * theDeltaTime) - planeConstant;
	
	if (pseudoDistance > 0) return theVelocity;
		
	return bounceReflection(
		theVelocity, 
		normalize(planeNormal), 
		pseudoDistance,
		resilience, 
		friction, 
		minimalVelocity
	);
}
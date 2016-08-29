vec3 bounceReflection(
	vec3 theVelocity, vec3 theNormal, float thePlacement,
	float theResilience, float theFriction, float theMinimalVelocity
){
	// Distibute velocity to normal and tangential contributions.
	float normalContribution = dot(theVelocity, theNormal);
	vec3 vNormal = normalContribution * theNormal;
	vec3 vTangent = theVelocity - vNormal;
	
	if (thePlacement < 0){
		// Get particle outside the collider as quickly as possible,
		// either with original or reflected velocity.
		
		if (normalContribution <= 0.0){
			return vTangent - vNormal;
		} else {
			return theVelocity;
		}
	} 
	
	// Slow down particle with friction only if slower than minimal velocity.
	if (length(theVelocity) < theMinimalVelocity)
		theFriction = 1.0;

	// Slowdown tangential movement with friction (in theory 1 - friction)
	// and reflected normal movement via resilience factor.
	return vTangent * theFriction - vNormal * theResilience;
}

vec3 simpleReflection(vec3 theVelocity, vec3 theNormal, float theResilience){
	return reflect(theVelocity, theNormal) * theResilience;
}

uniform vec3 minCorner;
uniform vec3 maxCorner;
    
uniform float resilience;
uniform float friction;
uniform float minimalVelocity;
    
vec3 function(vec3 theVelocity, vec3 thePosition, vec2 theTexID, float theDeltaTime){
	vec3 futurePosition = thePosition + theVelocity * theDeltaTime;
    	    
	if (futurePosition.x < minCorner.x){
		return bounceReflection(
			theVelocity,
			vec3(1, 0, 0),
			thePosition.x - minCorner.x,
			resilience,
			friction,
			minimalVelocity
		);
	}
	if (futurePosition.y < minCorner.y){
		return bounceReflection(
			theVelocity,
			vec3(0, 1, 0),
			thePosition.y - minCorner.y,
			resilience,
			friction,
			minimalVelocity
		);
	}
	if (futurePosition.z < minCorner.z){
		return bounceReflection(
			theVelocity,
			vec3(0, 0, 1),
			thePosition.z - minCorner.z,
			resilience,
			friction,
			minimalVelocity
		);
	}
	if (futurePosition.x > maxCorner.x){
		return bounceReflection(
			theVelocity, 
			vec3(-1, 0, 0), 
			maxCorner.x - thePosition.x,
			resilience, 
			friction, 
			minimalVelocity
		);
	}
	if (futurePosition.y > maxCorner.y){
		return bounceReflection(
			theVelocity, 
			vec3(0, -1, 0), 
			maxCorner.y - thePosition.y,
			resilience, 
			friction, 
			minimalVelocity
		);
	}
	if (futurePosition.z > maxCorner.z){
		return bounceReflection(
			theVelocity, 
			vec3(0, 0, -1), 
			maxCorner.z - thePosition.z,
			resilience, 
			friction, 
			minimalVelocity
		);
    }
	return theVelocity;
}
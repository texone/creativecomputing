/*
 * constraint to let particles bounce of a sphere
 */

uniform sampler3D texture;
	
uniform vec3 textureScale;
uniform vec3 textureOffset;
	
uniform float minLength;
	
uniform vec3 minCut;
uniform vec3 maxCut;
	
uniform float minForce;
	
uniform float resilience;
uniform float friction;
uniform float minimalVelocity;
	
uniform vec3 constraint(vec3 theVelocity, vec3 thePosition, vec2 theTexID,float theDeltaTime){
		
	vec3 futurePos = (thePosition + theVelocity * theDeltaTime);
	vec3 texturePos = (futurePos - textureOffset) / textureScale;
		
	vec3 force = tex3D(texture, texturePos);
	
	if(
		texturePos.x >= maxCut.x || 
		texturePos.y >= maxCut.y || 
		texturePos.z >= maxCut.z || 
		texturePos.x <= minCut.x || 
		texturePos.y <= minCut.y || 
		texturePos.z <= minCut.z ||
		length(force) < minForce
	){
		return theVelocity;
	}

	return bounceReflection(
		theVelocity, 
		normalize(force),
		-1,
		resilience, 
		friction, 
		minimalVelocity
	);
}


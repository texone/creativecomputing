
uniform float strength;
uniform float index;

uniform vec3 position;
uniform float radius;
	
vec3 function(vec3 thePosition, vec3 theVelocity, vec2 theTexID, float theDeltaTime){
	vec3 force = position - thePosition;
	float dist = length(force);
	
	if(dist >= radius)return vec3(0.0);
	
	float myFallOff = 1.0 - dist / radius;
	float myForce = myFallOff * myFallOff;
	force = force * myForce / dist;
	return force * lifeTimeBlend(theTexID, index) * strength;
}

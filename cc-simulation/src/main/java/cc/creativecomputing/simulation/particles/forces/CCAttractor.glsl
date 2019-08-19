
uniform float strength;
uniform float index;

uniform float direction;

uniform vec3 position;
uniform float radius;

uniform float attractionSpeed;
uniform float attractionForce;
uniform float stickDistance;
uniform float stickForce;

/*
vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	vec3 force = position - thePosition;
	float dist = length(force);
	
	if(dist >= radius)return vec3(0.0);
	
	float myFallOff = 1.0 - dist / radius;
	float myForce = myFallOff * myFallOff;
	force = force * myForce / dist;
	return force * strength * direction;
}
*/


vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	vec3 dir = position - thePosition;
	float distToCenter = length(dir);
	float distToSurface = distToCenter - radius;
	
	dir /= max(0.0001,distToCenter); // safe normalize
	
	float spdNormal = dot(dir,theVelocity);
	float ratio = smoothstep(0.0,stickDistance * 2.0,abs(distToSurface));
	float tgtSpeed = sign(distToSurface) * attractionSpeed * ratio;
	float deltaSpeed = tgtSpeed - spdNormal;
	return sign(deltaSpeed) * min(abs(deltaSpeed),deltaTime * mix(stickForce,attractionForce,ratio)) * dir  * strength * direction;
}
uniform sampler2DRect targetPositionTexture;

uniform float strength;
uniform float index;

uniform vec3 center;
uniform float scale;
uniform float lookAhead;
uniform float maxForce;
uniform float nearDistance;
uniform float nearMaxForce;

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	
	vec4 targetInfos = texture2DRect (targetPositionTexture, theTexID);
	vec3 target = targetInfos.xyz * scale;
	
	float targetStrength = targetInfos.w;
		
	if(target.x == 0.0)return vec3(0.0);
		
	target += center;
	//float factor = (-dot(normalize(target - thePosition),normalize(theVelocity)) + 1) / 2;
	vec3 force = target - (thePosition + theVelocity * theDeltaTime * lookAhead);
		
	float distance = length(force);
	if(nearMaxForce > 0 && distance < nearDistance && distance > nearMaxForce){
		return force / distance * nearMaxForce * targetStrength;
	}
	if(maxForce > 0 && distance > maxForce){
		return force / distance * maxForce * targetStrength;
	}
	
	return force * strength;// * targetStrength * strength;// / (theDeltaTime * 60);
}
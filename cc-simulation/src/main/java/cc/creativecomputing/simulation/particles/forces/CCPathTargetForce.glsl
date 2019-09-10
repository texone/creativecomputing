uniform sampler2DRect targetPositionTexture;
uniform sampler2DRect targetPathTexture;

uniform float strength;
uniform float index;
uniform float blend;

uniform vec3 center;
uniform float scale;
uniform float lookAhead;
uniform float maxForce;
uniform float nearDistance;
uniform float nearMaxForce;
uniform float pathAdd;
uniform float pathLength;

uniform float noiseAdd;
uniform float noiseAmount;

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	
	vec4 targetInfos = texture2DRect (targetPositionTexture, theTexID);
	float noiseVal = noise(vec3(targetInfos.x * 100, noiseAdd * 0.05,0));
	noiseVal *= 2;
	noiseVal -= 1;
	noiseVal *= 10000 * noiseAmount;
	targetInfos.y += noiseVal;
	targetInfos.y += pathAdd;
	
	vec3 jump = texture2DRect(targetPathTexture, vec2(targetInfos.x,0)).xyz * scale;
	jump *= floor(targetInfos.y / pathLength);
	
	targetInfos.y = mod(targetInfos.y, pathLength - 1);
	
	vec3 target = texture2DRect(targetPathTexture, targetInfos.xy + vec2(0,1)).xyz * scale + jump;
	vec3 target2 = texture2DRect(targetPathTexture, targetInfos.xy + vec2(0,3)).xyz * scale + jump;

	vec3 dirT = normalize(target2 - target);
	target.xy += vec2(dirT.y*targetInfos.z, -dirT.x*targetInfos.z);
	
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
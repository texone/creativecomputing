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
uniform float pathScale;

uniform float noiseAdd;
uniform float noiseAmount;
uniform float randomAmount;

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	
	vec4 targetInfos = texture2DRect (targetPositionTexture, theTexID);
	float noiseVal = sin(noiseAdd * 0.05 + targetInfos.w * randomAmount * 3.2);
	noiseVal *= 10000 * noiseAmount;
	targetInfos.y *= pathScale;
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
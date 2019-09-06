uniform float amount;

vec2 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime, float theBlend){
	return vec2(theBlend, amount); 
}
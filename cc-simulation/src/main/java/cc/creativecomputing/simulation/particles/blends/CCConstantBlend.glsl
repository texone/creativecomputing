uniform float amount;

uniform float constantBlend;

vec2 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	return vec2(constantBlend, amount); 
}
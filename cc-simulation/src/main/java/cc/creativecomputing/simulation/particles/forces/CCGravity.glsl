uniform vec3 direction;
uniform float strength;
uniform float index;

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	return direction * strength; 
}
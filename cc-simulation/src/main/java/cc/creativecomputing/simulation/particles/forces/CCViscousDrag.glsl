uniform float drag;
	
uniform float strength;
uniform float index;
uniform float blend;

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	return theVelocity * theDeltaTime * -drag * strength; 
}

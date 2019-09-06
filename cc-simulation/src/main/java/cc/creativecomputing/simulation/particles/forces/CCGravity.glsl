uniform vec3 direction;
uniform float strength;
uniform float index;
uniform float blend;

uniform vec3 randomAmount;

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	return (direction + randDirection3D(theTexID) * randomAmount * rand(theTexID)) * strength; 
}
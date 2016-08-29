uniform float drag;
	
uniform float strength;

vec3 function(vec3 thePosition, vec3 theVelocity, vec2 theTexID, float theDeltaTime){
	return theVelocity * theDeltaTime * -drag * strength; 
}

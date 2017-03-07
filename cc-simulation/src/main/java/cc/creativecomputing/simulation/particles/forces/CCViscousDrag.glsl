uniform float drag;
	
uniform float strength;
uniform float index;

vec3 function(vec3 thePosition, vec3 theVelocity, vec2 theTexID, float theDeltaTime){
	return theVelocity * theDeltaTime * -drag * lifeTimeBlend(theTexID, index) * strength; 
}

uniform sampler2DRect terrainTexture;

uniform vec2 textureSize;
uniform vec3 scale;
uniform vec3 offset;
uniform float exponent;

uniform float strength;
uniform float index;

vec3 function(vec3 thePosition, vec3 theVelocity, vec2 theTexID, float theDeltaTime){
	
	vec3 fPosition = thePosition + theVelocity * theDeltaTime;
		
	vec2 terrainPos = fPosition.xz / scale.xz + offset.xz;
	float height = texture2DRect(terrainTexture, terrainPos).x;
			
	float displacement = fPosition.y - height * scale.y + offset.y + theVelocity.y;
		
	return vec3(0,clamp(-displacement,-1,1),0) * lifeTimeBlend(theTexID, index) * strength;
}
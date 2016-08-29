
uniform float scale;
uniform float strength;
uniform vec3 offset;

uniform int octaves;
uniform float gain;
uniform float lacunarity;

uniform float height;
	
vec3 function(vec3 thePosition, vec3 theVelocity, vec2 theTexID, float theDeltaTime){
	vec3 fPosition = thePosition + theVelocity * theDeltaTime;
	vec3 noisePosition = (fPosition) * scale + offset;
		
	float displacement =  fPosition.y - (octavedNoise(vec3(noisePosition.xz,0), octaves, gain, lacunarity) * 2 - 1) * height +  + theVelocity.y;
	return vec3(0,clamp(-displacement,-1,1),0) * strength;
}
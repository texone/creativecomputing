
uniform float scale;
uniform float strength;
uniform vec3 offset;
uniform float index;
uniform float blend;

uniform int octaves;
uniform float gain;
uniform float lacunarity;
	
vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	vec3 noisePosition = (thePosition + theVelocity) * scale + offset;
	vec3 result = vec3(
		octavedNoise(noisePosition, octaves, gain, lacunarity),
		octavedNoise(noisePosition+1000, octaves, gain, lacunarity),
		octavedNoise(noisePosition+2000, octaves, gain, lacunarity)
	) *2 - 1;
	/*
	noisePosition = (thePosition + theVelocity) * scale + offset.yzx;
	result += vec3(
		snoise(noisePosition),
		snoise(noisePosition+100),
		snoise(noisePosition+200)
	);
	*/
		//result *= 50 * theDeltaTime;
	return result * strength;
}
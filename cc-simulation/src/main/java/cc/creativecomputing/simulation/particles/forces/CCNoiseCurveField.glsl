
uniform float strength;
uniform float index;

uniform float prediction;
	
uniform float radius;
	
uniform int octaves;
uniform float gain;
uniform float lacunarity;
	
uniform float offset;
uniform float scale;
uniform float outputScale;
	
vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	vec3 futurePosition = thePosition + theVelocity * prediction;
	
	float y = outputScale * ((octavedNoise(vec3(futurePosition.x * scale + offset,0, 0), octaves, gain, lacunarity)) * 2 - 1);
	float z = outputScale * ((octavedNoise(vec3(futurePosition.x * scale + offset + 100, 0, 0), octaves, gain, lacunarity)) * 2 - 1);
	vec3 myCurvePoint = vec3(futurePosition.x, y, z);
	
	float curveDistance = distance(myCurvePoint, futurePosition);
	
	vec3 result = vec3(0,0,0);
			
	if(curveDistance > radius * 2){
		result = (myCurvePoint - futurePosition) / curveDistance;
	}else if(curveDistance > radius && curveDistance <= radius * 2){
		float blend = (curveDistance - radius) / radius;
		result = result * (1 - blend) + (myCurvePoint-futurePosition) / curveDistance * blend;
	}
	
	return result * strength;
}
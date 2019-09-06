uniform sampler2D curveData;

uniform float prediction;
uniform float radius;
uniform float outputScale;

uniform float minX;
uniform float rangeX;

uniform float strength;
uniform float index;
uniform float blend;

vec3 function(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime){
	vec3 futurePosition = thePosition + theVelocity * prediction;
	
	vec3 result = vec3(0);
		
	float relativeX = (futurePosition.x - minX) / rangeX;
	vec3 myCurvePoint = texture2D(curveData, vec2(relativeX, 0.5)).xyz;
	myCurvePoint.yz *= outputScale;
	
	float curveDistance = distance(myCurvePoint, futurePosition);
		
	if(curveDistance > radius * 2){
		result = (myCurvePoint - futurePosition) / curveDistance;	
	}else if(curveDistance > radius && curveDistance <= radius * 2){
		float blend = (curveDistance - radius) / radius;
		result = result * (1 - blend) + (myCurvePoint-futurePosition) / curveDistance * blend;
	}
		
	return result * strength;
}
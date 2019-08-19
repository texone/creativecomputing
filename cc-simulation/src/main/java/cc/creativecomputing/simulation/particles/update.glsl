#version 120 

@define noise

uniform float deltaTime;

uniform sampler2DRect colorTexture;
uniform sampler2DRect staticPositions;
uniform sampler2DRect staticAges;
uniform sampler2DRect positionTexture;
uniform sampler2DRect velocityTexture;
uniform sampler2DRect infoTexture;
uniform float staticPositionBlend;

uniform sampler2DRect lifeTimeBlends;
uniform sampler2DRect groupInfoTexture;

@define blends

float blend(vec3 thePosition, vec3 theVelocity, vec4 theInfos, vec4 theGroupInfos, vec2 theTexID, float theDeltaTime, float forceIndex){

	float myBlend = 0;
	float myAmount = 0;
	vec2 blendInfo;
	
@apply blends

	if(myAmount == 0)myBlend /= myAmount;
	//float progress = theInfos.x / theInfos.y;
	if(theInfos.y <= 0)return 1;
	//if(groupInfos.x >= 0.)progress = groupInfos.x;
	return texture2DRect (lifeTimeBlends, vec2(myBlend * 100.0, forceIndex)).x;
}

float rand(vec2 n){
  return fract(sin(dot(n, vec2(12.9898,78.233)))* 43758.5453123);
}

const float PI = 3.1415926535897932384626433832795;

vec2 randDirection2D(vec2 n) {
	float angle = rand(n + 3000) * 2 * PI;
	return vec2(cos(angle), sin(angle));
}

vec3 randDirection3D(vec2 n) {
	float myTheta = rand(n + 3000) * PI;
	float myPhi = rand(n + 6000) * 2 * PI;
	
	return vec3(
		sin(myTheta) * cos(myPhi),
		sin(myTheta) * sin(myPhi),
		cos(myTheta)
	);
}

// insert forces
@define forces

// insert constraints

vec3 bounceReflection(
	vec3 theVelocity, vec3 theNormal, float thePlacement,
	float theResilience, float theFriction, float theMinimalVelocity
){
	// Distibute velocity to normal and tangential contributions.
	float normalContribution = dot(theVelocity, theNormal);
	vec3 vNormal = normalContribution * theNormal;
	vec3 vTangent = theVelocity - vNormal;
	
	if (thePlacement < 0){
		// Get particle outside the collider as quickly as possible,
		// either with original or reflected velocity.
		
		if (normalContribution <= 0.0){
			return vTangent - vNormal;
		} else {
			return theVelocity;
		}
	} 
	
	// Slow down particle with friction only if slower than minimal velocity.
	if (length(theVelocity) < theMinimalVelocity)
		theFriction = 1.0;

	// Slowdown tangential movement with friction (in theory 1 - friction)
	// and reflected normal movement via resilience factor.
	return vTangent * theFriction - vNormal * theResilience;
}

@define constraints

uniform float useAgeBlends;

uniform vec3 moveAll;

void main (){
	vec2 texID = gl_FragCoord.xy;
	vec4 position = texture2DRect (positionTexture, texID);
	vec3 velocity = texture2DRect (velocityTexture, texID).xyz;
	vec4 infos = texture2DRect (infoTexture, texID);
	vec4 groupInfos = texture2DRect (groupInfoTexture, infos.zw);
	if(infos.z < 0)groupInfos = vec4(-1.0);
	vec4 color = texture2DRect (colorTexture, texID);
	vec3 acceleration = vec3(0,0,0);

// apply forces
@apply forces

	velocity = velocity + acceleration * (deltaTime * 60);

// apply constraints


@apply constraints
	
	
	/*
	for(int i = 0; i < constraints.length;i++){
		velocity = constraints[i].constraint(velocity, position.xyz,texID, deltaTime);
	}
	
	for(int i = 0; i < impulses.length;i++){
		velocity += impulses[i].impulse(position.xyz,velocity, texID, deltaTime);
	}
	*/
	vec4 lastInfo = texture2DRect(infoTexture, texID);
	float myAge = lastInfo.x;
	vec2 myGroup = lastInfo.zw;

	float staticAge = texture2DRect (staticAges, texID).x;
	
	vec4 info = vec4(
		0,//mix(lastInfo.x + deltaTime, staticAge, useAgeBlends) * float(lastInfo.y > 0),
		lastInfo.y,
		lastInfo.z,
		lastInfo.w
	);
	
	if(myAge >= lastInfo.y && lastInfo.y > 0 && lastInfo.z < 0.0)position.xyz = vec3(1000000,0,0);
	
	vec3 staticPosition = texture2DRect (staticPositions, texID).xyz;
	vec4 newPosition = vec4(mix(position.xyz + deltaTime * velocity , staticPosition, staticPositionBlend),1);
	newPosition.xyz += moveAll;
	
	gl_FragData[0] = newPosition;
	gl_FragData[1] = info;
	gl_FragData[2] = vec4(velocity, 1.0);
	gl_FragData[3] = color;
}
	           
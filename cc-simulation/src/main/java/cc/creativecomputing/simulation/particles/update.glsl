#version 120 

@CCInsert defineNoise

uniform float deltaTime;

uniform sampler2DRect colorTexture;
uniform sampler2DRect staticPositions;
uniform sampler2DRect positionTexture;
uniform sampler2DRect velocityTexture;
uniform sampler2DRect infoTexture;
uniform float staticPositionBlend;

uniform sampler2DRect lifeTimeBlends;

float lifeTimeBlend(vec2 texID, float forceIndex){
	vec4 infos = texture2DRect (infoTexture, texID);
	return texture2DRect (lifeTimeBlends, vec2(infos.x / infos.y * 100.0, forceIndex)).x;
}

// insert forces
@CCInsert defineForces

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

@CCInsert defineConstraints

void main (){
	vec2 texID = gl_FragCoord.xy;
	vec3 position = texture2DRect (positionTexture, texID).xyz;
	vec3 velocity = texture2DRect (velocityTexture, texID).xyz;
	vec4 color = texture2DRect (colorTexture, texID);
	vec3 acceleration = vec3(0,0,0);

// apply forces
@CCInsert applyForces

	velocity = velocity + acceleration * (deltaTime * 60);

// apply constraints


@CCInsert applyConstraints
	
	
	/*
	for(int i = 0; i < constraints.length;i++){
		velocity = constraints[i].constraint(velocity, position,texID, deltaTime);
	}
	
	for(int i = 0; i < impulses.length;i++){
		velocity += impulses[i].impulse(position,velocity, texID, deltaTime);
	}
	*/
	vec4 lastInfo = texture2DRect(infoTexture, texID);
	float myAge = lastInfo.x;
	int myStep = int(lastInfo.w);
	
	vec4 info = vec4(
		lastInfo.x + deltaTime,
		lastInfo.y,
		lastInfo.z,
		lastInfo.w
	);
	
	if(myAge >= lastInfo.y && lastInfo.z == 0.0)position = vec3(1000000,0,0);
	vec3 staticPosition = texture2DRect (staticPositions, texID).xyz;
	vec4 newPosition = vec4(mix(position + vec3(1,0,0) + deltaTime * velocity, staticPosition, staticPositionBlend),1);
	
	gl_FragData[0] = newPosition;
	gl_FragData[1] = info;
	gl_FragData[2] = vec4(velocity, 1.0);
	gl_FragData[3] = color;
}
	           

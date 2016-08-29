#version 120 

@define noise

uniform float deltaTime;

uniform sampler2DRect colorTexture;
uniform sampler2DRect staticPositions;
uniform sampler2DRect positionTexture;
uniform sampler2DRect velocityTexture;
uniform sampler2DRect infoTexture;
uniform float staticPositionBlend;

// insert forces
@define forces

void main (){
	vec2 texID = gl_FragCoord.xy;
	vec3 position = texture2DRect (positionTexture, texID).xyz;
	vec3 velocity = texture2DRect (velocityTexture, texID).xyz;
	vec4 color = texture2DRect (colorTexture, texID);
	vec3 acceleration = vec3(0,0,0);

// apply forces
@apply forces
	
	/*
	for(int i = 0; i < forces.length;i++){
		acceleration = acceleration + forces[i].force(position,velocity,texID,deltaTime);
	}
	*/
	velocity = velocity + acceleration * (deltaTime * 60);
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
	vec4 newPosition = vec4(mix(position + deltaTime * velocity, staticPosition, staticPositionBlend),1); 
	
	gl_FragData[0] = newPosition;
	gl_FragData[1] = info;
	gl_FragData[2] = vec4(velocity, 1.0);
	gl_FragData[3] = color;
}
	           
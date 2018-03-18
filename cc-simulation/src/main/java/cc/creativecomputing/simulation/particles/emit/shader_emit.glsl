#version 120 

uniform sampler2DRect positionTexture;
uniform sampler2DRect velocityTexture;
uniform sampler2DRect infoTexture;
uniform float deltaTime;

uniform vec3 randomSeed;

float rand(vec2 n){
  return fract(sin(dot(n.xy, randomSeed.xy))* randomSeed.z);
}

uniform float emitProb;

bool function(
	vec2 texID, 
	out vec4 newPosition, 
	out vec4 newInfo,
	out vec4 newVelocity
) {
	if(rand(texID) > emitProb)return false;
	
	newPosition.x = cos(rand(texID + vec2(3000,0)) * 6.3) * 300;
	newPosition.y = sin(rand(texID + vec2(3000,0)) * 6.3) * 300; 
			
	newInfo.x = 0;
	newInfo.y = 10;
	newInfo.z = -1;
			
	newVelocity.xy = newPosition.xy / 10;
			
	return true;
}

bool function2(
	vec2 texID, 
	out vec4 newPosition, 
	out vec4 newInfo,
	out vec4 newVelocity
) {
	if(rand(texID) > emitProb)return false;
	
	newPosition.x = cos(rand(texID + vec2(3000,0)) * 6.3) * 150;
	newPosition.y = sin(rand(texID + vec2(3000,0)) * 6.3) * 150; 
			
	newInfo.x = 0;
	newInfo.y = 10;
	newInfo.z = -1;
			
	newVelocity.xy = newPosition.xy / 10;
			
	return true;
}

void main (){
	
	vec2 texID = gl_FragCoord.xy;
	vec4 position = texture2DRect (positionTexture, texID);
	vec4 velocity = texture2DRect (velocityTexture, texID);
	vec4 info = texture2DRect(infoTexture, texID);
	
	if(info.x < info.y){
		gl_FragData[0] = position;
		gl_FragData[1] = info;
		gl_FragData[2] = velocity;
		gl_FragData[3] = vec4(1.0);
		return;
	}
	
	position.xyz = vec3(1000000,0,0);

	vec4 newInfo;
	vec4 newPosition; 
	vec4 newVelocity;
	vec4 newColor = vec4(1.0,0.0,0.0,1.0);
		
	if(function(texID, newPosition, newInfo, newVelocity)){
		gl_FragData[0] = newPosition;
		gl_FragData[1] = newInfo;
		gl_FragData[2] = newVelocity;
		gl_FragData[3] = newColor;
		return;
	}

	gl_FragData[0] = position;
	gl_FragData[1] = info;
	gl_FragData[2] = velocity;
	gl_FragData[3] = vec4(1.0);
		
	/*
	gl_FragData[0] = newPosition;
	gl_FragData[1] = info;
	gl_FragData[2] = vec4(velocity, 1.0);
	gl_FragData[3] = newColor;*/
	
	
	
}
	           
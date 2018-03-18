#version 120 

uniform sampler2DRect positionTexture;
uniform sampler2DRect velocityTexture;
uniform sampler2DRect infoTexture;
uniform sampler2DRect colorTexture;
uniform float deltaTime;

uniform vec2 randomSeed;

float rand(vec2 n){
  return fract(sin(dot(n + randomSeed, vec2(12.9898,78.233)))* 43758.5453123);
}

const float PI = 3.1415926535897932384626433832795;

vec2 randDirection(vec2 n) {
	return vec2(
		cos(rand(n + 3000) * 2 * PI),
		sin(rand(n + 3000) * 2 * PI)
	);
}

uniform float emitProb;

@define emitter

void main (){
	
	vec2 texID = gl_FragCoord.xy;
	vec4 position = texture2DRect (positionTexture, texID);
	vec4 velocity = texture2DRect (velocityTexture, texID);
	vec4 info = texture2DRect(infoTexture, texID);
	vec4 color = texture2DRect(colorTexture, texID);
	
//	if(info.x < info.y){
//		gl_FragData[0] = position;
//		gl_FragData[1] = info;
//		gl_FragData[2] = velocity;
//		gl_FragData[3] = vec4(1.0);
//		return;
//	}
//	
//	position.xyz = vec3(1000000,0,0);

	vec4 newInfo;
	vec4 newPosition; 
	vec4 newVelocity;
	vec4 newColor;
		
	// apply forces
	@apply emitter

	gl_FragData[0] = position;
	gl_FragData[1] = info;
	gl_FragData[2] = velocity;
	gl_FragData[3] = color;
		
	/*
	gl_FragData[0] = newPosition;
	gl_FragData[1] = info;
	gl_FragData[2] = vec4(velocity, 1.0);
	gl_FragData[3] = newColor;*/
	
	
	
}
	           
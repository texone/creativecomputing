#version 120 

uniform sampler2DRect positionTexture;
uniform sampler2DRect velocityTexture;
uniform sampler2DRect infoTexture;
uniform sampler2DRect colorTexture;
uniform sampler2DRect geometryTexture;
uniform sampler2DRect geometryColorTexture;
uniform float deltaTime;

uniform vec2 gBufferSize;
uniform vec3 randomSeed;
uniform float emitProb;
uniform float minLifeTime;
uniform float maxLifeTime;
uniform float lifeTimeSpreadPow;

uniform mat4 inverseView;

float rand(vec2 n){
  return fract(sin(dot(n.xy, randomSeed.xy))* randomSeed.z);
}

void main (){
	//vec3 position = mul(inverseView, texture2DRect (positionTexture, texID));
	vec2 texID = gl_FragCoord.xy;
	vec3 position =  texture2DRect (positionTexture, texID);
	vec4 velocity = texture2DRect (velocityTexture, texID);
	

	vec4 newPosition;
	vec4 newInfo;
	vec3 newVelocity;
	vec4 newColor;
	
	newVelocity = velocity;
	newColor = texture2DRect (colorTexture, texID);
	
	newInfo = texture2DRect(infoTexture, texID);
	float myAge = newInfo.x;
	
	if(myAge >= newInfo.y && newInfo.z == 0.0){
		position = vec3(1000000,0,0);
		
		if(rand(texID) < emitProb){
			vec2 myTexCoord = vec2(rand(texID + vec2(3000,0)),rand(texID + vec2(4000,0)));
			vec4 gBufferPosition = tex2D(geometryTexture,myTexCoord);
			vec4 gBufferColor = tex2D(geometryColorTexture,myTexCoord);
			
			if(length(gBufferPosition) > 1 && gBufferColor.r >= 0.0){
				float lifeTimeRand = rand(texID + vec2(5000,0));
				float lifeTime = lerp(minLifeTime, maxLifeTime, pow(lifeTimeRand, lifeTimeSpreadPow));
				position = gBufferPosition;
				newInfo.x = 0;
				newInfo.y = lifeTime;
				newInfo.z = 0;
				newVelocity = vec3(0,0,0);
				newColor = gBufferColor;
			}
		}
	}
	
	//vec2 myTexCoord = vec2(rand(texID + vec2(3000,0)),rand(texID + vec2(4000,0)));
	//position = tex2D(geometryTexture,myTexCoord);
	newPosition = vec4(position,1); 
}
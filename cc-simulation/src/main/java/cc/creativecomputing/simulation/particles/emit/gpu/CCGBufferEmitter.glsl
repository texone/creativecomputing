uniform float propability;

uniform sampler2D geometryTexture;
uniform sampler2D geometryColorTexture;
uniform sampler2D geometryDepthTexture;
uniform vec2 gBufferSize;
uniform float emitProb;
uniform float minLifeTime;
uniform float maxLifeTime;
uniform float lifeTimeSpreadPow;

bool function(
	in vec4 position,
	in vec4 info,
	in vec4 velocity,
	in vec4 color,
	vec2 texID, 
	out vec4 newPosition, 
	out vec4 newInfo,
	out vec4 newVelocity,
	out vec4 newColor
) {
	if(rand(texID) > propability)return false;
	
	vec2 myTexCoord = vec2(rand(texID + vec2(3000,0)),rand(texID + vec2(4000,0)));// * gBufferSize;
	myTexCoord += 0.01;
	myTexCoord *= 0.88;
	myTexCoord += vec2(
		rand(texID + vec2(123.432,12.54) + randomSeed),
		rand(texID + vec2(45.23,34.54) + randomSeed)
	) * 0.1;
	vec4 gBufferPosition = texture2D(geometryTexture,myTexCoord);
	vec4 gBufferColor = texture2D(geometryColorTexture,myTexCoord);
	
	if(length(gBufferPosition) < 1 )return false;
	
	float lifeTimeRand = rand(texID + vec2(5000,0));
	float lifeTime = mix(minLifeTime, maxLifeTime, pow(lifeTimeRand, lifeTimeSpreadPow));
	newPosition = gBufferPosition;
	newInfo.x = 0;
	newInfo.y = lifeTime;
	newInfo.z = 0;
	newVelocity = vec4(0,0,0,1.0);
	newColor = gBufferColor;
	return true;
	
	
	
	/*
	newPosition.x = cos(rand(texID + vec2(3000,0)) * 6.3) * radius + center.x;
	newPosition.y = sin(rand(texID + vec2(3000,0)) * 6.3) * radius + center.y; 
			
	newInfo.x = 0;
	newInfo.y = 10;
	newInfo.z = -1;
			
	newVelocity.xy = newPosition.xy / 10;
	
	newColor = vec4(1.0);*/
			
	return true;
}
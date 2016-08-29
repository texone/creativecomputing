uniform samplerRECT positionTexture;
uniform samplerRECT velocityTexture;
uniform samplerRECT infoTexture;
uniform samplerRECT colorTexture;

uniform float deltaTime;
uniform float densityThreshold;

uniform float3 randomSeed;
uniform float emitProb;
uniform float emitSpeed;
uniform float emitLifetime;

uniform samplerRECT emitTexture1;
uniform samplerRECT emitTexture2;

uniform int width;
uniform int height;


float rand(float2 n){
  return fract(sin(dot(n.xy, randomSeed.xy))* randomSeed.z);
}

float randB(float2 id, float2 offset, float a, float b) {
	return lerp (a, b, rand(id+offset));
}

void main (
	in 	float2 texID : WPOS,
	out float4 newPosition : COLOR0,
	out float4 newInfo : COLOR1,
	out float3 newVelocity : COLOR2,
	out float4 newColor : COLOR3
){
	float3 lastPosition = (float3)texRECT (positionTexture, texID);
	float4 lastVelocity = texRECT (velocityTexture, texID);
	float4 lastInfo = texRECT(infoTexture, texID);
	float4 lastColor = texRECT(colorTexture, texID);
	float3 position;
	
	newPosition = float4 (lastPosition,1);
	newVelocity = lastVelocity;
	newInfo = lastInfo;
	newColor = lastColor;
	
	if (lastInfo.x >= lastInfo.y && lastInfo.z == 0.0) {
		
		position = float3 (rand(texID + float2(3000,0)) * 2 - 1, rand(texID + float2(4000,0)) * 2 - 1,0);
		position.x = width*position.x; // + randB(texID, float2(100,0),1,5);
		position.y = height*position.y; // + randB(texID, float2(100,0),1,5);
		position.z = 0*randB(texID, float2(0,100),-20,20);
		
		vec2 lookupPosition = vec2(position.x, height-position.y);
		float3 h = texRECT (emitTexture1, lookupPosition);
		float3 density = texRECT (emitTexture2, position);
		position -= vec3(width/2, height/2, 0);
		
		
		// green emit always
		if (h.y > 0 && rand(texID)<emitProb) {
			newInfo.x = 0;
			newInfo.y = emitLifetime;
			newInfo.z = 0;
			newVelocity = float3(0, 0, 0);
			newPosition = float4(position,1);
			newColor = float4(1.0,0.0,0.0,0.0);
		}
		
		// red grow only
		if (h.x>0 && rand(texID)<emitProb && density.x>=densityThreshold) {
			newInfo.x = 0;
			newInfo.y = emitLifetime;
			newInfo.z = 0;
			newVelocity = float3(0, 0, 0);
			newPosition = float4(position,1);
			newColor = float4(1.0,0.0,0.0,0.0);
		}
		
		// grow everywhere else
	}; 
	
}
	           
uniform samplerRECT positionTexture;
uniform samplerRECT velocityTexture;
uniform samplerRECT infoTexture;
uniform float deltaTime;

uniform float3 randomSeed;
uniform float emitProb;

float rand(float2 n){
  return fract(sin(dot(n.xy, randomSeed.xy))* randomSeed.z);
}

void main (
	in 	float2 texID : WPOS,
	out float4 newPosition : COLOR0,
	out float4 newInfo : COLOR1,
	out float3 newVelocity : COLOR2,
	out float4 newColor : COLOR3
){
	float3 position = (float3)texRECT (positionTexture, texID);
	float4 velocity = texRECT (velocityTexture, texID);
	
	newVelocity = velocity;
	
	float4 lastInfo = texRECT(infoTexture, texID);
	float myAge = lastInfo.x;
	
	newInfo =lastInfo;
	
	if(lastInfo.x >= lastInfo.y && lastInfo.z == 0.0){
		position = float3(1000000,0,0);
		
		if(rand(texID) < emitProb){
			position = float3(rand(texID + float2(3000,0)) * 2 - 1,rand(texID + float2(4000,0)) * 2 - 1, 0);
			position *= 300;
			newInfo.x = 0;
			newInfo.y = 10;
			newInfo.z = 0;
			newVelocity = float3(rand(texID + float2(1000,0)) * 2 - 1,rand(texID + float2(2000,0)) * 2 - 1, 0);
			newVelocity *= 10;
		}
	}
	newPosition = float4(position,1); 
	newColor = float4(1.0);
}
	           
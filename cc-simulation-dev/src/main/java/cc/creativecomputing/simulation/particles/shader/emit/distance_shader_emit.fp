uniform samplerRECT positionTexture;
uniform samplerRECT velocityTexture;
uniform samplerRECT infoTexture;
uniform float deltaTime;

uniform float3 randomSeed;
uniform float emitProb;

sampler3D texture;

float minLifeTime;
float lifeTimeRange;
	
float3 textureScale;
float3 textureOffset;
	
float minLength;

float directionOffset;
	
float3 minCut;
float3 maxCut;
	
float minForce;

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
			position *= 400;
			
			float3 texturePos = (position - textureOffset) / textureScale;
		
			float3 direction = tex3D(texture, texturePos);
			position += direction * directionOffset;
	
			if(
				texturePos.x < maxCut.x
				&& texturePos.y < maxCut.y 
				// && texturePos.z < maxCut.z
			    && texturePos.x > minCut.x
				&& texturePos.y > minCut.y
				// && texturePos.z > minCut.z 
			    && length(direction) > minForce
			){
				newInfo.x = 0;
				newInfo.y = 1;
				newInfo.z = 0;
				newVelocity = direction;
				newVelocity *= 10;
			}else{
				position = float3(1000000,0,0);
			}
			
		}
	}
	newPosition = float4(position,1); 
	newColor = float4(1.0);
}
	           
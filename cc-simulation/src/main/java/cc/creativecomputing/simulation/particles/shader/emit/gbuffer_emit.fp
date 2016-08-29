uniform samplerRECT positionTexture;
uniform samplerRECT velocityTexture;
uniform samplerRECT infoTexture;
uniform samplerRECT colorTexture;
uniform sampler2D geometryTexture;
uniform sampler2D geometryColorTexture;
uniform float deltaTime;

uniform float2 gBufferSize;
uniform float3 randomSeed;
uniform float emitProb;
uniform float minLifeTime;
uniform float maxLifeTime;
uniform float lifeTimeSpreadPow;

uniform float4x4 inverseView;

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
	//float3 position = mul(inverseView, texRECT (positionTexture, texID));
	float3 position =  texRECT (positionTexture, texID);
	float4 velocity = texRECT (velocityTexture, texID);
	
	newVelocity = velocity;
	newColor = texRECT (colorTexture, texID);
	
	newInfo = texRECT(infoTexture, texID);
	float myAge = newInfo.x;
	
	if(myAge >= newInfo.y && newInfo.z == 0.0){
		position = float3(1000000,0,0);
		
		if(rand(texID) < emitProb){
			float2 myTexCoord = float2(rand(texID + float2(3000,0)),rand(texID + float2(4000,0)));
			float4 gBufferPosition = tex2D(geometryTexture,myTexCoord);
			float4 gBufferColor = tex2D(geometryColorTexture,myTexCoord);
			
			if(length(gBufferPosition) > 1 && gBufferColor.r >= 0.0){
				float lifeTimeRand = rand(texID + float2(5000,0));
				float lifeTime = lerp(minLifeTime, maxLifeTime, pow(lifeTimeRand, lifeTimeSpreadPow));
				position = gBufferPosition;
				newInfo.x = 0;
				newInfo.y = lifeTime;
				newInfo.z = 0;
				newVelocity = float3(0,0,0);
				newColor = gBufferColor;
			}
		}
	}
	
	//float2 myTexCoord = float2(rand(texID + float2(3000,0)),rand(texID + float2(4000,0)));
	//position = tex2D(geometryTexture,myTexCoord);
	newPosition = float4(position,1); 
}
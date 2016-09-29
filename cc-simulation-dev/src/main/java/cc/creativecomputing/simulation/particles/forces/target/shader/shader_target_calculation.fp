uniform samplerRECT positionTexture;
uniform samplerRECT velocityTexture;
uniform samplerRECT targetTexture;

uniform float3 randomSeed;
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
	out float4 targetPosition : COLOR0
){
	float4 particlePosition = texRECT (positionTexture, texID);
	float4 particleVelocity = texRECT (velocityTexture, texID);	
		
	

	texID.x = texID.x / 500 * width;
	texID.y = texID.y / 500 * height;
	
	float4 targetTextureValue  = texRECT (targetTexture, texID);
	if (targetTextureValue.x > 0) {
		targetPosition = float4(texID, -100,1);
	}
	else {
		targetPosition = float4(0,0,0,0);
	}
}
	           
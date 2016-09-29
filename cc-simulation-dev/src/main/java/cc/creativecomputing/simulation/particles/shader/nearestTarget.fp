// texture holding the grid and storing which position is taken and which not
uniform samplerRECT targets;

// texture holding the target at avery particle xyid and the duration after
// which the target is applied
uniform samplerRECT particleTargetInfos;

uniform samplerRECT particlePositions;
uniform samplerRECT particleInfos;
uniform samplerRECT particleVelocities;

uniform sampler2D mask;

uniform float2 textureSize;
uniform float2 textureScale;
uniform float2 textureOffset;

uniform float deltaTime;
uniform float lookAhead;
uniform float targetTime;

uniform bool useMask;

float4 main(
	in float2 wPos : WPOS,
	out float4 oColor2 : COLOR1
) : COLOR0{
	float4 particleTargetInfo = texRECT(particleTargetInfos, wPos);
	float4 particleInfo = texRECT(particleInfos, wPos);
	
	// particle is dead delete target
	if(particleInfo.x >= particleInfo.y || particleInfo.x == 0){
		return float4(0,0,0,0);
	}
	
	// particle has target and is alive just leave everything as it is
	if(particleTargetInfo.z > 0){
		if(particleTargetInfo.z > targetTime){
			return float4(0,0,particleTargetInfo.z,0);
		} else {
			return float4(particleTargetInfo.xy,particleTargetInfo.z + deltaTime,1);
		}
	}
	
	
	// if particles target time is not reached discard 
	//if(particleInfo.x < particleTargetInfo.w)use = 0;
	
	float3 particlePosition = texRECT(particlePositions, wPos);
	float3 particleVelocity = texRECT(particleVelocities, wPos);
	
	float2 texturePos = particlePosition.xy + particleVelocity.xy * lookAhead;
	texturePos = ((texturePos * float2(1,1)) - textureOffset)  / textureScale;
	texturePos = floor(texturePos);
	
	// particle is outside grid so discard
	if(texturePos.x < 0 || texturePos.y < 0 || texturePos.x >= textureSize.x || texturePos.y >= textureSize.y){
		return float4(1,0,0,0);
	}
	
	
	float4 targetInfo = texRECT(targets, texturePos.xy);
	
	if(useMask){
		float2 texCoord = texturePos.xy / textureSize;
		float4 maskColor = tex2D(mask, float2(texCoord.x, texCoord.y));
		if(!maskColor.x)return float4(3,0,0,0);
	}
	
	// target is taken so discard
	if(targetInfo.z > 0){
		return float4(2,0,0,0);
	}
	
	return float4(texturePos.xy,deltaTime,1);
	
}
uniform samplerRECT positions;
uniform samplerRECT blendTexture;
uniform samplerRECT oldBlends;

uniform float2 textureScale;
uniform float2 textureOffset;

uniform float deltaTime;

void main(
	in float2 wPos : WPOS,
	out float4 oColor : COLOR
){
	float3 position = texRECT(positions, wPos);
	float2 texturePos = (position.xy * float2(1,1)) / textureScale + textureOffset;
	
	float oldBlend = texRECT(oldBlends, wPos);
	oColor = float4(saturate(oldBlend + (texRECT(blendTexture, texturePos.xy).x - 0.5) * 2 * deltaTime),0,0,1);
	//oColor = float4(position.x / 1200.0 + 0.5,position.y/600.0+0.5,0,1);
}
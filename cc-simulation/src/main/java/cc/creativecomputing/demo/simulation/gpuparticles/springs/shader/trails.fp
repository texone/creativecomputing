uniform samplerRECT infoTexture;

uniform float startblend = 3;

void main(
	in float4 iColor : COLOR,
	in float2 iTexCoord : TEXCOORD0,
	out float4 oColor : COLOR0
){
	float4 infos = texRECT(infoTexture, floor(iTexCoord));
	oColor = float4(0,0,0, saturate(min(infos.x / startblend , (infos.y - infos.x)/startblend)) );
	
	oColor = float4(iColor.rgb, iColor.a * saturate(min(infos.x / startblend , (infos.y - infos.x)/startblend)));
}
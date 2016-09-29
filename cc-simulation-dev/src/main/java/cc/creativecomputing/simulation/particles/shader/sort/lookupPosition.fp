uniform samplerRECT sortTexture : TEXUNIT0;
uniform samplerRECT positionTexture : TEXUNIT1;

void main(
	in float2 iTexCoord : TEXCOORD0,
	out float4 oColor : COLOR0
){
	float2 sortIndex = texRECT(sortTexture, iTexCoord.xy).y;
	half2 particleIndex = unpack_2half(sortIndex);
	oColor = (float4)texRECT(positionTexture, particleIndex);
}
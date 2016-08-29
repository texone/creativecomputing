uniform samplerRECT infos : TEXUNIT1;

void main(
	in float2 iTexID : TEXCOORD0,
	in float4 iColor : COLOR,
	out float4 oColor : COLOR0
){
	float4 myValues = texRECT(infos, iTexID);
	float myAlpha = saturate(1 - myValues.x / myValues.y);
	iColor.a *= myAlpha * myAlpha;
	oColor = iColor;
}
uniform sampler2D texture : TEXUNIT0;

void main(
	in 	float4 	iColor 	: COLOR,
	in 	float2 	iTexCoord 	: TEXCOORD0,
	in 	float 	iBlur	 : TEXCOORD1,
	
	out float4 	oColor 	: COLOR0,
	out float4 	oBlur	: COLOR1
){
	oColor = iColor;// * float4(tex2D(texture, iTexCoord).xyz, 1);
	oBlur = iBlur;
}


uniform sampler2D texture : TEXUNIT0;

void main(
	in 	float2 	iTexCoord 	: TEXCOORD0,
	
	out float4 	oColor 	: COLOR0
){
	oColor = tex2D(texture, iTexCoord);
}


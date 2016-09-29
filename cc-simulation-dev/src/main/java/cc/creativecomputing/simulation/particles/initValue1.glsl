void main(
	in float4 value1 : TEXCOORD1,
	in float4 value3 : TEXCOORD3,
	out float4 color1 : COLOR1 ,
	out float4 color3 : COLOR3
){
	color1 = value1;
	color3 = value3;
}

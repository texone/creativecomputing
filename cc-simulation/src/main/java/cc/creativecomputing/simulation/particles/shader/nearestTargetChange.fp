void main(
	in float4 iColor : TEXCOORD0,
	out float4 oColor : COLOR
){
	if(iColor.a == 0)discard;
	oColor = float4(iColor.xy,1,1);
}
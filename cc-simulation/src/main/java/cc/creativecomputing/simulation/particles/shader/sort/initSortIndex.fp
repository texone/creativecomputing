void main(
	in float2 iSortIndex : TEXCOORD0,
	out float4 oSortIndex : COLOR0
){
	oSortIndex = float4(iSortIndex,0.0,1.0);
	//oSortIndex = float4(1.0,2.0,0.0,1.0);
	
}
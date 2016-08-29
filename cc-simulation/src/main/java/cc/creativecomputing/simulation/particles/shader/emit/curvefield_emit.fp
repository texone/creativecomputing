float radius;
float offset;
float scale;
float outputScale;

float3 curveAtPoint(float x){
	float y = outputScale * (snoise(float2(x * scale + offset,0)));
	float z = outputScale * (snoise(float2(x * scale + offset + 100, 0)));
	return float3(x, y, z);
}
	
float3 curvePos(float3 thePosition){
	float3 result = float3(0,0,0);
		
	float3 myCurvePoint1 = curveAtPoint(thePosition.x);
	float3 myCurvePoint2 = curveAtPoint(thePosition.x + 1);
	
	float3 myCurveDirection = normalize(myCurvePoint2 - myCurvePoint1);
	float3 myAxis1 = normalize(cross(myCurveDirection, float3(0,0,1)));
	float3 myAxis2 = normalize(cross(myCurveDirection, myAxis1));
	
	float3 myPoint = (sin(thePosition.z) * myAxis1 + cos(thePosition.z) * myAxis2) * radius * thePosition.y;
	
	return myCurvePoint1 + myPoint;
}

void main(
	in float4 iPosition : TEXCOORD0,
	in float4 iInfo : TEXCOORD1,
	in float4 iVelocity : TEXCOORD2,
	in float4 iColor : TEXCOORD3, 
	out float4 oPosition : COLOR0,
	out float4 oInfo : COLOR1 ,
	out float4 oVelocity : COLOR2,
	out float4 oColor : COLOR3 
){
	oPosition.xyz = curvePos(iPosition.xyz);
	oPosition.w = iPosition.w;
	oInfo = iInfo;
	oVelocity = iVelocity;
	oColor = iColor;
}

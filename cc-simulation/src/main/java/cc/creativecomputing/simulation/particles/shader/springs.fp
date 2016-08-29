uniform float appWidth;
uniform samplerRECT forceIDs;
uniform samplerRECT particleIDs;
uniform samplerRECT springDataTexture;

uniform samplerRECT positionTexture;
uniform samplerRECT velocityTexture;

uniform float 		deltaTime;
uniform float		make2D = 1;

void main (
	in  float2 texIDs : WPOS,
	out float3 forceA : COLOR0,
	out float3 forceB : COLOR1
){
	/** 
	 * Stores the spring values
	 * x = restLength
	 * y = springConstant
	 * z = damping
	 **/
	float3 springData = texRECT(springDataTexture, texIDs);
	float4 ids = texRECT(particleIDs, texIDs);
	
	float3 positionA = texRECT (positionTexture, ids.xy);
	float3 positionB = texRECT (positionTexture, ids.zw);
	
	float3 velocityA = texRECT (velocityTexture, ids.xy);
	float3 velocityB = texRECT (velocityTexture, ids.zw);
	
	float3 a2b = positionA - positionB;
	float a2bDistance = length(a2b);

	if (a2bDistance == 0) {
		a2b = float3(0,0,0);
	} else {
		a2b /= a2bDistance;
	}

	// spring force is proportional to how much it stretched
	float springForce = -(a2bDistance - springData.x) * springData.y;

	// want velocity along line b/w a & b, damping force is proportional to this
	float3 Va2b = velocityA - velocityB;

	float dampingForce = -springData.z * dot(a2b, Va2b);

	// forceB is same as forceA in opposite direction

	float r = springForce + dampingForce;
	a2b *= r;

	forceA = a2b;
	forceB = -a2b;
}
	           
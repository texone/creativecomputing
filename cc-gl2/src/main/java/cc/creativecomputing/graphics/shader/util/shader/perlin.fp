uniform sampler2D 	permTexture;
uniform sampler2D 	gradTexture;

float3 fade(float3 t){
	return t * t * t * (t * (t * 6 - 15) + 10); // new curve
//	return t * t * (3 - 2 * t); // old curve
}

float4 fade(float4 t){
	return t * t * t * (t * (t * 6 - 15) + 10); // new curve
//	return t * t * (3 - 2 * t); // old curve
}

float perm(float x){
	return tex2D(permTexture, float2(x,0));
}

float grad(float x, float3 p){
	return dot(tex2D(gradTexture, float2(x*16,0)), p);
}

// original version
float inoise(float3 p){
	float3 P = fmod(floor(p), 256.0);	// FIND UNIT CUBE THAT CONTAINS POINT
  	p -= floor(p);                      // FIND RELATIVE X,Y,Z OF POINT IN CUBE.
	float3 f = fade(p);                 // COMPUTE FADE CURVES FOR EACH OF X,Y,Z.

	P = P / 256.0;
	const float one = 1.0 / 256.0;
	
    // HASH COORDINATES OF THE 8 CUBE CORNERS
  	float A = perm(P.x) + P.y;
  	float4 AA;
  	AA.x = perm(A) + P.z;
	AA.y = perm(A + one) + P.z;
  	float B =  perm(P.x + one) + P.y;
  	AA.z = perm(B) + P.z;
  	AA.w = perm(B + one) + P.z;
 
	// AND ADD BLENDED RESULTS FROM 8 CORNERS OF CUBE
  	return lerp( lerp( lerp( grad(perm(AA.x    ), p ),  
                             grad(perm(AA.z    ), p + float3(-1, 0, 0) ), f.x),
                       lerp( grad(perm(AA.y    ), p + float3(0, -1, 0) ),
                             grad(perm(AA.w    ), p + float3(-1, -1, 0) ), f.x), f.y),
                             
                 lerp( lerp( grad(perm(AA.x+one), p + float3(0, 0, -1) ),
                             grad(perm(AA.z+one), p + float3(-1, 0, -1) ), f.x),
                       lerp( grad(perm(AA.y+one), p + float3(0, -1, -1) ),
                             grad(perm(AA.w+one), p + float3(-1, -1, -1) ), f.x), f.y), f.z);
}
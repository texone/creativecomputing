#1.4

uniform sampler2D texture;
uniform int lod;
uniform float texSize;
uniform float alpha;

ivec2 closestNeighbor(float theScale, vec2 mipPosition, float theLod){
	ivec2 result = ivec2(0,0);
	float maxBright = 0.0;
	
	for(int x = -1; x <= 0; x++){
		for(int y = -1; y <= 1; y++){
			float myBrightness = texture2DLod(texture, mipPosition + vec2(float(x) * theScale, float(y) * theScale),theLod).r;
			if(myBrightness  >= maxBright){
				result = ivec2(x,y);
				maxBright = myBrightness;
			}
		}
	}
	return result;
}

vec2 gradient (float theScale, vec2 mipPosition, float theLod) {
	
	float outy     = 1.0*texture2DLod(texture, mipPosition+vec2(-1, -1) * theScale,theLod).r
	+ 2.0*texture2DLod(texture, mipPosition+vec2(0, -1) * theScale,theLod).r
	+ 1.0*texture2DLod(texture, mipPosition+vec2(1, -1) * theScale,theLod).r
	
	-  1.0*texture2DLod(texture, mipPosition+vec2(-1, 1) * theScale,theLod).r
	-  2.0*texture2DLod(texture, mipPosition+vec2(0, 1) * theScale,theLod).r
	-  1.0*texture2DLod(texture, mipPosition+vec2(1, 1) * theScale,theLod).r;
	
	
	float outx     =  1.0*texture2DLod(texture, mipPosition+vec2  (-1, -1) * theScale,theLod).r
	+  2.0*texture2DLod(texture, mipPosition+vec2(-1, 0) * theScale,theLod).r
	+  1.0*texture2DLod(texture, mipPosition+vec2(-1, 1) * theScale,theLod).r
	
	-  1.0*texture2DLod(texture, mipPosition+vec2(1, -1) * theScale,theLod).r
	-  2.0*texture2DLod(texture, mipPosition+vec2(1, 0) * theScale,theLod).r
	-  1.0*texture2DLod(texture, mipPosition+vec2(1, 1) * theScale,theLod).r;
	
	return vec2(outx, outy);
}
void main(){

	vec2 mipPosition = gl_MultiTexCoord0.xy;
	
	for(int i = lod; i >= lod; i--){
		float maxSize = pow(2.0, float(i)) / texSize * 0.5;
		/*
		ivec2 closestNeighbor = closestNeighbor(maxSize, mipPosition, float(lod));
		mipPosition += vec2(
			float(closestNeighbor.x) * maxSize,
			float(closestNeighbor.y) * maxSize
		);*/
		mipPosition = gradient (maxSize, mipPosition, float(lod));
	}
	
	mipPosition -= gl_MultiTexCoord0.xy;
	mipPosition = mipPosition * 15.0;
	//
	//mipPosition *= texSize;
	/*
	float maxSize = pow(2.0, float(lod)) / texSize * 0.5;
	ivec2 closestNeighbor = closestNeighbor(maxSize, gl_MultiTexCoord0.xy, float(lod));
	mipPosition = vec2(
		float(closestNeighbor.x) * 5.0,
		float(closestNeighbor.y) * 5.0
	);
*/
	
	vec4 pos = gl_Vertex;
	pos.xy += mipPosition * 1.0 * gl_MultiTexCoord0.z;
	gl_Position = gl_ModelViewProjectionMatrix * pos;
	gl_FrontColor = vec4(gl_MultiTexCoord0.z, 0.0, 1.0 -gl_MultiTexCoord0.z, alpha);
	//gl_FrontColor = vec4(1.0, 1.0, 1.0, 1.0);
}
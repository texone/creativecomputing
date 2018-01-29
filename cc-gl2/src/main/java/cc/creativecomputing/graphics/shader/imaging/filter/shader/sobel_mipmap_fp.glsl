#version 120
#1.4

uniform sampler2D texture;
uniform int lod;
uniform float textureWidth;
uniform float textureHeight;
uniform float alpha;
uniform bool shift = false;

ivec2 closestNeighbor(float theScale, vec2 mipPosition, float theLod){
	ivec2 result = ivec2(0,0);
	float maxBright = 0.0;
	
	for(int x = -1; x <= 0; x++){
		for(int y = -1; y <= 1; y++){
			float myBrightness = texture2D(texture, mipPosition + vec2(float(x) * theScale, float(y) * theScale),theLod).r;
			if(myBrightness  >= maxBright){
				result = ivec2(x,y);
				maxBright = myBrightness;
			}
		}
	}
	return result;
}

float pointThreshold (vec2 pos, float theLod) {
	float thresh = 0.001;
	float ret = texture2D (texture, pos, theLod).b;
	return (ret>thresh ? ret : 0);
}

vec2 gradient (vec2 theScale, vec2 mipPosition, float theLod) {
	
	float outy = 
	  1.0 * pointThreshold (mipPosition+vec2 (-1, -1)*theScale, theLod)
	+ 2.0 * pointThreshold (mipPosition+vec2 (0, -1) * theScale,theLod)
	+ 1.0 * pointThreshold (mipPosition+vec2 (1, -1) * theScale,theLod)
	
	-  1.0 * pointThreshold (mipPosition+vec2 (-1, 1)* theScale,theLod)
	-  2.0 * pointThreshold (mipPosition+vec2 (0, 1) * theScale,theLod)
	-  1.0 * pointThreshold (mipPosition+vec2 (1, 1) * theScale,theLod);
	
	float outx =  2.0 * pointThreshold (mipPosition+vec2 (-1, 0) * theScale,theLod)
				- 2.0 * pointThreshold (mipPosition+vec2 ( 1, 0) * theScale,theLod)
				+ 1.0 * pointThreshold (mipPosition+vec2 (-1, -1) * theScale,theLod)
				- 1.0 * pointThreshold (mipPosition+vec2 ( 1, -1) * theScale,theLod)
				+ 1.0 * pointThreshold (mipPosition+vec2 (-1, 1) * theScale,theLod)
				- 1.0 * pointThreshold (mipPosition+vec2 ( 1, 1) * theScale,theLod);

	return vec2 (theLod*theLod*outx/lod/lod, theLod*theLod*outy/lod/lod);
}

void main(){
	vec2 mipPosition = gl_TexCoord[0].xy;
	vec2 ret      = vec2(0.0, 0.0);
	
	for (int i=1; i<=lod; i++) {
		vec2 maxSize = 0.5 * pow(2.0, float(i)) / vec2(textureWidth, textureHeight);
		ret += gradient (maxSize, mipPosition, float(i));
	}


	gl_FragData[0] = vec4 (ret, length(ret), 1.0);
	gl_FragData[1] = vec4 (ret*0.5 + vec2(0.5, 0.5), 0.0, 1.0);
}
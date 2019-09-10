
uniform sampler2D depthTex0;
uniform sampler2D depthTex1;

vec3 hsb2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

@CCProperty(name = "depth scale", min = 0, max = 1)
uniform float depthScale;

const vec2 size = vec2(0.01,0.0); 
const vec3 sign = vec3(1.0,0.0,-1.0);

@CCProperty(name = "depth min", min = 0, max = 1)
uniform float depthMin;
@CCProperty(name = "depth max", min = 0, max = 1)
uniform float depthMax;

float depth(sampler2D depthTex, vec2 pos){
	float result =  texture2D(depthTex, pos).x;
	if(result < 0.001)result = 0.0;
	if(result > depthMax)result = 0.0;
	return result;
}

float depth0(vec2 pos){
	return depth(depthTex0, pos);
}

float depth1(vec2 pos){
	return depth(depthTex1, pos);
}

uniform float depthToMeters;
uniform vec2 depthOffset;
uniform vec2 depthFocalLength;
uniform vec2 depthTextureSize;

vec4 deproject(vec2 index, float depth) {
   return vec4((index - depthOffset)/ depthFocalLength * depth, depth, 1.0);
}

uniform vec3 boundMin;
uniform vec3 boundMax;

void main(){
/*
	float depth = float(depth1(gl_TexCoord[0].xy) != 0);
	depth *= float(depth1(gl_TexCoord[0].xy + vec2(0.01,0.0)) != 0);
	depth *= float(depth0(gl_TexCoord[0].xy + vec2(0.0,0.01)) != 0);
	//depth = depth1(gl_TexCoord[0].xy).x;
	*/
	float depth = texture2D(depthTex0, gl_TexCoord[0]).x;
	
	vec2 depthIndices = gl_TexCoord[0].xy * depthTextureSize;

	vec4 position = deproject(depthIndices, depth * depthToMeters)*100 ;
	
	position.xyz *= 1000;

	float inBound = 1;

	inBound *= float(position.x > boundMin.x * 0.01);
	inBound *= float(position.x < boundMax.x * 0.01);
	
	inBound *= float(position.y > boundMin.y * 0.01);
	inBound *= float(position.y < boundMax.y * 0.01);
	
	inBound *= float(position.z > boundMin.z * 0.01);
	inBound *= float(position.z < boundMax.z * 0.01);
	
	gl_FragColor = vec4(inBound, inBound, inBound,1.0);
	
}

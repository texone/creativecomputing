
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



void main(){
	float depth = depth1(gl_TexCoord[0].xy) != 0;
	depth *= depth1(gl_TexCoord[0].xy + vec2(0.01,0.0)) != 0;
	depth *= depth0(gl_TexCoord[0].xy + vec2(0.0,0.01)) != 0;
	
	gl_FragColor = vec4(depth, depth, depth,1.0);
	depth = depth1(gl_TexCoord[0].xy);
	gl_FragColor = vec4(hsb2rgb(vec3(depth * 10.0,1.0,1.0)),1.0);
	
}

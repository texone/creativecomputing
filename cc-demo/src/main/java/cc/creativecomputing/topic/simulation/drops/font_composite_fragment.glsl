uniform sampler2D fontTexture;

@CCProperty(name = "depth scale", min = 0, max = 0.01)
uniform float depthScale;

const vec2 size = vec2(0.01,0.0); 
const vec3 sign = vec3(1.0,0.0,-1.0);

vec3 normal(vec2 pos, vec2 theOffset){
	float s01 = texture2D(fontTexture, pos + theOffset * sign.xy).b;
	float s21 = texture2D(fontTexture, pos + theOffset * sign.zy).b;
	float s10 = texture2D(fontTexture, pos + theOffset * sign.yx).b;
	float s12 = texture2D(fontTexture, pos + theOffset * sign.yz).b;
	
	vec3 va = normalize(vec3(sign.xy * 0.1, s21-s01));
	vec3 vb = normalize(vec3(sign.yx * 0.1, s12-s10));
	return cross(va,vb); 
}

void main(){
	vec2 uv = vec2(gl_TexCoord[0].x, 1- gl_TexCoord[0].y);
	vec4 texColor = texture2D(fontTexture, uv);
	vec3 normal = normal(uv, vec2(0.001,0.001)) * 0.5 +0.5;
	normal.b *= 0.5;
	gl_FragColor = vec4(normal ,texColor.b * 6.5);	
}
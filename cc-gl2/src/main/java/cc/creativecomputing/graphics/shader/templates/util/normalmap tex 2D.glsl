@CCProperty(name = "depth scale", min = 0, max = 1)
uniform float depthScale;

const vec2 size = vec2(0.01,0.0); 
const vec3 sign = vec3(1.0,0.0,-1.0);

vec3 normal(vec2 pos, vec2 theOffset){
	float s01 = texture2D(channel0, pos + theOffset * sign.xy).x;
	float s21 = texture2D(channel0, pos + theOffset * sign.zy).x;
	float s10 = texture2D(channel0, pos + theOffset * sign.yx).x;
	float s12 = texture2D(channel0, pos + theOffset * sign.yz).x;
	
	vec3 va = normalize(vec3(sign.xy * depthScale, s21-s01));
	vec3 vb = normalize(vec3(sign.yx * depthScale, s12-s10));
	return cross(va,vb); 
}
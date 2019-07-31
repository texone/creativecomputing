#1.4

uniform sampler2D texture;
uniform float lod;

@CCProperty(name = "depth scale", min = 0, max = 1)
uniform float depthScale;

//const vec2 size = vec2(0.01,0.0); 
const vec3 sign = vec3(1.0,0.0,-1.0);

float depth(vec2 pos){
	float d = 0;
	
	for(int i = 0;i< 10;i++){
		d += texture2DLod(texture, pos,i);
	}
	d/=10;
	//d*=1.4;
	return d;
}

vec3 normal(vec2 pos, vec2 theOffset){
	float ds = 0.1;
	float s01 = depth(pos + theOffset * sign.xy);
	float s21 = depth(pos + theOffset * sign.zy);
	float s10 = depth(pos + theOffset * sign.yx);
	float s12 = depth(pos + theOffset * sign.yz);
	
	vec3 va = normalize(vec3(sign.xy * ds, s21-s01));
	vec3 vb = normalize(vec3(sign.yx * ds, s12-s10));
	return cross(va,vb); 
}

void main(){
	float d = depth(gl_MultiTexCoord0.xy);	
	vec3 norm = (normal(gl_MultiTexCoord0.xy, vec2(0.01)));
	norm.z = 0;
	norm = normalize(norm);
	norm += 1;
	norm /= 2;
	vec4 col = texture2DLod(texture, gl_MultiTexCoord0.xy,lod);
	vec4 pos = gl_Vertex;
	//pos.z += d * 200.0;
	gl_Position = gl_ModelViewProjectionMatrix * pos;
	gl_FrontColor = vec4(norm,1);//vec4(1.0, 1.0, 1.0, 1.0);
}
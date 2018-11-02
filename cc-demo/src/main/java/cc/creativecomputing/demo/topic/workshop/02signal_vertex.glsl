#version 120


float hash(float p){

	return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
}

vec3 hash(vec3 p){
	p = vec3(
		dot(p, vec3(127.1, 311.7, 74.7)),
		dot(p, vec3(269.5, 183.3, 246.1)),
		dot(p, vec3(113.5, 271.9, 124.6))
	);

	return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
}

// return value noise (in x) and its derivatives (in yzw)
vec4 noise3( in vec3 x ){
    // grid
    vec3 p = floor(x);
    vec3 w = fract(x);
    
    #if 1
    // quintic interpolant
    vec3 u = w*w*w*(w*(w*6.0-15.0)+10.0);
    vec3 du = 30.0*w*w*(w*(w-2.0)+1.0);
    #else
    // cubic interpolant
    vec3 u = w*w*(3.0-2.0*w);
    vec3 du = 6.0*w*(1.0-w);
    #endif    
    
	// gradients
    	vec3 ga = hash( p+vec3(0.0,0.0,0.0) );
    	vec3 gb = hash( p+vec3(1.0,0.0,0.0) );
	vec3 gc = hash( p+vec3(0.0,1.0,0.0) );
	vec3 gd = hash( p+vec3(1.0,1.0,0.0) );
	vec3 ge = hash( p+vec3(0.0,0.0,1.0) );
	vec3 gf = hash( p+vec3(1.0,0.0,1.0) );
	vec3 gg = hash( p+vec3(0.0,1.0,1.0) );
    vec3 gh = hash( p+vec3(1.0,1.0,1.0) );
    
    // projections
    float va = dot( ga, w-vec3(0.0,0.0,0.0) );
    float vb = dot( gb, w-vec3(1.0,0.0,0.0) );
    float vc = dot( gc, w-vec3(0.0,1.0,0.0) );
    float vd = dot( gd, w-vec3(1.0,1.0,0.0) );
    float ve = dot( ge, w-vec3(0.0,0.0,1.0) );
    float vf = dot( gf, w-vec3(1.0,0.0,1.0) );
    float vg = dot( gg, w-vec3(0.0,1.0,1.0) );
    float vh = dot( gh, w-vec3(1.0,1.0,1.0) );
	
    // interpolations
    return vec4( va + u.x*(vb-va) + u.y*(vc-va) + u.z*(ve-va) + u.x*u.y*(va-vb-vc+vd) + u.y*u.z*(va-vc-ve+vg) + u.z*u.x*(va-vb-ve+vf) + (-va+vb+vc-vd+ve-vf-vg+vh)*u.x*u.y*u.z,    // value
                 ga + u.x*(gb-ga) + u.y*(gc-ga) + u.z*(ge-ga) + u.x*u.y*(ga-gb-gc+gd) + u.y*u.z*(ga-gc-ge+gg) + u.z*u.x*(ga-gb-ge+gf) + (-ga+gb+gc-gd+ge-gf-gg+gh)*u.x*u.y*u.z +   // derivatives
                 du * (vec3(vb,vc,ve) - va + u.yzx*vec3(va-vb-vc+vd,va-vc-ve+vg,va-vb-ve+vf) + u.zxy*vec3(va-vb-ve+vf,va-vb-vc+vd,va-vc-ve+vg) + u.yzx*u.zxy*(-va+vb+vc-vd+ve-vf-vg+vh) ));
}

float noise(float i){
	return noise3(vec3(i,0,0)).x ;
}

float saw(float i){
	return fract(i) * 2 - 1;
}

float rect(float i){
	return float(fract(i) > 0.5) * 2 - 1;
}

float tri(float i, float top){
	i = fract(i);
	
	float rise = (1 - top) / 2;
	return 
		i / rise * float(i < rise) + 
		float(i > rise && i < 1 - rise) + 
		float(i > 1 - rise) * (1 - (i - (1 - rise)) / rise);
}

float octavedNoise(float d){
	int octaves = 3;
	float lacunarity = 2;
	float gain = 0.5;

	float result = 0;
	float freq = 1;
	float fallOff = 1;
	for(int i = 0; i <= octaves;i++){
		result += noise(d * i * freq) * fallOff;
		freq *= lacunarity;
		fallOff *= gain;
	}
	return  result;
}

void main(){
	vec4 vert = vec4(0);
	vert.xyz = gl_Vertex.xyz;
	vert.x *= 200;
	vert.x-=100;
	vert.y = pow(sin(gl_Vertex.x * 20) / 2 + 0.5, 20) * 50;
	//vert.y = rect(gl_Vertex.x * 4) * 50;
	vert.w = gl_Vertex.w;
	gl_Position = gl_ModelViewProjectionMatrix * vert;
	gl_FrontColor = gl_Color;
}
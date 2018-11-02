#version 120

@CCProperty(name = size, min = 0, max = 1000)
uniform float size;

@CCProperty(name = blend, min = 0, max = 1)
uniform float blend;

vec3 grid(){
	float d = gl_Vertex.x;
	vec3 vert = vec3(0);
	vert.x = mod(d * size * size, size);
	vert.y = floor(d * size);
	vert.xy -= size * 0.5;
	return vert;
}

const float PI = 3.1415926535897932384626433832795;

vec3 circle(){
	float d = gl_Vertex.x;
	float r = gl_Vertex.y;
	d += r;
	vec3 vert = vec3(0);
	vert.x = cos(d * PI * 2) * size * 0.5;
	vert.y = sin(d* PI * 2) * size * 0.5;
	return vert;
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
vec4 noise( in vec3 x ){
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

@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;

vec4 octavedNoise(in vec3 s){
	float myScale = scale;
	float myFallOff = gain;
	
	int myOctaves = int(floor(octaves)); 
	vec4 myResult = vec4(0.);  
	float myAmp = 0.;
	
	for(int i = 0; i < myOctaves;i++){
		vec4 noiseVal = noise(s * myScale);
		myResult += noiseVal * myFallOff;
		myAmp += myFallOff;
		myFallOff *= gain;
		myScale *= lacunarity;
	}
	float myBlend = octaves - float(myOctaves);
	
	myResult += noise(s * myScale) * myFallOff * myBlend;   
	myAmp += myFallOff * myBlend;
	
	if(myAmp > 0.0){
		myResult /= myAmp;
	}
	
	return myResult;
}

uniform float time;

vec3 noised(){
	float d = gl_Vertex.x * 1000;
	float r = gl_Vertex.y * 100;
	//d += r;

	vec4 n = octavedNoise(vec3(d,d + 1000, r +time * 5));
	return n.xyz * vec3(size* 3,size,size ) * 0.5;
}

float saturate(float d){
	return max(min(d,1),0);
}

float ease(float d){
	return smoothstep(0,1,d);
}

void main(){
	vec4 vert = vec4(0);
	float blendRadius = 0.1 * blend;
	float d = gl_Vertex.x;
	float r = gl_Vertex.y;
	float myBlend = saturate(blend * 4 - d - r);
	//float myBlend = saturate(mix(-d, d+1, blend / 0.5));
	float gridB = ease(saturate(1 - myBlend * 2));
	float noiseB = ease(1 - abs((myBlend - 0.5) * 2));
	float circleB = ease(saturate((myBlend - 0.5) * 2));
	myBlend = smoothstep(0,1,myBlend);
	vert.xyz = grid() * gridB;
	vert.xyz += noised() * noiseB;
	vert.xyz += circle() * circleB;
	//vert.xyz = noised();
	vert.w = gl_Vertex.w;
	gl_Position = gl_ModelViewProjectionMatrix * vert;
	gl_FrontColor = gl_Color;
}
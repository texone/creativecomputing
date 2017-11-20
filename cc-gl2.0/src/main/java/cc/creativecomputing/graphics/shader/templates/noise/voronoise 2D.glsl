// Created by inigo quilez - iq/2014
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// This is a procedural pattern that has 2 parameters, that generalizes cell-noise,
// perlin-noise and voronoi, all of which can be written in terms of the former as:
//
// cellnoise(x) = pattern(0,0,x)
// perlin(x) = pattern(0,1,x)
// voronoi(x) = pattern(1,0,x)
//
// From this generalization of the three famouse patterns, a new one (which I call
// "Voronoise") emerges naturally. It's like perlin noise a bit, but within a jittered
// grid like voronoi):
//
// voronoise(x) = pattern(1,1,x)
//
// Not sure what one would use this generalization for, because it's slightly slower
// than perlin or voronoise (and certainly much slower than cell noise), and in the
// end as a shading TD you just want one or another depending of the type of visual
// features you are looking for, I can't see a blending being needed in real life.
// But well, if only for the math fun it was worth trying. And they say a bit of
// mathturbation can be healthy anyway!
// Use the mouse to blend between different patterns:
// cell noise    u=0,v=0
// voronoi      u=1,v=0
// perlin noise u=0,v1=
// voronoise    u=1,v=1
// More info here: http://iquilezles.org/www/articles/voronoise/voronoise.htm

float hash1(float n) {
	return fract(sin(n) * 43758.5453);
}

vec2 hash2(vec2  p) {
	p = vec2(
		dot(p,vec2(127.1, 311.7)),
		dot(p,vec2(269.5, 183.3))
	); 
	return fract(sin(p) * 43758.5453);
}

vec3 hash3(vec2 p){
	vec3 q = vec3(
		dot(p,vec2(127.1, 311.7)),
		dot(p,vec2(269.5, 183.3)),
		dot(p,vec2(419.2, 371.9))
	);
    return fract(sin(q) * 43758.5453);
}

@CCProperty(name = "u", min = 0, max = 1)
uniform float u;

@CCProperty(name = "v", min = 0, max = 1)
uniform float v;


float voronoise( in vec2 x ){
    vec2 p = floor(x);
    vec2 f = fract(x);
		
	float k = 1.0+63.0*pow(1.0-v,4.0);
	
	float va = 0.0;
	float wt = 0.0;
    for( int j=-2; j<=2; j++ )
    for( int i=-2; i<=2; i++ ){
        vec2 g = vec2( float(i),float(j) );
		vec3 o = hash3( p + g )*vec3(u,u,1.0);
		vec2 r = g - f + o.xy;
		float d = dot(r,r);
		float ww = pow( 1.0-smoothstep(0.0,1.414,sqrt(d)), k );
		va += o.z*ww;
		wt += ww;
    }
	
    return va/wt;
}

@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;

float octavedNoise(in vec2 s){
	float myScale = scale;
	float myFallOff = gain;
	
	int myOctaves = int(floor(octaves)); 
	float myResult = 0.; 
	float myAmp = 0.;
	
	for(int i = 0; i < myOctaves;i++){
		float noiseVal = voronoise(s * myScale);
		myResult += noiseVal * myFallOff;
		myAmp += myFallOff;
		myFallOff *= gain;
		myScale *= lacunarity;
	}
	float myBlend = octaves - float(myOctaves);
	
	myResult += voronoise(s * myScale) * myFallOff * myBlend;   
	myAmp += myFallOff * myBlend;
	
	if(myAmp > 0.0){
		myResult /= myAmp;
	}
	
	return myResult;
}

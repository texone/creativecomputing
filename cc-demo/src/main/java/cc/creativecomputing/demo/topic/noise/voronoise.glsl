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
	return fract(sin(n)*43758.5453);
}

vec2 hash2(vec2  p) {
	p = vec2(
		dot(p,vec2(127.1,311.7)),
		dot(p,vec2(269.5,183.3))
	); 
	return fract(sin(p)*43758.5453);
}


//@CCProperty(name = "time", min = 10, max = 30)
uniform float time;

@CCProperty(name = "euclidian", min = 0, max = 1)
uniform float euclidian;
@CCProperty(name = "manhattan", min = 0, max = 1)
uniform float manhattan;
@CCProperty(name = "triangular", min = 0, max = 1)
uniform float triangular;

vec4 voronoi( in vec2 x ){
	vec2 n = floor( x );
	vec2 f = fract( x );

	vec3 m = vec3( 8.0 );
	float m2 = 8.0;
	
	for( int j=-2; j<=2; j++ ){
		for( int i=-2; i<=2; i++ ){
			vec2 g = vec2( float(i),float(j) );
			vec2 o = hash2( n + g );

			// animate
			o = 0.5 + 0.5*sin( time * 0.1 + 6.2831*o );    
 			vec2 r = g - f + o;

			// euclidean		
			vec2 d0 = vec2( sqrt(dot(r,r)), 1.0 );
			// manhattan		
			vec2 d1 = vec2( 0.71*(abs(r.x) + abs(r.y)), 1.0 );
			// triangular		
			vec2 d2 = vec2(
				max(abs(r.x)*0.866025+r.y*0.5,-r.y), 
				step(0.0,0.5*abs(r.x)+0.866025*r.y)*(1.0+step(0.0,r.x))
			);

			vec2 d = d0;   

			d = d0 * euclidian;
			d += d1 * manhattan;
			d += d2 * triangular;

			d /= (euclidian + manhattan + triangular);
		
			if( d.x<m.x ){
				m2 = m.x;
				m.x = d.x;
				m.y = hash1( dot(n+g,vec2(7.0,113.0) ) );
				m.z = d.y;
			}else if( d.x<m2 ){
				m2 = d.x;
			}

		}
	}
	return vec4( m, m2-m.x );
}

@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 5)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;

@CCProperty(name = "octave random", min = 0, max = 1)
uniform float octaveRandom; 

vec4 octavedNoise(in vec2 s){
    float myScale = scale;
    float myFallOff = gain;
    
    int myOctaves = int(ceil(octaves));
    vec4 myResult = vec4(0.); 
    float myAmp = 0.;

    float myRandomOn = 1.;
    float myRandomOnPre = 1.;
    float myMaxOct = octaves;
    
    for(int i = 0; i < myOctaves;i++){
    	
    	   float myBlend = myRandomOn * clamp(octaves - float(i),0.,1.);
        vec4 noiseVal = voronoi(s * myScale) * myBlend;
	   
	   if(noiseVal.y < (octaveRandom * 0.5) && myRandomOn > 0.){
	   	myRandomOn = 0.;
	   }
	   if(noiseVal.y < (octaveRandom * 0.5- 0.1)  && myRandomOn > 0.){
	   	myRandomOnPre = 0.;
	   }
        noiseVal *= myBlend;
        myResult += noiseVal * myFallOff;
        myAmp += myFallOff * myBlend;
        myFallOff *= gain;
        myScale *= lacunarity;
    }
    
    if(myAmp > 0.0){
        myResult /= myAmp;
    }
    
    return myResult;
}

uniform vec2 iResolution;
uniform sampler2D tex0; 
uniform sampler2D tex1; 
uniform float randomOffset;

uniform float blendARefraction;
uniform float blendBRefraction;
uniform float blendAB;
uniform float blendRandom;
@CCProperty(name = "bla3", min = 0, max = 3)
uniform vec4 fBlends;


uniform sampler2D shaper;

void main(){
	vec2 uv = gl_FragCoord.xy / iResolution.xx;
	vec2 texUV = gl_FragCoord.xy / iResolution.xy;
	texUV = vec2(texUV.x, 1.0 - texUV.y); 
    
	vec4 fxyzw = octavedNoise( 24.0*uv); 
    
	float f =  fxyzw.x * fBlends.x;
	f +=  fxyzw.y * fBlends.y;
	f +=  fxyzw.z * fBlends.z;
	f +=  fxyzw.w * fBlends.w;
	f /= (fBlends.x + fBlends.y + fBlends.z + fBlends.w);

	vec2 dir = normalize(vec2(cos(f * 6.2), sin(f * 6.2)));// / iResolution * randomOffset * 20.0;
	
	vec2 outputDir = dir * 0.5 + 0.5;
	vec2 texUVDir = dir / iResolution * randomOffset * 20.0;
	//texUV += dir;


	 
    float o = f;//pow(fxyzw.x / pow((1. - fxyzw.y) , .3),1.0);
    o = texture2D(shaper, vec2(o,0.5)).r;
    gl_FragData[0] = vec4(o,o,o,1.);
 }


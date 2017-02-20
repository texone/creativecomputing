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

// ell noise    u=0,v=0
// voronoi      u=1,v=0
// perlin noise u=0,v1=
// voronoise    u=1,v=1

// More info here: http://iquilezles.org/www/articles/voronoise/voronoise.htm

vec3 hash3( vec2 p )
{
    vec3 q = vec3( dot(p,vec2(127.1,311.7)),
                  dot(p,vec2(269.5,183.3)),
                  dot(p,vec2(419.2,371.9)) );
    return fract(sin(q)*43758.5453);
}

float noise01( in vec2 x, vec3 uv )
{
    vec2 p = floor(x);
    vec2 f = fract(x);
    
    float k = 1.0+63.0*pow(1.0-uv.y,4.0);
    
    float va = 0.0;
    float wt = 0.0;
    for( int j=-2; j<=2; j++ )
        for( int i=-2; i<=2; i++ )
        {
            vec2 g = vec2( float(i),float(j) );
            vec3 o = hash3( p + g )*vec3(uv.x,uv.x,1.0);
            vec2 r = g - f + o.xy;
            float d = dot(r,r);
            float ww = pow( 1.0-smoothstep(0.0,1.6,sqrt(d)), k );
            va += o.z*ww;
            wt += ww;
        }
    
    return va/wt;
}

uniform float scale;
uniform float gain;
uniform float octaves;
uniform float lacunarity;

float octavedNoise(in vec2 s, vec3 d){
    float myScale = scale;
    float myFallOff = gain;
    
    int myOctaves = int(floor(octaves));
    float myResult = 0.;
    float myAmp = 0.;
    
    for(int i = 0; i < myOctaves;i++){
        float noiseVal = noise01(s * myScale, d);
        myResult += noiseVal * myFallOff;
        myAmp += myFallOff;
        myFallOff *= gain;
        myScale *= lacunarity;
    }
    float myBlend = octaves - float(myOctaves);
    
    myResult += noise01(s * myScale, d) * myFallOff * myBlend; 
    myAmp += myFallOff * myBlend;
    
    if(myAmp > 0.0){
        myResult /= myAmp;
    }
    
    return myResult;
}

uniform vec2 iResolution;
uniform vec3 noiseBlend;
uniform sampler2D tex0; 
uniform sampler2D tex1; 
uniform float randomOffset;

uniform float blend;
uniform float blendRandom;

uniform float colorScale;
uniform float colorShift;

uniform float brightnessScale;
uniform float brightnessShift;

uniform vec2 noiseOffset;

void main(){
    vec2 uv = gl_FragCoord.xy / iResolution.xx;
    vec2 texUV = gl_FragCoord.xy / iResolution.xy;
    texUV = vec2(texUV.x, 1.0 - texUV.y); 
    
    float f = octavedNoise( 24.0*uv + noiseOffset, noiseBlend);
    
    vec2 dir = normalize(vec2(cos(f * 6.2), sin(f * 6.2))) / iResolution * randomOffset * 10.;
	texUV += dir;
	
	texUV.x *= colorScale;
	texUV.x += colorShift;
	
	texUV.y *= brightnessScale;
	texUV.y += brightnessShift;  
	vec4 color0 = texture2D(tex0,texUV);
	vec4 color1 = texture2D(tex0,texUV + vec2(0.5,0.0));

	float noiseBlend = smoothstep(blend - 0.05,blend,f);   
	
	vec4 color = mix(color0, color1, noiseBlend); 
    
    gl_FragColor = color;//vec4(f,f,f,1);//vec4( dir, 0.0, 1.0 );
}



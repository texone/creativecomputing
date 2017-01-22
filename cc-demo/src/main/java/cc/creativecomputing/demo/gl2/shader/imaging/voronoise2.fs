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

float hash1( float n ) { return fract(sin(n)*43758.5453); }
vec2  hash2( vec2  p ) { p = vec2( dot(p,vec2(127.1,311.7)), dot(p,vec2(269.5,183.3)) ); return fract(sin(p)*43758.5453); }


uniform vec3 noiseBlend;
uniform float time;

vec4 voronoi( in vec2 x )
{
    vec2 n = floor( x );
    vec2 f = fract( x );

	vec3 m = vec3( 8.0 );
	float m2 = 8.0;
    for( int j=-2; j<=2; j++ )
    for( int i=-2; i<=2; i++ )
    {
        vec2 g = vec2( float(i),float(j) );
        vec2 o = hash2( n + g );

		// animate
        o = 0.5 + 0.5*sin( time * 0. + 6.2831*o );    
 
		vec2 r = g - f + o;

        // euclidean		
		vec2 d0 = vec2( sqrt(dot(r,r)), 1.0 );
        // manhattam		
		vec2 d1 = vec2( 0.71*(abs(r.x) + abs(r.y)), 1.0 );
        // triangular		
		vec2 d2 = vec2( max(abs(r.x)*0.866025+r.y*0.5,-r.y), 
				        step(0.0,0.5*abs(r.x)+0.866025*r.y)*(1.0+step(0.0,r.x)) );

		vec2 d = d0;   

		d = d0 * noiseBlend.x;
		d += d1 * noiseBlend.y;
		d += d2 * noiseBlend.z;

		d /= (noiseBlend.x + noiseBlend.y + noiseBlend.z);
		
        if( d.x<m.x )
        {
			m2 = m.x;
            m.x = d.x;
            m.y = hash1( dot(n+g,vec2(7.0,113.0) ) );
			m.z = d.y;
        }
		else if( d.x<m2 )
		{
			m2 = d.x;
		}

    }
    return vec4( m, m2-m.x );
}

uniform float scale;
uniform float gain;
uniform float octaves;
uniform float lacunarity;

vec4 octavedNoise(in vec2 s){
    float myScale = scale;
    float myFallOff = gain;
    
    int myOctaves = int(floor(octaves));
    vec4 myResult = vec4(0.); 
    float myAmp = 0.;
    
    for(int i = 0; i < myOctaves;i++){
        vec4 noiseVal = voronoi(s * myScale);
        myResult += noiseVal * myFallOff;
        myAmp += myFallOff;
        myFallOff *= gain;
        myScale *= lacunarity;
    }
    float myBlend = octaves - float(myOctaves);
    
    myResult += voronoi(s * myScale) * myFallOff * myBlend;   
    myAmp += myFallOff * myBlend;
    
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

uniform vec4 fBlends;

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

	vec2 dir = normalize(vec2(cos(f * 6.2), sin(f * 6.2))) / iResolution * randomOffset * 20.0;
	//texUV += dir;

	float noiseBlendARefraction = smoothstep(f,f + blendRandom,blendARefraction * (1.0 + blendRandom)); 
	float noiseBlendBRefraction = smoothstep(f,f + blendRandom,blendBRefraction * (1.0 + blendRandom)); 
	float noiseBlendAB = smoothstep(f,f + blendRandom,blendAB * (1.0 + blendRandom)); 
	

	vec4 color0 = texture2D(tex0,texUV + dir * noiseBlendARefraction); 
	vec4 color1 = texture2D(tex1,texUV + dir * noiseBlendBRefraction);
	
	vec4 color = mix(color0, color1, noiseBlendAB);   
    
    gl_FragColor = color;//vec4(f);//vec4(texUV,0.0,1.0);vec4(f.xyz,1);//vec4( dir, 0.0, 1.0 );
}
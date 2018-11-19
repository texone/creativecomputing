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

float eval(vec2 fracCoord){
	vec2 uv = fracCoord.xy / iResolution.xx;
	vec4 fxyzw = octavedNoise( 24.0*uv); 
    
	float f =  fxyzw.x * fBlends.x;
	f +=  fxyzw.y * fBlends.y;
	f +=  fxyzw.z * fBlends.z;
	f +=  fxyzw.w * fBlends.w;
	f /= (fBlends.x + fBlends.y + fBlends.z + fBlends.w);
	
	f = texture2D(shaper, vec2(f,0.5)).r;
	return f;
}

vec3 normal(vec2 coords){

	vec2 xOffset = vec2(1.0, 0.0);
	vec2 yOffset = vec2(0.0, 1.0);
     
	float s01 = eval(coords.xy - xOffset);
	float s21 = eval(coords.xy + xOffset);
	float s10 = eval(coords.xy - yOffset);
	float s12 = eval(coords.xy + yOffset); 
    
	vec3 va = normalize(vec3(0.01,0,(s21-s01)));
	vec3 vb = normalize(vec3(0,0.01,s12-s10));
	return cross(va,vb);
}

/**
 * Lighting contribution of a single point light source via Phong illumination.
 * 
 * The vec3 returned is the RGB color of the light's contribution.
 *
 * k_a: Ambient color
 * k_d: Diffuse color
 * k_s: Specular color
 * alpha: Shininess coefficient
 * p: position of point being lit
 * eye: the position of the camera
 * lightPos: the position of the light
 * lightIntensity: color/intensity of the light
 *
 * See https://en.wikipedia.org/wiki/Phong_reflection_model#Description
 */
vec3 phongContribForLight(vec3 k_d, vec3 k_s, float alpha, vec3 p, vec3 n, vec3 eye,
                          vec3 lightPos, vec3 lightIntensity) {
    vec3 N = n;
    vec3 L = normalize(lightPos - p);
    vec3 V = normalize(eye - p);
    vec3 R = normalize(reflect(-L, N));
    
    float dotLN = dot(L, N);
    float dotRV = dot(R, V);
    
    if (dotLN < 0.0) {
        // Light not visible from this point on the surface
        return vec3(0.0, 0.0, 0.0);
    } 
    
    if (dotRV < 0.0) {
        // Light reflection in opposite direction as viewer, apply only diffuse
        // component
        return lightIntensity * (k_d * dotLN);
    }
    return lightIntensity * (k_d * dotLN + k_s * pow(dotRV, alpha));
}

uniform float iTime;
/**
 * Lighting via Phong illumination.
 * 
 * The vec3 returned is the RGB color of that point after lighting is applied.
 * k_a: Ambient color
 * k_d: Diffuse color
 * k_s: Specular color
 * alpha: Shininess coefficient
 * p: position of point being lit
 * eye: the position of the camera
 *
 * See https://en.wikipedia.org/wiki/Phong_reflection_model#Description
 */
vec3 phongIllumination(vec3 k_a, vec3 k_d, vec3 k_s, float alpha, vec3 p, vec3 n, vec3 eye) {
    const vec3 ambientLight = 0.5 * vec3(1.0, 1.0, 1.0);
    vec3 color = ambientLight * k_a;
    
    vec3 light1Pos = vec3(4.0 * sin(iTime),
                          2.0,
                          4.0 * cos(iTime));
    vec3 light1Intensity = vec3(0.8, 0.4, 0.4);
    
    color += phongContribForLight(k_d, k_s, alpha, p,n, eye,
                                  light1Pos,
                                  light1Intensity);
    
    vec3 light2Pos = vec3(20.0 * sin(0.37 * iTime),
                          20.0 * cos(0.37 * iTime),
                          2.0);
    vec3 light2Intensity = vec3(0.5);
    
    color += phongContribForLight(k_d, k_s, alpha, p,n, eye,
                                  light2Pos,
                                  light2Intensity);    
                                  
    return color; 
}

void main(){
	float f = eval(gl_FragCoord.xy);
	vec3 norm = normal(gl_FragCoord);

	// The closest point on the surface to the eyepoint along the view ray
	
	vec3 eye = vec3(0.0, 0.0, 15.0);
	vec3 p = vec3(gl_FragCoord.xy * 0.15,f *100);
	//p += noisef(p * 9. +vec3(xOffset, 0.0,zOffset)) * dir * 0.5; 
	//p += noisef(p * 200. +vec3(xOffset, 0.0,zOffset)) * dir * 0.1; 
    
    	vec3 K_a = vec3(0.0, 0.0, 1.0);
    	vec3 K_d = vec3(1.5, 0.9, 1.);
    	vec3 K_s = vec3(1.0, 1.0, 1.0); 
    	float shininess = 10.0;
    
    vec3 color = phongIllumination(K_a, K_d, K_s, shininess, p,norm, eye);
    
	gl_FragData[0] = vec4(color ,1.);
 }


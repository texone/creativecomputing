const int MAX_MARCHING_STEPS = 50;
const float MIN_DIST = 10.0;
const float MAX_DIST = 50.0;
const float EPSILON = 0.05;

/**
 * Signed distance function for a sphere centered at the origin with radius r.
 */
float sphereSDF(vec3 p, float r) {
	return length(p) - r;
}

float cylinderSDF( vec3 p, vec3 c ){
	return length(p.yz - c.xy) - c.z;
}

// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

//#define HIGH_QUALITY_NOISE

uniform sampler2D randomTexture;

float noiset(vec3 x){
	vec3 p = floor(x);
	vec3 f = fract(x);
	f = f*f*(3.0-2.0*f);
#ifndef HIGH_QUALITY_NOISE
	vec2 uv = (p.xy+vec2(37.0,17.0)*p.z) + f.xy;
	vec2 rg = texture2D( randomTexture, (uv+ 0.5)/256.0).yx;
#else
	vec2 uv = (p.xy+vec2(37.0,17.0)*p.z);
	vec2 rg1 = texture2D( randomTexture, (uv+ vec2(0.5,0.5))/256.0).yx;
	vec2 rg2 = texture2D( randomTexture, (uv+ vec2(1.5,0.5))/256.0).yx;
	vec2 rg3 = texture2D( randomTexture, (uv+ vec2(0.5,1.5))/256.0).yx;
	vec2 rg4 = texture2D( randomTexture, (uv+ vec2(1.5,1.5))/256.0).yx;
	vec2 rg = mix( mix(rg1,rg2,f.x), mix(rg3,rg4,f.x), f.y );
#endif	
	return mix( rg.x, rg.y, f.z ) * 2. - 1.;
}

// The MIT License
// Copyright © 2013 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

vec3 hash(vec3 p){
	p = vec3(
		dot(p, vec3(127.1, 311.7, 74.7)),
		dot(p, vec3(269.5, 183.3, 246.1)),
		dot(p, vec3(113.5, 271.9, 124.6))
	);

	return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
}

float noisef( in vec3 p){
	vec3 i = floor( p );
	vec3 f = fract( p );
	vec3 u = f*f*(3.0-2.0*f); 

	return mix( mix( mix( dot( hash( i + vec3(0.0,0.0,0.0) ), f - vec3(0.0,0.0,0.0) ), 
                           dot( hash( i + vec3(1.0,0.0,0.0) ), f - vec3(1.0,0.0,0.0) ), u.x),
                      mix( dot( hash( i + vec3(0.0,1.0,0.0) ), f - vec3(0.0,1.0,0.0) ), 
                           dot( hash( i + vec3(1.0,1.0,0.0) ), f - vec3(1.0,1.0,0.0) ), u.x), u.y),
                 mix( mix( dot( hash( i + vec3(0.0,0.0,1.0) ), f - vec3(0.0,0.0,1.0) ), 
                           dot( hash( i + vec3(1.0,0.0,1.0) ), f - vec3(1.0,0.0,1.0) ), u.x),
                      mix( dot( hash( i + vec3(0.0,1.0,1.0) ), f - vec3(0.0,1.0,1.0) ), 
                           dot( hash( i + vec3(1.0,1.0,1.0) ), f - vec3(1.0,1.0,1.0) ), u.x), u.y), u.z );
}

// The MIT License
// Copyright © 2017 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


vec3 hash3(in vec3 p) {
    vec3 q = vec3(dot(p, vec3(127.1, 311.7, 189.2)),
                  dot(p, vec3(269.5, 183.3, 324.7)),
                  dot(p, vec3(419.2, 371.9, 128.5)));
    return fract(sin(q) * 43758.5453);
}

@CCProperty(name = "u", min = 0, max = 1)
uniform float u;

@CCProperty(name = "v", min = 0, max = 1)
uniform float v;

float voronoise(in vec3 x) {
    // adapted from IQ's 2d voronoise:
    // http://www.iquilezles.org/www/articles/voronoise/voronoise.htm
    vec3 p = floor(x);
    vec3 f = fract(x);

    float s = 1.0 + 31.0 * v;
    float va = 0.0;
    float wt = 0.0;
    for (int k=-2; k<=1; k++)
    for (int j=-2; j<=1; j++)
    for (int i=-2; i<=1; i++) {
        vec3 g = vec3(float(i), float(j), float(k));
        vec3 o = hash3(p + g) * vec3(u,u,1.0);
        vec3 r = g - f + o + 0.5;
        float d = dot(r, r);
        float w = pow(1.0 - smoothstep(0.0, 1.414, sqrt(d)), s);
        va += o.z * w;
        wt += w;
     }
     return (va / wt) * 2. -1.;
}


@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;

float octavedNoise(in vec3 s){ 
	float myScale = scale;
	float myFallOff = gain;
	
	int myOctaves = int(floor(octaves)); 
	float myResult = 0.; 
	float myAmp = 0.;
	
	for(int i = 0; i < myOctaves;i++){
		float noiseVal = noisef(s * myScale); 
		myResult += noiseVal * myFallOff;
		myAmp += myFallOff;
		myFallOff *= gain;
		myScale *= lacunarity;
	}
	float myBlend = octaves - float(myOctaves);
	
	myResult += noisef(s * myScale) * myFallOff * myBlend;   
	myAmp += myFallOff * myBlend;
	
	if(myAmp > 0.0){
		myResult /= myAmp;
	}
	
	return myResult;
}

@CCProperty(name = "x offset", min = 0, max = 10)
uniform float xOffset;
@CCProperty(name = "z offset", min = 0, max = 10)
uniform float zOffset;

float fbm( vec3 p )
{
	p += vec3(xOffset, 2.0,zOffset);
    float f;
    f  = 0.5000*noisef( p ); p = p*2.02;
    f += 0.2500*noisef( p ); p = p*2.03;
    f += 0.1250*noisef( p );
    return f;
}


@CCProperty(name = "cylinder", min = 0, max = 1)
uniform float cylinder;
@CCProperty(name = "noise", min = 0, max = 1)
uniform float noise;

float objectSDF(vec3 samplePoint){
	return sphereSDF(samplePoint, 1) * cylinder + fbm(samplePoint*vec3(1.0, 1., 1.) ) * noise; 
} 
 
float twist( vec3 p ) 
{
    float c = cos(0.5*p.x);
    float s = sin(0.5*p.x);
    mat2  m = mat2(c,-s,s,c);
    return objectSDF(vec3(p.x,m*p.yz));
}



/**
 * Signed distance function describing the scene.
 * 
 * Absolute value of the return value indicates the distance to the surface.
 * Sign indicates whether the point is inside or outside the surface,
 * negative indicating inside.
 */
float sceneSDF(vec3 samplePoint) { 
	return twist(samplePoint);    
}

/**
 * Return the shortest distance from the eyepoint to the scene surface along
 * the marching direction. If no part of the surface is found between start and end,
 * return end.
 * 
 * eye: the eye point, acting as the origin of the ray
 * marchingDirection: the normalized direction to march in
 * start: the starting distance away from the eye
 * end: the max distance away from the ey to march before giving up
 */
float shortestDistanceToSurface(vec3 eye, vec3 marchingDirection, float start, float end) {
	float depth = start;
	for (int i = 0; i < MAX_MARCHING_STEPS; i++) {
		float dist = sceneSDF(eye + depth * marchingDirection);
		if (dist < EPSILON) {
			return depth;
		}
		depth += dist;
		if (depth >= end) {
			return end;
		}
	}
	return end;
}
            

/**
 * Return the normalized direction to march in from the eye point for a single pixel.
 * 
 * fieldOfView: vertical field of view in degrees
 * size: resolution of the output image
 * fragCoord: the x,y coordinate of the pixel in the output image
 */
vec3 rayDirection(float fieldOfView, vec2 size, vec2 fragCoord) {
	vec2 xy = fragCoord - size / 2.0;
	float z = size.y / tan(radians(fieldOfView) / 2.0);
	return normalize(vec3(xy, -z));
}

/**
 * Using the gradient of the SDF, estimate the normal on the surface at point p.
 */
vec3 estimateNormal(vec3 p) {
	return normalize(vec3(
		sceneSDF(vec3(p.x + EPSILON, p.y, p.z)) - sceneSDF(vec3(p.x - EPSILON, p.y, p.z)),
		sceneSDF(vec3(p.x, p.y + EPSILON, p.z)) - sceneSDF(vec3(p.x, p.y - EPSILON, p.z)),
		sceneSDF(vec3(p.x, p.y, p.z  + EPSILON)) - sceneSDF(vec3(p.x, p.y, p.z - EPSILON))
    ));
}

uniform vec2 iResolution; 

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
vec3 phongContribForLight(vec3 k_d, vec3 k_s, float alpha, vec3 p, vec3 eye,
                          vec3 lightPos, vec3 lightIntensity) {
    vec3 N = estimateNormal(p);
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
vec3 phongIllumination(vec3 k_a, vec3 k_d, vec3 k_s, float alpha, vec3 p, vec3 eye) {
    const vec3 ambientLight = 0.5 * vec3(1.0, 1.0, 1.0);
    vec3 color = ambientLight * k_a;
    
    vec3 light1Pos = vec3(4.0 * sin(iTime),
                          2.0,
                          4.0 * cos(iTime));
    vec3 light1Intensity = vec3(0.8, 0.4, 0.4);
    
    color += phongContribForLight(k_d, k_s, alpha, p, eye,
                                  light1Pos,
                                  light1Intensity);
    
    vec3 light2Pos = vec3(20.0 * sin(0.37 * iTime),
                          20.0 * cos(0.37 * iTime),
                          2.0);
    vec3 light2Intensity = vec3(0.5);
    
    color += phongContribForLight(k_d, k_s, alpha, p, eye,
                                  light2Pos,
                                  light2Intensity);    
                                  
    return color; 
}

void main(  ){
	vec2 uv = gl_FragCoord.xy;
	//uv += hash3(vec3(floor(uv / 120),0))*320;mod(uv, 100);
	vec3 dir = rayDirection(45.0, iResolution.xy, uv); 
	vec3 eye = vec3(0.0, 0.0, 15.0);
	float dist = shortestDistanceToSurface(eye, dir, MIN_DIST, MAX_DIST);
    
	if (dist > MAX_DIST - EPSILON) {
		// Didn't hit anything
		gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
		return;
	}
    
	// The closest point on the surface to the eyepoint along the view ray
	vec3 p = eye + dist * dir;
	//p += noisef(p * 9. +vec3(xOffset, 0.0,zOffset)) * dir * 0.5; 
	//p += noisef(p * 200. +vec3(xOffset, 0.0,zOffset)) * dir * 0.1; 
    
    vec3 K_a = vec3(0.0, 0.0, 1.0);
    vec3 K_d = vec3(1.5, 0.9, 1.);
    vec3 K_s = vec3(1.0, 1.0, 1.0); 
    float shininess = 10.0;
    
    vec3 color = phongIllumination(K_a, K_d, K_s, shininess, p, eye);
    
	gl_FragColor = vec4(color, 1.0);

	//gl_FragColor = vec4(1);
}
const int MAX_MARCHING_STEPS = 50;
const float MIN_DIST = 10.0;
const float MAX_DIST = 50.0;
const float EPSILON = 0.01;

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

#define HIGH_QUALITY_NOISE

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

float noise( in vec3 p){
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

@CCProperty(name = "x offset", min = 0, max = 10)
uniform float xOffset;
@CCProperty(name = "z offset", min = 0, max = 10)
uniform float zOffset;

float fbm( vec3 p )
{
	p += vec3(xOffset, 2.0,zOffset);
    float f;
    f  = 0.5000*noise( p ); p = p*2.02;
    f += 0.2500*noise( p ); p = p*2.03;
    f += 0.1250*noise( p );
    return f;
}

@CCProperty(name = "cylinder", min = 0, max = 1)
uniform float cylinder;
@CCProperty(name = "noise", min = 0, max = 1)
uniform float noise;

/**
 * Signed distance function describing the scene.
 * 
 * Absolute value of the return value indicates the distance to the surface.
 * Sign indicates whether the point is inside or outside the surface,
 * negative indicating inside.
 */
float sceneSDF(vec3 samplePoint) {    
	return cylinderSDF(samplePoint, vec3(0., 0.,0.3)) * cylinder + fbm(samplePoint*vec3(1.0, 1., 1.)) * noise; 
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

void main(  ){
	vec3 dir = rayDirection(45.0, iResolution.xy, gl_FragCoord.xy); 
	vec3 eye = vec3(0.0, 0.0, 15.0);
	float dist = shortestDistanceToSurface(eye, dir, MIN_DIST, MAX_DIST);
    
	if (dist > MAX_DIST - EPSILON) {
		// Didn't hit anything
		gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
		return;
	}
    
	// The closest point on the surface to the eyepoint along the view ray
	vec3 p = eye + dist * dir;
    
	gl_FragColor = vec4(estimateNormal(p), 1.0);
}
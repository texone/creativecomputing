uniform sampler2D fontTexture;



// The MIT License
// Copyright © 2013 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


// Gradient Noise (http://en.wikipedia.org/wiki/Gradient_noise), not to be confused with
// Value Noise, and neither with Perlin's Noise (which is one form of Gradient Noise)
// is probably the most convenient way to generate noise (a random smooth signal with 
// mostly all its energy in the low frequencies) suitable for procedural texturing/shading,
// modeling and animation.
//
// It produces smoother and higher quality than Value Noise, but it's of course slighty more
// expensive.
//
// The princpiple is to create a virtual grid/latice all over the plane, and assign one
// random vector to every vertex in the grid. When querying/requesting a noise value at
// an arbitrary point in the plane, the grid cell in which the query is performed is
// determined (line 32), the four vertices of the grid are determined and their random
// vectors fetched (lines 37 to 40). Then, the position of the current point under 
// evaluation relative to each vertex is doted (projected) with that vertex' random
// vector, and the result is bilinearly interpolated (lines 37 to 40 again) with a 
// smooth interpolant (line 33 and 35).

vec2 hash( vec2 x ){
    const vec2 k = vec2( 0.3183099, 0.3678794 );
    x = x*k + k.yx;
    return -1.0 + 2.0*fract( 16.0 * k*fract( x.x*x.y*(x.x+x.y)) );
}

float noise( in vec2 p ){
    vec2 i = floor( p );
    vec2 f = fract( p );
	
	vec2 u = f*f*(3.0-2.0*f);

    return mix( mix( dot( hash( i + vec2(0.0,0.0) ), f - vec2(0.0,0.0) ), 
                     dot( hash( i + vec2(1.0,0.0) ), f - vec2(1.0,0.0) ), u.x),
                mix( dot( hash( i + vec2(0.0,1.0) ), f - vec2(0.0,1.0) ), 
                     dot( hash( i + vec2(1.0,1.0) ), f - vec2(1.0,1.0) ), u.x), u.y);
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
		float noiseVal = noise(s * myScale); 
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

//@CCProperty(name = "noise", min = 0, max = 4)
uniform float noiseAmt;
//@CCProperty(name = "alpha amp", min = 0, max = 1)
uniform float alphaAmp;


@CCProperty(name = "smoothing", min = 0, max = 0.1)
uniform float smoothing = 0.1;


@CCProperty(name = "shadow smoothing", min = 0, max = 0.5)
uniform float shadowSmoothing = 0.1; // Between 0 and 0.5
const vec4 shadowColor = vec4(0,0,0,1);

@CCProperty(name = "depth scale", min = 0, max = 1)
uniform float depthScale;

const vec2 size = vec2(0.01,0.0); 
const vec3 sign = vec3(1.0,0.0,-1.0);

float depth(vec2 pos, vec2 fragOffset){
	vec4 myColor = texture2D(fontTexture, pos);
	float noise = octavedNoise((gl_FragCoord.xy + fragOffset * 10) * 0.04);

	float distance = mod(myColor.a * 1.,1.) + noise * noiseAmt * myColor.a ;
	return distance;

}

vec3 normal(vec2 pos, vec2 theOffset){
	float s01 = depth(pos + theOffset * sign.xy, sign.xy).x;
	float s21 = depth(pos + theOffset * sign.zy, sign.zy).x;
	float s10 = depth(pos + theOffset * sign.yx, sign.yx).x;
	float s12 = depth(pos + theOffset * sign.yz, sign.yz).x;
	
	vec3 va = normalize(vec3(sign.xy * depthScale, s21-s01));
	vec3 vb = normalize(vec3(sign.yx * depthScale, s12-s10));
	return cross(va,vb); 
}


void main() {
	vec4 myColor = texture2D(fontTexture, gl_TexCoord[0].xy);
	float noise = octavedNoise(gl_FragCoord.xy * 0.4);

	float distance = mod(myColor.a * 1.,1.)+ noise * noiseAmt * myColor.a;
	float edge = 0.5;
	float alpha = smoothstep(edge - smoothing, edge + smoothing, distance);
	vec4 text = vec4(1.0,1.0,1.0,alpha);

    	distance -= edge;
    distance *= 1;

    vec3 myNormal = normal(gl_TexCoord[0].xy, vec2(0.0001,0.0001)) * 0.5 + 0.5;

    float shadowDistance = texture2D(fontTexture, gl_TexCoord[0].xy - vec2(-0.00)).a;
    float shadowAlpha = smoothstep(0.5 - shadowSmoothing, 0.5 + shadowSmoothing, shadowDistance);
    vec4 shadow = vec4(shadowColor.rgb, shadowColor.a * shadowAlpha * 0.5);

    gl_FragColor = vec4(distance,distance,distance,alpha * alphaAmp );//mix(shadow, text, text.a);
}
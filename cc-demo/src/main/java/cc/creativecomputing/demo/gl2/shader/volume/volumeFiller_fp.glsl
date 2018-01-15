uniform float z;

uniform vec3 volumePosition;
uniform vec3 volumeScale;

uniform vec3 checkPosition;
uniform float colorScale;

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

float noise(in vec3 x) {
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
     return va / wt;
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

void main(){
	vec3 position = vec3(gl_TexCoord[0].xy,z) * volumeScale + volumePosition;
	float density = octavedNoise(position);
	gl_FragColor = vec4(density,density,density,1.0);
	
}
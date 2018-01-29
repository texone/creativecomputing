// The MIT License
// Copyright © 2014 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


// Smooth Voronoi - avoiding aliasing, by replacing the usual min() function, which is
// discontinuous, with a smooth version. That can help preventing some aliasing, and also
// provides with more artistic control of the final procedural textures/models.

// The parameter w controls the smoothness

float hash1(float n) {
	return fract(sin(n)*43758.5453);
}
vec2  hash2(vec2  p) {
	p = vec2(
		dot(p,vec2(127.1,311.7)), 
		dot(p,vec2(269.5,183.3))
	); 
	return fract(sin(p)*43758.5453);
}


@CCProperty(name = "smooth", min = 0, max = 1)
uniform float smooth;
@CCProperty(name = "offset", min = 0, max = 10)
uniform float offset;

vec4 noise( in vec2 x){
	vec2 n = floor( x );
	vec2 f = fract( x );

	vec4 m = vec4( 8.0, 0.0, 0.0, 0.0 );
	for( int j=-2; j<=2; j++ )
	for( int i=-2; i<=2; i++ ){
		vec2 g = vec2(float(i), float(j));
        vec2 o = hash2( n + g );
		
		// animate
        o = 0.5 + 0.5*sin( offset + 6.2831*o );

		// distance to cell		
		float d = length(g - f + o);
		
        // do the smoth min for colors and distances		
		vec3 col = vec3(0.5*sin( hash1(dot(n+g,vec2(7.0,113.0))) )  + 0.25) * 1.5;   
		float h = smoothstep( 0.0, 1.0, 0.5 + 0.5*(m.x-d)/smooth ); 
		
	    m.x   = mix( m.x,     d, h ) - h*(1.0-h)*smooth/(1.0+3.0*smooth); // distance
		m.yzw = mix( m.yzw, col, h ) - h*(1.0-h)*smooth/(1.0+3.0*smooth); // color  
    }
	
	return m;
}


@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;



vec4 octavedNoise(in vec2 s){ 
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
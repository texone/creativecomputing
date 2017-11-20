// The MIT License
// Copyright © 2013 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


//===============================================================================================
//===============================================================================================


vec3 hash(vec3 x){
	x = vec3(
		dot(x,vec3(127.1,311.7, 74.7)),
		dot(x,vec3(269.5,183.3,246.1)),
		dot(x,vec3(113.5,271.9,124.6))
	);

	return fract(sin(x)*43758.5453123);
}

// returns closest, second closest, and cell id
vec3 noise(in vec3 x){
	vec3 p = floor(x);
	vec3 f = fract(x);

	float id = 0.0;
	vec2 res = vec2( 100.0 );
	for(int k=-1; k<=1; k++ )
	for(int j=-1; j<=1; j++ )
	for(int i=-1; i<=1; i++ ){
		vec3 b = vec3(float(i), float(j), float(k));
		vec3 r = vec3( b ) - f + hash( p + b );
		float d = dot( r, r );

		if( d < res.x ){
			id = dot( p+b, vec3(1.0,57.0,113.0 ) );
			res = vec2( d, res.x );			
		}else if( d < res.y ){
			res.y = d;
		}
	}

	return vec3( sqrt( res ), abs(id) );
}


@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;



vec3 octavedNoise(in vec3 s){ 
	float myScale = scale;
	float myFallOff = gain;
	
	int myOctaves = int(floor(octaves)); 
	vec3 myResult = vec3(0.);  
	float myAmp = 0.;
	
	for(int i = 0; i < myOctaves;i++){
		vec3 noiseVal = noise(s * myScale);   
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
// The MIT License
// Copyright © 2013 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

// I've not seen anybody out there computing correct cell interior distances for Voronoi
// patterns yet. That's why they cannot shade the cell interior correctly, and why you've
// never seen cell boundaries rendered correctly. 

// However, here's how you do mathematically correct distances (note the equidistant and non
// degenerated grey isolines inside the cells) and hence edges (in yellow):

// http://www.iquilezles.org/www/articles/voronoilines/voronoilines.htm

vec2 hash2(vec2 p ) {
	p=vec2(
		dot(p,vec2(127.1,311.7)),
		dot(p,vec2(269.5,183.3))
	); 
	return fract(sin(p)*18.5453);
}

@CCProperty(name = "offset", min = 0, max = 10)
uniform float offset;

vec3 noise(in vec2 x){ 
    vec2 n = floor(x);
    vec2 f = fract(x);

	//----------------------------------
	// first pass: regular voronoi
	//----------------------------------
	vec2 mg, mr;

	float md = 8.0;
	for( int j=-1; j<=1; j++ )
	for( int i=-1; i<=1; i++ ){
		vec2 g = vec2(float(i),float(j));
		vec2 o = hash2( n + g );
		
		o = 0.5 + 0.5*sin( offset + 6.2831*o ); 
       
		vec2 r = g + o - f;
		float d = dot(r,r);

		if( d<md ){
			md = d;
			mr = r;
			mg = g;
		}
	}

	//----------------------------------
	// second pass: distance to borders
	//----------------------------------
     
	float md2 = 8.0;
   
	for( int j=-2; j<=2; j++ )
	for( int i=-2; i<=2; i++ ){
		vec2 g = mg + vec2(float(i),float(j));
		vec2 o = hash2( n + g );
		
		o = 0.5 + 0.5*sin( offset + 6.2831*o );
       
		vec2 r = g + o - f;

		if( dot(mr-r,mr-r)>0.00001 )
		md2 = min( md2, dot( 0.5*(mr+r), normalize(r-mr) ) );
	}

	return vec3( md, md2, md2 );
}


@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;



vec3 octavedNoise(in vec2 s){ 
	float myScale = scale * .1;
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
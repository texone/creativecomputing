// Created by inigo quilez - iq/2014
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

float hash1(float n){
	return fract(sin(n)*43758.5453);
}
vec2 hash2(vec2  p){
	p = vec2(
		dot(p,vec2(127.1,311.7)), 
		dot(p,vec2(269.5,183.3))
	);
	return fract(sin(p)*43758.5453);
}

@CCProperty(name = "offset", min = 0, max = 10)
uniform float offset;

@CCProperty(name = "mode", min = 0, max = 3)
uniform float mode;

@CCProperty(name = "euclidian", min = 0, max = 1)
uniform float euclidian;
@CCProperty(name = "manhattan", min = 0, max = 1)
uniform float manhattan;
@CCProperty(name = "triangular", min = 0, max = 1)
uniform float triangular;

vec4 noise(in vec2 x){
	vec2 n = floor( x );
	vec2 f = fract( x );

	vec3 m = vec3( 8.0 );
	float m2 = 8.0;
	for( int j=-2; j<=2; j++ )
	for( int i=-2; i<=2; i++ ){
		vec2 g = vec2( float(i),float(j) );
		vec2 o = hash2( n + g );

		// animate
		o = 0.5 + 0.5*sin( offset + 6.2831*o );

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

		vec2 d = d0 * euclidian + d1 * manhattan + d2 * triangular;

		d /= (euclidian + manhattan + triangular);
		
		if( d.x<m.x ){
			m2 = m.x;
			m.x = d.x;
			m.y = hash1( dot(n+g,vec2(7.0,113.0)));
			m.z = d.y;
		}else if( d.x<m2 ){
			m2 = d.x;
		}

    }
    return vec4( m, m2-m.x );
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
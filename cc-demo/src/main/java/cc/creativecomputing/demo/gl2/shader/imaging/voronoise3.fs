float hash1( float n ) { return fract(sin(n)*43758.5453); }
vec2  hash2( vec2  p ) { p = vec2( dot(p,vec2(127.1,311.7)), dot(p,vec2(269.5,183.3)) ); return fract(sin(p)*43758.5453); }


uniform vec3 noiseBlend;
uniform float time;

vec4 voronoi( in vec2 x ){
	vec2 n = floor( x );
	vec2 f = fract( x );

	vec3 m = vec3( 8.0 );
	float m2 = 8.0;
	for( int j=-2; j<=2; j++ )
		for( int i=-2; i<=2; i++ ){
			vec2 g = vec2( float(i),float(j) );
			vec2 o = hash2( n + g );

			// animate
			o = 0.5 + 0.5*sin( time * 0.1 + 6.2831*o );    
 
 			vec2 r = g - f + o;

			// euclidean		
			vec2 d0 = vec2( sqrt(dot(r,r)), 1.0 );
			// manhattam		
			vec2 d1 = vec2( 0.71*(abs(r.x) + abs(r.y)), 1.0 );
			// triangular		
			vec2 d2 = vec2(
				max(abs(r.x)*0.866025+r.y*0.5,-r.y), 
				step(0.0,0.5*abs(r.x)+0.866025*r.y)*(1.0+step(0.0,r.x))
			);

			vec2 d = d0;   
			d = d0 * noiseBlend.x;
			d += d1 * noiseBlend.y;
			d += d2 * noiseBlend.z;
			d /= (noiseBlend.x + noiseBlend.y + noiseBlend.z);
		
        		if( d.x<m.x ){
				m2 = m.x;
				m.x = d.x;
				m.y = hash1( dot(n+g,vec2(7.0,113.0) ) );
				m.z = d.y;
			} else if( d.x < m2 ){
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

uniform vec4 fTexOffsetBlends;
uniform vec4 fNormalOffsetBlends;

void main(){
	vec2 uv = gl_FragCoord.xy / iResolution.xx;
	vec2 texUV = gl_FragCoord.xy / iResolution.xy;
	texUV = vec2(texUV.x, 1.0 - texUV.y); 
    
	vec4 fxyzw = octavedNoise( 24.0*uv); 
    
	float ft =  fxyzw.x * fTexOffsetBlends.x;
	ft +=  fxyzw.y * fTexOffsetBlends.y;
	ft +=  fxyzw.z * fTexOffsetBlends.z;
	ft +=  fxyzw.w * fTexOffsetBlends.w;
	
	float fn =  fxyzw.x * fNormalOffsetBlends.x;
	fn +=  fxyzw.y * fNormalOffsetBlends.y;
	fn +=  fxyzw.z * fNormalOffsetBlends.z;
	fn +=  fxyzw.w * fNormalOffsetBlends.w;
    
	fn /= (fNormalOffsetBlends.x + fNormalOffsetBlends.y + fNormalOffsetBlends.z + fNormalOffsetBlends.w);
 
	
    
	gl_FragColor = vec4(ft,fn,0.0,1.0);//vec4(f);//vec4(texUV,0.0,1.0);vec4(f.xyz,1);//vec4( dir, 0.0, 1.0 );
}
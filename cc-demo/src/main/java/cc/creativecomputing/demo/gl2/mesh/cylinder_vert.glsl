// The MIT License
// Copyright © 2017 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


// Computes the analytic derivatives of a 3D Gradient Noise. This can be used for example to compute normals to a
// 3d rocks based on Gradient Noise without approximating the gradient by having to take central differences. More
// info here: http://iquilezles.org/www/articles/gradientnoise/gradientnoise.htm

vec3 hash(vec3 p){
	p = vec3(
		dot(p, vec3(127.1, 311.7, 74.7)),
		dot(p, vec3(269.5, 183.3, 246.1)),
		dot(p, vec3(113.5, 271.9, 124.6))
	);

	return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
}

// return value noise (in x) and its derivatives (in yzw)
vec4 noise( in vec3 x ){
    // grid
    vec3 p = floor(x);
    vec3 w = fract(x);
    
    #if 1
    // quintic interpolant
    vec3 u = w*w*w*(w*(w*6.0-15.0)+10.0);
    vec3 du = 30.0*w*w*(w*(w-2.0)+1.0);
    #else
    // cubic interpolant
    vec3 u = w*w*(3.0-2.0*w);
    vec3 du = 6.0*w*(1.0-w);
    #endif    
    
    // gradients
    vec3 ga = hash( p+vec3(0.0,0.0,0.0) );
    vec3 gb = hash( p+vec3(1.0,0.0,0.0) );
	vec3 gc = hash( p+vec3(0.0,1.0,0.0) );
	vec3 gd = hash( p+vec3(1.0,1.0,0.0) );
	vec3 ge = hash( p+vec3(0.0,0.0,1.0) );
	vec3 gf = hash( p+vec3(1.0,0.0,1.0) );
    vec3 gg = hash( p+vec3(0.0,1.0,1.0) );
    vec3 gh = hash( p+vec3(1.0,1.0,1.0) );
    
    // projections
    float va = dot( ga, w-vec3(0.0,0.0,0.0) );
    float vb = dot( gb, w-vec3(1.0,0.0,0.0) );
    float vc = dot( gc, w-vec3(0.0,1.0,0.0) );
    float vd = dot( gd, w-vec3(1.0,1.0,0.0) );
    float ve = dot( ge, w-vec3(0.0,0.0,1.0) );
    float vf = dot( gf, w-vec3(1.0,0.0,1.0) );
    float vg = dot( gg, w-vec3(0.0,1.0,1.0) );
    float vh = dot( gh, w-vec3(1.0,1.0,1.0) );
	
    // interpolations
    return vec4( va + u.x*(vb-va) + u.y*(vc-va) + u.z*(ve-va) + u.x*u.y*(va-vb-vc+vd) + u.y*u.z*(va-vc-ve+vg) + u.z*u.x*(va-vb-ve+vf) + (-va+vb+vc-vd+ve-vf-vg+vh)*u.x*u.y*u.z,    // value
                 ga + u.x*(gb-ga) + u.y*(gc-ga) + u.z*(ge-ga) + u.x*u.y*(ga-gb-gc+gd) + u.y*u.z*(ga-gc-ge+gg) + u.z*u.x*(ga-gb-ge+gf) + (-ga+gb+gc-gd+ge-gf-gg+gh)*u.x*u.y*u.z +   // derivatives
                 du * (vec3(vb,vc,ve) - va + u.yzx*vec3(va-vb-vc+vd,va-vc-ve+vg,va-vb-ve+vf) + u.zxy*vec3(va-vb-ve+vf,va-vb-vc+vd,va-vc-ve+vg) + u.yzx*u.zxy*(-va+vb+vc-vd+ve-vf-vg+vh) ));
}

@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;

// replace outType, inType and evalFunc with whatever you need

vec4 fbm(in vec3 s){ 
	float myScale = scale * .1;
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

@CCProperty(name = "warp1", min = 0, max = 10)
uniform float warp1;

vec4 warp1domain(in vec3 p, out vec4 q){
	q = fbm(p + vec3(0.0,0.0,0.0));

	return fbm(p + warp1 * q.yzw); 
}

@CCProperty(name = "warp2", min = 0, max = 10)
uniform float warp2;

vec4 warp2domain(in vec3 p, out vec4 q, out vec4 r){
	q = fbm(p );

	r = fbm( p + warp1*q.yzw );

	return fbm( p + warp2 * r.yzw); 
}

@CCProperty(name = "radius", min = 1, max = 300)
uniform float radius;

@CCProperty(name = "length", min = 1, max = 2000)
uniform float length;

@CCProperty(name = "nromal Displace", min = -20, max = 20)
uniform float normalDisplace;


vec4 ridge(in vec3 s){  
	float myScale = scale * 0.1;
	float myFallOff = gain;
	
	int myOctaves = int(floor(octaves)); 
	vec4 myResult = vec4(0.);  
	float myAmp = 0.;
	 s *= myScale; 
	for(int i = 0; i < myOctaves;i++){
		vec4 noiseVal = noise(s);  
		myResult += abs(noiseVal) * myFallOff;
		myAmp += myFallOff;
		myFallOff *= gain;
		s = s * lacunarity; 
	}
	float myBlend = octaves - float(myOctaves);
	
	myResult += abs(noise(s)) * myFallOff * myBlend;    
	myAmp += myFallOff * myBlend;
	
	if(myAmp > 0.0){
		myResult /= myAmp;
	}
 	myResult = 1. - myResult;
 	//myResult = pow(myResult , 10.);
	return myResult;
}

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
vec3 noisew(in vec3 x){
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

@CCProperty(name = "iTime", min = 0, max = 10)
uniform float iTime;

void main(){
	vec4 myPos = gl_Vertex * vec4(length / 2., radius, radius, 1.);
	myPos.xyz += (noise(vec3(gl_Vertex.x * 2. + 100. +iTime,0.0,0.)).x ) * 50.;
	vec4 q;
	vec4 r;
	vec4 myNoise = warp2domain(myPos.xyz+vec3(iTime,0.0,0.0), q,r);
	myPos.yz *= 1. + myNoise.x * 0.8 + q.yz * 0.3 + r.yw * 0.3;
	myPos.xyz -= (myNoise.yzw * 2. - 1.) * vec3(normalDisplace); 

	vec3 myNormal = normalize(gl_Normal.xyz + (myNoise.yzw * 2. - 1.) * 0.3);
	gl_Position = gl_ModelViewProjectionMatrix * myPos;
	gl_FrontColor = vec4((myNormal+ 1.) / 2., 1.0); 
}
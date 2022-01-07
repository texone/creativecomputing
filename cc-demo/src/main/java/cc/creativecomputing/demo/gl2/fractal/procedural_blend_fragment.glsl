uniform float aspect;
uniform float time;

// NOISE FUNCTIONS

float hash1(float n){
	return fract(sin(n)*43758.5453);
}

vec2  hash2(vec2  p) {
	p = vec2(
		dot(p, vec2(127.1, 311.7)), 
		dot(p, vec2(269.5, 183.3))
	);
	return fract(sin(p) * 43758.5453);
}

vec3 hash3(vec2 p){
	vec3 q = vec3(
		dot(p, vec2(127.1, 311.7)),
		dot(p, vec2(269.5, 183.3)),
		dot(p, vec2(419.2, 371.9))
	);
    return fract(sin(q) * 43758.5453);
}

@CCProperty(name = "noise euclidian", min = 0, max = 1)
uniform float _NoiseEuclidian;
@CCProperty(name = "noise triangular", min = 0, max = 1)
uniform float _NoiseTriangular;
@CCProperty(name = "noise speed", min = 0, max = 5)
uniform float _NoiseSpeed;
@CCProperty(name = "noise smooth", min = 0, max = 1)
uniform float _NoiseSmooth;

vec4 voronoi( in vec2 pos , out vec2 texPos){
	vec2 cell = floor(pos);
	vec2 cellOffset = fract(pos);

	vec4 m = vec4( 4.0, 4.0, 4.0, 4.0);

	float sharpness = 1.0 + 63.0 * pow(1.0 - _NoiseSmooth, 4.0); 

	float value = 0.0;
	float accum = 0.0;

	vec2 mSamplePos;

	for(float x = -2.; x <= 2.; x++){
		for(float y = -2.; y <= 2.; y++){
			vec2 samplePos = vec2(x, y);
			vec3 center = hash3(cell + samplePos);

			// animate
			center = 0.5 + 0.5 * sin(time * _NoiseSpeed + 6.2831 * center );    
 
			vec2 r = samplePos - cellOffset + center.xy;

			// euclidean		
			vec2 d0 = vec2( length(r), 1.0 );
			// triangular		
			vec2 d2 = vec2(
				max(abs(r.x) * 0.866025 + r.y * 0.5, -r.y), 
				step(0.0, 0.5 * abs(r.x) + 0.866025 * r.y) * (1.0 + step(0.0,r.x))
			);

			vec2 d = vec2(0.0,0.0);   
	
			d += d0 * _NoiseEuclidian;
			d += d2 * _NoiseTriangular;

			d /= (_NoiseEuclidian + _NoiseTriangular);

			float hashDistance = hash1( dot(cell + samplePos, vec2(7.0,113.0)));
		
			if( d.x < m.x ){
				m.w = m.x;
				m.x = d.x;
				m.y = hashDistance;
				m.z = d.y;
				mSamplePos = samplePos;
			} else if( d.x < m.w ){
				m.w = d.x;
			}

			float sample = pow(1.0 - smoothstep(0.0, 1.6, sqrt(d.x)), sharpness);
            
			value += center.z * sample;
			accum += sample;
		}
	}
	texPos = (cell + mSamplePos);
	m.w -= m.x;
	m.z = m.y;
	m.y = value / accum;
   	return m;
}

@CCProperty(name = "noise scale", min = 0, max = 50)
uniform float _NoiseScale;
@CCProperty(name = "noise gain", min = 0, max = 1)
uniform float _NoiseGain;
@CCProperty(name = "noise octaves", min = 1, max = 10)
uniform float _NoiseOctaves;
@CCProperty(name = "noise lacunarity", min = 0, max = 4)
uniform float _NoiseLacunarity;

vec4 octavedVoronoise(vec2 s, float octaves, vec4 firstNoise, out vec2 texPos){
	float myScale = 1.;
	float myFallOff = _NoiseGain;
			    
	int myOctaves = int(floor(octaves));
	vec4 myResult = vec4(0,0,0,0); 
	float myAmp = 0.;

	vec4 noiseVal = firstNoise;
	vec2 myTexResult;
			    
	for(int i = 0; i <= myOctaves;i++){
		myResult += noiseVal * myFallOff;
		myTexResult += texPos;
		myAmp += myFallOff;
		myFallOff *= _NoiseGain;
		myScale *= _NoiseLacunarity;
		noiseVal = voronoi(s * myScale, texPos);
	}
	float myBlend = octaves - float(myOctaves);
			    
	myResult += noiseVal * myFallOff * myBlend;   
	myTexResult += texPos * myBlend;
	myAmp += myFallOff * myBlend;
			    
	if(myAmp > 0.0){
		myResult /= myAmp;
	}

	texPos = myTexResult / (pow(_NoiseLacunarity, octaves - 1.) * 0.5);
			    
	return myResult;
}

@CCProperty(name = "octaves random", min = 0, max = 1)
uniform float _OctavesRandom;

vec4 noise(vec2 uv, out vec2 texPos){
	vec4 noiseVal = voronoi(uv * _NoiseScale, texPos);
	float octaves = mix(_NoiseOctaves, noiseVal.z * _NoiseOctaves, _OctavesRandom);
	return octavedVoronoise(uv * _NoiseScale, octaves, noiseVal, texPos);
}

float distancedNoise(vec4 noise, vec4 distances){
	float mySum = distances.x + distances.y + distances.z + distances.w;
	if(mySum <= 0.)mySum = 1.;
	return dot(noise, distances) / mySum;
}


// BLEND FUNCTIONS

@CCProperty(name = "blend range", min = 0, max = 1)
uniform float _BlendRange;
@CCProperty(name = "blend global", min = 0, max = 1)
uniform float _GlobalBlend;
@CCProperty(name = "blend x", min = 0, max = 1)
uniform float _X_Blend;
@CCProperty(name = "blend y", min = 0, max = 1)
uniform float _Y_Blend;
@CCProperty(name = "blend x mod offset", min = -1, max = 1)
uniform float _X_ModOffset;
@CCProperty(name = "blend x mod amt", min = 0, max = 1)
uniform float _X_ModBlend;
@CCProperty(name = "blend x mod", min = 0, max = 10)
uniform float _X_Mod;
@CCProperty(name = "blend x mod flip", min = 0, max = 1)
uniform float _X_FlipMod;
@CCProperty(name = "reverse blend", min = 0, max = 1)
uniform float _ReverseBlend;

float modX(float x, float _mod){
	return mod(x * _X_Mod + _X_ModOffset, _mod);
}

float linearstep(float edge0, float edge1, float x){
	return  clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
}

vec3 linearstep(float edge0, float edge1, vec3 x){
	return  clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
}

vec3 linearstep(vec3 edge0, vec3 edge1, vec3 x){
	return  clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
}

vec3 blender(vec2 uv, vec3 blends){
	float globalBlend = max(0.001,_GlobalBlend);
	float myOffsetSum = _X_Blend + _Y_Blend + globalBlend + _X_ModBlend;

	float x = uv.x;
	float y = uv.y;
	if(_ReverseBlend > 0.5){
		x = 1 - x;
		y = 1 - y;
	}
	vec3 myModulation = blends * globalBlend;
	myModulation += x * _X_Blend;
	myModulation += y * _Y_Blend;

	if(_X_FlipMod > 0.){
		float modu = modX(uv.x, 2.0);
		if(modu > 1.){
			myModulation += (1. - modX(uv.x, 1.0)) * _X_ModBlend; 
		}else{
			myModulation += modX(uv.x, 1.0) * _X_ModBlend; 
		}
	} else {
		myModulation += modX(uv.x, 1.0) * _X_ModBlend; 
	}

	if(myOffsetSum > 0.){
		myModulation /= myOffsetSum;
	}
	//linearstep(f,f + _BlendRandom, blendAB * (1.0 + _BlendRandom));
	vec3 myBlends = linearstep(myModulation, myModulation + _BlendRange, blends * (1. + _BlendRange));



	return myBlends; //clamp((myBlend / _BlendRange -  1.0 / _BlendRange) + blends * (1.0 / _BlendRange + 1.),0.0,1.0);  
}

@CCProperty(name = "blend progress", min = 0, max = 1)
uniform float _BlendProgress;
@CCProperty(name = "blend random", min = 0, max = 1)
uniform float _BlendRandom;
@CCProperty(name = "blend offset", min = 0, max = 1)
uniform float _BlendOffset;
@CCProperty(name = "blend distances", min = 0, max = 1)
uniform vec4 _BlendDistances;

@CCProperty(name = "A distances", min = 0, max = 1)
uniform vec4 _ADistances;
@CCProperty(name = "A refract", min = 0, max = 1)
uniform float _ARefract;
@CCProperty(name = "A blend refract", min = 0, max = 1)
uniform float _BlendARefract;

@CCProperty(name = "B distances", min = 0, max = 1)
uniform vec4 _BDistances;
@CCProperty(name = "B refract", min = 0, max = 1)
uniform float _BRefract;
@CCProperty(name = "B blend refract", min = 0, max = 1)
uniform float _BlendBRefract;

@CCProperty(name = "t distances", min = 0, max = 1)
uniform vec4 _TDistances;
@CCProperty(name = "t refract", min = 0, max = 1)
uniform float _TRefract;
@CCProperty(name = "t blend refract", min = 0, max = 1)
uniform float _BlendTRefract;
@CCProperty(name = "t blend", min = 0, max = 1)
uniform float _TBlend;


@CCProperty(name = "refraction", min = 0, max = 1)
uniform float _Refraction;

uniform sampler2D textureA;
uniform sampler2D textureB;
uniform sampler3D tex3D;

@CCProperty(name = "depth", min = 0, max = 1)
uniform float _Depth;
@CCProperty(name = "depth start", min = 0, max = 1)
uniform float _DepthStart;

vec3 pal( in float t, in vec3 a, in vec3 b, in vec3 c, in vec3 d )
{
    return a + b*cos( 6.28318*(c*t+d) );
}

void main(){
	vec2 uv = gl_TexCoord[0].xy/ vec2(1.25, aspect) * 0.9;

	vec2 noiseTexPos;
	vec4 f4 = noise(uv, noiseTexPos);
	float fA = distancedNoise(f4, _ADistances);
	float fB = distancedNoise(f4, _BDistances);
	float fT = distancedNoise(f4, _TDistances);
	float f = distancedNoise(f4, _BlendDistances);
	noiseTexPos *= 1 / _NoiseScale * 2.;
	
	vec3 blends = blender(noiseTexPos, vec3(_BlendARefract, _BlendBRefract, _BlendProgress));

	float blendAR = linearstep(0.0, 1.0, blends.z * (1. + 2. * _BlendOffset));
	float blendAB = linearstep(_BlendOffset, 1.0 + _BlendOffset, blends.z * (1. + 2. * _BlendOffset));
	float blendBR = 1.0 - linearstep(2.0 * _BlendOffset, 1.0 + 2.0 * _BlendOffset, blends.z * (1. + 2. * _BlendOffset));

	float noiseBlendARefraction = linearstep(fA,fA + _BlendRandom, blendAR * (1.0 + _BlendRandom)); 
	float noiseBlendBRefraction = linearstep(fB,fB + _BlendRandom, blendBR * (1.0 + _BlendRandom)); 
	float noiseBlendAB          = linearstep(f,f + _BlendRandom, blendAB * (1.0 + _BlendRandom)); 
	float noiseBlendT          = linearstep(f,f + _BlendRandom, _TBlend * (1.0 + _BlendRandom)); 
	
	vec2 texUVDirA = vec2(cos(fA * 6.2), sin(fA * 6.2)) / aspect * _Refraction;
	vec2 texUVDirB = vec2(cos(fB * 6.2), sin(fB * 6.2)) / aspect * _Refraction;
	vec2 texUVDirT = vec2(cos(fT * 6.2), sin(fT * 6.2)) / aspect * _Refraction;

	vec2 muv = vec2(gl_TexCoord[0].x, 1 - gl_TexCoord[0].y);
	vec4 color0 = tex2D(textureA, uv + texUVDirA * (noiseBlendARefraction * _BlendARefract + _ARefract)); 
	color0 = color0 * 1.3 - 0.1;
	color0 = vec4(pal( uv.x+texUVDirB.x+time*0.1, vec3(0.5,0.5,0.5),vec3(0.5,0.5,0.5),vec3(1.0,1.0,1.0),vec3(0.0,0.33,0.67) ),1);
	//color0 = smoothstep(0.,1.,color0) * 1.2 - 0.;
	uv = vec2(uv.x, 1 - uv.y) * vec2(0.7,1);
	vec4 maskCol = texture3D(tex3D,vec3(muv + texUVDirB * (noiseBlendBRefraction * _BlendBRefract + _BRefract),_DepthStart + _Depth * f));

	maskCol = maskCol * 1.4 - 0.2;
	maskCol = vec4(pal( uv.y+texUVDirB.y+time*0.2, vec3(0.5,0.5,0.5),vec3(0.5,0.5,0.5),vec3(1.0,1.0,1.0),vec3(0.0,0.33,0.67) ),1);
	vec4 col = mix(color0, maskCol, noiseBlendAB);   
	
	vec4 colorT = texture2DLod(textureB, uv * 2.5 + texUVDirT * (_BlendTRefract + _TRefract) - vec2(0.1,1.4),1.5) ; 
	
				//return col;//float4(f,f,f,1);//col;////float4(dir,0,1);//
	f = noiseBlendAB;
	gl_FragColor = mix(col,colorT, 0);
	//gl_FragColor = vec4(pal( uv.x+texUVDirB.x, vec3(0.5,0.5,0.5),vec3(0.5,0.5,0.5),vec3(1.0,1.0,1.0),vec3(0.0,0.33,0.67) ),1);
	//gl_FragColor.a = maskCol.r;
}
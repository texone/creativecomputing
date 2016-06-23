
uniform float time;
uniform sampler2D randomTexture;
uniform sampler2D inputMask;
uniform vec2 randomTextureResolution;
uniform vec2 resolution;

/* 
float hash( float n ) { return fract(sin(n)*43758.5453123); }

float noise( in vec3 x )
{
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
	
    float n = p.x + p.y * 157.0 + 113.0 * p.z;
    return mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
                   mix( hash(n+157.0), hash(n+158.0),f.x),f.y),
               mix(mix( hash(n+113.0), hash(n+114.0),f.x),
                   mix( hash(n+270.0), hash(n+271.0),f.x),f.y),f.z);
}

*/
float noise( in vec3 x ){
    vec3 p = floor(x);
    vec3 f = fract(x);
	f = f * f * (3.0 - 2.0 * f);
	
	vec2 uv = (p.xy + vec2(37.0, 17.0) * p.z) + f.xy;
	vec2 rg = texture2D( randomTexture, (uv+ 0.5) / 256.0 ).yx;//, -100.0
	return mix( rg.x, rg.y, f.z );
}

uniform int octaves;
uniform float gain;
uniform float lacunarity;

uniform float speedGain;
uniform vec3 noiseMovement;

float octavedNoise(in vec3 p){
	float result = 0.0;
	float myFallOff = gain;
	float mySpeedFallOff = speedGain;
	float myAmp = 0.0;
	
	vec3 q = p - noiseMovement * mySpeedFallOff * time;
	for(int i = 0; i < octaves; i++){
		myAmp += myFallOff;
		result += myFallOff * noise( q ); 
		q = q * lacunarity - noiseMovement * time * mySpeedFallOff;
		myFallOff *= gain;
		mySpeedFallOff *= speedGain;
	}
	
	return result / myAmp;
}

uniform float densityStart;
uniform float densityNoiseAmp;
uniform float densitySinusColorMod;

uniform float maskBlend;

uniform float zBlendStart;
uniform float zBlendRange;

float density( vec3 p ){
	float result = densityStart + p.z;

	float noiseValue = octavedNoise(p + vec3(0.0,0.0,zBlendStart));


    vec4 mask = texture2D(inputMask,(p.xy / 2.0 + vec2(1.0,1.0)) * 0.5);
    float maske = mix(mask.r, 1.0, maskBlend);

	result = clamp(result + densityNoiseAmp * noiseValue, 0.0, 1.0 ) * (maske);
	
	return result;
}

uniform float marchStepSize;
uniform int marchSteps;

uniform float densityScale;

uniform vec4 densityColor0;
uniform float densityColor0Amp;
uniform vec4 densityColor1;
uniform float densityColor1Amp;

uniform vec4 depthColor0;
uniform float depthColor0Amp;
uniform vec4 depthColor1;
uniform float depthColor1Amp;

vec3 raymarch( in vec3 rayOrigin, in vec3 rayDirection){
	vec4 sum = vec4( 0.0 );

	float t = 0.0;

    // dithering	
	t += marchStepSize * texture2D( randomTexture, gl_FragCoord.xy / randomTextureResolution.x ).x;
	
	for( int i = 0; i < marchSteps; i++ ){
		if( sum.a > 0.99 ) continue;
		
		vec3 pos = rayOrigin + t * rayDirection;
		float densityValue = density(pos);
		
		vec3 col = mix(densityColor0Amp * densityColor0.rgb, densityColor1Amp * densityColor1.rgb, densityValue ) + densitySinusColorMod * sin(pos);
		//vec3 col = mix( vec3(1.0,0.9,0.8), vec3(0.4,0.15,0.1), densityValue ) + densitySinusColorMod * sin(pos);
		//col.rgb *= mix( 3.1 * vec3(1.0, 0.5, 0.05), vec3(0.48, 0.53, 0.5), clamp((pos.z - 0.2) / 2.0, 0.0, 1.0 ) );
		col *= mix(depthColor0Amp * depthColor0.rgb, depthColor1Amp * depthColor1.rgb, clamp((pos.z + zBlendStart) / zBlendRange, 0.0, 1.0 ) );
		
		densityValue *= densityScale;
		col.rgb *= densityValue;

		sum = sum + vec4(col, densityValue) * (1.0 - sum.a);	

		t += marchStepSize;
	}

	return clamp( sum.xyz, 0.0, 1.0 );
	//return clamp( sum.aaa, 0.0, 1.0 );
}

uniform vec3 rayOrigin;

uniform vec3 cameraPosition;

uniform float brightness;
uniform float saturation;
uniform float contrast;

// For all settings: 1.0 = 100% 0.5=50% 1.5 = 150%
vec3 ContrastSaturationBrightness(vec3 color){
	// Increase or decrease theese values to adjust r, g and b color channels seperately
	const float AvgLumR = 0.5;
	const float AvgLumG = 0.5;
	const float AvgLumB = 0.5;
	
	const vec3 LumCoeff = vec3(0.2125, 0.7154, 0.0721);
	
	vec3 AvgLumin = vec3(AvgLumR, AvgLumG, AvgLumB);
	vec3 brtColor = color * brightness;
	vec3 intensity = vec3(dot(brtColor, LumCoeff));
	vec3 satColor = mix(intensity, brtColor, saturation);
	vec3 conColor = mix(AvgLumin, satColor, contrast);
	return conColor;
}

void main(void){
	vec2 q = gl_FragCoord.xy / resolution.xy;
    vec2 p = -1.0 + 2.0 * q;
    //p *= 0.5;
    p.x *= resolution.x/ resolution.y;
    
	
    // camera
    //vec3 rayOrigin = 5.0 * normalize(vec3(1.0, 1.5, 0.0));
	
	// build ray
    vec3 ww = normalize(  - rayOrigin);
    vec3 uu = normalize(cross( vec3(0.0,1.0,0.0), ww ));
    vec3 vv = normalize(cross(ww,uu));
    //vec3 rayDirection = normalize( p.x * uu + p.y * vv + 2.0 * ww );
	
    // raymarch	
	//vec3 col = raymarch( rayOrigin, rayDirection);
	vec3 rayDirection = normalize(vec3(p,0.0) - cameraPosition);
	vec3 color = raymarch( vec3(p,0.0), rayDirection );
	
	// contrast and vignetting	
	//color = ContrastSaturationBrightness(color);
	//color = color * 0.5 + 0.5 * color * color * (3.0 - 2.0 * color);
	//color *= 0.25 + 0.75 * pow( 16.0 * q.x * q.y * (1.0 - q.x) * (1.0 - q.y), 0.1 );
	
    gl_FragColor = vec4( color, 1.0 );
}

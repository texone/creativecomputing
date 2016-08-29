#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform sampler3D pointSprite;

uniform float time;
uniform sampler2D randomTexture;

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
	vec2 rg = texture2D( randomTexture, (uv+ 0.5) / 256.0, -100.0 ).yx;
	return mix( rg.x, rg.y, f.z );
}

uniform int octaves;
uniform float gain;
uniform float lacunarity;

uniform float speedGain;
uniform vec3 noiseMovement;

uniform float noisePow;
uniform float noiseBlendAmount;
uniform float noiseScale;

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

void main(){
	float myColor = pow(octavedNoise(vec3(gl_TexCoord[0].xy * noiseScale,gl_TexCoord[0].w * 100)), noisePow);
   gl_FragColor = gl_Color * texture3D(pointSprite, vec3(gl_TexCoord[0].xyz));//gl_Color;// * 
    gl_FragColor.a *= gl_FragColor.r * mix(1.0,myColor,noiseBlendAmount);
   //gl_FragColor = vec4(myColor, myColor, myColor, 1.0);
}
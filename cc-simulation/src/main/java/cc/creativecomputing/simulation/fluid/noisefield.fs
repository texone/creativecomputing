uniform sampler2D velocity;

uniform vec2 gridSize;

uniform sampler2D randomTexture;

float noise( in vec3 x ){
    vec3 p = floor(x);
    vec3 f = fract(x);
	f = f * f * (3.0 - 2.0 * f);
	
	vec2 uv = (p.xy + vec2(37.0, 17.0) * p.z) + f.xy;
	vec2 rg = texture2D( randomTexture, (uv+ 0.5) / 256.0 ).yx;//, -100.0
	return mix( rg.x, rg.y, f.z );
}

float octavedNoise(in vec3 p, int octaves, float gain, float lacunarity){
	float result = 0.0;
	float myFallOff = gain;
	float myAmp = 0.0;
	
	vec3 q = p;
	for(int i = 0; i < octaves; i++){
		myAmp += myFallOff;
		result += myFallOff * noise( q ); 
		q = q * lacunarity;
		myFallOff *= gain;
	}
	
	return result / myAmp;
}

uniform float scale;
uniform vec3 offset;

uniform int octaves;
uniform float gain;
uniform float lacunarity; 

uniform float noiseAmount;

uniform float minX;
uniform float maxX;

uniform float dissipation;

void main()
{
    vec2 uv = gl_FragCoord.xy / gridSize.xy;
    
    float amount = smoothstep(minX, maxX, uv.x);

    vec2 oldForce = texture2D(velocity, uv).xy * dissipation;
    
    vec3 noisePosition = vec3(uv * scale * vec2(1,gridSize.y / gridSize.x), 0) + offset;
	vec2 result = vec2(
		octavedNoise(noisePosition, octaves, gain, lacunarity),
		octavedNoise(noisePosition+1000.0, octaves, gain, lacunarity)
	) *2.0 - 1.0;

    gl_FragColor = vec4(mix(oldForce, result, noiseAmount * amount), 0.0, 1.0);
}
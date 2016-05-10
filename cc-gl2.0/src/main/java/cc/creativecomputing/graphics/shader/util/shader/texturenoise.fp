uniform sampler2D noiseTexture;

uniform int octaves = 4;
uniform float falloff = 0.5;

float noise(float2 P){
	float result = 0;
	float scale = 1;
	float fall = 0.5;
	
	for(int i = 0; i < octaves;i++){
		result += (tex2D(noiseTexture,(P * scale))*2-1) * fall;
		scale *= 1.6952;
		fall *= falloff;
	}
	
	return result;
}
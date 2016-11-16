#version 120
#extension GL_EXT_gpu_shader4 : enable

uniform sampler2D normals;
uniform sampler2D positions;
uniform sampler2D random;

uniform float sampleRadius;
uniform float intensity;
uniform float scale;
uniform float bias;
uniform float jitter;
uniform float selfOcclusion;
uniform vec2 screenSize;
uniform vec2 invScreenSize;

vec3 getPosition(vec2 uv){
	  return texture2D(positions, uv).xyz;
}

vec3 getNormal(vec2 uv){
	  return normalize(texture2D(normals, uv).xyz * 2.0 - 1.0);
}

vec2 getRandom(vec2 uv){
	  return texture2D(random, screenSize * uv / jitter ).xy;
}

float doAmbientOcclusion(vec2 tcoord, vec2 uv, vec3 p, vec3 cnorm){
	float depth = texture2D(normals, tcoord + uv).a;
	if( depth < 0.00001 || depth > 0.999 ) return 0;
	
	vec3 diff = getPosition(tcoord + uv) - p;
	vec3 v = normalize(diff);
	float d = length(diff) * scale;
	return max(0.0 - selfOcclusion, dot(cnorm,v)-bias) * (1.0 / (1.0 + d * d)) * intensity;
}

vec2 vec[4];

void main(){
	vec[0] = vec2(1,0);
	vec[1] = vec2(0,1);
	vec[2] = vec2(-1,0);
	vec[3] = vec2(0,-1);
	
	vec2 uv = gl_TexCoord[0].st;
	uv.y = 1.0 - uv.y;

	float depth = texture2D(normals, uv).a;
	if( depth < 0.00001 || depth > 0.999 ) discard;

	vec3 position = getPosition(uv);
	vec3 normal = getNormal(uv);
	vec2 rand = getRandom(uv);
	float rad = sampleRadius / position.z; 

	int iterations = 4;
	float ao = 0.0;

	for (int j = 0; j < iterations; ++j) { 
		vec2 coord1 = reflect(vec[j],rand)*rad; 
		vec2 coord2 = vec2(coord1.x * 0.707 - coord1.y * 0.707, coord1.x * 0.707 + coord1.y * 0.707); 
		
		ao += doAmbientOcclusion(uv, coord1 * 0.5, position, normal); 
		ao += doAmbientOcclusion(uv, coord2 * 1.5, position, normal); 
		ao += doAmbientOcclusion(uv, coord1 * 3.75, position, normal); 
		ao += doAmbientOcclusion(uv, coord2, position, normal);  
	}

	ao /= float(iterations) * 4.0; 
	ao+= selfOcclusion;

	ao = 1.0 - ao;
	gl_FragColor = vec4(ao, ao, ao, 1.0);
}
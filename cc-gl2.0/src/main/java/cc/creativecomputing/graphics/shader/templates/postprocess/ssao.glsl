uniform sampler2D col;
uniform sampler2D normal;
uniform sampler2D pos;

uniform vec3 samples[64];
// tile noise texture over screen based on screen dimensions divided by noise size
uniform vec2 noiseScale;

@CCProperty(name = "ssao kernel size", min = 0, max = 64)
uniform float kernelSize;
@CCProperty(name = "ssao radius", min = 0, max = 1)
uniform float radius;
@CCProperty(name = "ssao bias", min = 0, max = 0.1)
uniform float bias;

vec3 hash3(vec2 p){
	vec3 q = vec3(
		dot(p,vec2(127.1,311.7)),
		dot(p,vec2(269.5,183.3)),
		dot(p,vec2(419.2,371.9))
	);
    return fract(sin(q)*43758.5453);
}
uniform mat4 projection;


float ssao(){
	vec3 fragPos   = texture2D(pos,gl_TexCoord[0].xy).xyz;
	vec3 normal    = texture2D(normal, gl_TexCoord[0].xy).rgb;
	vec3 randomVec = hash3(gl_TexCoord[0].xy);
	vec3 tangent   = normalize(randomVec - normal * dot(randomVec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 TBN       = mat3(tangent, bitangent, normal);

	float occlusion = 0.0;
	for(int i = 0; i < kernelSize; ++i){
    	// get sample position
    	vec3 sample = TBN * samples[i]; // From tangent to view-space
    	sample = fragPos + sample * radius * 100;

    	vec4 offset = vec4(sample, 1.0);
		offset = projection * offset;    	  // from view to clip-space
		offset.xyz /= offset.w;               // perspective divide
		offset.xyz  = offset.xyz * 0.5 + 0.5; // transform to range 0.0 - 1.0

		float sampleDepth = texture2D(pos,offset.xy).z;
		float rangeCheck = smoothstep(0.0, 0.01, radius / abs(fragPos.z - sampleDepth));
		occlusion += (sampleDepth >= sample.z + bias ? 1.0 : 0.0) * rangeCheck;
		//occlusion += (sampleDepth <= sample.z + bias ? 1.0 : 0.0);
	}
	float sampleDepth = texture2D(pos,gl_TexCoord[0].xy).z;
	occlusion = 1.0 - (occlusion / kernelSize) * 1.;
	return occlusion;// * 1 / kernelSize ;
}

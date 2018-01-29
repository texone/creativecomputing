#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect IN0;

uniform float offset = 0.0;
uniform float gain = 1.0;
uniform float thresh = 0.0;

void main() {

	vec3 ret = vec3 (0.0, 0.0, 0.0);
	vec2 coords = gl_TexCoord[0].xy;
		
	ret =  texture2DRect(IN0, gl_TexCoord[0].xy).rgb ;
	
	if (ret.x*ret.x + ret.y*ret.y < thresh) {
		ret = vec3(0.0, 0.0, 0.0);
	}
	ret *= gain;
	ret += vec3 (offset, offset, 0.0);
	
	gl_FragColor = vec4 (ret.xy, 0.0, 1.0);	
}
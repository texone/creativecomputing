uniform float saturation;

uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D modSampler;

varying vec3 halfVector;

void main(){
	//float NdotL = abs(dot(normalize(normal), lightDir));
	//gl_FragData[0] = (NdotL * diffuse + ambient) * texture2DLod(textureSampler, vec2(gl_TexCoord[0].s, 1.0 - gl_TexCoord[0].t),gl_Color.r);
	
	//vec4 color0 = texture2DLod(textureSampler0, gl_TexCoord[0].st,4.0);
	//vec4 color1 = texture2DLod(textureSampler1, gl_TexCoord[0].st,4.0);
	vec4 color0 = texture2D(textureSampler0, gl_TexCoord[0].st);
	vec4 color1 = texture2D(textureSampler1, gl_TexCoord[0].st);
	vec4 color = mix(color0, color1, gl_Color.a);
	
	vec4 color2 = texture2D(modSampler, gl_TexCoord[1].st);
	//color.xyz *= (gl_Color.b * 2.0 - 1.0) * saturation + 1.0;
	color.xyz *= (color2.r * 2.0 - 1.0) * saturation + 1.0;
	//color.a = 0.99;//gl_Color.a;
	gl_FragData[0] = color;
}
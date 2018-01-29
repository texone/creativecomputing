uniform float strength;
uniform sampler2D sceneTex;

void main(){
	vec4 color = vec4(0.0);
	vec2 uv = gl_TexCoord[0].xy;
	uv.y = 1.0 - uv.y;
	
	color = texture2D(sceneTex, uv) * 0.2270270270;
	color += texture2D(sceneTex, uv + strength * vec2(0.0, 1.3846153846)) * 0.3162162162;
	color += texture2D(sceneTex, uv - strength * vec2(0.0, 1.3846153846)) * 0.3162162162;
	color += texture2D(sceneTex, uv + strength * vec2(0.0, 3.2307692308)) * 0.0702702703;
	color += texture2D(sceneTex, uv - strength * vec2(0.0, 3.2307692308)) * 0.0702702703;
	gl_FragColor = color;
}
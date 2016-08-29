uniform float strength;
uniform sampler2D sceneTex;
uniform sampler2D depth;

void main(){
	vec4 color = vec4(0.0);
	vec2 uv = gl_TexCoord[0].xy;
	uv.y = 1.0 - uv.y;
	
	float d =  strength * ( 1.0 - texture2D( depth, uv ).a );
	color = texture2D(sceneTex, uv) * 0.2270270270;
	color += texture2D(sceneTex, uv + d * vec2(1.3846153846, 0.0)) * 0.3162162162;
	color += texture2D(sceneTex, uv - d * vec2(1.3846153846, 0.0)) * 0.3162162162;
	color += texture2D(sceneTex, uv + d * vec2(3.2307692308, 0.0)) * 0.0702702703;
	color += texture2D(sceneTex, uv - d * vec2(3.2307692308, 0.0)) * 0.0702702703; 
	gl_FragColor = color;
}
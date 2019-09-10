#version 120 

uniform sampler2DRect debugBuffer;

void main(){
	vec4 col = texture2DRect(debugBuffer, gl_TexCoord[0].xy);

	float r = float(col.x >= 0.);
	gl_FragColor = vec4(col.rgb * 0.1,1.0);
	float d = r;//col.x / col.y;
	//gl_FragColor = vec4(d,d,d,1.);
	//gl_FragColor = col;
}
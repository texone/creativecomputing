uniform sampler2D color;
uniform sampler2D alpha;

void main(){
	vec4 colorP = texture2D(color, gl_TexCoord[0].xy);
	vec4 AlphaP = texture2D(alpha, gl_TexCoord[0].xy);
	colorP.b = gl_TexCoord[1].x;
	gl_FragColor = vec4(colorP);	
	gl_FragColor.a = AlphaP.a;
}
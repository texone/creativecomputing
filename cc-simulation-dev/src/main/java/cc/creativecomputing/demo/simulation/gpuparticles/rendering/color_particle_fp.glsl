
uniform sampler2D colorSampler;

void main(){
	gl_FragColor = gl_Color * texture2D(colorSampler,gl_TexCoord[0].xy);
}
@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;

void main(){
	gl_FragColor =  vec4(gl_TexCoord[0].xy,scale,1);
}

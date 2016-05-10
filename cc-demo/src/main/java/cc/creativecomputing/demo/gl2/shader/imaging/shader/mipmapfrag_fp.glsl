#1.4

uniform sampler2D texture;
uniform float lod;

void main(){
	gl_FragColor = texture2D(texture, gl_TexCoord[0].xy,lod);
}
uniform float amount;

void main(){
	if(gl_TexCoord[0].z > amount)discard;
	gl_FragColor = gl_Color;
}

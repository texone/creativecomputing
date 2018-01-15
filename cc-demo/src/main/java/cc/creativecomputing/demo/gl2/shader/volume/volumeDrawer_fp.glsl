uniform sampler3D cubeSampler;

void main(){
	gl_FragColor = texture3D(cubeSampler, gl_TexCoord[0].xyz);
}
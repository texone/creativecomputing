uniform sampler2D Tex0, Tex1, Tex2;

void main (void)
{
	vec4 Fullres = texture2D(Tex0, gl_TexCoord[0].st);
	vec4 Blurred = texture2D(Tex1, gl_TexCoord[1].st);
	vec4 Blur = texture2D(Tex2, gl_TexCoord[0].st);

	// HLSL linear interpolation function
	gl_FragColor = vec4(Fullres.rgb + Blur.a * (Blurred.rgb - Fullres.rgb),1);
	gl_FragColor.w = 1.0;
}

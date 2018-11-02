

uniform sampler2D filterTexture;

void main(
){
	/*
	in float4 iColor : COLOR,
	in float4 iTextureCoords : TEXCOORD0,
	out float4 oColor : COLOR
	*/
	//oColor = iColor * tex2Dproj( filterTexture, iTextureCoords);
	//oColor = float4(1,1,1,1);
	float r = gl_TexCoord[0].z ;
	float y = 1 - abs(gl_TexCoord[0].x);
	float x1 = gl_TexCoord[0].y / r;
	float x2 = (1 - gl_TexCoord[0].y) / r;
	float x = min(x1,x2);
	float d = min(y, x);

	float dist = 1 - distance(gl_TexCoord[0].xy * vec2(1,1/r), vec2(0,1));
	d = dist * float(gl_TexCoord[0].y < r);
	d += y * float(gl_TexCoord[0].y > r);
	gl_FragColor = vec4(1,1,1,1);
}
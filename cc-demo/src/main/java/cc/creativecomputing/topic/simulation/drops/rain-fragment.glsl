
// textures
uniform sampler2D waterMap;
uniform sampler2D textureShine;
uniform sampler2D textureFg;
uniform sampler2D textureBg;

// the texCoords passed in from the vertex shader.


void main() {
	vec4 bg = texture2D(textureBg, gl_TexCoord[0].xy);

	vec4 cur = texture2D(waterMap, gl_TexCoord[0].xy);

	float d=cur.b; // "thickness"
	float x=cur.g;
	float y=cur.r;

	//float a=clamp(cur.a*alphaMultiply-alphaSubtract, 0.0,1.0);

	vec2 refraction = vec2(x,y) * 2.0 - 1.;
	vec2 refractionPos = gl_TexCoord[0].xy + refraction * (0.05 + d * 0.2);

	vec4 fg = texture2D(textureFg, refractionPos);

	float borderAlpha;
	borderAlpha += texture2D(waterMap,gl_TexCoord[0].xy + vec2(0., 0. - d * 0.15)).b;
	borderAlpha += texture2D(waterMap,gl_TexCoord[0].xy + vec2(0., 0. + d * 0.05)).b * 0.95;
	borderAlpha += texture2D(waterMap,gl_TexCoord[0].xy + vec2(0., 0. + d * 0.05)).r * 0.35;

	//borderAlpha=borderAlpha*alphaMultiply-(alphaSubtract+0.5);
	borderAlpha = clamp(borderAlpha, 0., 1.);
	//borderAlpha*=0.2;
	vec4 border=mix(vec4(1.,1.,1.,1),vec4(0.,0.,0.,1),1-borderAlpha *1);
   // fg=blend(border,fg);
  
	float d2 = 1 -dot(cur.xyz, normalize(vec3(2.2,0.2,2.4))) * 0.5;
	d2 = abs(d2);
	d2 = pow(d2,2);
	gl_FragColor = border;//mix(bg, fg, cur.r + cur.g);//blend(bg,fg);
}

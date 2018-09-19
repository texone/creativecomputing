
// textures
uniform sampler2D waterMap;
uniform sampler2D textureShine;
uniform sampler2D textureFg;
uniform sampler2D textureBg;

// the texCoords passed in from the vertex shader.
uniform vec2 resolution;
uniform vec2 parallax;
uniform bool renderShine;
uniform bool renderShadow;
uniform float brightness;
uniform float alphaMultiply;
uniform float alphaSubtract;

// alpha-blends two colors
vec4 blend(vec4 bg,vec4 fg){
  vec3 bgm=bg.rgb * bg.a;
  vec3 fgm=fg.rgb * fg.a;
  
  float ia = 1.0 - fg.a;
  float a = fg.a + bg.a * ia;
  vec3 rgb;
  if(a != 0.0){
    rgb = (fgm + bgm * ia) / a;
  }else{
    rgb = vec3(0.0,0.0,0.0);
  }
  return vec4(rgb,a);
}


void main() {
	
  //vec4 bg=texture2D(textureBg,scaledTexCoord()+parallax(parallaxBg));
  
	vec4 bg = texture2D(textureBg,gl_TexCoord[0].xy);

	vec4 cur = texture2D(waterMap,gl_TexCoord[0].xy);

	float d=cur.b; // "thickness"
	float x=cur.g;
	float y=cur.r;

	float a=clamp(cur.a*alphaMultiply-alphaSubtract, 0.0,1.0);

	vec2 refraction = vec2(x,y) * 2.0 - 1.;
	vec2 refractionPos = gl_TexCoord[0].xy + refraction * (0.05 + d * 0.2);

  vec4 fg=texture2D(textureFg, refractionPos);

	vec4 tex;
  if(renderShine){
    float maxShine=490.0;
    float minShine=maxShine*0.18;
    vec2 shinePos=vec2(0.5,0.5) + ((1.0/512.0)*refraction)* -(minShine+((maxShine-minShine)*d));
    vec4 shine=texture2D(textureShine,shinePos);
    tex=blend(tex,shine);
  }
	//vec4 fg=vec4(tex.rgb * brightness,a);

  
/*
  if(renderShadow){
    float borderAlpha = fgColor(0.,0.-(d*6.0)).a;
    borderAlpha=borderAlpha*alphaMultiply-(alphaSubtract+0.5);
    borderAlpha=clamp(borderAlpha,0.,1.);
    borderAlpha*=0.2;
    vec4 border=vec4(0.,0.,0.,borderAlpha);
    fg=blend(border,fg);
  }*/

  gl_FragColor = mix(fg, bg, d);//blend(bg,fg);
}

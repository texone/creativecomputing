// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

#define HIGH_QUALITY_NOISE

uniform sampler2D randomTexture;

float noise(vec3 x){
	vec3 p = floor(x);
	vec3 f = fract(x);
	f = f*f*(3.0-2.0*f);
#ifndef HIGH_QUALITY_NOISE
	vec2 uv = (p.xy+vec2(37.0,17.0)*p.z) + f.xy;
	vec2 rg = texture2D( randomTexture, (uv+ 0.5)/256.0).yx;
#else
	vec2 uv = (p.xy+vec2(37.0,17.0)*p.z);
	vec2 rg1 = texture2D( randomTexture, (uv+ vec2(0.5,0.5))/256.0).yx;
	vec2 rg2 = texture2D( randomTexture, (uv+ vec2(1.5,0.5))/256.0).yx;
	vec2 rg3 = texture2D( randomTexture, (uv+ vec2(0.5,1.5))/256.0).yx;
	vec2 rg4 = texture2D( randomTexture, (uv+ vec2(1.5,1.5))/256.0).yx;
	vec2 rg = mix( mix(rg1,rg2,f.x), mix(rg3,rg4,f.x), f.y );
#endif	
	return mix( rg.x, rg.y, f.z );
}
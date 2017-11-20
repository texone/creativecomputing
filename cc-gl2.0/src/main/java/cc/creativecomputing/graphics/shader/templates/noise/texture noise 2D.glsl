// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

uniform sampler2D randomTexture;

float noise(vec2 x){
	vec2 p = floor(x);
	vec2 f = fract(x);
	vec2 uv = p.xy + f.xy*f.xy*(3.0-2.0*f.xy);
	return texture2D( randomTexture, (uv+118.4)/256.0).x;
}
#version 120 

uniform sampler2DRect colorTex;
uniform sampler2DRect brightTex;

uniform float amp = 1;
uniform float powVal = 1;

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

uniform vec3 lightDir = vec3(0.75,0,1);
uniform float diffuseAmp;
uniform float specularAmp;
uniform float specularBrightAmp;

uniform float specularPow = 5;
uniform float specularBrightPow = 3;

void main(){

	vec4 color = texture2DRect(colorTex, gl_FragCoord.xy);
	vec4 bright = texture2DRect(brightTex, gl_FragCoord.xy);

	float scale = bright.r + bright.g;
	float blend = scale;
	scale *= amp; 
	//scale = mod(scale, 0.9);
	scale = pow((cos(scale * 4) - 1) / 2,2);
	scale = min(scale, 1);
	scale = pow(scale,powVal); 

	vec3 hsv = rgb2hsv(color.rgb); 
	hsv.r += bright.g * 1.5;  
	hsv.r += blend * 0.03;
	//hsv.g -= blend * 0.06;
	color.rgb = hsv2rgb(hsv); 
	color.rgb *= scale;

	//normal
    float s01 = texture2DRect(brightTex, gl_FragCoord.xy + vec2(-1,0)).x;
    float s21 = texture2DRect(brightTex, gl_FragCoord.xy + vec2(1,0)).x;
    float s10 = texture2DRect(brightTex, gl_FragCoord.xy + vec2(0,-1)).x;
    float s12 = texture2DRect(brightTex, gl_FragCoord.xy + vec2(0,1)).x; 
    
    vec3 va = normalize(vec3(0.001,0,(s21-s01)));
    vec3 vb = normalize(vec3(0,0.001,s12-s10));
    vec3 normal = cross(va,vb);
	
	float ppDiffuse			= abs( dot( normal, normalize(lightDir) )); 
	float ppSpecular		= pow( ppDiffuse, specularPow );
	float ppSpecularBright	= pow( ppDiffuse, specularBrightPow );
	
	color.rgb = color.rgb * mix(1,ppDiffuse,diffuseAmp) + ppSpecular * specularAmp * scale + ppSpecularBright * specularBrightAmp * scale;

	gl_FragColor = color;
}

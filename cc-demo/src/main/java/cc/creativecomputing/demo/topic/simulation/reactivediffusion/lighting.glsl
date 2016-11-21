#version 120 

uniform sampler2D colorTex;
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

uniform vec2 windowSize;

uniform float refraction;

void main(){

	vec2 uv = gl_FragCoord.xy / windowSize;
	vec4 bright = texture2DRect(brightTex, gl_FragCoord.xy);  

	float f = bright.x; 
	vec2 dir = normalize(vec2(cos(f * 6.2), sin(f * 6.2))) / windowSize * refraction;
	vec2 texUV = uv + dir;
	
	vec4 color = texture2D(colorTex, texUV);

	vec2 xOffset = vec2(1.0, 0.0);
     vec2 yOffset = vec2(0.0, 1.0);
/*
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
	*/
	//color = min(color, vec4(1.0,1.0,1.0,1.0)); 
	float scale = length(color); 

	vec3 hsv = rgb2hsv(color.rgb); 
	
	//hsv.r += bright.g;  
	//hsv.g -= blend * 0.06;
	color.rgb = hsv2rgb(hsv); 
	
	//normal
    float s01 = texture2DRect(brightTex, gl_FragCoord.xy - xOffset).y;
    float s21 = texture2DRect(brightTex, gl_FragCoord.xy + xOffset).y;
    float s10 = texture2DRect(brightTex, gl_FragCoord.xy - yOffset).y;
    float s12 = texture2DRect(brightTex, gl_FragCoord.xy + yOffset).y; 
    
    vec3 va = normalize(vec3(0.25,0,(s21-s01)));
    vec3 vb = normalize(vec3(0,0.25,s12-s10));
    vec3 normal = cross(va,vb);
	
	float ppDiffuse		= abs( dot( normal, normalize(lightDir) )); 
	float ppSpecular		= pow( ppDiffuse, specularPow );
	float ppSpecularBright	= pow( ppDiffuse, specularBrightPow );
	color.rgb = color.rgb * mix(1,ppDiffuse,diffuseAmp) + ppSpecular * specularAmp + ppSpecularBright * specularBrightAmp;
	
	gl_FragColor = color;//vec4(normal,1.0);//bright;//vec4(normal,1.0);//pow(color,vec4(3., 2.0, 2.0, 1.0)); //vec4(normal * 0.5 + 0.5,1.0); // 
}




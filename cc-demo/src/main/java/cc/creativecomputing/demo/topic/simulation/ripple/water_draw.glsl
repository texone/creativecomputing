uniform sampler2DRect heightMap;
uniform sampler2DRect normalMap;
uniform sampler2DRect backgroundTexture;

uniform float refraction;

// black on white
const float contrast = 1.0;

void main() {

	// take absolut value of current height and scale it a bit
    float intensity = contrast * abs( texture2DRect(heightMap, gl_TexCoord[0].xy).y);

	float shape =  texture2DRect(heightMap, gl_TexCoord[0].xy).x;
	
	vec4 normal = normalize(texture2DRect(normalMap, gl_TexCoord[0].xy) * 2.0 - 1.0);

    // invert to white on black
    intensity = 1.0 - intensity;   

	gl_FragColor = vec4(intensity, intensity, intensity, 1);
    
    // displace texture coordinates
	vec2 newUV = gl_TexCoord[0].xy + normal.xy * refraction;// * (1-intensity);

	//newUV = vec2(xOff, yOff);
	gl_FragColor = texture2DRect( backgroundTexture, newUV );
	//color = texRECT( normalMap, gl_TexCoord[0] );
	//color = vec4(intensity, intensity, intensity, 1);
 }


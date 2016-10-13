#version 120 

uniform sampler2DRect crystalTexture0; 
uniform sampler2DRect crystalTexture1; 
uniform sampler2DRect particleTexture;
uniform float increase;

uniform vec2 OFFSETS[ 8 ] = vec2[8](
	vec2( 1.0, -1.0 ),
	vec2( 0.0, -1.0 ),
	vec2(-1.0, -1.0 ),
	vec2( 1.0,  0.0 ),
	vec2(-1.0,  0.0 ),
	vec2( 1.0,  1.0 ),
	vec2( 0.0,  1.0 ),
	vec2(-1.0,  1.0 ) 
);

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main(){

	vec4 iTexCoord = gl_TexCoord[0];
	vec4 oColor = texture2DRect(crystalTexture0,iTexCoord.xy);
	vec4 oBright = texture2DRect(crystalTexture1,iTexCoord.xy);
	
	if(length(oBright.rgb) > 0){
		oBright.r += increase;
	}else{
		vec4 c;
		float count;
		for(int i = 0; i < 8; i++){
			vec4 myCol = texture2DRect(crystalTexture0,iTexCoord.xy + OFFSETS[i]);
			if(length(myCol.rgb) > 0){
				c += myCol;
				count++;
			}
		}
		c /= count; 

		vec4 particleCol = texture2DRect(particleTexture,iTexCoord.xy);
		//vec4(hsv2rgb(vec3(mod(gl_Vertex.z * 100, 1.0) * 0.2,1.0,1.0)), 1.0);
		if(length(c.rgb) > 0 && length(particleCol.r) > 0){
			oBright = vec4(increase, particleCol.y, increase, 1.0); 
			oColor = c;  
			oColor.a = 1;
		}
		
	}
	
	gl_FragData[0] = oColor; 
	gl_FragData[1] = oBright; 
}

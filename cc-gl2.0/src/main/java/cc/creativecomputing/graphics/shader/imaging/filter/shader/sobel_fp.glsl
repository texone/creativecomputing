#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect IN0;



uniform int width;
uniform int height;
uniform bool offset;

void main() {

	vec2 coords = gl_TexCoord[0].xy;
	float out0 = 0.0;
	float outx;
	float outy;
	
	//if (coords.x>0 && coords.x <width-1 && coords.y > 0 && coords.y < height-1) {
		
	
		outy     = 1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(-1, -1)).r
				 + 2*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(0, -1)).r
		         + 1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(1, -1)).r
		        
				 -  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(-1, 1)).r
				 -  2*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(0, 1)).r
		         -  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(1, 1)).r;
		         
		         
		outx     =  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2  (-1, -1)).r
				 +  2*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(-1, 0)).r
		         +  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(-1, 1)).r
		         
				 -  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(1, -1)).r
				 -  2*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(1, 0)).r
		         -  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2(1, 1)).r;


		float brightness = sqrt(outx*outx+outy*outy);
		
		if (offset) {
			outx = outx*0.5 + 0.5;
			outy = outy*0.5 + 0.5;
		}
		gl_FragData[0] = vec4 (outx, outy, 0.0, 1.0);
		gl_FragData[1] = vec4 (brightness, brightness, brightness, 1.0);
	
		
	//}
}
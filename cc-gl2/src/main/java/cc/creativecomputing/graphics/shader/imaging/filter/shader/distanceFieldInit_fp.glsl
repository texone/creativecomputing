#version 120
#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect IN0;
uniform bool shift = false;

uniform int width;
uniform int height;


void main() {

	vec2 output = vec2 (0.0, 0.0);
	vec2 coords = gl_TexCoord[0].xy;
		
	/*
	if (coords.x>0 && coords.x <width-1 && coords.y > 0 && coords.y < height-1) {
		
		output.y  = 1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (-1, -1)).r
				  + 2*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (0, -1)).r
		          + 1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (1, -1)).r
		         
				  - 1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (-1, 1)).r
				  - 2*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (0, 1)).r
		          - 1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (1, 1)).r;
		                  
		output.x =  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (-1, -1)).r
				 +  2*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (-1, 0)).r
		         +  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (-1, 1)).r
		         
				 -  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (1, -1)).r
				 -  2*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (1, 0)).r
		         -  1*texture2DRect(IN0, gl_TexCoord[0].xy+vec2 (1, 1)).r;
	}
		*/
		
		int n = 30;
		int r = 400;
		
		for (int i=-n; i<=n; i++) {
			for (int j=-n; j<=n; j++) {
				if (!(i==0 && j==0) && (i*i+j*j)<r) {
					output +=  texture2DRect(IN0, gl_TexCoord[0].xy+vec2(i,j)).r * vec2(i,j) / (i*i+j*j); 
				}
			}
		}
		//output /= 10;
		//output =  texture2DRect(IN0, gl_TexCoord[0].xy).rg;
	
		gl_FragColor = vec4 (output, 0.0, 1.0);	
}
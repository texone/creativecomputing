#1.4

// maximum size supported by this shader

// array of offsets for accessing the base image

// value for each location in the convolution kernel
uniform float kernelValue[200];

uniform int kernelWidth;
uniform int kernelHeight;

uniform sampler2DRect decal;
uniform vec2 pixelScale;

void main(){
    vec4 sum = vec4 (0.0);
		
	int xStart = -kernelWidth / 2;
	int yStart = -kernelHeight / 2;

    for (int x = 0; x < kernelWidth; x++){
    	float xOffset = float(xStart + x);
    	for (int y = 0; y < kernelHeight; y++){
    		float yOffset = float(yStart + y);
    		
    		int i = x * kernelHeight + y;
    		vec2 texCoord = gl_TexCoord[0].xy + vec2(xOffset, yOffset);
       		vec4 tmp = texture2DRect(decal, texCoord);
       		sum += tmp * kernelValue[i];
       	}
    }
    
    
    gl_FragColor = sum;//texture2D(decal, gl_TexCoord[0].xy);
}
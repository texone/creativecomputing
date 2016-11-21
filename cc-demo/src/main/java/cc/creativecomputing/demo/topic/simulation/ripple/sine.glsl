uniform float amplitude;

const float PI = 3.141592654;

// draw a circle with sine shaped intensity to the red channel
// the circle is centered at texcoords [0.5, 0.5]
// the overall strength is defined by amplitude

void main(){
	float center_distance =  length(2.0 * (gl_TexCoord[0].xy - 0.5));
	float h = amplitude * max(0.0, cos(0.5 * PI * center_distance));
	gl_FragColor = vec4(h, 0.0, 0.0, 1.0);
}



#extension GL_ARB_texture_rectangle : enable

uniform vec3 randAdd;
uniform float thresh;

float rand(vec2 n){
  return fract(sin(dot(n.xy, randAdd.xy))* randAdd.z);
}


void main(void){
  float x = rand(gl_FragCoord.xy);
  if (x<thresh) {
	  x = 0.0;
  }
  gl_FragColor = vec4(x, x, x, 1.0);
}



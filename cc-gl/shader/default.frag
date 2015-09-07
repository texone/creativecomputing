#version 430 core

in vec3 colour;
in vec3 normal;
out vec4 frag_colour;

void main () {
  //frag_colour = vec4 (colour, 1.0);
  frag_colour = vec4 (normal, 1.0);
  //frag_colour = vec4 (1.0, 1.0, 0.0, 1.0);
}
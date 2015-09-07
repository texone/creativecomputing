#version 430 core

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_colour;
layout(location = 2) in vec3 vertex_normal;

out vec3 colour;
out vec3 normal;

void main () {
  colour = vertex_colour;
  normal = vertex_normal;
  gl_Position = vec4 (vertex_position, 1.0);
}
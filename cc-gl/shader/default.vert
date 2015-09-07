#version 430 core

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_colour;
layout(location = 2) in vec3 vertex_normal;

out vec3 colour;
out vec3 normal;

void main () {
  colour = vertex_colour;
  mat3 normal_matrix = mat3(view_matrix * model_matrix);
  normal = normalize(transpose(inverse(normal_matrix)) * vertex_normal);
  normal = vertex_normal;
  //vec4 pos = vec4 (vertex_position, 1.0); 
  gl_Position = projection_matrix * view_matrix * model_matrix * vec4 (vertex_position, 1.0);
  //gl_Position = vec4(vertex_position, 1.0);
 // gl_Position = model_matrix * (view_matrix * (projection_matrix * pos));
//  normal = vertex_normal;
}
#version 410

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;
uniform vec4 the_colour;

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_normal;

layout(location = 3) in vec4 instance_weights;
layout(location = 4) in vec4 instance_colour;

out vec4 vs_fs_colour;
out vec3 vs_fs_normal;

void main (void) {

  vec4 pos = vec4(vertex_position, 1.0);
  mat4 m = mat4(1.0, 0.0, 0.0, 0.0,
  				0.0, 1.0, 0.0, 0.0, 
  				0.0, 0.0, 1.0, 0.0, 
  				0.0, 0.0, 0.0, 1.0);
  vec4 weights = normalize(instance_weights);
  
  for (int n = 0; n < 4; n++) {
  	m[n] += (model_matrix[n] * weights[n]);
  }
  
  
  mat3 normal_matrix = mat3(view_matrix * m);
  vs_fs_normal = normalize(transpose(inverse(normal_matrix)) * vertex_normal);
   
  vs_fs_colour = instance_colour;
  //vs_fs_colour = the_colour;
  //vs_fs_colour = vec4(0.1 * gl_InstanceID, 0.0, 1.0, 1.0);
    
 gl_Position = projection_matrix * view_matrix * m * pos;
  
}
#version 410

in vec4 vs_fs_colour;
in vec4 vs_fs_normal;
layout (location = 0) out vec4 colour;

void main (void) {
	colour = vs_fs_colour * (0.1 + abs(vs_fs_normal.z)) + vec4(0.8, 0.9, 0.7, 1.0) * pow(abs(vs_fs_normal.z), 40.0);
	//colour = vec4(1.0f, 1.0f, 0.0f, 1.0f);
}
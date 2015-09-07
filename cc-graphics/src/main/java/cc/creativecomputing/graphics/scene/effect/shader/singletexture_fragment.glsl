#version 400

layout( location = 0 ) out vec4 FragColor;

uniform sampler2D s; 

in vec2 vTexcoord;

void main() {
    FragColor = texture(s, vTexcoord);
}
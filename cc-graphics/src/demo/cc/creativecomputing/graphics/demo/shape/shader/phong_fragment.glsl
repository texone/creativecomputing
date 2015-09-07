#version 400

in vec3 LightIntensity;

layout( location = 0 ) out vec4 FragColor;

void main() {
    FragColor = vec4(LightIntensity.rgb, 1.0);//vec4(1.0);//
}

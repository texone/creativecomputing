#version 400

in vec3 TexCoord;
layout (location = 0) out vec4 FragColor;

uniform vec4 InnerColor;
uniform vec4 OuterColor;
uniform float RadiusInner;
uniform float RadiusOuter;

void main() {
    float dx = TexCoord.x - 0.5;
    float dy = TexCoord.y - 0.5;
    float dist = sqrt(dx * dx + dy * dy);
    FragColor = mix( InnerColor, OuterColor, smoothstep( RadiusInner, RadiusOuter, dist));
}

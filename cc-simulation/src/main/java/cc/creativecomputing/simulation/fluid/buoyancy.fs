uniform sampler2D velocity;
uniform sampler2D temperature;
uniform sampler2D density;
uniform float ambientTemperature;
uniform float timestep;
uniform float sigma;
uniform float kappa;

uniform vec2 gridSize;
uniform float gridScale;

void main()
{
    vec2 uv = gl_FragCoord.xy / gridSize.xy;
    
    float t = texture2D(temperature, uv).r;
    vec2 v = texture2D(velocity, uv).xy;

    gl_FragColor = vec4(v, 0.0, 1.0);

    if (t > ambientTemperature) {
        float D = texture2D(density, uv).x;
        gl_FragColor.y += timestep * (t - ambientTemperature) * sigma - D * kappa;//vec4((timestep * (t - ambientTemperature) * sigma - D * kappa ) * vec2(0.0, 0.0), 0.0, 1.0);
    }
    
}
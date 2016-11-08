uniform sampler2D p;
uniform sampler2D w;
uniform sampler2D bounds;

uniform vec2 gridSize;
uniform float gridScale;

void main()
{
    vec2 uv = gl_FragCoord.xy / gridSize.xy;
    
    vec3 bound = texture2D(bounds, uv).xyz;
    if (bound.x > 0.0) {
        gl_FragColor = vec4(bound.yz, 0.0, 1.0);
        return;
    }

    vec2 xOffset = vec2(1.0 / gridSize.x, 0.0);
    vec2 yOffset = vec2(0.0, 1.0 / gridSize.y);

    float pl = texture2D(p, uv - xOffset).x;
    float pr = texture2D(p, uv + xOffset).x;
    float pb = texture2D(p, uv - yOffset).x;
    float pt = texture2D(p, uv + yOffset).x;
    float pc = texture2D(p, uv).x;
    
    // Find neighboring obstacles:
    vec3 ol = texture2D(bounds, uv - xOffset).xyz;
    vec3 or = texture2D(bounds, uv + xOffset).xyz;
    vec3 ob = texture2D(bounds, uv - yOffset).xyz;
    vec3 ot = texture2D(bounds, uv + yOffset).xyz;
    
    // Use center pressure for solid cells:
    vec2 obstV = vec2(0);
    vec2 vMask = vec2(1);
    
    if (ol.x > 0.0) { pl = pc; obstV.x = ol.y; vMask.x = 0.0; }
    if (or.x > 0.0) { pr = pc; obstV.x = or.y; vMask.x = 0.0; }
    if (ob.x > 0.0) { pb = pc; obstV.y = ob.z; vMask.y = 0.0; }
    if (ot.x > 0.0) { pt = pc; obstV.y = ot.z; vMask.y = 0.0; }

    float scale = 0.5 / gridScale;
    vec2 gradient = scale * vec2(pr - pl, pt - pb);

    vec2 oldV = texture2D(w, uv).xy;
    vec2 newV = oldV - gradient;

    gl_FragColor = vec4((vMask * newV) + obstV, 0.0, 1.0);
}

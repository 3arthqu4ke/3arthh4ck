#shader vert
#version 120

void main(void) {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
}

#shader frag
#version 120

uniform sampler2D sampler;
uniform vec2 dimensions;
uniform vec2 center;
uniform vec4 color;

float dist(vec2 vector1, vec2 vector2) {
    return sqrt(((vector1.x - vector2.x) * (vector1.x - vector2.x)) + ((vector1.y - vector2.y) * (vector1.y - vector2.y)));
}

/*float closer(vec2 value, float x, float y) {
    const float x1 = value.x - (x / 2.0);
    const float x2 = value.x + (x / 2.0);

    const float y1 = value.y - (y / 2.0);
    const float y2 = value.y + (y / 2.0);

    const float distX1 = dist(value, vec2(x1, value.y));
    const float distX2 = dist(value, vec2(x2, value.y));

    const float distY1 = dist(value, vec2(value.x, y1));
    const float distY2 = dist(value, vec2(value.x, y2));

    return 0.0; // placeholder
}*/

void main() {
    float factor = dist(vec2(gl_FragCoord.x, gl_FragCoord.y), vec2(center.x + (dimensions.x / 2.0), center.y + (dimensions.y / 2.0))) / dist(center, vec2(center.x + (dimensions.x / 2.0), center.y + (dimensions.y / 2.0)));
    gl_FragColor = mix(vec4(0, 0, 0, 1), vec4(color.x, color.y, color.z, 1), factor);
}
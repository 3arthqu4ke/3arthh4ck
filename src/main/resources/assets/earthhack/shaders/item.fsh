#version 120

uniform sampler2D DiffuseSampler;

void main() {
    float alpha = 1.0f;
    vec4 color = texture2D(DiffuseSampler, gl_FragCoord.xy);
    if (color.a < 1.0f) {
        alpha = 0.0f;
    }
    gl_FragColor = vec4(1, 1, 1, 1);
}

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;
uniform float alpha;

void main() {
    vec2 uv = gl_FragCoord/resolution.xy;
    // Time varying pixel color
    vec3 color = 0.5 + 0.5*cos(time+uv.xyx+vec3(0,2,4));

    gl_FragCoord = vec4(color, 1.0f);
}
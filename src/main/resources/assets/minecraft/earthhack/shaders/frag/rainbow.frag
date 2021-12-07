// #version 120
#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D texture;
uniform vec2 texelSize;

uniform float radius;
uniform float divider;
uniform float maxSample;

uniform float time;
uniform vec2 resolution;

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    vec2 uv = gl_FragCoord/resolution.xy;
    // Time varying pixel color
    vec3 color = 0.5 + 0.5*cos(time+uv.xyx+vec3(0,2,4));

    if(centerCol.a != 0) {
        gl_FragColor = vec4(centerCol.rgb, 0);
    } else {

        float alpha = 0;

        for (float x = -radius; x < radius; x++) {
            for (float y = -radius; y < radius; y++) {
                vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y));

                if (currentColor.a != 0)
                alpha += divider > 0 ? max(0, (maxSample - distance(vec2(x, y), vec2(0))) / divider) : 1;
            }
        }
        gl_FragColor = vec4(color, alpha);
    }
}
#shader vert
#version 120

void main(void) {
    //Map gl_MultiTexCoord0 to index zero of gl_TexCoord
    gl_TexCoord[0] = gl_MultiTexCoord0;

    //Calculate position by multiplying model, view and projection matrix by the vertex vector
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

#shader frag
#version 120

uniform sampler2D texture;
uniform vec2 texelSize;

uniform vec3 color;

uniform float radius;
uniform float divider;
uniform float maxSample;
uniform float mixFactor;
uniform float minAlpha;

uniform vec2 dimensions;

uniform bool blur;

uniform sampler2D image;
uniform float imageMix;
uniform bool useImage;

vec4 blur13(sampler2D image, vec2 uv, vec2 resolution, vec2 direction) {
    vec4 color = vec4(0.0);
    vec2 off1 = vec2(1.411764705882353) * direction;
    vec2 off2 = vec2(3.2941176470588234) * direction;
    vec2 off3 = vec2(5.176470588235294) * direction;
    if (texture2D(image, uv + (off1 / resolution)).a == 0
    || texture2D(image, uv + (off2 / resolution)).a == 0
    || texture2D(image, uv + (off3 / resolution)).a == 0) {
        return texture2D(image, uv);
    }
    color += texture2D(image, uv) * 0.1964825501511404;
    color += texture2D(image, uv + (off1 / resolution)) * 0.2969069646728344;
    color += texture2D(image, uv - (off1 / resolution)) * 0.2969069646728344;
    color += texture2D(image, uv + (off2 / resolution)) * 0.09447039785044732;
    color += texture2D(image, uv - (off2 / resolution)) * 0.09447039785044732;
    color += texture2D(image, uv + (off3 / resolution)) * 0.010381362401148057;
    color += texture2D(image, uv - (off3 / resolution)) * 0.010381362401148057;
    return color;
}

void main() {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);

    if (blur) {
        if (centerCol.a != 0) {
            centerCol = blur13(texture, gl_TexCoord[0].xy, dimensions, vec2(2, 2));
        }
    }

    if (useImage) {
        if (centerCol.a != 0) {
            centerCol = mix(centerCol, texture2D(image, gl_TexCoord[0].xy), imageMix);
        }
    }

    float alpha = 0;

    if (centerCol.a != 0) {
        gl_FragColor = vec4(mix(centerCol.rgb, color, mixFactor), centerCol.a);
        // gl_FragColor = vec4(centerCol.rgb, centerCol.a);
    } else {
        for (float x = -radius; x < radius; x++) {
            for (float y = -radius; y < radius; y++) {
                vec4 currentColor = texture2D(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y));

                /*if (blur) {
                    currentColor = blur13(texture, gl_TexCoord[0].xy + vec2(texelSize.x * x, texelSize.y * y), dimensions, vec2(3, 3));
                }*/

                if (currentColor.a != 0)
                alpha += divider > 0 ? max(0, (maxSample - distance(vec2(x, y), vec2(0))) / divider) : 1;
                alpha *= minAlpha;
            }
        }
        gl_FragColor = vec4(color, alpha);
    }
}
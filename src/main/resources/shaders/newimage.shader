#shader vert
#version 130

void main(void) {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
}

    #shader frag
    #version 130

uniform sampler2D overlaySampler;
uniform sampler2D sampler;
uniform vec2 dimensions;
uniform float imageX;
uniform float imageY;
uniform float imageWidth;
uniform float imageHeight;
uniform float mixFactor;
uniform float colorMixFactor;
uniform vec4 inputColor;
uniform bool fill;

void main() {
    // vec2 coords1 = vec2(gl_FragCoord.x / imageDimensions.z, gl_FragCoord.y / imageDimensions.w);
    if (gl_FragCoord.x >= imageX && gl_FragCoord.y >= imageY && gl_FragCoord.x <= (imageX + imageWidth) && gl_FragCoord.y <= (imageY + imageHeight)) {
        vec2 coords = vec2(((gl_FragCoord.x - imageX) / imageWidth), ((gl_FragCoord.y - imageY) / imageHeight));

        /*vec4 blurColor = blur13(sampler, gl_TexCoord[0].xy / dimensions, dimensions, vec2(1, 0));
        vec4 blurOverlay = blur13(overlaySampler, gl_FragCoord.xy / dimensions, dimensions, vec2(1, 0));*/

        vec4 texColor = texture(sampler, gl_TexCoord[0].xy);
        vec4 overlayColor = texture(overlaySampler, coords);

        vec4 color = mix(texture(sampler, gl_TexCoord[0].xy), texture(overlaySampler, coords), mixFactor);
        // vec4 finalColor = mix(blurColor, blurOverlay, mixFactor);
        color.xyz = mix(color.xyz, inputColor.xyz, colorMixFactor);
        // finalColor.xyz = mix(finalColor.xyz, inputColor.xyz, colorMixFactor);
        color.a = inputColor.a;
        // finalColor.a = inputColor.a;
        if (texColor.a == 0.0 && !fill) color.a = 0.0;
        gl_FragColor = color;
    } else {
        gl_FragColor = vec4(1, 1, 1, 1);
    }
}
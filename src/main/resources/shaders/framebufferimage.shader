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

uniform sampler2D sampler;
uniform sampler2D overlaySampler;
uniform float mixFactor;
uniform float colorMixFactor;
uniform vec4 inputColor;

void main(void) {
    vec4 framebufferColor = texture2D(sampler, gl_TexCoord[0].xy);
    if (framebufferColor.a > 0.0) {
        gl_FragColor = mix(framebufferColor, texture2D(overlaySampler, gl_TexCoord[0].xy), mixFactor);
    }
}
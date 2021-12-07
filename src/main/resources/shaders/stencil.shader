#shader vert
#version 130

void main(void) {
    //Map gl_MultiTexCoord0 to index zero of gl_TexCoord
    gl_TexCoord[0] = gl_MultiTexCoord0;

    //Calculate position by multiplying model, view and projection matrix by the vertex vector
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

#shader frag
#version 130

uniform sampler2D sampler;
uniform sampler2D overlaySampler;
uniform usampler2D stencilSampler;
uniform float mixFactor;
uniform float colorMixFactor;
uniform vec4 inputColor;
uniform vec2 dimensions;

void main(void) {
    vec4 framebufferColor = texture2D(sampler, gl_TexCoord[0].xy);
    uvec4 stencil = texture(stencilSampler, vec2(gl_FragCoord.x / dimensions.x, gl_FragCoord.y / dimensions.y));
    int stencilNew = int(stencil.r);
    int stencilNew1 = int(stencil.g);
    int stencilNew2 = int(stencil.b);
    int stencilNew3 = int(stencil.a);
    if (framebufferColor.a > 0.0) {
        gl_FragColor = mix(framebufferColor, texture2D(overlaySampler, gl_TexCoord[0].xy), mixFactor);
    }
}
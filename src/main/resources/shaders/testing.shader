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

void main(void) {
    vec4 centerColor = texture2D(sampler, gl_TexCoord[0].xy);
    if (centerColor.a != 0.0) {
        gl_FragColor = vec4(centerColor.rgb, 0);
    } else {
        gl_FragColor = vec4(1, 1, 1, 1);
    }
}
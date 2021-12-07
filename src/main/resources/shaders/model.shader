#shader vert
#version 130

void main(void) {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
}

#shader frag
#version 130

uniform sampler2D sampler;

void main() {
    gl_FragColor = texture(sampler, gl_TexCoord[0].xy);
}
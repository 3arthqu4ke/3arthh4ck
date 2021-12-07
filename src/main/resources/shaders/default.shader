#shader vert
#version 120

void main(void) {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
}

#shader frag
#version 120

void main(void) {
    gl_FragColor = vec4(1, 1, 1, 1);
}
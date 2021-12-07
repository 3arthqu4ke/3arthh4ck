#shader vert
#version 330 compatibility
layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 tex;

out vec2 texCoord;

void main()
{
    gl_Position = gl_ModelViewProjectionMatrix * vec4(pos, 1.0);
    texCoord = tex;
}

#shader frag
#version 330 compatibility

out vec4 FragColor;

in vec2 texCoord;

uniform sampler2D sampler;

void main()
{
    FragColor = texture(sampler, texCoord);
}
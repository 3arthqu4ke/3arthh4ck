#shader vert
#version 330 compatibility
layout (location = 0) in vec3 pos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 tex;
layout (location = 3) in ivec4 bones;
layout (location = 4) in vec4 weights;

// uniform mat4 model;
// uniform mat4 projection;

const int MAX_BONES = 100;
const int MAX_BONE_INFLUENCE = 4;

uniform mat4 finalBonesMatrices[100];

out vec2 texCoord;

void main()
{
    vec4 totalPosition = vec4(0.0f);
    for(int i = 0 ; i < MAX_BONE_INFLUENCE ; i++)
    {
        if(bones[i] == -1) {
            continue;
        }
        if(bones[i] >= MAX_BONES) {
            totalPosition = vec4(pos, 1.0f);
            break;
        }
        vec4 localPosition = finalBonesMatrices[bones[i]] * vec4(pos, 1.0f);
        totalPosition += localPosition * weights[i];
        vec3 localNormal = mat3(finalBonesMatrices[bones[i]]) * normal;
    }
    // gl_Position = projection * model * vec4(pos, 1.0);
    gl_Position = gl_ModelViewProjectionMatrix * totalPosition;
    // gl_Position = gl_ModelViewProjectionMatrix * vec4(pos, 1.0);
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
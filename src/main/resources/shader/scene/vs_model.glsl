#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normals;
layout (location = 2) in vec2 texCoords;

layout (std140) uniform Matrices {
    mat4 projection;
    mat4 view;
};

out vec2 TexCoords;
uniform mat4 modelMtx;

void main() {
    gl_Position = projection * view * modelMtx * vec4(position, 1.0f);
    TexCoords = texCoords;
}

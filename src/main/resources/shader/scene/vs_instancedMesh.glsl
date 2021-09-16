#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normals;
layout (location = 2) in vec2 texCoords;
layout (location = 3) in vec3 instancePosition;

layout (std140) uniform Matrices {
    mat4 projection;
    mat4 view;
};

out vec2 TexCoords;

void main() {
    mat4 modelMatrix = mat4(1.0);
    //第三列存储的就是translate的位置值
    modelMatrix[3] = vec4(instancePosition, 1.0f);
    gl_Position = projection * view * modelMatrix * vec4(position, 1.0f);
    TexCoords = texCoords;
}


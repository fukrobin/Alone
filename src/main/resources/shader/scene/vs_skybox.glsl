#version 330 core

layout (location = 0) in vec3 position;

layout (std140) uniform Matrices {
    mat4 projection;
    mat4 view;
};

out vec3 TexCoords;

void main()
{
    mat4 _view = view;
    _view[3] = vec4(0, 0, 0, 1.0f);
    vec4 pos =   projection * _view * vec4(position, 1.0);
    gl_Position = pos.xyww;
    TexCoords = position;
}
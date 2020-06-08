#version 330 core

in vec2 TexCoords;

out vec4 fragColor;

uniform sampler2D modelTexture;


void main() {
    fragColor = texture(modelTexture, TexCoords);
}

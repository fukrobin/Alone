#version 330 core

in vec2 TexCoord;

out vec4 fragColor;

uniform sampler2D screenTexture;

void main() {
    fragColor = texture(screenTexture, TexCoord);
}

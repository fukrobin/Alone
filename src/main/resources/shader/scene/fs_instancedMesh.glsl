#version 330 core
#extension GL_ARB_conservative_depth : warn

in vec2 TexCoords;

out vec4 fragColor;

uniform sampler2D Texture;

void main() {
    fragColor = texture(Texture, TexCoords);
}

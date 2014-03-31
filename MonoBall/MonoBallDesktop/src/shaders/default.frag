#version 150
uniform sampler2D sampler0;

in vec3 normal;
in vec4 color;
in vec3 texcoord;

out vec4 frag_color;

void main() {
    frag_color = color * texture(sampler0, texcoord.xy);
}
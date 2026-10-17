#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D MainSampler;

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main() {
    vec3 color = texture(MainSampler, texCoord).rgb;
    float luminance = dot(color, vec3(0.2126, 0.7152, 0.0722));

    // The first test preset: stronger colors without changing game geometry.
    color = mix(vec3(luminance), color, 1.22);
    color = (color - 0.5) * 1.035 + 0.5;

    fragColor = vec4(clamp(color, 0.0, 1.0), 1.0);
}

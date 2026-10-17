#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D MainSampler;

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

void main() {
    vec3 color = texture(MainSampler, texCoord).rgb;

    // Gentle contrast and warm color grading.
    color = clamp((color - 0.5) * 1.08 + 0.5, 0.0, 1.0);
    color *= vec3(1.045, 1.005, 0.955);

    // Soft vignette on the outer edges.
    vec2 centered = texCoord * 2.0 - 1.0;
    float vignette = 1.0 - 0.18 * smoothstep(0.25, 1.48, dot(centered, centered));

    fragColor = vec4(clamp(color * vignette, 0.0, 1.0), 1.0);
}

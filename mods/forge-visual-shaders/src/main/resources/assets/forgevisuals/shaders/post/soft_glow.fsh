#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D MainSampler;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

vec3 highlights(vec2 uv) {
    vec3 c = texture(MainSampler, clamp(uv, vec2(0.0), vec2(1.0))).rgb;
    // Isolate bright areas to create a lightweight one-pass approximate glow.
    return max(c - vec3(0.68), vec3(0.0));
}

void main() {
    vec3 original = texture(MainSampler, texCoord).rgb;
    vec2 pixel = 1.0 / max(OutSize, vec2(1.0));

    vec3 halo = highlights(texCoord) * 2.0;
    halo += highlights(texCoord + pixel * vec2( 3.0, 0.0));
    halo += highlights(texCoord + pixel * vec2(-3.0, 0.0));
    halo += highlights(texCoord + pixel * vec2( 0.0, 3.0));
    halo += highlights(texCoord + pixel * vec2( 0.0,-3.0));
    halo += highlights(texCoord + pixel * vec2( 2.0, 2.0));
    halo += highlights(texCoord + pixel * vec2(-2.0,-2.0));
    halo += highlights(texCoord + pixel * vec2( 2.0,-2.0));
    halo += highlights(texCoord + pixel * vec2(-2.0, 2.0));

    vec3 result = original + (halo / 10.0) * 0.85;
    fragColor = vec4(clamp(result, 0.0, 1.0), 1.0);
}

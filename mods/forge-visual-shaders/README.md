# Forge Visual Shaders — milestone 1 (MC 26.3 / Forge 66)

Experimental visual shader effects for Forge 26.3 implemented using **Minecraft 26.3's native post-effect API**, not OptiFine internals.

## What it actually does
Three shader effects run on the rendered screen:
- `forgevisuals:cinematic` — warm tones, contrast and soft vignette
- `forgevisuals:vibrant` — stronger saturation and contrast
- `forgevisuals:soft_glow` — lightweight screen-space approximation of glow around bright pixels

These are **post-processing effects**. They do NOT add shadows, water reflections, waving grass, depth-aware ray tracing, or support for existing OptiFine/Iris shaderpacks (Complementary, BSL, etc).

## Download

The [Build Forge Visual Shaders](https://github.com/pupupu-web/Minecraft-Forge-Mods/actions/workflows/build-forge-visual-shaders.yml) workflow produces two artifacts:

1. `ForgeVisualShaders-26.3` — a Forge mod JAR containing all shader assets.
2. `ForgeVisualShaders-26.3-ResourcePack` — standalone vanilla/Forge resource-pack ZIP, to test if the Forge mod assets are not discovered by your installation.

**Use either the JAR or the ZIP during a test, not both**, to avoid duplicate resource definitions.

## In-game testing

### Mod JAR
Place the JAR in the Forge 66.x `mods` directory. Ensure the shader effect IDs are available in a world with cheats/operator privileges. If they are not, use the standalone resource pack for this stage.

### Standalone resource pack
Put the resource-pack ZIP inside `resourcepacks`, then enable it from Options > Resource Packs. Minecraft 26.3 can run these native post effects even without any shader mod.

Apply one effect at a time:
```mcfunction
/posteffect add @s forgevisuals:cinematic
/posteffect add @s forgevisuals:vibrant
/posteffect add @s forgevisuals:soft_glow
/posteffect list @s
/posteffect clear @s
```

Effects can stack. Prefer `/posteffect clear @s` before testing a different look.
No cheats available? Temporarily open a single-player world to LAN and enable cheats, or try a creative testing world.

## Next milestones

- v0.2: client GUI / hotkey for selecting native post effects without commands or cheats
- v0.3: saved settings, shader pack discovery/validation and an experimental compatibility layer
- major milestone (separate and substantially harder): real Iris/OptiFine shaderpack rendering, shadows/depth, compatibility with Minecraft's RenderPearl Vulkan/OpenGL backend. Requires renderer modifications/port work beyond native post effects.

Source is MIT licensed, newly written for this project. No OptiFine code or bundled proprietary shaderpacks.

# Cozy Beds 0.2.0 — NeoForge 26.3

An actual client-side bedcover renderer instead of the crude 0.1.0 stretched wool blocks.

## What changed
- Replaces **all block-display spawning** with baked 3D entity-model render layers.
- Draws a 3-part, subtly sculpted, 1–3-pixel-thick blanket and a narrow white folded top edge.
- Adds a flat, softly textured pillow. No new collision boxes and no placement items.
- Matches the vanilla **16 bed colors** by tinting a single handmade pixel-art fabric texture.
- Hides sleeping player/villager torso, limbs, and selected clothing model parts using NeoForge's official render-state modifiers, leaving the head visible.
- Aligns with vanilla entity sleeping rotation (all four bed directions).
- Cats remain untouched.
- Removes older v0.1.0 spawned displays from loaded worlds, using their old unique persistent tag.

## Important compatibility notes
This uses **NeoForge 26.3's entity render layer API**, not renderer mixins or invasive block model replacement.
Minecraft 26.3, Java 25 and NeoForge 26.3.0.90 are required.
The mod must be installed on server and client (legacy display cleanup runs server side).
The hiding modifier is client-only; gameplay and sleep position are unchanged.

## Limitations / testing
The render geometry is newly built and the JAR is compiled in CI, but **visual placement still requires an actual Minecraft client test**.
Different armor models, sleeping villager clothes, shaders and first-person sleeping camera may need adjustment.
Model-part override behavior varies with other custom entity renderers.

## Build
Inside `mods/cozy-beds` run `gradle --no-daemon build` with Java 25 and Gradle 9.2.1.
Or download the automatically produced JAR from the [Cozy Beds GitHub Actions build](https://github.com/pupupu-web/Minecraft-Forge-Mods/actions/workflows/build-cozy-beds.yml).

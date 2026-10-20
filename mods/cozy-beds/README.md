# Cozy Beds 0.2.1 — NeoForge 26.3

Version 0.2.1 fixes the bed geometry placement regression demonstrated in the user's 0.2.0 screenshot.

- Renders the custom quilt in bed-centered **world coordinates**, independent of the sleeping humanoid's complex rotations.
- Restricts the quilt to the actual bed dimensions (14/16 blocks wide; from head to foot).
- Removes the extra white pillow cube that collided with the villager's face. Vanilla bedding already includes its own pillow.
- Adds visible seams, hand-painted stitched-fabric texture and a contrasting white cuff.
- Keeps all 16 bed colors; lightly shades white quilt to remain distinct from the white mattress.
- Retains automatic cleanup of old 0.1.0 block displays, and attempts to hide sleeping torsos, arms and legs through render-state modifiers.
- Does not change collisions, sleeping behavior or cats.

**Check in the Minecraft client** for exact fit and clothing clipping with every villager profession; a passing GitHub build verifies Java compilation only.

## Build
Gradle 9.2.1, Java 25, NeoForge 26.3.0.90.
`cd mods/cozy-beds && gradle --no-daemon build`

Download JAR via [GitHub Actions](https://github.com/pupupu-web/Minecraft-Forge-Mods/actions/workflows/build-cozy-beds.yml).
Replace *all* older CozyBeds JARs in your mods folder before installing this version.

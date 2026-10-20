# Cozy Beds — NeoForge 26.3

**Version 0.1.0 (initial proof of concept)**

A vanilla-style solution to the awkward appearance of Minecraft sleeping poses.

### What happens
- When a **player or villager** sleeps in a normal colored bed, a low-profile wool-textured quilt, a folded white cuff and a 3D white pillow appear.
- The quilt matches **all 16 vanilla bed colors**.
- The additions are `block_display` entities without block collisions and vanish when the sleeper wakes up.
- Bed orientation and sleeping facing are detected automatically.
- Decorative displays are reused or cleaned up after world reloads, so there should not be duplicates.
- Sleeping cats are left alone by the mod's logic; they are not made invisible or moved.

### Limitations
- Initial implementation uses simple cuboid Minecraft wool textures, not a custom curved animated blanket or a full bed remesh.
- Player/villager bodies are covered rather than relocated or hidden; some armor or poses may clip through the blanket.
- Added pillow is shown **while occupied** only.
- Straw beds and modded bed types are not yet supported.
- On multiplayer servers, install on both server and clients (NeoForge mod).
- Needs in-game visual testing; a successful Java build does not guarantee ideal clipping with every skin, armor or mod.

### Build
This is an **independent Gradle project**, not part of the Forge build at the repository root.

```bash
cd mods/cozy-beds
gradle --no-daemon build
```

Use **Java 25** and **Gradle 9.2.1**, targeting **Minecraft 26.3 / NeoForge 26.3.0.90**.

### GitHub Actions
Run the **Cozy Beds / NeoForge 26.3** workflow or push changes to this directory.
Then download its `CozyBeds-NeoForge-26.3` build artifact, unzip it, and put the JAR in the Minecraft `mods` folder.

### Testing checklist
1. Sleep on a red bed and a blue bed, including all four compass orientations.
2. Watch another player in multiplayer while sleeping.
3. Watch a villager sleep in a village.
4. Test an armored player and a cat sharing a bed; check clipping.
5. Wake, break the bed, restart the world and revisit the area; decorations should disappear/reuse correctly.

This version intentionally avoids invasive renderer mixins to reduce risk of crashing Sodium or Iris.

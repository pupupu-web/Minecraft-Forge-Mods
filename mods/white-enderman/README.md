# White Enderman (Minecraft 26.3 / Forge 66)

An **independent mod** stored separately from the Forge Build Check project.

## In-game features
- New entity `whiteenderman:white_enderman` (vanilla Enderman AI, teleporting, block carrying, aggression and glowing eye layer).
- Pale-white textured Enderman without modifying the original black Enderman.
- Custom spawn egg in the Creative **Spawn Eggs** tab.
- Localized entity and egg names: English, Ukrainian and Russian.
- No natural spawning in v1; deliberate for this first runtime test.

## Test
Download **WhiteEnderman-26.3** from the latest successful [Actions](https://github.com/pupupu-web/Minecraft-Forge-Mods/actions/workflows/build-white-enderman.yml) run, unpack the artifact and install `white-enderman-1.0.0.jar` in the game `mods` folder.

With cheats enabled, use:
```mcfunction
/summon whiteenderman:white_enderman ~ ~ ~
/give @s whiteenderman:white_enderman_spawn_egg
```

Requires Minecraft 26.3, Forge 66.x, and a launcher compatible with Java 25.

## Building
This folder is a standalone Gradle build. The GitHub workflow decodes two tiny **original** 8-bit PNG source textures from Base64 into `src/main/resources/assets/whiteenderman/textures/`, then builds the project in the cloud. This keeps the connector-uploaded text source complete without local binaries or secrets.
